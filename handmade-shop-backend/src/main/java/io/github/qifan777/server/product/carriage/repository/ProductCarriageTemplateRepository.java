package io.github.qifan777.server.product.carriage.repository;

import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.product.carriage.entity.ProductCarriageTemplate;
import io.github.qifan777.server.product.carriage.entity.ProductCarriageTemplateFetcher;
import io.github.qifan777.server.product.carriage.entity.ProductCarriageTemplateTable;
import io.github.qifan777.server.product.carriage.entity.dto.ProductCarriageTemplateSpec;
import io.github.qifan777.server.user.root.entity.UserFetcher;
import io.qifan.infrastructure.common.exception.BusinessException;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCarriageTemplateRepository extends JRepository<ProductCarriageTemplate, String> {
    ProductCarriageTemplateTable t = ProductCarriageTemplateTable.$;
    ProductCarriageTemplateFetcher COMPLEX_FETCHER_FOR_ADMIN = ProductCarriageTemplateFetcher.$.allScalarFields()
            .creator(UserFetcher.$.phone().nickname())
            .editor(UserFetcher.$.phone().nickname());
    ProductCarriageTemplateFetcher COMPLEX_FETCHER_FOR_FRONT = ProductCarriageTemplateFetcher.$.allScalarFields()
            .creator(true);

    default Page<ProductCarriageTemplate> findPage(QueryRequest<ProductCarriageTemplateSpec> queryRequest,
                                                   Fetcher<ProductCarriageTemplate> fetcher) {
        ProductCarriageTemplateSpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(t)
                .where(query)
                .orderBy(SpringOrders.toOrders(t, pageable.getSort()))
                .select(t.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }

    default void updateAllInvalid() {
        sql().createUpdate(t)
                .set(t.valid(), false)
                .where(t.valid().eq(true))
                .execute();
    }

    default ProductCarriageTemplate findValid() {
        return sql().createQuery(t)
                .where(t.valid().eq(true))
                .select(t)
                .fetchOptional()
                .orElseThrow(() -> new BusinessException("未找到可用的运费模板"));
    }
}