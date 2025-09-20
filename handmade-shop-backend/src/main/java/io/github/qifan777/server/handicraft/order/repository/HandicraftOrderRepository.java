package io.github.qifan777.server.handicraft.order.repository;

import io.github.qifan777.server.Fetchers;
import io.github.qifan777.server.coupon.user.entity.CouponUserRelFetcher;
import io.github.qifan777.server.dict.model.DictConstants;
import io.github.qifan777.server.handicraft.order.entity.HandicraftOrder;
import io.github.qifan777.server.handicraft.order.entity.HandicraftOrderFetcher;
import io.github.qifan777.server.handicraft.order.entity.HandicraftOrderTable;
import io.github.qifan777.server.handicraft.order.entity.dto.HandicraftOrderSpec;
import io.github.qifan777.server.handicraft.root.entity.HandicraftFetcher;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.order.entity.BaseOrderFetcher;
import io.github.qifan777.server.payment.entity.PaymentFetcher;
import io.github.qifan777.server.user.root.entity.UserFetcher;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface HandicraftOrderRepository extends JRepository<HandicraftOrder, String> {
    HandicraftOrderTable t = HandicraftOrderTable.$;
    HandicraftOrderFetcher COMPLEX_FETCHER_FOR_ADMIN = HandicraftOrderFetcher.$.allScalarFields()
            .items(Fetchers.HANDICRAFT_ORDER_ITEM_FETCHER.allScalarFields())
            .handicraft(HandicraftFetcher.$.allScalarFields())
            .baseOrder(BaseOrderFetcher.$
                    .allScalarFields()
                    .creator()
                    .couponUser(CouponUserRelFetcher.$.allScalarFields())
                    .payment(PaymentFetcher.$.allScalarFields()))
            .creator(UserFetcher.$.phone().nickname())
            .editor(UserFetcher.$.phone().nickname());
    HandicraftOrderFetcher COMPLEX_FETCHER_FOR_FRONT = HandicraftOrderFetcher.$.allScalarFields()
            .items(Fetchers.HANDICRAFT_ORDER_ITEM_FETCHER.allScalarFields())
            .handicraft(HandicraftFetcher.$.allScalarFields())
            .baseOrder(BaseOrderFetcher.$
                    .allScalarFields()
                    .creator()
                    .couponUser(CouponUserRelFetcher.$.allScalarFields())
                    .payment(PaymentFetcher.$.allScalarFields()))
            .creator(true);

    default Page<HandicraftOrder> findPage(QueryRequest<HandicraftOrderSpec> queryRequest,
                                           Fetcher<HandicraftOrder> fetcher) {
        HandicraftOrderSpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(t)
                .where(query)
                .orderBy(SpringOrders.toOrders(t, pageable.getSort()))
                .select(t.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }

    default List<HandicraftOrder> findUnpaidOrder() {
        return sql().createQuery(t)
                .where(t.createdTime().le(LocalDateTime.now().minusMinutes(5)))
                .where(t.status().eq(DictConstants.BookingOrderStatus.TO_BE_PAID))
               .select(t)
                .execute();
    }
}