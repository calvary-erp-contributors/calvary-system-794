package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReceiptEmailRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReceiptEmailRequest.class);
        ReceiptEmailRequest receiptEmailRequest1 = new ReceiptEmailRequest();
        receiptEmailRequest1.setId(1L);
        ReceiptEmailRequest receiptEmailRequest2 = new ReceiptEmailRequest();
        receiptEmailRequest2.setId(receiptEmailRequest1.getId());
        assertThat(receiptEmailRequest1).isEqualTo(receiptEmailRequest2);
        receiptEmailRequest2.setId(2L);
        assertThat(receiptEmailRequest1).isNotEqualTo(receiptEmailRequest2);
        receiptEmailRequest1.setId(null);
        assertThat(receiptEmailRequest1).isNotEqualTo(receiptEmailRequest2);
    }
}
