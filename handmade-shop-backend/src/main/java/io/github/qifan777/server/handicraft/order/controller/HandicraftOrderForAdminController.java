package io.github.qifan777.server.handicraft.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import io.github.qifan777.server.handicraft.order.entity.HandicraftOrder;
import io.github.qifan777.server.handicraft.order.entity.dto.HandicraftOrderInput;
import io.github.qifan777.server.handicraft.order.entity.dto.HandicraftOrderSpec;
import io.github.qifan777.server.handicraft.order.repository.HandicraftOrderRepository;
import io.github.qifan777.server.handicraft.order.service.HandicraftOrderService;
import io.github.qifan777.server.infrastructure.aop.NotRepeat;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
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
@RequestMapping("admin/handicraft-order")
@AllArgsConstructor
@DefaultFetcherOwner(HandicraftOrderRepository.class)
@SaCheckPermission("/handicraft-order")
@Transactional
@Slf4j
public class HandicraftOrderForAdminController {
    private final HandicraftOrderRepository handicraftOrderRepository;
    private final HandicraftOrderService handicraftOrderService;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") HandicraftOrder findById(@PathVariable String id) {
        return handicraftOrderRepository.findById(id, HandicraftOrderRepository.COMPLEX_FETCHER_FOR_ADMIN).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") HandicraftOrder> query(@RequestBody QueryRequest<HandicraftOrderSpec> queryRequest) {
        return handicraftOrderRepository.findPage(queryRequest, HandicraftOrderRepository.COMPLEX_FETCHER_FOR_ADMIN);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated HandicraftOrderInput handicraftOrderInput) {
        return handicraftOrderRepository.save(handicraftOrderInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        handicraftOrderRepository.deleteAllById(ids);
        return true;
    }

    @PostMapping("{id}/paid/cancel/admin")
    @NotRepeat
    public String paidCancelForAdmin(@PathVariable String id) {
        return handicraftOrderService.paidCancelForAdmin(id);
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void unpaidCancelAutoCancel() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        StpUtil.switchTo("1");
        handicraftOrderRepository.findUnpaidOrder().forEach(handicraftOrder -> {
            try {
                handicraftOrderService.unpaidCancelForUser(handicraftOrder.id());
            } catch (Exception e) {
                log.error("自动取消订单失败", e);
            }
        });
    }
}