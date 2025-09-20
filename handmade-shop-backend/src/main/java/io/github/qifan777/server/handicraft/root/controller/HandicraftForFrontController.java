package io.github.qifan777.server.handicraft.root.controller;

import io.github.qifan777.server.Fetchers;
import io.github.qifan777.server.handicraft.root.entity.Handicraft;
import io.github.qifan777.server.handicraft.root.entity.dto.HandicraftSpec;
import io.github.qifan777.server.handicraft.root.repository.HandicraftRepository;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("front/handicraft")
@AllArgsConstructor
@DefaultFetcherOwner(HandicraftRepository.class)
@Transactional
public class HandicraftForFrontController {
    private final HandicraftRepository handicraftRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") Handicraft findById(@PathVariable String id) {
        return handicraftRepository.findById(id, HandicraftRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") Handicraft> query(@RequestBody QueryRequest<HandicraftSpec> queryRequest) {
        return handicraftRepository.findPage(queryRequest, HandicraftRepository.COMPLEX_FETCHER_FOR_FRONT);
    }
}
