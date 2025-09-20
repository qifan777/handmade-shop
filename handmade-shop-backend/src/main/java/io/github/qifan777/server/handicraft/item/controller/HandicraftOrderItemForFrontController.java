package io.github.qifan777.server.handicraft.item.controller;

import cn.dev33.satoken.stp.StpUtil;
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
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("front/handicraft-order-item")
@AllArgsConstructor
@DefaultFetcherOwner(HandicraftOrderItemRepository.class)
@Transactional
public class HandicraftOrderItemForFrontController {
    private final HandicraftOrderItemRepository handicraftOrderItemRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") HandicraftOrderItem findById(@PathVariable String id) {
        return handicraftOrderItemRepository.findById(id, HandicraftOrderItemRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") HandicraftOrderItem> query(@RequestBody QueryRequest<HandicraftOrderItemSpec> queryRequest) {
        queryRequest.getQuery().setCreatorId(StpUtil.getLoginIdAsString());
        return handicraftOrderItemRepository.findPage(queryRequest, HandicraftOrderItemRepository.COMPLEX_FETCHER_FOR_FRONT);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated HandicraftOrderItemInput handicraftOrderItemInput) {
        if (StringUtils.hasText(handicraftOrderItemInput.getId())) {
            HandicraftOrderItem handicraftOrderItem = handicraftOrderItemRepository.findById(handicraftOrderItemInput.getId(), HandicraftOrderItemRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
            if (!handicraftOrderItem.creator().id().equals(StpUtil.getLoginIdAsString())) {
                throw new BusinessException("只能修改自己的数据");
            }
        }
        return handicraftOrderItemRepository.save(handicraftOrderItemInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        handicraftOrderItemRepository.findByIds(ids, HandicraftOrderItemRepository.COMPLEX_FETCHER_FOR_FRONT).forEach(handicraftOrderItem -> {
            if (!handicraftOrderItem.creator().id().equals(StpUtil.getLoginIdAsString())) {
                throw new BusinessException("只能删除自己的数据");
            }
        });
        handicraftOrderItemRepository.deleteAllById(ids);
        return true;
    }
}
