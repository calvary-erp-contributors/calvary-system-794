package io.github.calvary.erp.emails;

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

import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.dto.TransactionItemEntryDTO;
import io.github.calvary.service.dto.TransferItemEntryDTO;
import java.util.List;

public interface MailNotification {
    void sendEmailSalesReceiptNotification(
        DealerDTO recipient,
        List<TransactionItemEntryDTO> transactionItems,
        List<TransferItemEntryDTO> transferItems
    );
}
