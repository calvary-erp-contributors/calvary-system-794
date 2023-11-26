package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionItemAmountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionItemAmountDTO.class);
        TransactionItemAmountDTO transactionItemAmountDTO1 = new TransactionItemAmountDTO();
        transactionItemAmountDTO1.setId(1L);
        TransactionItemAmountDTO transactionItemAmountDTO2 = new TransactionItemAmountDTO();
        assertThat(transactionItemAmountDTO1).isNotEqualTo(transactionItemAmountDTO2);
        transactionItemAmountDTO2.setId(transactionItemAmountDTO1.getId());
        assertThat(transactionItemAmountDTO1).isEqualTo(transactionItemAmountDTO2);
        transactionItemAmountDTO2.setId(2L);
        assertThat(transactionItemAmountDTO1).isNotEqualTo(transactionItemAmountDTO2);
        transactionItemAmountDTO1.setId(null);
        assertThat(transactionItemAmountDTO1).isNotEqualTo(transactionItemAmountDTO2);
    }
}
