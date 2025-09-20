package io.github.qifan777.server.order.controller;

import cn.dev33.satoken.stp.StpUtil;
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
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("front/base-order")
@AllArgsConstructor
@DefaultFetcherOwner(BaseOrderRepository.class)
@Transactional
public class BaseOrderForFrontController {
    private final BaseOrderRepository baseOrderRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") BaseOrder findById(@PathVariable String id) {
        return baseOrderRepository.findById(id, BaseOrderRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") BaseOrder> query(@RequestBody QueryRequest<BaseOrderSpec> queryRequest) {
        queryRequest.getQuery().setCreatorId(StpUtil.getLoginIdAsString());
        return baseOrderRepository.findPage(queryRequest, BaseOrderRepository.COMPLEX_FETCHER_FOR_FRONT);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated BaseOrderInput baseOrderInput) {
        if (StringUtils.hasText(baseOrderInput.getId())) {
            BaseOrder baseOrder = baseOrderRepository.findById(baseOrderInput.getId(), BaseOrderRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
            if (!baseOrder.creator().id().equals(StpUtil.getLoginIdAsString())) {
                throw new BusinessException("只能修改自己的数据");
            }
        }
        return baseOrderRepository.save(baseOrderInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        baseOrderRepository.findByIds(ids, BaseOrderRepository.COMPLEX_FETCHER_FOR_FRONT).forEach(baseOrder -> {
            if (!baseOrder.creator().id().equals(StpUtil.getLoginIdAsString())) {
                throw new BusinessException("只能删除自己的数据");
            }
        });
        baseOrderRepository.deleteAllById(ids);
        return true;
    }
}
