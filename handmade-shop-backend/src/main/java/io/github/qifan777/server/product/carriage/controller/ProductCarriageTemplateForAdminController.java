package io.github.qifan777.server.product.carriage.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.product.carriage.entity.ProductCarriageTemplate;
import io.github.qifan777.server.product.carriage.entity.dto.ProductCarriageTemplateInput;
import io.github.qifan777.server.product.carriage.entity.dto.ProductCarriageTemplateSpec;
import io.github.qifan777.server.product.carriage.repository.ProductCarriageTemplateRepository;
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
@RequestMapping("admin/product-carriage-template")
@AllArgsConstructor
@DefaultFetcherOwner(ProductCarriageTemplateRepository.class)
@SaCheckPermission("/product-carriage-template")
@Transactional
public class ProductCarriageTemplateForAdminController {
    private final ProductCarriageTemplateRepository productCarriageTemplateRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") ProductCarriageTemplate findById(@PathVariable String id) {
        return productCarriageTemplateRepository.findById(id, ProductCarriageTemplateRepository.COMPLEX_FETCHER_FOR_ADMIN).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") ProductCarriageTemplate> query(@RequestBody QueryRequest<ProductCarriageTemplateSpec> queryRequest) {
        return productCarriageTemplateRepository.findPage(queryRequest, ProductCarriageTemplateRepository.COMPLEX_FETCHER_FOR_ADMIN);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated ProductCarriageTemplateInput productCarriageTemplateInput) {
        if (productCarriageTemplateInput.isValid()){
            productCarriageTemplateRepository.updateAllInvalid();
        }
        return productCarriageTemplateRepository.save(productCarriageTemplateInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        productCarriageTemplateRepository.deleteAllById(ids);
        return true;
    }
}