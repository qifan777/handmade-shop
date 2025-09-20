package io.github.qifan777.server.handicraft.order.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.service.WxPayService;
import io.github.qifan777.server.Fetchers;
import io.github.qifan777.server.Objects;
import io.github.qifan777.server.dict.model.DictConstants;
import io.github.qifan777.server.handicraft.booking.entity.HandicraftBooking;
import io.github.qifan777.server.handicraft.booking.entity.HandicraftBookingFetcher;
import io.github.qifan777.server.handicraft.booking.entity.dto.HandicraftBookingView;
import io.github.qifan777.server.handicraft.booking.repository.HandicraftBookingRepository;
import io.github.qifan777.server.handicraft.order.entity.HandicraftOrder;
import io.github.qifan777.server.handicraft.order.entity.HandicraftOrderDraft;
import io.github.qifan777.server.handicraft.order.entity.HandicraftOrderFetcher;
import io.github.qifan777.server.handicraft.order.entity.HandicraftOrderTable;
import io.github.qifan777.server.handicraft.order.entity.dto.HandicraftOrderInput;
import io.github.qifan777.server.handicraft.order.repository.HandicraftOrderRepository;
import io.github.qifan777.server.infrastructure.model.WxPayPropertiesExtension;
import io.github.qifan777.server.payment.entity.Payment;
import io.github.qifan777.server.payment.entity.PaymentDraft;
import io.github.qifan777.server.payment.entity.PaymentTable;
import io.github.qifan777.server.payment.entity.dto.PaymentPriceView;
import io.github.qifan777.server.payment.model.WeChatPayModel;
import io.github.qifan777.server.payment.service.WeChatPayService;
import io.github.qifan777.server.refund.entity.RefundRecord;
import io.github.qifan777.server.refund.entity.RefundRecordDraft;
import io.github.qifan777.server.refund.repository.RefundRecordRepository;
import io.github.qifan777.server.vip.root.service.VipService;
import io.qifan.infrastructure.common.constants.ResultCode;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.EnableDtoGeneration;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
@EnableDtoGeneration
public class HandicraftOrderService {
    private final HandicraftOrderRepository handicraftOrderRepository;
    private final JSqlClient jSqlClient;
    private final WeChatPayService weChatPayService;
    private final WxPayService wxPayService;
    private final WxPayPropertiesExtension wxPayPropertiesExtension;
    private final HandicraftBookingRepository handicraftBookingRepository;
    private final VipService vipService;

    public String create(HandicraftOrderInput handicraftOrderInput) {
        String orderId = IdUtil.fastSimpleUUID();
        CalculateView calculated = calculate(handicraftOrderInput);
        Payment payment = PaymentDraft.$.produce(
                calculated.getPaymentPriceView().toEntity(),
                draft -> draft
                        .setId(orderId)
                        .setPayType(DictConstants.PayType.WE_CHAT_PAY)
        );
        HandicraftOrder entity = HandicraftOrderDraft.$.produce(handicraftOrderInput
                        .toEntity(),
                draft -> {
                    // 设置订单项关联的订单id
                    draft.setItems(draft.items().stream().map(item -> {
                        return Objects.createHandicraftOrderItem(item, itemDraft -> {
                            HandicraftBookingView handicraftBookingView = calculated
                                    .getBookingViews()
                                    .stream()
                                    .filter(bookingView -> bookingView.getId().equals(item.handicraftBookingId()))
                                    .findFirst().orElseThrow(() -> new BusinessException("未找到计算结果"));
                            itemDraft.setHandicraftOrderId(orderId)
                                    .setHandicraftBookingView(handicraftBookingView);
                        });
                    }).toList());
                    // 设置订单的id和状态
                    draft.setId(orderId)
                            .setStatus(DictConstants.BookingOrderStatus.TO_BE_PAID);
                    // 设置支付详情
                    draft.baseOrder()
                            .setId(orderId)
                            .setType(DictConstants.OrderType.BOOKING_ORDER)
                            .setPayment(payment);
                });
        var save = handicraftOrderRepository.save(entity);
        return save.id();
    }

    @Data
    @Accessors(chain = true)
    public static class CalculateView {
        PaymentPriceView paymentPriceView;
        List<HandicraftBookingView> bookingViews;
    }

