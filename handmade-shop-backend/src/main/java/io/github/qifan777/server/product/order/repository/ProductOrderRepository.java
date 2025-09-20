package io.github.qifan777.server.product.order.repository;

import io.github.qifan777.server.dict.model.DictConstants;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.order.entity.BaseOrderFetcher;
import io.github.qifan777.server.payment.entity.PaymentFetcher;
import io.github.qifan777.server.product.item.entity.ProductOrderItemFetcher;
import io.github.qifan777.server.product.order.entity.ProductOrder;
import io.github.qifan777.server.product.order.entity.ProductOrderFetcher;
import io.github.qifan777.server.product.order.entity.ProductOrderTable;
import io.github.qifan777.server.product.order.entity.dto.ProductOrderSpec;
import io.github.qifan777.server.product.root.entity.ProductFetcher;
import io.github.qifan777.server.user.root.entity.UserFetcher;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductOrderRepository extends JRepository<ProductOrder, String> {
    ProductOrderTable t = ProductOrderTable.$;
    ProductOrderFetcher COMPLEX_FETCHER_FOR_ADMIN = ProductOrderFetcher.$.allScalarFields()
            .items(ProductOrderItemFetcher.$
                    .product(ProductFetcher.$
                            .allScalarFields())
                    .productCount())
            .baseOrder(BaseOrderFetcher.$
                    .allScalarFields()
                    .creator()
                    .address()
                    .payment(PaymentFetcher.$.allScalarFields()))
            .creator(UserFetcher.$.phone().nickname())
            .editor(UserFetcher.$.phone().nickname());
    ProductOrderFetcher COMPLEX_FETCHER_FOR_FRONT = ProductOrderFetcher.$.allScalarFields()
            .items(ProductOrderItemFetcher.$
                    .product(ProductFetcher.$
                            .allScalarFields())
                    .productCount())
            .baseOrder(BaseOrderFetcher.$
                    .allScalarFields()
                    .creator()
                    .address()
                    .payment(PaymentFetcher.$.allScalarFields()))
            .creator(true);

    default Page<ProductOrder> findPage(QueryRequest<ProductOrderSpec> queryRequest,
                                        Fetcher<ProductOrder> fetcher) {
        ProductOrderSpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(t)
                .where(query)
                .orderBy(SpringOrders.toOrders(t, pageable.getSort()))
                .select(t.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }

    default List<ProductOrder> findUnpaidOrder() {
        return sql().createQuery(t)
                .where(t.status().eq(DictConstants.ProductOrderStatus.TO_BE_PAID))
                .where(t.createdTime().le(LocalDateTime.now().minusMinutes(5)))
                .select(t.fetch(ProductOrderFetcher.$.allScalarFields().creator(true)))
                .execute();
    }
}