package io.github.qifan777.server.handicraft.item.repository;

import io.github.qifan777.server.handicraft.item.entity.HandicraftOrderItem;
import io.github.qifan777.server.handicraft.item.entity.HandicraftOrderItemFetcher;
import io.github.qifan777.server.handicraft.item.entity.HandicraftOrderItemTable;
import io.github.qifan777.server.handicraft.item.entity.dto.HandicraftOrderItemSpec;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.user.root.entity.UserFetcher;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HandicraftOrderItemRepository extends JRepository<HandicraftOrderItem, String> {
    HandicraftOrderItemTable t = HandicraftOrderItemTable.$;
    HandicraftOrderItemFetcher COMPLEX_FETCHER_FOR_ADMIN = HandicraftOrderItemFetcher.$.allScalarFields()
            .creator(UserFetcher.$.phone().nickname())
            .editor(UserFetcher.$.phone().nickname());
    HandicraftOrderItemFetcher COMPLEX_FETCHER_FOR_FRONT = HandicraftOrderItemFetcher.$.allScalarFields()
            .creator(true);

    default Page<HandicraftOrderItem> findPage(QueryRequest<HandicraftOrderItemSpec> queryRequest,
                                               Fetcher<HandicraftOrderItem> fetcher) {
        HandicraftOrderItemSpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(t)
                .where(query)
                .orderBy(SpringOrders.toOrders(t, pageable.getSort()))
                .select(t.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }
}