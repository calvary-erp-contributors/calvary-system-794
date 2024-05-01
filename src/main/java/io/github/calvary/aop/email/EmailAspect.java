package io.github.calvary.aop.email;

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
import java.util.Arrays;
import java.util.Objects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import tech.jhipster.config.JHipsterConstants;

@Aspect
public class EmailAspect {

    private static final Logger log = LoggerFactory.getLogger(EmailAspect.class);

    private final Environment env;

    public EmailAspect(Environment env) {
        this.env = env;
    }

    @AfterReturning(
        pointcut = "execution(* io.github.calvary.erp.rest.ReceiptEmailRequestResourceProd.createReceiptEmailRequest(..))",
        returning = "response"
    )
    public void intiateReceiptEmailNotificationSequence(JoinPoint joinPoint, ResponseEntity<ReceiptEmailRequestDTO> response) {
        log.info("Email requisition response intercept completed successfully");

        ReceiptEmailRequestDTO requisition = Objects.requireNonNull(response.getBody());
        String reportId = String.valueOf(requisition.getId());

        initiateEmailNotificationSequence(reportId);
    }

    @Async
    void initiateEmailNotificationSequence(String reportId) {
        log.info("Report requisition with id: {} has been registered, commencing email notification sequence...", reportId);
    }
}
