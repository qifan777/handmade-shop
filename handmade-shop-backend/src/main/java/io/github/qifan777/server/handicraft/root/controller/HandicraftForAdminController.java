package io.github.qifan777.server.handicraft.root.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import io.github.qifan777.server.handicraft.booking.entity.HandicraftBooking;
import io.github.qifan777.server.handicraft.booking.entity.HandicraftBookingDraft;
import io.github.qifan777.server.handicraft.booking.entity.dto.HandicraftBookingConfigInput;
import io.github.qifan777.server.handicraft.booking.repository.HandicraftBookingRepository;
import io.github.qifan777.server.handicraft.root.entity.Handicraft;
import io.github.qifan777.server.handicraft.root.entity.dto.HandicraftInput;
import io.github.qifan777.server.handicraft.root.entity.dto.HandicraftSpec;
import io.github.qifan777.server.handicraft.root.repository.HandicraftRepository;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("admin/handicraft")
@AllArgsConstructor
@DefaultFetcherOwner(HandicraftRepository.class)
@SaCheckPermission("/handicraft")
@Transactional
public class HandicraftForAdminController {
    private final HandicraftRepository handicraftRepository;
    private final HandicraftBookingRepository handicraftBookingRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") Handicraft findById(@PathVariable String id) {
        return handicraftRepository.findById(id, HandicraftRepository.COMPLEX_FETCHER_FOR_ADMIN).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") Handicraft> query(@RequestBody QueryRequest<HandicraftSpec> queryRequest) {
        return handicraftRepository.findPage(queryRequest, HandicraftRepository.COMPLEX_FETCHER_FOR_ADMIN);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated HandicraftInput handicraftInput) {
        String id = handicraftRepository.save(handicraftInput.toEntity()).id();
        handicraftBookingRepository.deleteByHandicraftId(id);
        List<LocalDate> localDates = List.of(LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        List<HandicraftBooking> bookings = bookings(localDates, handicraftInput.getPeriods(), id);
        handicraftBookingRepository.saveEntities(bookings);
        return id;
    }

    public List<HandicraftBooking> bookings(List<LocalDate> localDates, List<HandicraftBookingConfigInput> bookingConfigInputs, String id) {
        return localDates.stream()
                .flatMap(localDate -> bookingConfigInputs
                        .stream()
                        .map(period -> HandicraftBookingDraft.$
                                .produce(period.toEntity(),
                                        draft -> draft.setHandicraftId(id)
                                                .setDate(localDate))))
                .toList();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        handicraftRepository.deleteAllById(ids);
        return true;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @PostMapping("generate")
    public void generate() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        StpUtil.switchTo("1");
        List<Handicraft> all = handicraftRepository.findAll();
        List<LocalDate> localDates = List.of(LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        all.forEach(handicraft -> {
            localDates.stream()
                    .filter(localDate -> handicraftBookingRepository.findHandicraftBookingCountByDate(handicraft.id(), localDate) == 0)
                    .forEach(localDate -> handicraftBookingRepository.saveEntities(bookings(List.of(localDate), handicraft.periods(), handicraft.id())));
        });
    }
}