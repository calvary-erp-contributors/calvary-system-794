package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReceiptEmailRequestMapperTest {

    private ReceiptEmailRequestMapper receiptEmailRequestMapper;

    @BeforeEach
    public void setUp() {
        receiptEmailRequestMapper = new ReceiptEmailRequestMapperImpl();
    }
}
