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

class BalanceSheetItemValueDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BalanceSheetItemValueDTO.class);
        BalanceSheetItemValueDTO balanceSheetItemValueDTO1 = new BalanceSheetItemValueDTO();
        balanceSheetItemValueDTO1.setId(1L);
        BalanceSheetItemValueDTO balanceSheetItemValueDTO2 = new BalanceSheetItemValueDTO();
        assertThat(balanceSheetItemValueDTO1).isNotEqualTo(balanceSheetItemValueDTO2);
        balanceSheetItemValueDTO2.setId(balanceSheetItemValueDTO1.getId());
        assertThat(balanceSheetItemValueDTO1).isEqualTo(balanceSheetItemValueDTO2);
        balanceSheetItemValueDTO2.setId(2L);
        assertThat(balanceSheetItemValueDTO1).isNotEqualTo(balanceSheetItemValueDTO2);
        balanceSheetItemValueDTO1.setId(null);
        assertThat(balanceSheetItemValueDTO1).isNotEqualTo(balanceSheetItemValueDTO2);
    }
}
