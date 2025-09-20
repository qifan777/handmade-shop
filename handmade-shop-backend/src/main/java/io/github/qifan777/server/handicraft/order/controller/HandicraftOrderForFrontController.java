package io.github.qifan777.server.handicraft.order.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import io.github.qifan777.server.handicraft.order.entity.HandicraftOrder;
import io.github.qifan777.server.handicraft.order.entity.dto.HandicraftOrderInput;
import io.github.qifan777.server.handicraft.order.entity.dto.HandicraftOrderSpec;
import io.github.qifan777.server.handicraft.order.repository.HandicraftOrderRepository;
import io.github.qifan777.server.handicraft.order.service.HandicraftOrderService;
import io.github.qifan777.server.infrastructure.aop.NotRepeat;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.payment.entity.dto.PaymentPriceView;
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
@RequestMapping("front/handicraft-order")
@AllArgsConstructor
@DefaultFetcherOwner(HandicraftOrderRepository.class)
@Transactional
public class HandicraftOrderForFrontController {
    private final HandicraftOrderRepository handicraftOrderRepository;
    private final HandicraftOrderService handicraftOrderService;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") HandicraftOrder findById(@PathVariable String id) {
        return handicraftOrderRepository.findById(id, HandicraftOrderRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") HandicraftOrder> query(@RequestBody QueryRequest<HandicraftOrderSpec> queryRequest) {
        queryRequest.getQuery().setCreatorId(StpUtil.getLoginIdAsString());
        return handicraftOrderRepository.findPage(queryRequest, HandicraftOrderRepository.COMPLEX_FETCHER_FOR_FRONT);
    }

    @PostMapping("create")
    @NotRepeat
    public String create(@RequestBody @Validated HandicraftOrderInput orderInput) {
        return handicraftOrderService.create(orderInput);
    }

    @PostMapping("{id}/prepay/wechat")
    @NotRepeat
    public WxPayUnifiedOrderV3Result.JsapiResult prepayWechat(@PathVariable String id) {
        return handicraftOrderService.prepay(id);
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
        return handicraftOrderService.paymentNotifyWechat(body, signatureHeader);
    }

    @PostMapping("{id}/unpaid/cancel/user")
    @NotRepeat
    public String unpaidCancelForUser(@PathVariable String id) {
        return handicraftOrderService.unpaidCancelForUser(id);
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
        return handicraftOrderService.refundNotifyWeChat(body, signatureHeader);
    }

    @PostMapping("calculate")
    public HandicraftOrderService.CalculateView calculate(@Validated @RequestBody HandicraftOrderInput handicraftOrderInput) {
        return handicraftOrderService.calculate(handicraftOrderInput);
    }


}
