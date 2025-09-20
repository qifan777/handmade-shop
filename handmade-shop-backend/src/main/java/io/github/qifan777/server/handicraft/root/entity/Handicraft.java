package io.github.qifan777.server.handicraft.root.entity;

import io.github.qifan777.server.dict.model.DictConstants;
import io.github.qifan777.server.handicraft.booking.entity.HandicraftBooking;
import io.github.qifan777.server.handicraft.booking.entity.dto.HandicraftBookingConfigInput;
import io.github.qifan777.server.handicraft.category.entity.HandicraftCategory;
import io.github.qifan777.server.infrastructure.jimmer.BaseEntity;
import io.github.qifan777.server.infrastructure.model.UploadFile;
import io.qifan.infrastructure.generator.core.*;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.sql.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 手工预约
 */
@GenEntity
@Entity
public interface Handicraft extends BaseEntity {

    /**
     * 手工名称
     */
    @GenTextField(label = "手工名称", order = 0)
    @Key
    String name();

    /**
     * 封面
     */
    @GenImageField(label = "封面", order = 1)
    String cover();

    /**
     * 价格
     */
    @GenNumberField(label = "价格", order = 2)
    BigDecimal price();

    /**
     * 标签
     */
    @Null
    @GenValueField(label = "标签", order = 3)
    @Serialized
    List<String> tags();

    /**
     * 描述
     */
    @GenTextAreaField(label = "描述", order = 4)
    @Null
    String description();

    /**
     * 优惠价格
     */
    @GenNumberField(label = "优惠价格", order = 5)
    @Null
    BigDecimal discountPrice();

    /**
     * 提示
     */
    @GenTextField(label = "提示", order = 6)
    @Null
    String tip();

    @GenAssociationField(label = "类别", order = 7, prop = "categoryId")
    @ManyToOne
    HandicraftCategory category();

    @IdView
    String categoryId();

    /**
     * 计费方式
     */
    @GenDictField(label = "计费方式", order = 8, dictEnName = DictConstants.CHARGING_TYPE)
    DictConstants.ChargingType chargingType();

    /**
     * 是否锁定
     */
    @GenBooleanField(label = "是否锁定", order = 9)
    boolean locked();


    @OneToMany(mappedBy = "handicraft")
    List<HandicraftBooking> handicraftBookings();

    @Serialized
    List<HandicraftBookingConfigInput> periods();

    @Serialized
    List<UploadFile> primaryPictures();

    @Null
    @Serialized
    List<UploadFile> video();
}
