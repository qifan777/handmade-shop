
package io.github.qifan777.server.handicraft.booking.controller;

import io.github.qifan777.server.handicraft.booking.entity.HandicraftBooking;
import io.github.qifan777.server.handicraft.booking.entity.dto.HandicraftBookingSpec;
import io.github.qifan777.server.handicraft.booking.repository.HandicraftBookingRepository;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("front/handicraft-booking")
@AllArgsConstructor
@DefaultFetcherOwner(HandicraftBookingRepository.class)
@Transactional
public class HandicraftBookingForFrontController {
    private final HandicraftBookingRepository handicraftBookingRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") HandicraftBooking findById(@PathVariable String id) {
        return handicraftBookingRepository.findById(id, HandicraftBookingRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") HandicraftBooking> query(@RequestBody QueryRequest<HandicraftBookingSpec> queryRequest) {
        return handicraftBookingRepository.findPage(queryRequest, HandicraftBookingRepository.COMPLEX_FETCHER_FOR_FRONT);
    }

}
