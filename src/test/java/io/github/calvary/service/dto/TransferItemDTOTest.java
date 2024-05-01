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
