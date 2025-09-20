package io.github.qifan777.server.product.carriage.controller;

import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.product.carriage.entity.ProductCarriageTemplate;
import io.github.qifan777.server.product.carriage.entity.dto.ProductCarriageTemplateSpec;
import io.github.qifan777.server.product.carriage.repository.ProductCarriageTemplateRepository;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("front/product-carriage-template")
@AllArgsConstructor
@DefaultFetcherOwner(ProductCarriageTemplateRepository.class)
@Transactional
public class ProductCarriageTemplateForFrontController {
    private final ProductCarriageTemplateRepository productCarriageTemplateRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") ProductCarriageTemplate findById(@PathVariable String id) {
        return productCarriageTemplateRepository.findById(id, ProductCarriageTemplateRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") ProductCarriageTemplate> query(@RequestBody QueryRequest<ProductCarriageTemplateSpec> queryRequest) {
        return productCarriageTemplateRepository.findPage(queryRequest, ProductCarriageTemplateRepository.COMPLEX_FETCHER_FOR_FRONT);
    }

}
