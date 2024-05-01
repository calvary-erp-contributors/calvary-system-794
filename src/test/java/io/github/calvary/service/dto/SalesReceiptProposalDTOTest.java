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

class SalesReceiptProposalDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptProposalDTO.class);
        SalesReceiptProposalDTO salesReceiptProposalDTO1 = new SalesReceiptProposalDTO();
        salesReceiptProposalDTO1.setId(1L);
        SalesReceiptProposalDTO salesReceiptProposalDTO2 = new SalesReceiptProposalDTO();
        assertThat(salesReceiptProposalDTO1).isNotEqualTo(salesReceiptProposalDTO2);
        salesReceiptProposalDTO2.setId(salesReceiptProposalDTO1.getId());
        assertThat(salesReceiptProposalDTO1).isEqualTo(salesReceiptProposalDTO2);
        salesReceiptProposalDTO2.setId(2L);
        assertThat(salesReceiptProposalDTO1).isNotEqualTo(salesReceiptProposalDTO2);
        salesReceiptProposalDTO1.setId(null);
        assertThat(salesReceiptProposalDTO1).isNotEqualTo(salesReceiptProposalDTO2);
    }
}
