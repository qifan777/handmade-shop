package io.github.qifan777.server.handicraft.booking.entity;

import io.github.qifan777.server.handicraft.root.entity.Handicraft;
import io.github.qifan777.server.infrastructure.jimmer.BaseEntity;
import io.qifan.infrastructure.generator.core.*;
import org.babyfish.jimmer.Formula;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.IdView;
import org.babyfish.jimmer.sql.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 手工预约时间段
 */
@GenEntity
@Entity
public interface HandicraftBooking extends BaseEntity {


    /**
     * 开始时间
     */
    @GenDateTimeField(label = "开始时间", order = 1)
    LocalTime startTime();

    /**
     * 结束时间
     */
    @GenDateTimeField(label = "结束时间", order = 2)
    LocalTime endTime();

    /**
     * 日期
     */
    @GenDateTimeField(label = "日期", order = 3)
    LocalDate date();

    /**
     * 价格
     */
    @GenNumberField(label = "价格", order = 4)
    BigDecimal price();

    /**
     * 人数限制
     */
    @GenNumberField(label = "人数限制", order = 5)
    int peopleLimit();

    /**
     * 是否锁定
     */
    @GenBooleanField(label = "是否锁定", order = 6)
    boolean locked();

    /**
     * 手工
     */
    @GenAssociationField(label = "手工", order = 7, prop = "handicraftId")
    @ManyToOne
    Handicraft handicraft();

    @IdView
    String handicraftId();

    @Formula(sql = """
            (select count(*)
            from handicraft_order_item t1
                     left join handicraft_order t2 on
                t1.handicraft_order_id = t2.id
            where t2.status = 'TO_BE_CONFIRMED'
              and t1.handicraft_booking_id = %alias.id)
            """)
    Integer bookingCount();
}

