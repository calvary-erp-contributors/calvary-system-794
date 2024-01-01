package io.github.calvary.service.dto;

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
