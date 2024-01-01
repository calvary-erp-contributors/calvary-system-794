package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SalesReceiptCompilationMapperTest {

    private SalesReceiptCompilationMapper salesReceiptCompilationMapper;

    @BeforeEach
    public void setUp() {
        salesReceiptCompilationMapper = new SalesReceiptCompilationMapperImpl();
    }
}
