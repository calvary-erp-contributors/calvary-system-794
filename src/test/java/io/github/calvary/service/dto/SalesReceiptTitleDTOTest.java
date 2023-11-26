package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesReceiptTitleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptTitleDTO.class);
        SalesReceiptTitleDTO salesReceiptTitleDTO1 = new SalesReceiptTitleDTO();
        salesReceiptTitleDTO1.setId(1L);
        SalesReceiptTitleDTO salesReceiptTitleDTO2 = new SalesReceiptTitleDTO();
        assertThat(salesReceiptTitleDTO1).isNotEqualTo(salesReceiptTitleDTO2);
        salesReceiptTitleDTO2.setId(salesReceiptTitleDTO1.getId());
        assertThat(salesReceiptTitleDTO1).isEqualTo(salesReceiptTitleDTO2);
        salesReceiptTitleDTO2.setId(2L);
        assertThat(salesReceiptTitleDTO1).isNotEqualTo(salesReceiptTitleDTO2);
        salesReceiptTitleDTO1.setId(null);
        assertThat(salesReceiptTitleDTO1).isNotEqualTo(salesReceiptTitleDTO2);
    }
}
