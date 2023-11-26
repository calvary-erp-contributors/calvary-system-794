package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesReceiptDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptDTO.class);
        SalesReceiptDTO salesReceiptDTO1 = new SalesReceiptDTO();
        salesReceiptDTO1.setId(1L);
        SalesReceiptDTO salesReceiptDTO2 = new SalesReceiptDTO();
        assertThat(salesReceiptDTO1).isNotEqualTo(salesReceiptDTO2);
        salesReceiptDTO2.setId(salesReceiptDTO1.getId());
        assertThat(salesReceiptDTO1).isEqualTo(salesReceiptDTO2);
        salesReceiptDTO2.setId(2L);
        assertThat(salesReceiptDTO1).isNotEqualTo(salesReceiptDTO2);
        salesReceiptDTO1.setId(null);
        assertThat(salesReceiptDTO1).isNotEqualTo(salesReceiptDTO2);
    }
}
