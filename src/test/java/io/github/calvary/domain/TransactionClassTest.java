package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionClassTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionClass.class);
        TransactionClass transactionClass1 = new TransactionClass();
        transactionClass1.setId(1L);
        TransactionClass transactionClass2 = new TransactionClass();
        transactionClass2.setId(transactionClass1.getId());
        assertThat(transactionClass1).isEqualTo(transactionClass2);
        transactionClass2.setId(2L);
        assertThat(transactionClass1).isNotEqualTo(transactionClass2);
        transactionClass1.setId(null);
        assertThat(transactionClass1).isNotEqualTo(transactionClass2);
    }
}
