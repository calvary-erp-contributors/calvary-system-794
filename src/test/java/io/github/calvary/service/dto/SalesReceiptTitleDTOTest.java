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

class SalesReceiptTitleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptTitleDTO.class);
        SalesReceiptTitleDTO salesReceiptTitleDTO1 = new SalesReceiptTitleDTO();
        salesReceiptTitleDTO1.setId(1L);
        SalesReceiptTitleDTO salesReceiptTitleDTO2 = new SalesReceiptTitleDTO();
        assertThat(salesReceiptTitleDTO1).isNotEqualTo(salesReceiptTitleDTO2);
        salesReceiptTitleDTO2.setId(salesReceiptTitleDTO1.getId());
        assertThat(salesReceiptTitleDTO1).isEqualTo(salesReceiptTitleDTO2);
        salesReceiptTitleDTO2.setId(2L);
        assertThat(salesReceiptTitleDTO1).isNotEqualTo(salesReceiptTitleDTO2);
        salesReceiptTitleDTO1.setId(null);
        assertThat(salesReceiptTitleDTO1).isNotEqualTo(salesReceiptTitleDTO2);
    }
}
