package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SalesReceiptTitleMapperTest {

    private SalesReceiptTitleMapper salesReceiptTitleMapper;

    @BeforeEach
    public void setUp() {
        salesReceiptTitleMapper = new SalesReceiptTitleMapperImpl();
    }
}
