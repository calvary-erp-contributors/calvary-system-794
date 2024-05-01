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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SalesReceiptEmailPersona.
 */
@Entity
@Table(name = "sales_receipt_email_persona")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "salesreceiptemailpersona")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptEmailPersona implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "email_identifier", nullable = false, unique = true)
    private UUID emailIdentifier;

    @NotNull
    @Column(name = "main_email", nullable = false)
    private String mainEmail;

    @Column(name = "clear_copy_email")
    private Boolean clearCopyEmail;

    @Column(name = "blind_copy_email")
    private Boolean blindCopyEmail;

    @NotNull
    @Pattern(regexp = "(^[a-z][a-z]*$)")
    @Column(name = "language_key_code", nullable = false)
    private String languageKeyCode;

    @NotNull
    @Column(name = "preferred_greeting", nullable = false)
    private String preferredGreeting;

    @Column(name = "preferred_greeting_designation")
    private String preferredGreetingDesignation;

    @Column(name = "preferred_prefix")
    private String preferredPrefix;

    @Column(name = "preferred_suffix")
    private String preferredSuffix;

    @Column(name = "time_based_greetings")
    private Boolean timeBasedGreetings;

    @Column(name = "slogan_based_greeting")
    private Boolean sloganBasedGreeting;

    @Column(name = "add_prefix")
    private Boolean addPrefix;

    @Column(name = "add_suffix")
    private Boolean addSuffix;

    @NotNull
    @Column(name = "preferred_signature", nullable = false)
    private String preferredSignature;

    @NotNull
    @Column(name = "preferred_signature_designation", nullable = false)
    private String preferredSignatureDesignation;

    @Column(name = "include_service_details")
    private Boolean includeServiceDetails;

    @Column(name = "include_message_of_the_day")
    private Boolean includeMessageOfTheDay;

    @Column(name = "include_treasury_quote")
    private Boolean includeTreasuryQuote;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "last_modifed_at")
    private ZonedDateTime lastModifedAt;

    @NotNull
    @Column(name = "persona_name", nullable = false, unique = true)
    private String personaName;

    @ManyToOne
    @JsonIgnoreProperties(value = { "createdBy", "lastModifiedBy", "systemIdentity" }, allowSetters = true)
    private ApplicationUser createdBy;

    @ManyToOne
    @JsonIgnoreProperties(value = { "createdBy", "lastModifiedBy", "systemIdentity" }, allowSetters = true)
    private ApplicationUser lastModifiedBy;

    @ManyToOne
    @JsonIgnoreProperties(value = { "dealerType", "salesReceiptEmailPersonas" }, allowSetters = true)
    private Dealer contributor;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SalesReceiptEmailPersona id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getEmailIdentifier() {
        return this.emailIdentifier;
    }

    public SalesReceiptEmailPersona emailIdentifier(UUID emailIdentifier) {
        this.setEmailIdentifier(emailIdentifier);
        return this;
    }

    public void setEmailIdentifier(UUID emailIdentifier) {
        this.emailIdentifier = emailIdentifier;
    }

    public String getMainEmail() {
        return this.mainEmail;
    }

    public SalesReceiptEmailPersona mainEmail(String mainEmail) {
        this.setMainEmail(mainEmail);
        return this;
    }

    public void setMainEmail(String mainEmail) {
        this.mainEmail = mainEmail;
    }

    public Boolean getClearCopyEmail() {
        return this.clearCopyEmail;
    }

    public SalesReceiptEmailPersona clearCopyEmail(Boolean clearCopyEmail) {
        this.setClearCopyEmail(clearCopyEmail);
        return this;
    }

    public void setClearCopyEmail(Boolean clearCopyEmail) {
        this.clearCopyEmail = clearCopyEmail;
    }

    public Boolean getBlindCopyEmail() {
        return this.blindCopyEmail;
    }

    public SalesReceiptEmailPersona blindCopyEmail(Boolean blindCopyEmail) {
        this.setBlindCopyEmail(blindCopyEmail);
        return this;
    }

    public void setBlindCopyEmail(Boolean blindCopyEmail) {
        this.blindCopyEmail = blindCopyEmail;
    }

    public String getLanguageKeyCode() {
        return this.languageKeyCode;
    }

    public SalesReceiptEmailPersona languageKeyCode(String languageKeyCode) {
        this.setLanguageKeyCode(languageKeyCode);
        return this;
    }

    public void setLanguageKeyCode(String languageKeyCode) {
        this.languageKeyCode = languageKeyCode;
    }

    public String getPreferredGreeting() {
        return this.preferredGreeting;
    }

    public SalesReceiptEmailPersona preferredGreeting(String preferredGreeting) {
        this.setPreferredGreeting(preferredGreeting);
        return this;
    }

    public void setPreferredGreeting(String preferredGreeting) {
        this.preferredGreeting = preferredGreeting;
    }

    public String getPreferredGreetingDesignation() {
        return this.preferredGreetingDesignation;
    }

    public SalesReceiptEmailPersona preferredGreetingDesignation(String preferredGreetingDesignation) {
        this.setPreferredGreetingDesignation(preferredGreetingDesignation);
        return this;
    }

    public void setPreferredGreetingDesignation(String preferredGreetingDesignation) {
        this.preferredGreetingDesignation = preferredGreetingDesignation;
    }

    public String getPreferredPrefix() {
        return this.preferredPrefix;
    }

    public SalesReceiptEmailPersona preferredPrefix(String preferredPrefix) {
        this.setPreferredPrefix(preferredPrefix);
        return this;
    }

    public void setPreferredPrefix(String preferredPrefix) {
        this.preferredPrefix = preferredPrefix;
    }

    public String getPreferredSuffix() {
        return this.preferredSuffix;
    }

    public SalesReceiptEmailPersona preferredSuffix(String preferredSuffix) {
        this.setPreferredSuffix(preferredSuffix);
        return this;
    }

    public void setPreferredSuffix(String preferredSuffix) {
        this.preferredSuffix = preferredSuffix;
    }

    public Boolean getTimeBasedGreetings() {
        return this.timeBasedGreetings;
    }

    public SalesReceiptEmailPersona timeBasedGreetings(Boolean timeBasedGreetings) {
        this.setTimeBasedGreetings(timeBasedGreetings);
        return this;
    }

    public void setTimeBasedGreetings(Boolean timeBasedGreetings) {
        this.timeBasedGreetings = timeBasedGreetings;
    }

    public Boolean getSloganBasedGreeting() {
        return this.sloganBasedGreeting;
    }

    public SalesReceiptEmailPersona sloganBasedGreeting(Boolean sloganBasedGreeting) {
        this.setSloganBasedGreeting(sloganBasedGreeting);
        return this;
    }

    public void setSloganBasedGreeting(Boolean sloganBasedGreeting) {
        this.sloganBasedGreeting = sloganBasedGreeting;
    }

    public Boolean getAddPrefix() {
        return this.addPrefix;
    }

    public SalesReceiptEmailPersona addPrefix(Boolean addPrefix) {
        this.setAddPrefix(addPrefix);
        return this;
    }

    public void setAddPrefix(Boolean addPrefix) {
        this.addPrefix = addPrefix;
    }

    public Boolean getAddSuffix() {
        return this.addSuffix;
    }

    public SalesReceiptEmailPersona addSuffix(Boolean addSuffix) {
        this.setAddSuffix(addSuffix);
        return this;
    }

    public void setAddSuffix(Boolean addSuffix) {
        this.addSuffix = addSuffix;
    }

    public String getPreferredSignature() {
        return this.preferredSignature;
    }

    public SalesReceiptEmailPersona preferredSignature(String preferredSignature) {
        this.setPreferredSignature(preferredSignature);
        return this;
    }

    public void setPreferredSignature(String preferredSignature) {
        this.preferredSignature = preferredSignature;
    }

    public String getPreferredSignatureDesignation() {
        return this.preferredSignatureDesignation;
    }

    public SalesReceiptEmailPersona preferredSignatureDesignation(String preferredSignatureDesignation) {
        this.setPreferredSignatureDesignation(preferredSignatureDesignation);
        return this;
    }

    public void setPreferredSignatureDesignation(String preferredSignatureDesignation) {
        this.preferredSignatureDesignation = preferredSignatureDesignation;
    }

    public Boolean getIncludeServiceDetails() {
        return this.includeServiceDetails;
    }

    public SalesReceiptEmailPersona includeServiceDetails(Boolean includeServiceDetails) {
        this.setIncludeServiceDetails(includeServiceDetails);
        return this;
    }

    public void setIncludeServiceDetails(Boolean includeServiceDetails) {
        this.includeServiceDetails = includeServiceDetails;
    }

    public Boolean getIncludeMessageOfTheDay() {
        return this.includeMessageOfTheDay;
    }

    public SalesReceiptEmailPersona includeMessageOfTheDay(Boolean includeMessageOfTheDay) {
        this.setIncludeMessageOfTheDay(includeMessageOfTheDay);
        return this;
    }

    public void setIncludeMessageOfTheDay(Boolean includeMessageOfTheDay) {
        this.includeMessageOfTheDay = includeMessageOfTheDay;
    }

    public Boolean getIncludeTreasuryQuote() {
        return this.includeTreasuryQuote;
    }

    public SalesReceiptEmailPersona includeTreasuryQuote(Boolean includeTreasuryQuote) {
        this.setIncludeTreasuryQuote(includeTreasuryQuote);
        return this;
    }

    public void setIncludeTreasuryQuote(Boolean includeTreasuryQuote) {
        this.includeTreasuryQuote = includeTreasuryQuote;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public SalesReceiptEmailPersona createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getLastModifedAt() {
        return this.lastModifedAt;
    }

    public SalesReceiptEmailPersona lastModifedAt(ZonedDateTime lastModifedAt) {
        this.setLastModifedAt(lastModifedAt);
        return this;
    }

    public void setLastModifedAt(ZonedDateTime lastModifedAt) {
        this.lastModifedAt = lastModifedAt;
    }

    public String getPersonaName() {
        return this.personaName;
    }

    public SalesReceiptEmailPersona personaName(String personaName) {
        this.setPersonaName(personaName);
        return this;
    }

    public void setPersonaName(String personaName) {
        this.personaName = personaName;
    }

    public ApplicationUser getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(ApplicationUser applicationUser) {
        this.createdBy = applicationUser;
    }

    public SalesReceiptEmailPersona createdBy(ApplicationUser applicationUser) {
        this.setCreatedBy(applicationUser);
        return this;
    }

    public ApplicationUser getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public void setLastModifiedBy(ApplicationUser applicationUser) {
        this.lastModifiedBy = applicationUser;
    }

    public SalesReceiptEmailPersona lastModifiedBy(ApplicationUser applicationUser) {
        this.setLastModifiedBy(applicationUser);
        return this;
    }

    public Dealer getContributor() {
        return this.contributor;
    }

    public void setContributor(Dealer dealer) {
        this.contributor = dealer;
    }

    public SalesReceiptEmailPersona contributor(Dealer dealer) {
        this.setContributor(dealer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesReceiptEmailPersona)) {
            return false;
        }
        return id != null && id.equals(((SalesReceiptEmailPersona) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptEmailPersona{" +
            "id=" + getId() +
            ", emailIdentifier='" + getEmailIdentifier() + "'" +
            ", mainEmail='" + getMainEmail() + "'" +
            ", clearCopyEmail='" + getClearCopyEmail() + "'" +
            ", blindCopyEmail='" + getBlindCopyEmail() + "'" +
            ", languageKeyCode='" + getLanguageKeyCode() + "'" +
            ", preferredGreeting='" + getPreferredGreeting() + "'" +
            ", preferredGreetingDesignation='" + getPreferredGreetingDesignation() + "'" +
            ", preferredPrefix='" + getPreferredPrefix() + "'" +
            ", preferredSuffix='" + getPreferredSuffix() + "'" +
            ", timeBasedGreetings='" + getTimeBasedGreetings() + "'" +
            ", sloganBasedGreeting='" + getSloganBasedGreeting() + "'" +
            ", addPrefix='" + getAddPrefix() + "'" +
            ", addSuffix='" + getAddSuffix() + "'" +
            ", preferredSignature='" + getPreferredSignature() + "'" +
            ", preferredSignatureDesignation='" + getPreferredSignatureDesignation() + "'" +
            ", includeServiceDetails='" + getIncludeServiceDetails() + "'" +
            ", includeMessageOfTheDay='" + getIncludeMessageOfTheDay() + "'" +
            ", includeTreasuryQuote='" + getIncludeTreasuryQuote() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", lastModifedAt='" + getLastModifedAt() + "'" +
            ", personaName='" + getPersonaName() + "'" +
            "}";
    }
}
