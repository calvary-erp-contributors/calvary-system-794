package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SalesReceiptMapperTest {

    private SalesReceiptMapper salesReceiptMapper;

    @BeforeEach
    public void setUp() {
        salesReceiptMapper = new SalesReceiptMapperImpl();
    }
}
