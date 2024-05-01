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

class SalesReceiptEmailPersonaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptEmailPersonaDTO.class);
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO1 = new SalesReceiptEmailPersonaDTO();
        salesReceiptEmailPersonaDTO1.setId(1L);
        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO2 = new SalesReceiptEmailPersonaDTO();
        assertThat(salesReceiptEmailPersonaDTO1).isNotEqualTo(salesReceiptEmailPersonaDTO2);
        salesReceiptEmailPersonaDTO2.setId(salesReceiptEmailPersonaDTO1.getId());
        assertThat(salesReceiptEmailPersonaDTO1).isEqualTo(salesReceiptEmailPersonaDTO2);
        salesReceiptEmailPersonaDTO2.setId(2L);
        assertThat(salesReceiptEmailPersonaDTO1).isNotEqualTo(salesReceiptEmailPersonaDTO2);
        salesReceiptEmailPersonaDTO1.setId(null);
        assertThat(salesReceiptEmailPersonaDTO1).isNotEqualTo(salesReceiptEmailPersonaDTO2);
    }
}
