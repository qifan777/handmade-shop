package io.github.qifan777.server.handicraft.root.repository;

import io.github.qifan777.server.handicraft.category.entity.HandicraftCategoryFetcher;
import io.github.qifan777.server.handicraft.root.entity.Handicraft;
import io.github.qifan777.server.handicraft.root.entity.HandicraftFetcher;
import io.github.qifan777.server.handicraft.root.entity.HandicraftTable;
import io.github.qifan777.server.handicraft.root.entity.dto.HandicraftSpec;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.user.root.entity.UserFetcher;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HandicraftRepository extends JRepository<Handicraft, String> {
    HandicraftTable t = HandicraftTable.$;
    HandicraftFetcher COMPLEX_FETCHER_FOR_ADMIN = HandicraftFetcher.$.allScalarFields()
            .categoryId()
            .category(HandicraftCategoryFetcher.$.allScalarFields())
            .creator(UserFetcher.$.phone().nickname())
            .editor(UserFetcher.$.phone().nickname());
    HandicraftFetcher COMPLEX_FETCHER_FOR_FRONT = HandicraftFetcher.$.allScalarFields()
            .creator(true);

    default Page<Handicraft> findPage(QueryRequest<HandicraftSpec> queryRequest,
                                      Fetcher<Handicraft> fetcher) {
        HandicraftSpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(t)
                .where(query)
                .orderBy(SpringOrders.toOrders(t, pageable.getSort()))
                .select(t.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }
}