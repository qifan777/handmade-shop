package io.github.qifan777.server.product.order.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import io.github.qifan777.server.infrastructure.aop.NotRepeat;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.payment.entity.dto.PaymentPriceView;
import io.github.qifan777.server.product.order.entity.ProductOrder;
import io.github.qifan777.server.product.order.entity.dto.ProductOrderInput;
import io.github.qifan777.server.product.order.entity.dto.ProductOrderSpec;
import io.github.qifan777.server.product.order.repository.ProductOrderRepository;
import io.github.qifan777.server.product.order.service.ProductOrderService;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.babyfish.jimmer.client.ApiIgnore;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("front/product-order")
@AllArgsConstructor
@DefaultFetcherOwner(ProductOrderRepository.class)
@Transactional
public class ProductOrderForFrontController {
    private final ProductOrderRepository productOrderRepository;
    private final ProductOrderService productOrderService;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") ProductOrder findById(@PathVariable String id) {
        return productOrderRepository.findById(id, ProductOrderRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") ProductOrder> query(@RequestBody QueryRequest<ProductOrderSpec> queryRequest) {
        queryRequest.getQuery().setCreatorId(StpUtil.getLoginIdAsString());
        return productOrderRepository.findPage(queryRequest, ProductOrderRepository.COMPLEX_FETCHER_FOR_FRONT);
    }


    @PostMapping("create")
    @NotRepeat
    public String create(@RequestBody @Validated ProductOrderInput productOrder) {
        return productOrderService.create(productOrder);
    }

    @PostMapping("{id}/prepay/wechat")
    @NotRepeat
    public WxPayUnifiedOrderV3Result.JsapiResult prepayWechat(@PathVariable String id) {
        return productOrderService.prepay(id);
    }

    @PostMapping("notify/pay/wechat")
    @ApiIgnore
    public String paymentNotifyWechat(@RequestBody String body,
                                      @RequestHeader(value = "Wechatpay-Timestamp") String timestamp,
                                      @RequestHeader(value = "Wechatpay-Nonce") String nonce,
                                      @RequestHeader(value = "Wechatpay-Signature") String signature,
                                      @RequestHeader(value = "Wechatpay-Serial") String serial) {
        SignatureHeader signatureHeader = SignatureHeader.builder().signature(signature)
                .serial(serial)
                .nonce(nonce)
                .timeStamp(timestamp).build();
        return productOrderService.paymentNotifyWechat(body, signatureHeader);
    }

    @PostMapping("{id}/unpaid/cancel/user")
    @NotRepeat
    public String unpaidCancelForUser(@PathVariable String id) {
        return productOrderService.unpaidCancelForUser(id);
    }


    @PostMapping("notify/refund/wechat")
    @ApiIgnore
    public String refundNotifyWeChat(@RequestBody String body,
                                     @RequestHeader(value = "Wechatpay-Timestamp") String timestamp,
                                     @RequestHeader(value = "Wechatpay-Nonce") String nonce,
                                     @RequestHeader(value = "Wechatpay-Signature") String signature,
                                     @RequestHeader(value = "Wechatpay-Serial") String serial) {
        SignatureHeader signatureHeader = SignatureHeader.builder().signature(signature)
                .serial(serial)
                .nonce(nonce)
                .timeStamp(timestamp).build();
        return productOrderService.refundNotifyWeChat(body, signatureHeader);
    }

    @PostMapping("calculate")
    public PaymentPriceView calculate(@Validated @RequestBody ProductOrderInput productOrderInput) {
        return productOrderService.calculate(productOrderInput);
    }
}
