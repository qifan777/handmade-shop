package io.github.qifan777.server.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.order.entity.BaseOrder;
import io.github.qifan777.server.order.entity.dto.BaseOrderInput;
import io.github.qifan777.server.order.entity.dto.BaseOrderSpec;
import io.github.qifan777.server.order.repository.BaseOrderRepository;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/base-order")
@AllArgsConstructor
@DefaultFetcherOwner(BaseOrderRepository.class)
@SaCheckPermission("/base-order")
@Transactional
public class BaseOrderForAdminController {
    private final BaseOrderRepository baseOrderRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") BaseOrder findById(@PathVariable String id) {
        return baseOrderRepository.findById(id, BaseOrderRepository.COMPLEX_FETCHER_FOR_ADMIN).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") BaseOrder> query(@RequestBody QueryRequest<BaseOrderSpec> queryRequest) {
        return baseOrderRepository.findPage(queryRequest, BaseOrderRepository.COMPLEX_FETCHER_FOR_ADMIN);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated BaseOrderInput baseOrderInput) {
        return baseOrderRepository.save(baseOrderInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        baseOrderRepository.deleteAllById(ids);
        return true;
    }
}