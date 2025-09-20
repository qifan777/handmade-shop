package io.github.qifan777.server.order.repository;

import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.order.entity.BaseOrder;
import io.github.qifan777.server.order.entity.BaseOrderFetcher;
import io.github.qifan777.server.order.entity.BaseOrderTable;
import io.github.qifan777.server.order.entity.dto.BaseOrderSpec;
import io.github.qifan777.server.user.root.entity.UserFetcher;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseOrderRepository extends JRepository<BaseOrder, String> {
    BaseOrderTable t = BaseOrderTable.$;
    BaseOrderFetcher COMPLEX_FETCHER_FOR_ADMIN = BaseOrderFetcher.$.allScalarFields()
            .creator(UserFetcher.$.phone().nickname())
            .editor(UserFetcher.$.phone().nickname());
    BaseOrderFetcher COMPLEX_FETCHER_FOR_FRONT = BaseOrderFetcher.$.allScalarFields()
            .creator(true);

    default Page<BaseOrder> findPage(QueryRequest<BaseOrderSpec> queryRequest,
                                     Fetcher<BaseOrder> fetcher) {
        BaseOrderSpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(t)
                .where(query)
                .orderBy(SpringOrders.toOrders(t, pageable.getSort()))
                .select(t.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }
}