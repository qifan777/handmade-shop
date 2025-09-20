package io.github.qifan777.server.handicraft.booking.entity.dto;

import io.github.qifan777.server.handicraft.booking.entity.HandicraftBooking;
import io.github.qifan777.server.handicraft.booking.entity.HandicraftBookingDraft;
import io.github.qifan777.server.handicraft.booking.entity.HandicraftBookingFetcher;
import io.qifan.infrastructure.generator.core.GenBooleanField;
import io.qifan.infrastructure.generator.core.GenDateTimeField;
import io.qifan.infrastructure.generator.core.GenEntity;
import io.qifan.infrastructure.generator.core.GenNumberField;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.internal.GeneratedBy;
import org.babyfish.jimmer.meta.PropId;
import org.babyfish.jimmer.runtime.ImmutableSpi;
import org.babyfish.jimmer.sql.fetcher.DtoMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@GeneratedBy(
        file = "<handmade-shop-backend>/src/main/dto/handicraft/HandicraftBooking.dto"
)
@GenEntity
public class HandicraftBookingView implements View<HandicraftBooking> {
    public static final DtoMetadata<HandicraftBooking, HandicraftBookingView> METADATA = 
        new DtoMetadata<HandicraftBooking, HandicraftBookingView>(
            HandicraftBookingFetcher.$
                .startTime()
                .endTime()
                .price()
                .peopleLimit()
                .locked()
                .date()
                .bookingCount(),
            HandicraftBookingView::new
    );

    private String id;

    private LocalTime startTime;

    private LocalTime endTime;

    private BigDecimal price;

    private Integer peopleLimit;

    private Boolean locked;

    private LocalDate date;

    private Integer bookingCount;

    private Integer minutes;

    public HandicraftBookingView() {
    }

    public HandicraftBookingView(@NotNull HandicraftBooking base) {
        this.id = base.id();
        this.startTime = base.startTime();
        this.endTime = base.endTime();
        this.price = base.price();
        this.peopleLimit = base.peopleLimit();
        this.locked = base.locked();
        this.date = base.date();
        this.bookingCount = ((ImmutableSpi)base).__isLoaded(PropId.byIndex(HandicraftBookingDraft.Producer.SLOT_BOOKING_COUNT)) ? base.bookingCount() : null;
    }

    @NotNull
    public String getId() {
        if (id == null) {
            throw new IllegalStateException("The property \"id\" is not specified");
        }
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    @NotNull
    @GenDateTimeField(
            label = "开始时间",
            order = 1
    )
    public LocalTime getStartTime() {
        if (startTime == null) {
            throw new IllegalStateException("The property \"startTime\" is not specified");
        }
        return startTime;
    }

    public void setStartTime(@NotNull LocalTime startTime) {
        this.startTime = startTime;
    }

    @NotNull
    @GenDateTimeField(
            label = "结束时间",
            order = 2
    )
    public LocalTime getEndTime() {
        if (endTime == null) {
            throw new IllegalStateException("The property \"endTime\" is not specified");
        }
        return endTime;
    }

    public void setEndTime(@NotNull LocalTime endTime) {
        this.endTime = endTime;
    }

    @NotNull
    @GenNumberField(
            label = "价格",
            order = 4
    )
    public BigDecimal getPrice() {
        if (price == null) {
            throw new IllegalStateException("The property \"price\" is not specified");
        }
        return price;
    }

    public void setPrice(@NotNull BigDecimal price) {
        this.price = price;
    }

    @GenNumberField(
            label = "人数限制",
            order = 5
    )
    public int getPeopleLimit() {
        if (peopleLimit == null) {
            throw new IllegalStateException("The property \"peopleLimit\" is not specified");
        }
        return peopleLimit;
    }

    public void setPeopleLimit(int peopleLimit) {
        this.peopleLimit = peopleLimit;
    }

    @GenBooleanField(
            label = "是否锁定",
            order = 6
    )
    public boolean isLocked() {
        if (locked == null) {
            throw new IllegalStateException("The property \"locked\" is not specified");
        }
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @NotNull
    @GenDateTimeField(
            label = "日期",
            order = 3
    )
    public LocalDate getDate() {
        if (date == null) {
            throw new IllegalStateException("The property \"date\" is not specified");
        }
        return date;
    }

    public void setDate(@NotNull LocalDate date) {
        this.date = date;
    }

    @Nullable
    public Integer getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(@Nullable Integer bookingCount) {
        this.bookingCount = bookingCount;
    }

    public int getMinutes() {
        if (minutes == null) {
            throw new IllegalStateException("The property \"minutes\" is not specified");
        }
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public HandicraftBooking toEntity() {
        return HandicraftBookingDraft.$.produce(__draft -> {
            __draft.setId(id);
            __draft.setStartTime(startTime);
            __draft.setEndTime(endTime);
            __draft.setPrice(price);
            __draft.setPeopleLimit(peopleLimit);
            __draft.setLocked(locked);
            __draft.setDate(date);
            __draft.setBookingCount(bookingCount);
        });
    }

    @Override
    public int hashCode() {
        int hash = Objects.hashCode(id);
        hash = hash * 31 + Objects.hashCode(startTime);
        hash = hash * 31 + Objects.hashCode(endTime);
        hash = hash * 31 + Objects.hashCode(price);
        hash = hash * 31 + Integer.hashCode(peopleLimit);
        hash = hash * 31 + Boolean.hashCode(locked);
        hash = hash * 31 + Objects.hashCode(date);
        hash = hash * 31 + Objects.hashCode(bookingCount);
        hash = hash * 31 + Integer.hashCode(minutes);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HandicraftBookingView other = (HandicraftBookingView) o;
        if (!Objects.equals(id, other.id)) {
            return false;
        }
        if (!Objects.equals(startTime, other.startTime)) {
            return false;
        }
        if (!Objects.equals(endTime, other.endTime)) {
            return false;
        }
        if (!Objects.equals(price, other.price)) {
            return false;
        }
        if (peopleLimit != other.peopleLimit) {
            return false;
        }
        if (locked != other.locked) {
            return false;
        }
        if (!Objects.equals(date, other.date)) {
            return false;
        }
        if (!Objects.equals(bookingCount, other.bookingCount)) {
            return false;
        }
        if (minutes != other.minutes) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HandicraftBookingView").append('(');
        builder.append("id=").append(id);
        builder.append(", startTime=").append(startTime);
        builder.append(", endTime=").append(endTime);
        builder.append(", price=").append(price);
        builder.append(", peopleLimit=").append(peopleLimit);
        builder.append(", locked=").append(locked);
        builder.append(", date=").append(date);
        builder.append(", bookingCount=").append(bookingCount);
        builder.append(", minutes=").append(minutes);
        builder.append(')');
        return builder.toString();
    }
}
