package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransferItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransferItemDTO.class);
        TransferItemDTO transferItemDTO1 = new TransferItemDTO();
        transferItemDTO1.setId(1L);
        TransferItemDTO transferItemDTO2 = new TransferItemDTO();
        assertThat(transferItemDTO1).isNotEqualTo(transferItemDTO2);
        transferItemDTO2.setId(transferItemDTO1.getId());
        assertThat(transferItemDTO1).isEqualTo(transferItemDTO2);
        transferItemDTO2.setId(2L);
        assertThat(transferItemDTO1).isNotEqualTo(transferItemDTO2);
        transferItemDTO1.setId(null);
        assertThat(transferItemDTO1).isNotEqualTo(transferItemDTO2);
    }
}
