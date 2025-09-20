package io.github.qifan777.server.handicraft.category.service;

import io.github.qifan777.server.handicraft.category.repository.HandicraftCategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class HandicraftCategoryService {
    private final HandicraftCategoryRepository handicraftCategoryRepository;

}