package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesReceiptProposalDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptProposalDTO.class);
        SalesReceiptProposalDTO salesReceiptProposalDTO1 = new SalesReceiptProposalDTO();
        salesReceiptProposalDTO1.setId(1L);
        SalesReceiptProposalDTO salesReceiptProposalDTO2 = new SalesReceiptProposalDTO();
        assertThat(salesReceiptProposalDTO1).isNotEqualTo(salesReceiptProposalDTO2);
        salesReceiptProposalDTO2.setId(salesReceiptProposalDTO1.getId());
        assertThat(salesReceiptProposalDTO1).isEqualTo(salesReceiptProposalDTO2);
        salesReceiptProposalDTO2.setId(2L);
        assertThat(salesReceiptProposalDTO1).isNotEqualTo(salesReceiptProposalDTO2);
        salesReceiptProposalDTO1.setId(null);
        assertThat(salesReceiptProposalDTO1).isNotEqualTo(salesReceiptProposalDTO2);
    }
}
