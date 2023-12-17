package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesReceiptProposalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptProposal.class);
        SalesReceiptProposal salesReceiptProposal1 = new SalesReceiptProposal();
        salesReceiptProposal1.setId(1L);
        SalesReceiptProposal salesReceiptProposal2 = new SalesReceiptProposal();
        salesReceiptProposal2.setId(salesReceiptProposal1.getId());
        assertThat(salesReceiptProposal1).isEqualTo(salesReceiptProposal2);
        salesReceiptProposal2.setId(2L);
        assertThat(salesReceiptProposal1).isNotEqualTo(salesReceiptProposal2);
        salesReceiptProposal1.setId(null);
        assertThat(salesReceiptProposal1).isNotEqualTo(salesReceiptProposal2);
    }
}
