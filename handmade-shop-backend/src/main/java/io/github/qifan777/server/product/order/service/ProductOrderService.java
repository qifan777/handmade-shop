package io.github.qifan777.server.product.order.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.service.WxPayService;
import io.github.qifan777.server.address.entity.dto.AddressView;
import io.github.qifan777.server.address.repository.AddressRepository;
import io.github.qifan777.server.dict.model.DictConstants;
import io.github.qifan777.server.infrastructure.model.WxPayPropertiesExtension;
import io.github.qifan777.server.order.entity.BaseOrderTable;
import io.github.qifan777.server.payment.entity.Payment;
import io.github.qifan777.server.payment.entity.PaymentDraft;
import io.github.qifan777.server.payment.entity.PaymentTable;
import io.github.qifan777.server.payment.entity.dto.PaymentPriceView;
import io.github.qifan777.server.payment.model.WeChatPayModel;
import io.github.qifan777.server.payment.service.WeChatPayService;
import io.github.qifan777.server.product.carriage.repository.ProductCarriageTemplateRepository;
import io.github.qifan777.server.product.item.entity.ProductOrderItemDraft;
import io.github.qifan777.server.product.order.entity.ProductOrder;
import io.github.qifan777.server.product.order.entity.ProductOrderDraft;
import io.github.qifan777.server.product.order.entity.ProductOrderFetcher;
import io.github.qifan777.server.product.order.entity.ProductOrderTable;
import io.github.qifan777.server.product.order.entity.dto.ProductOrderInput;
import io.github.qifan777.server.product.order.repository.ProductOrderRepository;
import io.github.qifan777.server.product.root.entity.Product;
import io.github.qifan777.server.product.root.entity.ProductTable;
import io.github.qifan777.server.refund.entity.RefundRecord;
import io.github.qifan777.server.refund.entity.RefundRecordDraft;
import io.github.qifan777.server.refund.repository.RefundRecordRepository;
import io.github.qifan777.server.vip.root.service.VipService;
import io.qifan.infrastructure.common.constants.ResultCode;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class ProductOrderService {
    private final ProductOrderRepository productOrderRepository;
    private final JSqlClient jSqlClient;
    private final WeChatPayService weChatPayService;
    private final WxPayService wxPayService;
    private final WxPayPropertiesExtension wxPayPropertiesExtension;
    private final AddressRepository addressRepository;
    private final ProductCarriageTemplateRepository productCarriageTemplateRepository;
    private final VipService vipService;

    public String create(ProductOrderInput productOrderInput) {
        String orderId = IdUtil.fastSimpleUUID();
        PaymentPriceView calculated = calculate(productOrderInput);
        Payment payment = PaymentDraft.$.produce(
                draft -> draft
                        .setId(orderId)
                        .setPayType(DictConstants.PayType.WE_CHAT_PAY)
                        .setCouponAmount(calculated.getCouponAmount())
                        // 商品价格计算
                        .setProductAmount(calculated.getProductAmount())
                        // 运费计算
                        .setDeliveryFee(calculated.getDeliveryFee())
                        // VIP价格计算
                        .setVipAmount(calculated.getVipAmount())
                        // 实际支付价格计算
                        .setPayAmount(calculated.getPayAmount())
        );
        productOrderInput.getItems().forEach(productOrderItem -> {
            // 扣减库存;
            ProductTable t = ProductTable.$;
            jSqlClient
                    .createUpdate(t)
                    .set(t.stock(), t.stock().minus(productOrderItem.getProductCount()))
                    .where(t.id().eq(productOrderItem.getProductId()))
                    .execute();
        });
        ProductOrder entity = ProductOrderDraft.$.produce(productOrderInput
                        .toEntity(),
                draft -> {
                    // 设置订单项关联的订单id
                    draft.setItems(draft
                            .items()
                            .stream()
                            .map(item -> ProductOrderItemDraft.$.produce(item, productOrderItemDraft -> productOrderItemDraft.setProductOrderId(orderId)))
                            .toList()
                    );
                    // 设置订单的id和状态
                    draft.setId(orderId)
                            .setStatus(DictConstants.ProductOrderStatus.TO_BE_PAID);
                    // 设置支付详情
                    draft.baseOrder()
                            .setId(orderId)
                            .setType(DictConstants.OrderType.PRODUCT_ORDER)
                            .setPayment(payment)
                            .setAddress(new AddressView(addressRepository.findUserAddressById(productOrderInput.getAddressId())
                                    .orElseThrow(() -> new BusinessException("地址不存在"))));
                });
        ProductOrder save = productOrderRepository.save(entity);
        return save.id();
    }

    public PaymentPriceView calculate(ProductOrderInput productOrderInput) {
        Payment produce = PaymentDraft.$.produce(draft -> {
            draft.setProductAmount(BigDecimal.ZERO)
                    .setDeliveryFee(BigDecimal.ZERO)
                    .setVipAmount(BigDecimal.ZERO)
                    .setCouponAmount(BigDecimal.ZERO);
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (var item : productOrderInput.getItems()) {
                Product product = Optional.ofNullable(jSqlClient.findById(Product.class, item.getProductId()))
                        .orElseThrow(() -> new BusinessException(ResultCode.NotFindError, "商品不存在"));
                if (product.stock() - item.getProductCount() < 0) {
                    throw new BusinessException(ResultCode.ValidateError, "商品库存不足");
                }
                BigDecimal price = product.price().multiply(BigDecimal.valueOf(item.getProductCount()));
                totalPrice = totalPrice.add(price);
            }
            // 计算商品总价
            draft.setProductAmount(totalPrice);
            // 计算优惠券
            // ...
            // 计算运费
            addressRepository.findUserAddressById(productOrderInput.getAddressId()).ifPresent(address -> {
                BigDecimal carriage = productCarriageTemplateRepository.findValid()
                        .configs()
                        .stream()
                        .filter(config -> String.join(";", config.getProvince()).contains((address.province())))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException("当前省份不支持发货请联系客服"))
                        .getPriceRanges()
                        .stream()
                        .filter(priceRange -> draft.productAmount().compareTo(priceRange.getMinPrice()) >= 0 &&
                                              draft.productAmount().compareTo(priceRange.getMaxPrice()) <= 0)
                        .findFirst()
                        .orElseThrow(() -> new BusinessException("运费模板不适应与该订单，请联系客服"))
                        .getCarriage();
                draft.setDeliveryFee(carriage);
            });

            // 计算VIP优惠价格
            draft.setVipAmount(vipService.calculate(totalPrice));
            // 计算实际支付价格
            draft.setPayAmount(
                    draft.productAmount()
                            .add(draft.deliveryFee())
                            .subtract(draft.couponAmount())
                            .subtract(draft.vipAmount())
            );
        });
        return new PaymentPriceView(produce);
    }

    public WxPayUnifiedOrderV3Result.JsapiResult prepay(String id) {
        ProductOrder productOrder = productOrderRepository.findById(id, ProductOrderRepository.COMPLEX_FETCHER_FOR_FRONT)
                .orElseThrow(() -> new BusinessException(ResultCode.NotFindError));
        checkStatus(productOrder, DictConstants.ProductOrderStatus.TO_BE_PAID);
        checkOwner(productOrder);
        WxPayUnifiedOrderV3Result.JsapiResult prepay = weChatPayService.prepay(new WeChatPayModel().setBaseOrder(productOrder.baseOrder())
                .setExpiredMinutes(5)
                .setNotifyUrl("/front/product-order/notify/pay/wechat"));
        log.info("预支付订单内容：{}", prepay);
        return prepay;
    }

    @SneakyThrows
    public String paymentNotifyWechat(String body, SignatureHeader signatureHeader) {
        WxPayNotifyV3Result.DecryptNotifyResult notifyResult = wxPayService.parseOrderNotifyV3Result(body, signatureHeader)
                .getResult();
        log.info("支付回调:{}", notifyResult);
        String outTradeNo = notifyResult.getOutTradeNo();
        ProductOrder productOrder = productOrderRepository.findById(outTradeNo,
                ProductOrderRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(
                () -> new BusinessException(ResultCode.NotFindError, "订单不存在"));
        // 设置微信支付订单id
        StpUtil.switchTo(productOrder.creator().id());
        PaymentTable t1 = PaymentTable.$;
        jSqlClient.createUpdate(t1)
                .where(t1.baseOrder().id().eq(productOrder.id()))
                .set(t1.tradeNo(), notifyResult.getTransactionId())
                .set(t1.payTime(), LocalDateTime.now())
                .execute();
        changeStatus(productOrder.id(), DictConstants.ProductOrderStatus.TO_BE_DELIVERED);
        return productOrder.id();
    }

    public String deliver(String id, String trackingNumber) {
        checkStatus(productOrderRepository.findById(id).orElseThrow(() -> new BusinessException(ResultCode.NotFindError, "订单不存在"))
                , DictConstants.ProductOrderStatus.TO_BE_DELIVERED, DictConstants.ProductOrderStatus.TO_BE_RECEIVED);
        var t = BaseOrderTable.$;
        jSqlClient.createUpdate(t)
                .set(t.trackingNumber(), trackingNumber)
                .where(t.id().eq(id));
        changeStatus(id, DictConstants.ProductOrderStatus.TO_BE_RECEIVED);
        return id;
    }

    public String unpaidCancelForUser(String id) {
        ProductOrder productOrder = productOrderRepository.findById(id, ProductOrderRepository.COMPLEX_FETCHER_FOR_FRONT)
                .orElseThrow(() -> new BusinessException(ResultCode.NotFindError, "订单不存在"));
        checkStatus(productOrder, DictConstants.ProductOrderStatus.TO_BE_PAID);
        checkOwner(productOrder);
        productOrder.items().forEach(productOrderItem -> {
            // 恢复库存
            ProductTable t = ProductTable.$;
            jSqlClient.createUpdate(t)
                    .where(t.id().eq(productOrderItem.productId()))
                    .set(t.stock(), t.stock().plus(productOrderItem.productCount()))
                    .execute();
        });
        changeStatus(productOrder.id(), DictConstants.ProductOrderStatus.CLOSED);
        return productOrder.id();
    }

    @SneakyThrows
    public String paidCancelForAdmin(String orderId) {
        ProductOrder productOrder = productOrderRepository.findById(orderId, ProductOrderRepository.COMPLEX_FETCHER_FOR_FRONT)
                .orElseThrow(() -> new BusinessException(ResultCode.NotFindError, "订单不存在"));
        checkStatus(productOrder, DictConstants.ProductOrderStatus.TO_BE_RECEIVED, DictConstants.ProductOrderStatus.TO_BE_DELIVERED);
        String refundOrderId = IdUtil.fastSimpleUUID();
        RefundRecord refundRecord = RefundRecordDraft.$.produce(draft -> {
            draft.setId(refundOrderId);
            WxPayRefundV3Request wxPayRefundV3Request = new WxPayRefundV3Request().setOutTradeNo(
                            orderId)
                    .setOutRefundNo(refundOrderId)
                    .setNotifyUrl(wxPayPropertiesExtension.getNotifyUrl() + "/front/product-order/notify/refund/wechat")
                    .setReason("退款")
                    .setAmount(new WxPayRefundV3Request
                            .Amount()
                            // 计算价格
                            .setRefund(productOrder.baseOrder().payment().payAmount()
                                    .multiply(BigDecimal.valueOf(100)).intValue())
                            .setTotal(productOrder.baseOrder().payment().payAmount()
                                    .multiply(BigDecimal.valueOf(100)).intValue())
                            .setCurrency("CNY"));
            WxPayRefundV3Result wxPayRefundV3Result = wxPayService.refundV3(wxPayRefundV3Request);
            draft.setRefundId(wxPayRefundV3Result.getRefundId())
                    .setStatus(DictConstants.RefundStatus.REFUNDING)
                    .setOrderId(orderId)
                    .setAmount(productOrder.baseOrder().payment().payAmount())
                    .setRefundApplicationDetails(wxPayRefundV3Result)
                    .setReason("退款");
        });
        return jSqlClient.save(refundRecord).getModifiedEntity().id();
    }

    @SneakyThrows
    public String refundNotifyWeChat(String body, SignatureHeader signatureHeader) {
        WxPayRefundNotifyV3Result.DecryptNotifyResult result = wxPayService.parseRefundNotifyV3Result(body, signatureHeader)
                .getResult();
        log.info("退款回调：{}", result);
        RefundRecord refundRecord = Optional.ofNullable(jSqlClient.findById(RefundRecordRepository.COMPLEX_FETCHER_FOR_FRONT, result.getOutRefundNo()))
                .orElseThrow(() -> new BusinessException(ResultCode.NotFindError, "退款订单不存在"));
        ProductOrder productOrder = productOrderRepository.findById(refundRecord.orderId(), ProductOrderFetcher.$.creator(true)).orElseThrow(() -> new BusinessException(ResultCode.NotFindError, "订单不存在"));
        StpUtil.switchTo(productOrder.creator().id());
        if (result.getRefundStatus().equals("SUCCESS")) {
            // 需要将订单回退到未支付状态再取消
            log.info("将订单状态回退到待支付");
            changeStatus(result.getOutTradeNo(), DictConstants.ProductOrderStatus.TO_BE_PAID);
            log.info("取消订单订单");
            unpaidCancelForUser(result.getOutTradeNo());
            log.info("将订单状态设置为已退款");
            changeStatus(result.getOutTradeNo(), DictConstants.ProductOrderStatus.REFUNDED);
            log.info("生成退款记录");
            jSqlClient.save(RefundRecordDraft.$.produce(refundRecord, draft -> {
                draft.setRefundNotifyDetails(result)
                        .setStatus(DictConstants.RefundStatus.SUCCESS)
                ;
            }));
        } else {
            jSqlClient.save(RefundRecordDraft.$.produce(refundRecord, draft -> draft.setRefundNotifyDetails(result)
                    .setStatus(DictConstants.RefundStatus.FAILED)));
        }

        return refundRecord.id();
    }


    public void changeStatus(String id, DictConstants.ProductOrderStatus productOrderStatus) {
        ProductOrderTable t = ProductOrderTable.$;
        productOrderRepository.sql().createUpdate(t)
                .where(t.id().eq(id))
                .set(t.status(), productOrderStatus)
                .execute();
    }

    public void checkStatus(ProductOrder productOrder, DictConstants.ProductOrderStatus... productOrderStatusList) {
        for (var status : productOrderStatusList) {
            if (productOrder.status().equals(status)) {
                return;
            }
        }
        throw new BusinessException(ResultCode.ParamSetIllegal, "订单状态不正确");
    }

    public void checkOwner(ProductOrder productOrder) {
        if (!productOrder.creator().id().equals(StpUtil.getLoginIdAsString())) {
            throw new BusinessException(ResultCode.ParamSetIllegal, "非本人操作");
        }
    }
}