package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransferItemEntryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransferItemEntryDTO.class);
        TransferItemEntryDTO transferItemEntryDTO1 = new TransferItemEntryDTO();
        transferItemEntryDTO1.setId(1L);
        TransferItemEntryDTO transferItemEntryDTO2 = new TransferItemEntryDTO();
        assertThat(transferItemEntryDTO1).isNotEqualTo(transferItemEntryDTO2);
        transferItemEntryDTO2.setId(transferItemEntryDTO1.getId());
        assertThat(transferItemEntryDTO1).isEqualTo(transferItemEntryDTO2);
        transferItemEntryDTO2.setId(2L);
        assertThat(transferItemEntryDTO1).isNotEqualTo(transferItemEntryDTO2);
        transferItemEntryDTO1.setId(null);
        assertThat(transferItemEntryDTO1).isNotEqualTo(transferItemEntryDTO2);
    }
}
