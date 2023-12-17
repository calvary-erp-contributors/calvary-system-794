package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransferItemEntryMapperTest {

    private TransferItemEntryMapper transferItemEntryMapper;

    @BeforeEach
    public void setUp() {
        transferItemEntryMapper = new TransferItemEntryMapperImpl();
    }
}
