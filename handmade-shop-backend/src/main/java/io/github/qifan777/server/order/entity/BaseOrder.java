package io.github.qifan777.server.order.entity;

import io.github.qifan777.server.address.entity.dto.AddressView;
import io.github.qifan777.server.coupon.user.entity.CouponUserRel;
import io.github.qifan777.server.dict.model.DictConstants;
import io.github.qifan777.server.infrastructure.jimmer.BaseEntity;
import io.github.qifan777.server.payment.entity.Payment;
import io.qifan.infrastructure.generator.core.GenDictField;
import io.qifan.infrastructure.generator.core.GenEntity;
import io.qifan.infrastructure.generator.core.GenTextField;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.sql.*;


/**
 * 基础订单
 */
@GenEntity
@Entity
public interface BaseOrder extends BaseEntity {

    /**
     * 支付订单id
     */
    @OneToOne
    Payment payment();

    @IdView
    String paymentId();

    /**
     * 地址详情
     */
    @Serialized
    @Null
    AddressView address();

    /**
     * 备注
     */
    @GenTextField(label = "备注")
    @Null
    String remark();

    /**
     * 物流单号
     */
    @GenTextField(label = "物流单号")
    @Null
    String trackingNumber();

    /**
     * 用户优惠券id
     */
    @Null
    @ManyToOne
    CouponUserRel couponUser();

    @IdView
    @Null
    String couponUserId();

    /**
     * 订单类型
     */
    @GenDictField(label = "订单类型", dictEnName = DictConstants.ORDER_TYPE)
    DictConstants.OrderType type();
}

