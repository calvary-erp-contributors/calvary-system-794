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

class SalesReceiptTitleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptTitle.class);
        SalesReceiptTitle salesReceiptTitle1 = new SalesReceiptTitle();
        salesReceiptTitle1.setId(1L);
        SalesReceiptTitle salesReceiptTitle2 = new SalesReceiptTitle();
        salesReceiptTitle2.setId(salesReceiptTitle1.getId());
        assertThat(salesReceiptTitle1).isEqualTo(salesReceiptTitle2);
        salesReceiptTitle2.setId(2L);
        assertThat(salesReceiptTitle1).isNotEqualTo(salesReceiptTitle2);
        salesReceiptTitle1.setId(null);
        assertThat(salesReceiptTitle1).isNotEqualTo(salesReceiptTitle2);
    }
}
