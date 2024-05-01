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

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link io.github.calvary.domain.SalesReceiptCompilation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptCompilationDTO implements Serializable {

    private Long id;

    private Instant timeOfCompilation;

    private UUID compilationIdentifier;

    private Integer receiptsCompiled;

    private ApplicationUserDTO compiledBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimeOfCompilation() {
        return timeOfCompilation;
    }

    public void setTimeOfCompilation(Instant timeOfCompilation) {
        this.timeOfCompilation = timeOfCompilation;
    }

    public UUID getCompilationIdentifier() {
        return compilationIdentifier;
    }

    public void setCompilationIdentifier(UUID compilationIdentifier) {
        this.compilationIdentifier = compilationIdentifier;
    }

    public Integer getReceiptsCompiled() {
        return receiptsCompiled;
    }

    public void setReceiptsCompiled(Integer receiptsCompiled) {
        this.receiptsCompiled = receiptsCompiled;
    }

    public ApplicationUserDTO getCompiledBy() {
        return compiledBy;
    }

    public void setCompiledBy(ApplicationUserDTO compiledBy) {
        this.compiledBy = compiledBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesReceiptCompilationDTO)) {
            return false;
        }

        SalesReceiptCompilationDTO salesReceiptCompilationDTO = (SalesReceiptCompilationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, salesReceiptCompilationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptCompilationDTO{" +
            "id=" + getId() +
            ", timeOfCompilation='" + getTimeOfCompilation() + "'" +
            ", compilationIdentifier='" + getCompilationIdentifier() + "'" +
            ", receiptsCompiled=" + getReceiptsCompiled() +
            ", compiledBy=" + getCompiledBy() +
            "}";
    }
}
