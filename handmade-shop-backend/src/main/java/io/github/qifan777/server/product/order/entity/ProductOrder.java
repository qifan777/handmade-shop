package io.github.qifan777.server.product.order.entity;

import io.github.qifan777.server.dict.model.DictConstants;
import io.github.qifan777.server.infrastructure.jimmer.BaseEntity;
import io.github.qifan777.server.order.entity.BaseOrder;
import io.github.qifan777.server.product.item.entity.ProductOrderItem;
import io.qifan.infrastructure.generator.core.GenDictField;
import io.qifan.infrastructure.generator.core.GenEntity;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.OneToMany;

import java.util.List;

/**
 * 商品订单
 */
@GenEntity
@Entity
public interface ProductOrder extends BaseEntity {

    /**
     * 订单状态
     */
    @GenDictField(label = "订单状态", order = 1, dictEnName = DictConstants.PRODUCT_ORDER_STATUS)
    DictConstants.ProductOrderStatus status();

    /**
     * 基础订单
     */
    @ManyToOne
    BaseOrder baseOrder();

    @OneToMany(mappedBy = "productOrder")
    List<ProductOrderItem> items();
}

