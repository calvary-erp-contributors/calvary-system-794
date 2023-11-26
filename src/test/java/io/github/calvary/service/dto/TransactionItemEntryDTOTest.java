package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionItemEntryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionItemEntryDTO.class);
        TransactionItemEntryDTO transactionItemEntryDTO1 = new TransactionItemEntryDTO();
        transactionItemEntryDTO1.setId(1L);
        TransactionItemEntryDTO transactionItemEntryDTO2 = new TransactionItemEntryDTO();
        assertThat(transactionItemEntryDTO1).isNotEqualTo(transactionItemEntryDTO2);
        transactionItemEntryDTO2.setId(transactionItemEntryDTO1.getId());
        assertThat(transactionItemEntryDTO1).isEqualTo(transactionItemEntryDTO2);
        transactionItemEntryDTO2.setId(2L);
        assertThat(transactionItemEntryDTO1).isNotEqualTo(transactionItemEntryDTO2);
        transactionItemEntryDTO1.setId(null);
        assertThat(transactionItemEntryDTO1).isNotEqualTo(transactionItemEntryDTO2);
    }
}