    public CalculateView calculate(HandicraftOrderInput handicraftOrderInput) {
        List<HandicraftBookingView> bookingViews = new ArrayList<>();
        Payment payment = Objects.createPayment(draft -> {
            draft.setDeliveryFee(BigDecimal.ZERO)
                    .setCouponAmount(BigDecimal.ZERO);
            // 计算原始总价
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (var item : handicraftOrderInput.getItems()) {
                HandicraftBooking booking = handicraftBookingRepository.findById(item.getHandicraftBookingId(), HandicraftBookingFetcher.$
                                .allScalarFields()
                                .bookingCount()
                                .handicraft(Fetchers.HANDICRAFT_FETCHER.allScalarFields()))
                        .orElseThrow(() -> new BusinessException(ResultCode.NotFindError, "预约信息不存在"));
                LocalDateTime endTime = LocalDateTime.of(booking.date(), booking.endTime());
                if (booking.endTime().equals(LocalTime.of(0, 0, 0))) {
                    endTime = endTime.plusDays(1);
                }
                if (endTime.isBefore(LocalDateTime.now())) {
                    throw new BusinessException(ResultCode.ValidateError, "预约时间已过");
                }
                if (booking.locked() || booking.locked()) {
                    throw new BusinessException(ResultCode.ValidateError, "预约已被锁定");
                }
                if (booking.peopleLimit() - booking.bookingCount() <= 0) {
                    throw new BusinessException(ResultCode.ValidateError, "预约人数已满");
                }
                HandicraftBookingView handicraftBookingView = new HandicraftBookingView(booking);
                LocalDateTime startDateTime = LocalDateTime.of(booking.date(), booking.startTime());
                long totalMinutes = startDateTime.until(endTime, ChronoUnit.MINUTES);
                // 设置总时间长
                handicraftBookingView.setMinutes((int) totalMinutes);
                if (booking.handicraft().chargingType().equals(DictConstants.ChargingType.PERIOD)) {
                    // 开始时间在当前时间和预约时间段的开头之间选一个
                    LocalDateTime currentDateTime = startDateTime.isBefore(LocalDateTime.now()) ? LocalDateTime.now() : startDateTime;
                    // 计算总时长
                    // 计算剩余时长
                    long remainMinutes = currentDateTime.until(endTime, ChronoUnit.MINUTES);
                    BigDecimal price = BigDecimal.valueOf(remainMinutes).divide(BigDecimal.valueOf(totalMinutes), 2, RoundingMode.HALF_DOWN).multiply(booking.price());
                    totalPrice = totalPrice.add(price);
                    handicraftBookingView.setMinutes((int) remainMinutes);
                    handicraftBookingView.setStartTime(currentDateTime.toLocalTime());
                    handicraftBookingView.setEndTime(endTime.toLocalTime());
                    handicraftBookingView.setPrice(price);
                } else {
                    totalPrice = totalPrice.add(booking.price());
                }
                bookingViews.add(handicraftBookingView);
            }
            draft.setProductAmount(totalPrice);
            // 计算vip优惠
            draft.setVipAmount(vipService.calculate(totalPrice));
            // 计算支付金额
            draft.setPayAmount(
                    draft.productAmount()
                            .add(draft.deliveryFee())
                            .subtract(draft.couponAmount())
                            .subtract(draft.vipAmount())
            );
        });
        return new CalculateView().setBookingViews(bookingViews).setPaymentPriceView(new PaymentPriceView(payment));
    }

    public WxPayUnifiedOrderV3Result.JsapiResult prepay(String id) {
        HandicraftOrder handicraftOrder = handicraftOrderRepository.findById(id, HandicraftOrderRepository.COMPLEX_FETCHER_FOR_FRONT)
                .orElseThrow(() -> new BusinessException(ResultCode.NotFindError));
        checkStatus(handicraftOrder, DictConstants.BookingOrderStatus.TO_BE_PAID);
        checkOwner(handicraftOrder);
        WxPayUnifiedOrderV3Result.JsapiResult prepay = weChatPayService.prepay(new WeChatPayModel().setBaseOrder(handicraftOrder.baseOrder())
                .setExpiredMinutes(5)
                .setNotifyUrl("/front/handicraft-order/notify/pay/wechat"));
        log.info("预支付订单内容：{}", prepay);
        return prepay;
    }

