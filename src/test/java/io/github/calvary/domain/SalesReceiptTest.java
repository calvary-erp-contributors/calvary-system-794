package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesReceiptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceipt.class);
        SalesReceipt salesReceipt1 = new SalesReceipt();
        salesReceipt1.setId(1L);
        SalesReceipt salesReceipt2 = new SalesReceipt();
        salesReceipt2.setId(salesReceipt1.getId());
        assertThat(salesReceipt1).isEqualTo(salesReceipt2);
        salesReceipt2.setId(2L);
        assertThat(salesReceipt1).isNotEqualTo(salesReceipt2);
        salesReceipt1.setId(null);
        assertThat(salesReceipt1).isNotEqualTo(salesReceipt2);
    }
}
