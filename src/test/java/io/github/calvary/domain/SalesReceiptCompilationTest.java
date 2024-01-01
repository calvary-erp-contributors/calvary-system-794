package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesReceiptCompilationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptCompilation.class);
        SalesReceiptCompilation salesReceiptCompilation1 = new SalesReceiptCompilation();
        salesReceiptCompilation1.setId(1L);
        SalesReceiptCompilation salesReceiptCompilation2 = new SalesReceiptCompilation();
        salesReceiptCompilation2.setId(salesReceiptCompilation1.getId());
        assertThat(salesReceiptCompilation1).isEqualTo(salesReceiptCompilation2);
        salesReceiptCompilation2.setId(2L);
        assertThat(salesReceiptCompilation1).isNotEqualTo(salesReceiptCompilation2);
        salesReceiptCompilation1.setId(null);
        assertThat(salesReceiptCompilation1).isNotEqualTo(salesReceiptCompilation2);
    }
}
