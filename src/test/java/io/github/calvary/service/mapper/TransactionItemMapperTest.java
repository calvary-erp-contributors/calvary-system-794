package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionItemMapperTest {

    private TransactionItemMapper transactionItemMapper;

    @BeforeEach
    public void setUp() {
        transactionItemMapper = new TransactionItemMapperImpl();
    }
}
