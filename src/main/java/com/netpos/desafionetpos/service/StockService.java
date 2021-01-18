package com.netpos.desafionetpos.service;

import com.netpos.desafionetpos.mapper.ProductMapper;
import com.netpos.desafionetpos.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final Logger log = LoggerFactory.getLogger(StockService.class);


    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public StockService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }
}
