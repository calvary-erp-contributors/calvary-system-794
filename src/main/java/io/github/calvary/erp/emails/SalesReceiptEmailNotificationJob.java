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

import io.github.calvary.service.dto.ReceiptEmailRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service calls the services to check which receipts ought to be notified on and then
 * populates such a list to send in a notification loop, using the SaleReceiptEmailService interface
 */
public interface SalesReceiptEmailNotificationJob {
    ReceiptEmailRequestDTO runNotificationJob(ReceiptEmailRequestDTO requisition);
}
