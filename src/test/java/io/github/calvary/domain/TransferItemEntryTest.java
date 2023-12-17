package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransferItemEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransferItemEntry.class);
        TransferItemEntry transferItemEntry1 = new TransferItemEntry();
        transferItemEntry1.setId(1L);
        TransferItemEntry transferItemEntry2 = new TransferItemEntry();
        transferItemEntry2.setId(transferItemEntry1.getId());
        assertThat(transferItemEntry1).isEqualTo(transferItemEntry2);
        transferItemEntry2.setId(2L);
        assertThat(transferItemEntry1).isNotEqualTo(transferItemEntry2);
        transferItemEntry1.setId(null);
        assertThat(transferItemEntry1).isNotEqualTo(transferItemEntry2);
    }
}
