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
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.SalesReceiptEmailPersona} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptEmailPersonaDTO implements Serializable {

    private Long id;

    @NotNull
    private UUID emailIdentifier;

    @NotNull
    private String mainEmail;

    private Boolean clearCopyEmail;

    private Boolean blindCopyEmail;

    @NotNull
    @Pattern(regexp = "(^[a-z][a-z]*$)")
    private String languageKeyCode;

    @NotNull
    private String preferredGreeting;

    private String preferredGreetingDesignation;

    private String preferredPrefix;

    private String preferredSuffix;

    private Boolean timeBasedGreetings;

    private Boolean sloganBasedGreeting;

    private Boolean addPrefix;

    private Boolean addSuffix;

    @NotNull
    private String preferredSignature;

    @NotNull
    private String preferredSignatureDesignation;

    private Boolean includeServiceDetails;

    private Boolean includeMessageOfTheDay;

    private Boolean includeTreasuryQuote;

    @NotNull
    private ZonedDateTime createdAt;

    private ZonedDateTime lastModifedAt;

    @NotNull
    private String personaName;

    private ApplicationUserDTO createdBy;

    private ApplicationUserDTO lastModifiedBy;

    private DealerDTO contributor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getEmailIdentifier() {
        return emailIdentifier;
    }

    public void setEmailIdentifier(UUID emailIdentifier) {
        this.emailIdentifier = emailIdentifier;
    }

    public String getMainEmail() {
        return mainEmail;
    }

    public void setMainEmail(String mainEmail) {
        this.mainEmail = mainEmail;
    }

    public Boolean getClearCopyEmail() {
        return clearCopyEmail;
    }

    public void setClearCopyEmail(Boolean clearCopyEmail) {
        this.clearCopyEmail = clearCopyEmail;
    }

    public Boolean getBlindCopyEmail() {
        return blindCopyEmail;
    }

    public void setBlindCopyEmail(Boolean blindCopyEmail) {
        this.blindCopyEmail = blindCopyEmail;
    }

    public String getLanguageKeyCode() {
        return languageKeyCode;
    }

    public void setLanguageKeyCode(String languageKeyCode) {
        this.languageKeyCode = languageKeyCode;
    }

    public String getPreferredGreeting() {
        return preferredGreeting;
    }

    public void setPreferredGreeting(String preferredGreeting) {
        this.preferredGreeting = preferredGreeting;
    }

    public String getPreferredGreetingDesignation() {
        return preferredGreetingDesignation;
    }

    public void setPreferredGreetingDesignation(String preferredGreetingDesignation) {
        this.preferredGreetingDesignation = preferredGreetingDesignation;
    }

    public String getPreferredPrefix() {
        return preferredPrefix;
    }

    public void setPreferredPrefix(String preferredPrefix) {
        this.preferredPrefix = preferredPrefix;
    }

    public String getPreferredSuffix() {
        return preferredSuffix;
    }

    public void setPreferredSuffix(String preferredSuffix) {
        this.preferredSuffix = preferredSuffix;
    }

    public Boolean getTimeBasedGreetings() {
        return timeBasedGreetings;
    }

    public void setTimeBasedGreetings(Boolean timeBasedGreetings) {
        this.timeBasedGreetings = timeBasedGreetings;
    }

    public Boolean getSloganBasedGreeting() {
        return sloganBasedGreeting;
    }

    public void setSloganBasedGreeting(Boolean sloganBasedGreeting) {
        this.sloganBasedGreeting = sloganBasedGreeting;
    }

    public Boolean getAddPrefix() {
        return addPrefix;
    }

    public void setAddPrefix(Boolean addPrefix) {
        this.addPrefix = addPrefix;
    }

    public Boolean getAddSuffix() {
        return addSuffix;
    }

    public void setAddSuffix(Boolean addSuffix) {
        this.addSuffix = addSuffix;
    }

    public String getPreferredSignature() {
        return preferredSignature;
    }

    public void setPreferredSignature(String preferredSignature) {
        this.preferredSignature = preferredSignature;
    }

    public String getPreferredSignatureDesignation() {
        return preferredSignatureDesignation;
    }

    public void setPreferredSignatureDesignation(String preferredSignatureDesignation) {
        this.preferredSignatureDesignation = preferredSignatureDesignation;
    }

    public Boolean getIncludeServiceDetails() {
        return includeServiceDetails;
    }

    public void setIncludeServiceDetails(Boolean includeServiceDetails) {
        this.includeServiceDetails = includeServiceDetails;
    }

    public Boolean getIncludeMessageOfTheDay() {
        return includeMessageOfTheDay;
    }

    public void setIncludeMessageOfTheDay(Boolean includeMessageOfTheDay) {
        this.includeMessageOfTheDay = includeMessageOfTheDay;
    }

    public Boolean getIncludeTreasuryQuote() {
        return includeTreasuryQuote;
    }

    public void setIncludeTreasuryQuote(Boolean includeTreasuryQuote) {
        this.includeTreasuryQuote = includeTreasuryQuote;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getLastModifedAt() {
        return lastModifedAt;
    }

    public void setLastModifedAt(ZonedDateTime lastModifedAt) {
        this.lastModifedAt = lastModifedAt;
    }

    public String getPersonaName() {
        return personaName;
    }

    public void setPersonaName(String personaName) {
        this.personaName = personaName;
    }

    public ApplicationUserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ApplicationUserDTO createdBy) {
        this.createdBy = createdBy;
    }

    public ApplicationUserDTO getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(ApplicationUserDTO lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public DealerDTO getContributor() {
        return contributor;
    }

    public void setContributor(DealerDTO contributor) {
        this.contributor = contributor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesReceiptEmailPersonaDTO)) {
            return false;
        }

        SalesReceiptEmailPersonaDTO salesReceiptEmailPersonaDTO = (SalesReceiptEmailPersonaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, salesReceiptEmailPersonaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptEmailPersonaDTO{" +
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
            ", createdBy=" + getCreatedBy() +
            ", lastModifiedBy=" + getLastModifiedBy() +
            ", contributor=" + getContributor() +
            "}";
    }
}
