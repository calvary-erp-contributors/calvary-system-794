package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReceiptEmailRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReceiptEmailRequestDTO.class);
        ReceiptEmailRequestDTO receiptEmailRequestDTO1 = new ReceiptEmailRequestDTO();
        receiptEmailRequestDTO1.setId(1L);
        ReceiptEmailRequestDTO receiptEmailRequestDTO2 = new ReceiptEmailRequestDTO();
        assertThat(receiptEmailRequestDTO1).isNotEqualTo(receiptEmailRequestDTO2);
        receiptEmailRequestDTO2.setId(receiptEmailRequestDTO1.getId());
        assertThat(receiptEmailRequestDTO1).isEqualTo(receiptEmailRequestDTO2);
        receiptEmailRequestDTO2.setId(2L);
        assertThat(receiptEmailRequestDTO1).isNotEqualTo(receiptEmailRequestDTO2);
        receiptEmailRequestDTO1.setId(null);
        assertThat(receiptEmailRequestDTO1).isNotEqualTo(receiptEmailRequestDTO2);
    }
}
