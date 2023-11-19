package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionItemDTO.class);
        TransactionItemDTO transactionItemDTO1 = new TransactionItemDTO();
        transactionItemDTO1.setId(1L);
        TransactionItemDTO transactionItemDTO2 = new TransactionItemDTO();
        assertThat(transactionItemDTO1).isNotEqualTo(transactionItemDTO2);
        transactionItemDTO2.setId(transactionItemDTO1.getId());
        assertThat(transactionItemDTO1).isEqualTo(transactionItemDTO2);
        transactionItemDTO2.setId(2L);
        assertThat(transactionItemDTO1).isNotEqualTo(transactionItemDTO2);
        transactionItemDTO1.setId(null);
        assertThat(transactionItemDTO1).isNotEqualTo(transactionItemDTO2);
    }
}
