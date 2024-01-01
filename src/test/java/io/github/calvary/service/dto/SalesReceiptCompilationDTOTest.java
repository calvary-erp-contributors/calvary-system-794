package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesReceiptCompilationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptCompilationDTO.class);
        SalesReceiptCompilationDTO salesReceiptCompilationDTO1 = new SalesReceiptCompilationDTO();
        salesReceiptCompilationDTO1.setId(1L);
        SalesReceiptCompilationDTO salesReceiptCompilationDTO2 = new SalesReceiptCompilationDTO();
        assertThat(salesReceiptCompilationDTO1).isNotEqualTo(salesReceiptCompilationDTO2);
        salesReceiptCompilationDTO2.setId(salesReceiptCompilationDTO1.getId());
        assertThat(salesReceiptCompilationDTO1).isEqualTo(salesReceiptCompilationDTO2);
        salesReceiptCompilationDTO2.setId(2L);
        assertThat(salesReceiptCompilationDTO1).isNotEqualTo(salesReceiptCompilationDTO2);
        salesReceiptCompilationDTO1.setId(null);
        assertThat(salesReceiptCompilationDTO1).isNotEqualTo(salesReceiptCompilationDTO2);
    }
}
