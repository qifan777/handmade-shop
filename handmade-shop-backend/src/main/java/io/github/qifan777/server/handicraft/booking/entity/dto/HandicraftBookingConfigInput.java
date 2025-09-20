package io.github.qifan777.server.handicraft.booking.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
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
import java.time.LocalTime;
import java.util.Objects;
import org.babyfish.jimmer.Input;
import org.babyfish.jimmer.internal.FixedInputField;
import org.babyfish.jimmer.internal.GeneratedBy;
import org.babyfish.jimmer.sql.fetcher.DtoMetadata;
import org.jetbrains.annotations.NotNull;

/**
 * 手工预约时间段
 */
@GeneratedBy(
        file = "<handmade-shop-backend>/src/main/dto/handicraft/HandicraftBooking.dto"
)
@JsonDeserialize(
        builder = HandicraftBookingConfigInput.Builder.class
)
@GenEntity
public class HandicraftBookingConfigInput implements Input<HandicraftBooking> {
    public static final DtoMetadata<HandicraftBooking, HandicraftBookingConfigInput> METADATA = 
        new DtoMetadata<HandicraftBooking, HandicraftBookingConfigInput>(
            HandicraftBookingFetcher.$
                .startTime()
                .endTime()
                .price()
                .peopleLimit()
                .locked(),
            HandicraftBookingConfigInput::new
    );

    @FixedInputField
    private LocalTime startTime;

    @FixedInputField
    private LocalTime endTime;

    @FixedInputField
    private BigDecimal price;

    @FixedInputField
    private Integer peopleLimit;

    @FixedInputField
    private Boolean locked;

    public HandicraftBookingConfigInput() {
    }

    public HandicraftBookingConfigInput(@NotNull HandicraftBooking base) {
        this.startTime = base.startTime();
        this.endTime = base.endTime();
        this.price = base.price();
        this.peopleLimit = base.peopleLimit();
        this.locked = base.locked();
    }

    /**
     * 开始时间
     */
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

    /**
     * 结束时间
     */
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

    /**
     * 价格
     */
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

    /**
     * 人数限制
     */
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

    /**
     * 是否锁定
     */
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

    @Override
    public HandicraftBooking toEntity() {
        return HandicraftBookingDraft.$.produce(__draft -> {
            __draft.setStartTime(startTime);
            __draft.setEndTime(endTime);
            __draft.setPrice(price);
            __draft.setPeopleLimit(peopleLimit);
            __draft.setLocked(locked);
        });
    }

    @Override
    public int hashCode() {
        int hash = Objects.hashCode(startTime);
        hash = hash * 31 + Objects.hashCode(endTime);
        hash = hash * 31 + Objects.hashCode(price);
        hash = hash * 31 + Integer.hashCode(peopleLimit);
        hash = hash * 31 + Boolean.hashCode(locked);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HandicraftBookingConfigInput other = (HandicraftBookingConfigInput) o;
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
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HandicraftBookingConfigInput").append('(');
        builder.append("startTime=").append(startTime);
        builder.append(", endTime=").append(endTime);
        builder.append(", price=").append(price);
        builder.append(", peopleLimit=").append(peopleLimit);
        builder.append(", locked=").append(locked);
        builder.append(')');
        return builder.toString();
    }

    @JsonPOJOBuilder(
            withPrefix = ""
    )
    public static class Builder {
        private LocalTime startTime;

        private LocalTime endTime;

        private BigDecimal price;

        private Integer peopleLimit;

        private Boolean locked;

        public Builder startTime(LocalTime startTime) {
            this.startTime = Objects.requireNonNull(startTime, "The property \"startTime\" cannot be null");
            return this;
        }

        public Builder endTime(LocalTime endTime) {
            this.endTime = Objects.requireNonNull(endTime, "The property \"endTime\" cannot be null");
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = Objects.requireNonNull(price, "The property \"price\" cannot be null");
            return this;
        }

        public Builder peopleLimit(int peopleLimit) {
            this.peopleLimit = Objects.requireNonNull(peopleLimit, "The property \"peopleLimit\" cannot be null");
            return this;
        }

        public Builder locked(boolean locked) {
            this.locked = Objects.requireNonNull(locked, "The property \"locked\" cannot be null");
            return this;
        }

        public HandicraftBookingConfigInput build() {
            HandicraftBookingConfigInput _input = new HandicraftBookingConfigInput();
            if (startTime == null) {
                throw Input.unknownNonNullProperty(HandicraftBookingConfigInput.class, "startTime");
            }
            _input.setStartTime(startTime);
            if (endTime == null) {
                throw Input.unknownNonNullProperty(HandicraftBookingConfigInput.class, "endTime");
            }
            _input.setEndTime(endTime);
            if (price == null) {
                throw Input.unknownNonNullProperty(HandicraftBookingConfigInput.class, "price");
            }
            _input.setPrice(price);
            if (peopleLimit == null) {
                throw Input.unknownNonNullProperty(HandicraftBookingConfigInput.class, "peopleLimit");
            }
            _input.setPeopleLimit(peopleLimit);
            if (locked == null) {
                throw Input.unknownNonNullProperty(HandicraftBookingConfigInput.class, "locked");
            }
            _input.setLocked(locked);
            return _input;
        }
    }
}
