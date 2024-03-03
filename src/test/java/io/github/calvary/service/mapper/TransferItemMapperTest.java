package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransferItemMapperTest {

    private TransferItemMapper transferItemMapper;

    @BeforeEach
    public void setUp() {
        transferItemMapper = new TransferItemMapperImpl();
    }
}
