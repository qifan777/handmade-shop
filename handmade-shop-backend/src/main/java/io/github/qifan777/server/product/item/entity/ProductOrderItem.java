package io.github.qifan777.server.product.item.entity;

import io.github.qifan777.server.infrastructure.jimmer.BaseEntity;
import io.github.qifan777.server.product.order.entity.ProductOrder;
import io.github.qifan777.server.product.root.entity.Product;
import io.qifan.infrastructure.generator.core.GenAssociationField;
import io.qifan.infrastructure.generator.core.GenEntity;
import io.qifan.infrastructure.generator.core.GenNumberField;
import org.babyfish.jimmer.sql.*;

/**
 * 商品订单项
 */
@GenEntity
@Entity
public interface ProductOrderItem extends BaseEntity {

    /**
     * 商品订单
     */
    @GenAssociationField(label = "商品订单", order = 0, prop = "productOrderId")
    @ManyToOne
    @Key
    ProductOrder productOrder();

    @IdView
    String productOrderId();

    /**
     * 商品
     */
    @GenAssociationField(label = "商品信息", order = 1, prop = "productId")
    @ManyToOne
    @Key
    Product product();

    @IdView
    String productId();

    /**
     * 数量
     */
    @GenNumberField(label = "数量", order = 2)
    @Column(name = "count")
    int productCount();
}

