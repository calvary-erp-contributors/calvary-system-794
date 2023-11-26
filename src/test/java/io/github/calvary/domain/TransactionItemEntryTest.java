package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionItemEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionItemEntry.class);
        TransactionItemEntry transactionItemEntry1 = new TransactionItemEntry();
        transactionItemEntry1.setId(1L);
        TransactionItemEntry transactionItemEntry2 = new TransactionItemEntry();
        transactionItemEntry2.setId(transactionItemEntry1.getId());
        assertThat(transactionItemEntry1).isEqualTo(transactionItemEntry2);
        transactionItemEntry2.setId(2L);
        assertThat(transactionItemEntry1).isNotEqualTo(transactionItemEntry2);
        transactionItemEntry1.setId(null);
        assertThat(transactionItemEntry1).isNotEqualTo(transactionItemEntry2);
    }
}