    @SneakyThrows
    public String paymentNotifyWechat(String body, SignatureHeader signatureHeader) {
        WxPayNotifyV3Result.DecryptNotifyResult notifyResult = wxPayService.parseOrderNotifyV3Result(body, signatureHeader)
                .getResult();
        log.info("支付回调:{}", notifyResult);
        String outTradeNo = notifyResult.getOutTradeNo();
        HandicraftOrder handicraftOrder = handicraftOrderRepository.findById(outTradeNo,
                HandicraftOrderRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(
                () -> new BusinessException(ResultCode.NotFindError, "订单不存在"));
        // 设置微信支付订单id
        StpUtil.switchTo(handicraftOrder.creator().id());
        PaymentTable t1 = PaymentTable.$;
        jSqlClient.createUpdate(t1)
                .where(t1.baseOrder().id().eq(handicraftOrder.id()))
                .set(t1.tradeNo(), notifyResult.getTransactionId())
                .set(t1.payTime(), LocalDateTime.now())
                .execute();
        changeStatus(handicraftOrder.id(), DictConstants.BookingOrderStatus.TO_BE_CONFIRMED);
        return handicraftOrder.id();
    }


    public String unpaidCancelForUser(String id) {
        HandicraftOrder handicraftOrder = handicraftOrderRepository.findById(id, HandicraftOrderRepository.COMPLEX_FETCHER_FOR_FRONT)
                .orElseThrow(() -> new BusinessException(ResultCode.NotFindError, "订单不存在"));
        checkStatus(handicraftOrder, DictConstants.BookingOrderStatus.TO_BE_PAID);
        checkOwner(handicraftOrder);
        changeStatus(handicraftOrder.id(), DictConstants.BookingOrderStatus.CLOSED);
        return handicraftOrder.id();
    }

    @SneakyThrows
    public String paidCancelForAdmin(String orderId) {
        HandicraftOrder handicraftOrder = handicraftOrderRepository.findById(orderId, HandicraftOrderRepository.COMPLEX_FETCHER_FOR_FRONT)
                .orElseThrow(() -> new BusinessException(ResultCode.NotFindError, "订单不存在"));
        checkStatus(handicraftOrder, DictConstants.BookingOrderStatus.TO_BE_CONFIRMED);
        String refundOrderId = IdUtil.fastSimpleUUID();
        RefundRecord refundRecord = RefundRecordDraft.$.produce(draft -> {
            draft.setId(refundOrderId);
            WxPayRefundV3Request wxPayRefundV3Request = new WxPayRefundV3Request().setOutTradeNo(
                            orderId)
                    .setOutRefundNo(refundOrderId)
                    .setNotifyUrl(wxPayPropertiesExtension.getNotifyUrl() + "/front/handicraft-order/notify/refund/wechat")
                    .setReason("退款")
                    .setAmount(new WxPayRefundV3Request
                            .Amount()
                            // 计算价格
                            .setRefund(handicraftOrder.baseOrder().payment().payAmount()
                                    .multiply(BigDecimal.valueOf(100)).intValue())
                            .setTotal(handicraftOrder.baseOrder().payment().payAmount()
                                    .multiply(BigDecimal.valueOf(100)).intValue())
                            .setCurrency("CNY"));
            WxPayRefundV3Result wxPayRefundV3Result = wxPayService.refundV3(wxPayRefundV3Request);
            draft.setRefundId(wxPayRefundV3Result.getRefundId())
                    .setStatus(DictConstants.RefundStatus.REFUNDING)
                    .setOrderId(orderId)
                    .setAmount(handicraftOrder.baseOrder().payment().payAmount())
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
        HandicraftOrder handicraftOrder = handicraftOrderRepository.findById(refundRecord.orderId(), HandicraftOrderFetcher.$.creator())
                .orElseThrow(() -> new BusinessException(ResultCode.NotFindError, "手工预约订单不存在"));
        StpUtil.switchTo(handicraftOrder.creator().id());
        if (result.getRefundStatus().equals("SUCCESS")) {
            // 需要将订单回退到未支付状态再取消
            log.info("将订单状态回退到待支付");
            changeStatus(result.getOutTradeNo(), DictConstants.BookingOrderStatus.TO_BE_PAID);
            log.info("取消订单订单");
            unpaidCancelForUser(result.getOutTradeNo());
            log.info("将订单状态设置为已退款");
            changeStatus(result.getOutTradeNo(), DictConstants.BookingOrderStatus.REFUNDED);
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


    public void changeStatus(String id, DictConstants.BookingOrderStatus orderStatus) {
        var t = HandicraftOrderTable.$;
        handicraftOrderRepository.sql().createUpdate(t)
                .where(t.id().eq(id))
                .set(t.status(), orderStatus)
                .execute();
    }

    public void checkStatus(HandicraftOrder handicraftOrder, DictConstants.BookingOrderStatus... orderStatuses) {
        for (var status : orderStatuses) {
            if (handicraftOrder.status().equals(status)) {
                return;
            }
        }
        throw new BusinessException(ResultCode.ParamSetIllegal, "订单状态不正确");
    }

    public void checkOwner(HandicraftOrder handicraftOrder) {
        if (!handicraftOrder.creator().id().equals(StpUtil.getLoginIdAsString())) {
            if (StpUtil.hasRole("管理员")) {
                return;
            }
            throw new BusinessException(ResultCode.ParamSetIllegal, "非本人操作");
        }
    }
}