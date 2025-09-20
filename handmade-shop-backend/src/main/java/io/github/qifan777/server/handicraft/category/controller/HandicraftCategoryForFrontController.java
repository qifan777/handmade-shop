package io.github.qifan777.server.handicraft.category.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.github.qifan777.server.handicraft.category.entity.HandicraftCategory;
import io.github.qifan777.server.handicraft.category.entity.dto.HandicraftCategoryInput;
import io.github.qifan777.server.handicraft.category.entity.dto.HandicraftCategorySpec;
import io.github.qifan777.server.handicraft.category.repository.HandicraftCategoryRepository;
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
@RequestMapping("front/handicraft-category")
@AllArgsConstructor
@DefaultFetcherOwner(HandicraftCategoryRepository.class)
@Transactional
public class HandicraftCategoryForFrontController {
    private final HandicraftCategoryRepository handicraftCategoryRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") HandicraftCategory findById(@PathVariable String id) {
        return handicraftCategoryRepository.findById(id, HandicraftCategoryRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") HandicraftCategory> query(@RequestBody QueryRequest<HandicraftCategorySpec> queryRequest) {
        return handicraftCategoryRepository.findPage(queryRequest, HandicraftCategoryRepository.COMPLEX_FETCHER_FOR_FRONT);
    }
}
