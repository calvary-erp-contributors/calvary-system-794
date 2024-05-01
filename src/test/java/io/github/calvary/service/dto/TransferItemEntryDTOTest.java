package io.github.calvary.service.dto;

/*-
 * Copyright © 2023 - 2024 Calvary ERP Contributors (mailnjeru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
