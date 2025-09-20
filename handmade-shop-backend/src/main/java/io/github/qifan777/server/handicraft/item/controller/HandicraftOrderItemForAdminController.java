package io.github.qifan777.server.handicraft.item.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.qifan777.server.handicraft.item.entity.HandicraftOrderItem;
import io.github.qifan777.server.handicraft.item.entity.dto.HandicraftOrderItemInput;
import io.github.qifan777.server.handicraft.item.entity.dto.HandicraftOrderItemSpec;
import io.github.qifan777.server.handicraft.item.repository.HandicraftOrderItemRepository;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
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
@RequestMapping("admin/handicraft-order-item")
@AllArgsConstructor
@DefaultFetcherOwner(HandicraftOrderItemRepository.class)
@SaCheckPermission("/handicraft-order-item")
@Transactional
public class HandicraftOrderItemForAdminController {
    private final HandicraftOrderItemRepository handicraftOrderItemRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") HandicraftOrderItem findById(@PathVariable String id) {
        return handicraftOrderItemRepository.findById(id, HandicraftOrderItemRepository.COMPLEX_FETCHER_FOR_ADMIN).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") HandicraftOrderItem> query(@RequestBody QueryRequest<HandicraftOrderItemSpec> queryRequest) {
        return handicraftOrderItemRepository.findPage(queryRequest, HandicraftOrderItemRepository.COMPLEX_FETCHER_FOR_ADMIN);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated HandicraftOrderItemInput handicraftOrderItemInput) {
        return handicraftOrderItemRepository.save(handicraftOrderItemInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        handicraftOrderItemRepository.deleteAllById(ids);
        return true;
    }
}