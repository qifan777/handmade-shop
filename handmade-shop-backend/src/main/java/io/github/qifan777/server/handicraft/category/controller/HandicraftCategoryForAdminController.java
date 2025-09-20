package io.github.qifan777.server.handicraft.category.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/handicraft-category")
@AllArgsConstructor
@DefaultFetcherOwner(HandicraftCategoryRepository.class)
@SaCheckPermission("/handicraft-category")
@Transactional
public class HandicraftCategoryForAdminController {
    private final HandicraftCategoryRepository handicraftCategoryRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") HandicraftCategory findById(@PathVariable String id) {
        return handicraftCategoryRepository.findById(id, HandicraftCategoryRepository.COMPLEX_FETCHER_FOR_ADMIN).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") HandicraftCategory> query(@RequestBody QueryRequest<HandicraftCategorySpec> queryRequest) {
        return handicraftCategoryRepository.findPage(queryRequest, HandicraftCategoryRepository.COMPLEX_FETCHER_FOR_ADMIN);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated HandicraftCategoryInput handicraftCategoryInput) {
        return handicraftCategoryRepository.save(handicraftCategoryInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        handicraftCategoryRepository.deleteAllById(ids);
        return true;
    }
}