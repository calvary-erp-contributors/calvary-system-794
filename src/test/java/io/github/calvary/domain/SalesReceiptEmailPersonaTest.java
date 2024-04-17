package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesReceiptEmailPersonaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptEmailPersona.class);
        SalesReceiptEmailPersona salesReceiptEmailPersona1 = new SalesReceiptEmailPersona();
        salesReceiptEmailPersona1.setId(1L);
        SalesReceiptEmailPersona salesReceiptEmailPersona2 = new SalesReceiptEmailPersona();
        salesReceiptEmailPersona2.setId(salesReceiptEmailPersona1.getId());
        assertThat(salesReceiptEmailPersona1).isEqualTo(salesReceiptEmailPersona2);
        salesReceiptEmailPersona2.setId(2L);
        assertThat(salesReceiptEmailPersona1).isNotEqualTo(salesReceiptEmailPersona2);
        salesReceiptEmailPersona1.setId(null);
        assertThat(salesReceiptEmailPersona1).isNotEqualTo(salesReceiptEmailPersona2);
    }
}
