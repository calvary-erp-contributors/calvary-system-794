package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransferItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransferItem.class);
        TransferItem transferItem1 = new TransferItem();
        transferItem1.setId(1L);
        TransferItem transferItem2 = new TransferItem();
        transferItem2.setId(transferItem1.getId());
        assertThat(transferItem1).isEqualTo(transferItem2);
        transferItem2.setId(2L);
        assertThat(transferItem1).isNotEqualTo(transferItem2);
        transferItem1.setId(null);
        assertThat(transferItem1).isNotEqualTo(transferItem2);
    }
}
