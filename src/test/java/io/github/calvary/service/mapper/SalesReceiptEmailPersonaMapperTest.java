package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SalesReceiptEmailPersonaMapperTest {

    private SalesReceiptEmailPersonaMapper salesReceiptEmailPersonaMapper;

    @BeforeEach
    public void setUp() {
        salesReceiptEmailPersonaMapper = new SalesReceiptEmailPersonaMapperImpl();
    }
}
