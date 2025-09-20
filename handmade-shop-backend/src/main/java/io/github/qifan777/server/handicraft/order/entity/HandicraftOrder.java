package io.github.qifan777.server.handicraft.order.entity;

import io.github.qifan777.server.dict.model.DictConstants;
import io.github.qifan777.server.handicraft.item.entity.HandicraftOrderItem;
import io.github.qifan777.server.handicraft.root.entity.Handicraft;
import io.github.qifan777.server.infrastructure.jimmer.BaseEntity;
import io.github.qifan777.server.order.entity.BaseOrder;
import io.qifan.infrastructure.generator.core.GenAssociationField;
import io.qifan.infrastructure.generator.core.GenDictField;
import io.qifan.infrastructure.generator.core.GenEntity;
import org.babyfish.jimmer.sql.*;

import java.util.List;


/**
 * 手工预约订单
 */
@GenEntity
@Entity
public interface HandicraftOrder extends BaseEntity {

    /**
     * 订单状态
     */
    @GenDictField(label = "订单状态", order = 1, dictEnName = DictConstants.BOOKING_ORDER_STATUS)
    DictConstants.BookingOrderStatus status();

    /**
     * 基础订单
     */
    @GenAssociationField(label = "基础订单", order = 1, prop = "baseOrderId")
    @OneToOne
    BaseOrder baseOrder();

    @IdView
    String baseOrderId();


    @ManyToOne
    Handicraft handicraft();

    @IdView
    String handicraftId();

    @OneToMany(mappedBy = "handicraftOrder")
    List<HandicraftOrderItem> items();
}
