package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesReceiptEmailPersonaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptEmailPersonaDTO.class);
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO1 = new SalesReceiptEmailPersonaDTO();
        salesReceiptEmailPersonaDTO1.setId(1L);
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO2 = new SalesReceiptEmailPersonaDTO();
        assertThat(salesReceiptEmailPersonaDTO1).isNotEqualTo(salesReceiptEmailPersonaDTO2);
        salesReceiptEmailPersonaDTO2.setId(salesReceiptEmailPersonaDTO1.getId());
        assertThat(salesReceiptEmailPersonaDTO1).isEqualTo(salesReceiptEmailPersonaDTO2);
        salesReceiptEmailPersonaDTO2.setId(2L);
        assertThat(salesReceiptEmailPersonaDTO1).isNotEqualTo(salesReceiptEmailPersonaDTO2);
        salesReceiptEmailPersonaDTO1.setId(null);
        assertThat(salesReceiptEmailPersonaDTO1).isNotEqualTo(salesReceiptEmailPersonaDTO2);
    }
}
