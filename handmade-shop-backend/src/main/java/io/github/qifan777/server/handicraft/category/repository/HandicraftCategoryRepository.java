package io.github.qifan777.server.handicraft.category.repository;

import io.github.qifan777.server.handicraft.category.entity.HandicraftCategory;
import io.github.qifan777.server.handicraft.category.entity.HandicraftCategoryFetcher;
import io.github.qifan777.server.handicraft.category.entity.HandicraftCategoryTable;
import io.github.qifan777.server.handicraft.category.entity.dto.HandicraftCategorySpec;
import io.github.qifan777.server.handicraft.root.entity.HandicraftFetcher;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.user.root.entity.UserFetcher;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HandicraftCategoryRepository extends JRepository<HandicraftCategory, String> {
    HandicraftCategoryTable t = HandicraftCategoryTable.$;
    HandicraftCategoryFetcher COMPLEX_FETCHER_FOR_ADMIN = HandicraftCategoryFetcher.$.allScalarFields()
            .handicrafts(HandicraftFetcher.$.allScalarFields())
            .creator(UserFetcher.$.phone().nickname())
            .editor(UserFetcher.$.phone().nickname());
    HandicraftCategoryFetcher COMPLEX_FETCHER_FOR_FRONT = HandicraftCategoryFetcher.$.allScalarFields()
            .creator(true);

    default Page<HandicraftCategory> findPage(QueryRequest<HandicraftCategorySpec> queryRequest,
                                              Fetcher<HandicraftCategory> fetcher) {
        HandicraftCategorySpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(t)
                .where(query)
                .orderBy(SpringOrders.toOrders(t, pageable.getSort()))
                .select(t.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }
}