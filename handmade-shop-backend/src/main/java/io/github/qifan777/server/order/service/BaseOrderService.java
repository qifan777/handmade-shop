package io.github.qifan777.server.order.service;

import io.github.qifan777.server.order.repository.BaseOrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class BaseOrderService {
    private final BaseOrderRepository baseOrderRepository;

}