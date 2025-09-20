package io.github.qifan777.server.payment.model;

import io.github.qifan777.server.order.entity.BaseOrder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WeChatPayModel {
    BaseOrder baseOrder;
    String notifyUrl;
    int expiredMinutes;
}
