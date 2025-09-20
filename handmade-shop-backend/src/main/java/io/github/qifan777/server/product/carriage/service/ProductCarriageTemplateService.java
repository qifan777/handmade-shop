package io.github.qifan777.server.product.carriage.service;

import io.github.qifan777.server.product.carriage.repository.ProductCarriageTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class ProductCarriageTemplateService {
    private final ProductCarriageTemplateRepository productCarriageTemplateRepository;

}