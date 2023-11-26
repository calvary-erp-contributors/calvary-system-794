package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionItemAmountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionItemAmount.class);
        TransactionItemAmount transactionItemAmount1 = new TransactionItemAmount();
        transactionItemAmount1.setId(1L);
        TransactionItemAmount transactionItemAmount2 = new TransactionItemAmount();
        transactionItemAmount2.setId(transactionItemAmount1.getId());
        assertThat(transactionItemAmount1).isEqualTo(transactionItemAmount2);
        transactionItemAmount2.setId(2L);
        assertThat(transactionItemAmount1).isNotEqualTo(transactionItemAmount2);
        transactionItemAmount1.setId(null);
        assertThat(transactionItemAmount1).isNotEqualTo(transactionItemAmount2);
    }
}
