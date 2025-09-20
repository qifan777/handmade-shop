package io.github.qifan777.server.handicraft.booking.controller;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.qifan777.server.handicraft.booking.entity.HandicraftBooking;
import io.github.qifan777.server.handicraft.booking.entity.dto.HandicraftBookingInput;
import io.github.qifan777.server.handicraft.booking.entity.dto.HandicraftBookingSpec;
import io.github.qifan777.server.handicraft.booking.repository.HandicraftBookingRepository;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/handicraft-booking")
@AllArgsConstructor
@DefaultFetcherOwner(HandicraftBookingRepository.class)
@SaCheckPermission("/handicraft-booking")
@Transactional
public class  HandicraftBookingForAdminController {
    private final  HandicraftBookingRepository handicraftBookingRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN")  HandicraftBooking findById(@PathVariable String id) {
        return handicraftBookingRepository.findById(id,HandicraftBookingRepository.COMPLEX_FETCHER_FOR_ADMIN).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page< @FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN")  HandicraftBooking> query(@RequestBody QueryRequest<HandicraftBookingSpec> queryRequest) {
        return handicraftBookingRepository.findPage(queryRequest, HandicraftBookingRepository.COMPLEX_FETCHER_FOR_ADMIN);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated HandicraftBookingInput handicraftBookingInput) {
        return handicraftBookingRepository.save(handicraftBookingInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        handicraftBookingRepository.deleteAllById(ids);
        return true;
    }
}