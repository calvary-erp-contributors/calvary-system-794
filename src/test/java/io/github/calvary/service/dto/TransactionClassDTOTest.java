package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionClassDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionClassDTO.class);
        TransactionClassDTO transactionClassDTO1 = new TransactionClassDTO();
        transactionClassDTO1.setId(1L);
        TransactionClassDTO transactionClassDTO2 = new TransactionClassDTO();
        assertThat(transactionClassDTO1).isNotEqualTo(transactionClassDTO2);
        transactionClassDTO2.setId(transactionClassDTO1.getId());
        assertThat(transactionClassDTO1).isEqualTo(transactionClassDTO2);
        transactionClassDTO2.setId(2L);
        assertThat(transactionClassDTO1).isNotEqualTo(transactionClassDTO2);
        transactionClassDTO1.setId(null);
        assertThat(transactionClassDTO1).isNotEqualTo(transactionClassDTO2);
    }
}
