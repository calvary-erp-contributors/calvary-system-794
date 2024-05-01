package io.github.calvary.domain;

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

class SalesReceiptProposalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptProposal.class);
        SalesReceiptProposal salesReceiptProposal1 = new SalesReceiptProposal();
        salesReceiptProposal1.setId(1L);
        SalesReceiptProposal salesReceiptProposal2 = new SalesReceiptProposal();
        salesReceiptProposal2.setId(salesReceiptProposal1.getId());
        assertThat(salesReceiptProposal1).isEqualTo(salesReceiptProposal2);
        salesReceiptProposal2.setId(2L);
        assertThat(salesReceiptProposal1).isNotEqualTo(salesReceiptProposal2);
        salesReceiptProposal1.setId(null);
        assertThat(salesReceiptProposal1).isNotEqualTo(salesReceiptProposal2);
    }
}
