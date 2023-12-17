package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SalesReceiptProposalMapperTest {

    private SalesReceiptProposalMapper salesReceiptProposalMapper;

    @BeforeEach
    public void setUp() {
        salesReceiptProposalMapper = new SalesReceiptProposalMapperImpl();
    }
}
