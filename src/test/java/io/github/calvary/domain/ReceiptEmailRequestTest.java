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

class ReceiptEmailRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReceiptEmailRequest.class);
        ReceiptEmailRequest receiptEmailRequest1 = new ReceiptEmailRequest();
        receiptEmailRequest1.setId(1L);
        ReceiptEmailRequest receiptEmailRequest2 = new ReceiptEmailRequest();
        receiptEmailRequest2.setId(receiptEmailRequest1.getId());
        assertThat(receiptEmailRequest1).isEqualTo(receiptEmailRequest2);
        receiptEmailRequest2.setId(2L);
        assertThat(receiptEmailRequest1).isNotEqualTo(receiptEmailRequest2);
        receiptEmailRequest1.setId(null);
        assertThat(receiptEmailRequest1).isNotEqualTo(receiptEmailRequest2);
    }
}
