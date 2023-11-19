package io.github.calvary.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TransactionAccount.
 */
@Entity
@Table(name = "transaction_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transactionaccount")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "account_name", nullable = false, unique = true)
    private String accountName;

    @Column(name = "account_number", unique = true)
    private String accountNumber;

    @Column(name = "opening_balance", precision = 21, scale = 2)
    private BigDecimal openingBalance;

    @ManyToOne
    @JsonIgnoreProperties(value = { "parentAccount", "transactionAccountType", "transactionCurrency" }, allowSetters = true)
    private TransactionAccount parentAccount;

    @ManyToOne(optional = false)
    @NotNull
    private TransactionAccountType transactionAccountType;

    @ManyToOne(optional = false)
    @NotNull
    private TransactionCurrency transactionCurrency;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TransactionAccount id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public TransactionAccount accountName(String accountName) {
        this.setAccountName(accountName);
        return this;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public TransactionAccount accountNumber(String accountNumber) {
        this.setAccountNumber(accountNumber);
        return this;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getOpeningBalance() {
        return this.openingBalance;
    }

    public TransactionAccount openingBalance(BigDecimal openingBalance) {
        this.setOpeningBalance(openingBalance);
        return this;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public TransactionAccount getParentAccount() {
        return this.parentAccount;
    }

    public void setParentAccount(TransactionAccount transactionAccount) {
        this.parentAccount = transactionAccount;
    }

    public TransactionAccount parentAccount(TransactionAccount transactionAccount) {
        this.setParentAccount(transactionAccount);
        return this;
    }

    public TransactionAccountType getTransactionAccountType() {
        return this.transactionAccountType;
    }

    public void setTransactionAccountType(TransactionAccountType transactionAccountType) {
        this.transactionAccountType = transactionAccountType;
    }

    public TransactionAccount transactionAccountType(TransactionAccountType transactionAccountType) {
        this.setTransactionAccountType(transactionAccountType);
        return this;
    }

    public TransactionCurrency getTransactionCurrency() {
        return this.transactionCurrency;
    }

    public void setTransactionCurrency(TransactionCurrency transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    public TransactionAccount transactionCurrency(TransactionCurrency transactionCurrency) {
        this.setTransactionCurrency(transactionCurrency);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionAccount)) {
            return false;
        }
        return id != null && id.equals(((TransactionAccount) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionAccount{" +
            "id=" + getId() +
            ", accountName='" + getAccountName() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", openingBalance=" + getOpeningBalance() +
            "}";
    }
}
