package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesReceiptTitleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptTitle.class);
        SalesReceiptTitle salesReceiptTitle1 = new SalesReceiptTitle();
        salesReceiptTitle1.setId(1L);
        SalesReceiptTitle salesReceiptTitle2 = new SalesReceiptTitle();
        salesReceiptTitle2.setId(salesReceiptTitle1.getId());
        assertThat(salesReceiptTitle1).isEqualTo(salesReceiptTitle2);
        salesReceiptTitle2.setId(2L);
        assertThat(salesReceiptTitle1).isNotEqualTo(salesReceiptTitle2);
        salesReceiptTitle1.setId(null);
        assertThat(salesReceiptTitle1).isNotEqualTo(salesReceiptTitle2);
    }
}
