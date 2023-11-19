package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionClassMapperTest {

    private TransactionClassMapper transactionClassMapper;

    @BeforeEach
    public void setUp() {
        transactionClassMapper = new TransactionClassMapperImpl();
    }
}
