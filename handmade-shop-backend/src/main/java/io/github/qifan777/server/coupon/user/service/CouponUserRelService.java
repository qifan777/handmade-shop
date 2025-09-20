package io.github.qifan777.server.coupon.user.service;

import cn.dev33.satoken.stp.StpUtil;
import io.github.qifan777.server.coupon.root.entity.Coupon;
import io.github.qifan777.server.coupon.user.entity.CouponUserRel;
import io.github.qifan777.server.coupon.user.entity.CouponUserRelTable;
import io.github.qifan777.server.coupon.user.repository.CouponUserRelRepository;
import io.github.qifan777.server.dict.model.DictConstants;
import io.qifan.infrastructure.common.constants.ResultCode;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class CouponUserRelService {
    private final CouponUserRelRepository couponUserRelRepository;

    public BigDecimal calculate(String id, BigDecimal amount) {
        var t = CouponUserRelTable.$;
        CouponUserRel couponUser = couponUserRelRepository.sql().createQuery(t)
                .where(t.userId().eq(StpUtil.getLoginIdAsString()))
                .where(t.status().eq(DictConstants.CouponUseStatus.UNUSED))
                .where(t.coupon().thresholdAmount().le(amount))
                .where(t.coupon().effectiveDate().le(LocalDateTime.now()))
                .where(t.coupon().expirationDate().ge(LocalDateTime.now()))
                .where(t.id().eq(id))
                .select(t.fetch(couponUserRelRepository.COMPLEX_FETCHER_FOR_ADMIN))
                .fetchOptional()
                .orElseThrow(() -> new BusinessException(ResultCode.NotFindError, "优惠券失效"));
        Coupon coupon = couponUser.coupon();
        if (coupon.couponType().equals(DictConstants.CouponType.DISCOUNT)) {
            return BigDecimal.TEN.subtract(coupon.discount()).divide(BigDecimal.TEN, RoundingMode.DOWN)
                    .multiply(amount);
        } else if (coupon.couponType().equals(DictConstants.CouponType.REDUCE)) {
            return coupon.amount();
        }
        throw new BusinessException(ResultCode.ParamSetIllegal, "优惠券类型错误");
    }
}