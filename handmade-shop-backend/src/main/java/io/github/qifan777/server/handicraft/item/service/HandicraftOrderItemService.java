package io.github.qifan777.server.handicraft.item.service;

import io.github.qifan777.server.handicraft.item.repository.HandicraftOrderItemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class HandicraftOrderItemService {
    private final HandicraftOrderItemRepository handicraftOrderItemRepository;

}