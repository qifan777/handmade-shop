package io.github.qifan777.server.product.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import io.github.qifan777.server.infrastructure.aop.NotRepeat;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.product.order.entity.ProductOrder;
import io.github.qifan777.server.product.order.entity.dto.ProductOrderInput;
import io.github.qifan777.server.product.order.entity.dto.ProductOrderSpec;
import io.github.qifan777.server.product.order.repository.ProductOrderRepository;
import io.github.qifan777.server.product.order.service.ProductOrderService;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@RestController
@RequestMapping("admin/product-order")
@AllArgsConstructor
@DefaultFetcherOwner(ProductOrderRepository.class)
@SaCheckPermission("/product-order")
@Transactional
@Slf4j
public class ProductOrderForAdminController {
    private final ProductOrderRepository productOrderRepository;
    private final ProductOrderService productOrderService;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") ProductOrder findById(@PathVariable String id) {
        return productOrderRepository.findById(id, ProductOrderRepository.COMPLEX_FETCHER_FOR_ADMIN).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") ProductOrder> query(@RequestBody QueryRequest<ProductOrderSpec> queryRequest) {
        return productOrderRepository.findPage(queryRequest, ProductOrderRepository.COMPLEX_FETCHER_FOR_ADMIN);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated ProductOrderInput productOrderInput) {
        return productOrderRepository.save(productOrderInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        productOrderRepository.deleteAllById(ids);
        return true;
    }

    @PostMapping("{id}/paid/cancel")
    @NotRepeat
    public String paidCancelForAdmin(@PathVariable String id) {
        return productOrderService.paidCancelForAdmin(id);
    }

    @PostMapping("{id}/deliver")
    @NotRepeat
    public String deliver(@PathVariable String id, @RequestParam String trackingNumber) {
        return productOrderService.deliver(id, trackingNumber);
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void unpaidCancelAutoCancel() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        productOrderRepository.findUnpaidOrder().forEach(handicraftOrder -> {
            try {
                StpUtil.switchTo(handicraftOrder.creator().id());
                productOrderService.unpaidCancelForUser(handicraftOrder.id());
            } catch (Exception e) {
                log.error("自动取消订单失败", e);
            }
        });
    }
}