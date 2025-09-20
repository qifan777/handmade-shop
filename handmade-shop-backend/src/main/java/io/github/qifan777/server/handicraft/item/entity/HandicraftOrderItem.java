package io.github.qifan777.server.handicraft.item.entity;

import io.github.qifan777.server.handicraft.booking.entity.HandicraftBooking;
import io.github.qifan777.server.handicraft.booking.entity.dto.HandicraftBookingView;
import io.github.qifan777.server.handicraft.order.entity.HandicraftOrder;
import io.github.qifan777.server.infrastructure.jimmer.BaseEntity;
import io.qifan.infrastructure.generator.core.GenAssociationField;
import io.qifan.infrastructure.generator.core.GenEntity;
import org.babyfish.jimmer.sql.*;

/**
 * 手工预约订单项
 */
@GenEntity
@Entity
public interface HandicraftOrderItem extends BaseEntity {

    /**
     * 预约订单
     */
    @GenAssociationField(label = "预约订单", order = 0, prop = "handicraftOrderId")
    @ManyToOne
    @Key
    HandicraftOrder handicraftOrder();

    @IdView
    String handicraftOrderId();

    /**
     * 预约
     */
    @GenAssociationField(label = "预约", order = 1, prop = "handicraftBookingId")
    @ManyToOne
    @Key
    HandicraftBooking handicraftBooking();

    @IdView
    String handicraftBookingId();

    @Serialized
    HandicraftBookingView handicraftBookingView();
}
