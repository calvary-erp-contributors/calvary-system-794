package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionItem.class);
        TransactionItem transactionItem1 = new TransactionItem();
        transactionItem1.setId(1L);
        TransactionItem transactionItem2 = new TransactionItem();
        transactionItem2.setId(transactionItem1.getId());
        assertThat(transactionItem1).isEqualTo(transactionItem2);
        transactionItem2.setId(2L);
        assertThat(transactionItem1).isNotEqualTo(transactionItem2);
        transactionItem1.setId(null);
        assertThat(transactionItem1).isNotEqualTo(transactionItem2);
    }
}
