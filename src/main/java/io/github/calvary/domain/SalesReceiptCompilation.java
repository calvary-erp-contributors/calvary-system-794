package io.github.calvary.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SalesReceiptCompilation.
 */
@Entity
@Table(name = "sales_receipt_compilation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "salesreceiptcompilation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptCompilation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "time_of_compilation")
    private Instant timeOfCompilation;

    @Column(name = "compilation_identifier")
    private UUID compilationIdentifier;

    @Column(name = "receipts_compiled")
    private Integer receiptsCompiled;

    @ManyToOne
    @JsonIgnoreProperties(value = { "createdBy", "lastModifiedBy", "systemIdentity" }, allowSetters = true)
    private ApplicationUser compiledBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SalesReceiptCompilation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimeOfCompilation() {
        return this.timeOfCompilation;
    }

    public SalesReceiptCompilation timeOfCompilation(Instant timeOfCompilation) {
        this.setTimeOfCompilation(timeOfCompilation);
        return this;
    }

    public void setTimeOfCompilation(Instant timeOfCompilation) {
        this.timeOfCompilation = timeOfCompilation;
    }

    public UUID getCompilationIdentifier() {
        return this.compilationIdentifier;
    }

    public SalesReceiptCompilation compilationIdentifier(UUID compilationIdentifier) {
        this.setCompilationIdentifier(compilationIdentifier);
        return this;
    }

    public void setCompilationIdentifier(UUID compilationIdentifier) {
        this.compilationIdentifier = compilationIdentifier;
    }

    public Integer getReceiptsCompiled() {
        return this.receiptsCompiled;
    }

    public SalesReceiptCompilation receiptsCompiled(Integer receiptsCompiled) {
        this.setReceiptsCompiled(receiptsCompiled);
        return this;
    }

    public void setReceiptsCompiled(Integer receiptsCompiled) {
        this.receiptsCompiled = receiptsCompiled;
    }

    public ApplicationUser getCompiledBy() {
        return this.compiledBy;
    }

    public void setCompiledBy(ApplicationUser applicationUser) {
        this.compiledBy = applicationUser;
    }

    public SalesReceiptCompilation compiledBy(ApplicationUser applicationUser) {
        this.setCompiledBy(applicationUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesReceiptCompilation)) {
            return false;
        }
        return id != null && id.equals(((SalesReceiptCompilation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptCompilation{" +
            "id=" + getId() +
            ", timeOfCompilation='" + getTimeOfCompilation() + "'" +
            ", compilationIdentifier='" + getCompilationIdentifier() + "'" +
            ", receiptsCompiled=" + getReceiptsCompiled() +
            "}";
    }
}
