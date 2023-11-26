package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionItemEntryMapperTest {

    private TransactionItemEntryMapper transactionItemEntryMapper;

    @BeforeEach
    public void setUp() {
        transactionItemEntryMapper = new TransactionItemEntryMapperImpl();
    }
}
