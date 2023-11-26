package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionItemAmountMapperTest {

    private TransactionItemAmountMapper transactionItemAmountMapper;

    @BeforeEach
    public void setUp() {
        transactionItemAmountMapper = new TransactionItemAmountMapperImpl();
    }
}
