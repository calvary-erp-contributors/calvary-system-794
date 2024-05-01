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

class SalesReceiptEmailPersonaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesReceiptEmailPersona.class);
        SalesReceiptEmailPersona salesReceiptEmailPersona1 = new SalesReceiptEmailPersona();
        salesReceiptEmailPersona1.setId(1L);
        SalesReceiptEmailPersona salesReceiptEmailPersona2 = new SalesReceiptEmailPersona();
        salesReceiptEmailPersona2.setId(salesReceiptEmailPersona1.getId());
        assertThat(salesReceiptEmailPersona1).isEqualTo(salesReceiptEmailPersona2);
        salesReceiptEmailPersona2.setId(2L);
        assertThat(salesReceiptEmailPersona1).isNotEqualTo(salesReceiptEmailPersona2);
        salesReceiptEmailPersona1.setId(null);
        assertThat(salesReceiptEmailPersona1).isNotEqualTo(salesReceiptEmailPersona2);
    }
}
