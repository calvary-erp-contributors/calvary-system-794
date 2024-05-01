package io.github.calvary.service.criteria;

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
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.SalesReceiptEmailPersona} entity. This class is used
 * in {@link io.github.calvary.web.rest.SalesReceiptEmailPersonaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sales-receipt-email-personas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesReceiptEmailPersonaCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private UUIDFilter emailIdentifier;

    private StringFilter mainEmail;

    private BooleanFilter clearCopyEmail;

    private BooleanFilter blindCopyEmail;

    private StringFilter languageKeyCode;

    private StringFilter preferredGreeting;

    private StringFilter preferredGreetingDesignation;

    private StringFilter preferredPrefix;

    private StringFilter preferredSuffix;

    private BooleanFilter timeBasedGreetings;

    private BooleanFilter sloganBasedGreeting;

    private BooleanFilter addPrefix;

    private BooleanFilter addSuffix;

    private StringFilter preferredSignature;

    private StringFilter preferredSignatureDesignation;

    private BooleanFilter includeServiceDetails;

    private BooleanFilter includeMessageOfTheDay;

    private BooleanFilter includeTreasuryQuote;

    private ZonedDateTimeFilter createdAt;

    private ZonedDateTimeFilter lastModifedAt;

    private StringFilter personaName;

    private LongFilter createdById;

    private LongFilter lastModifiedById;

    private LongFilter contributorId;

    private Boolean distinct;

    public SalesReceiptEmailPersonaCriteria() {}

    public SalesReceiptEmailPersonaCriteria(SalesReceiptEmailPersonaCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.emailIdentifier = other.emailIdentifier == null ? null : other.emailIdentifier.copy();
        this.mainEmail = other.mainEmail == null ? null : other.mainEmail.copy();
        this.clearCopyEmail = other.clearCopyEmail == null ? null : other.clearCopyEmail.copy();
        this.blindCopyEmail = other.blindCopyEmail == null ? null : other.blindCopyEmail.copy();
        this.languageKeyCode = other.languageKeyCode == null ? null : other.languageKeyCode.copy();
        this.preferredGreeting = other.preferredGreeting == null ? null : other.preferredGreeting.copy();
        this.preferredGreetingDesignation = other.preferredGreetingDesignation == null ? null : other.preferredGreetingDesignation.copy();
        this.preferredPrefix = other.preferredPrefix == null ? null : other.preferredPrefix.copy();
        this.preferredSuffix = other.preferredSuffix == null ? null : other.preferredSuffix.copy();
        this.timeBasedGreetings = other.timeBasedGreetings == null ? null : other.timeBasedGreetings.copy();
        this.sloganBasedGreeting = other.sloganBasedGreeting == null ? null : other.sloganBasedGreeting.copy();
        this.addPrefix = other.addPrefix == null ? null : other.addPrefix.copy();
        this.addSuffix = other.addSuffix == null ? null : other.addSuffix.copy();
        this.preferredSignature = other.preferredSignature == null ? null : other.preferredSignature.copy();
        this.preferredSignatureDesignation =
            other.preferredSignatureDesignation == null ? null : other.preferredSignatureDesignation.copy();
        this.includeServiceDetails = other.includeServiceDetails == null ? null : other.includeServiceDetails.copy();
        this.includeMessageOfTheDay = other.includeMessageOfTheDay == null ? null : other.includeMessageOfTheDay.copy();
        this.includeTreasuryQuote = other.includeTreasuryQuote == null ? null : other.includeTreasuryQuote.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.lastModifedAt = other.lastModifedAt == null ? null : other.lastModifedAt.copy();
        this.personaName = other.personaName == null ? null : other.personaName.copy();
        this.createdById = other.createdById == null ? null : other.createdById.copy();
        this.lastModifiedById = other.lastModifiedById == null ? null : other.lastModifiedById.copy();
        this.contributorId = other.contributorId == null ? null : other.contributorId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SalesReceiptEmailPersonaCriteria copy() {
        return new SalesReceiptEmailPersonaCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public UUIDFilter getEmailIdentifier() {
        return emailIdentifier;
    }

    public UUIDFilter emailIdentifier() {
        if (emailIdentifier == null) {
            emailIdentifier = new UUIDFilter();
        }
        return emailIdentifier;
    }

    public void setEmailIdentifier(UUIDFilter emailIdentifier) {
        this.emailIdentifier = emailIdentifier;
    }

    public StringFilter getMainEmail() {
        return mainEmail;
    }

    public StringFilter mainEmail() {
        if (mainEmail == null) {
            mainEmail = new StringFilter();
        }
        return mainEmail;
    }

    public void setMainEmail(StringFilter mainEmail) {
        this.mainEmail = mainEmail;
    }

    public BooleanFilter getClearCopyEmail() {
        return clearCopyEmail;
    }

    public BooleanFilter clearCopyEmail() {
        if (clearCopyEmail == null) {
            clearCopyEmail = new BooleanFilter();
        }
        return clearCopyEmail;
    }

    public void setClearCopyEmail(BooleanFilter clearCopyEmail) {
        this.clearCopyEmail = clearCopyEmail;
    }

    public BooleanFilter getBlindCopyEmail() {
        return blindCopyEmail;
    }

    public BooleanFilter blindCopyEmail() {
        if (blindCopyEmail == null) {
            blindCopyEmail = new BooleanFilter();
        }
        return blindCopyEmail;
    }

    public void setBlindCopyEmail(BooleanFilter blindCopyEmail) {
        this.blindCopyEmail = blindCopyEmail;
    }

    public StringFilter getLanguageKeyCode() {
        return languageKeyCode;
    }

    public StringFilter languageKeyCode() {
        if (languageKeyCode == null) {
            languageKeyCode = new StringFilter();
        }
        return languageKeyCode;
    }

    public void setLanguageKeyCode(StringFilter languageKeyCode) {
        this.languageKeyCode = languageKeyCode;
    }

    public StringFilter getPreferredGreeting() {
        return preferredGreeting;
    }

    public StringFilter preferredGreeting() {
        if (preferredGreeting == null) {
            preferredGreeting = new StringFilter();
        }
        return preferredGreeting;
    }

    public void setPreferredGreeting(StringFilter preferredGreeting) {
        this.preferredGreeting = preferredGreeting;
    }

    public StringFilter getPreferredGreetingDesignation() {
        return preferredGreetingDesignation;
    }

    public StringFilter preferredGreetingDesignation() {
        if (preferredGreetingDesignation == null) {
            preferredGreetingDesignation = new StringFilter();
        }
        return preferredGreetingDesignation;
    }

    public void setPreferredGreetingDesignation(StringFilter preferredGreetingDesignation) {
        this.preferredGreetingDesignation = preferredGreetingDesignation;
    }

    public StringFilter getPreferredPrefix() {
        return preferredPrefix;
    }

    public StringFilter preferredPrefix() {
        if (preferredPrefix == null) {
            preferredPrefix = new StringFilter();
        }
        return preferredPrefix;
    }

    public void setPreferredPrefix(StringFilter preferredPrefix) {
        this.preferredPrefix = preferredPrefix;
    }

    public StringFilter getPreferredSuffix() {
        return preferredSuffix;
    }

    public StringFilter preferredSuffix() {
        if (preferredSuffix == null) {
            preferredSuffix = new StringFilter();
        }
        return preferredSuffix;
    }

    public void setPreferredSuffix(StringFilter preferredSuffix) {
        this.preferredSuffix = preferredSuffix;
    }

    public BooleanFilter getTimeBasedGreetings() {
        return timeBasedGreetings;
    }

    public BooleanFilter timeBasedGreetings() {
        if (timeBasedGreetings == null) {
            timeBasedGreetings = new BooleanFilter();
        }
        return timeBasedGreetings;
    }

    public void setTimeBasedGreetings(BooleanFilter timeBasedGreetings) {
        this.timeBasedGreetings = timeBasedGreetings;
    }

    public BooleanFilter getSloganBasedGreeting() {
        return sloganBasedGreeting;
    }

    public BooleanFilter sloganBasedGreeting() {
        if (sloganBasedGreeting == null) {
            sloganBasedGreeting = new BooleanFilter();
        }
        return sloganBasedGreeting;
    }

    public void setSloganBasedGreeting(BooleanFilter sloganBasedGreeting) {
        this.sloganBasedGreeting = sloganBasedGreeting;
    }

    public BooleanFilter getAddPrefix() {
        return addPrefix;
    }

    public BooleanFilter addPrefix() {
        if (addPrefix == null) {
            addPrefix = new BooleanFilter();
        }
        return addPrefix;
    }

    public void setAddPrefix(BooleanFilter addPrefix) {
        this.addPrefix = addPrefix;
    }

    public BooleanFilter getAddSuffix() {
        return addSuffix;
    }

    public BooleanFilter addSuffix() {
        if (addSuffix == null) {
            addSuffix = new BooleanFilter();
        }
        return addSuffix;
    }

    public void setAddSuffix(BooleanFilter addSuffix) {
        this.addSuffix = addSuffix;
    }

    public StringFilter getPreferredSignature() {
        return preferredSignature;
    }

    public StringFilter preferredSignature() {
        if (preferredSignature == null) {
            preferredSignature = new StringFilter();
        }
        return preferredSignature;
    }

    public void setPreferredSignature(StringFilter preferredSignature) {
        this.preferredSignature = preferredSignature;
    }

    public StringFilter getPreferredSignatureDesignation() {
        return preferredSignatureDesignation;
    }

    public StringFilter preferredSignatureDesignation() {
        if (preferredSignatureDesignation == null) {
            preferredSignatureDesignation = new StringFilter();
        }
        return preferredSignatureDesignation;
    }

    public void setPreferredSignatureDesignation(StringFilter preferredSignatureDesignation) {
        this.preferredSignatureDesignation = preferredSignatureDesignation;
    }

    public BooleanFilter getIncludeServiceDetails() {
        return includeServiceDetails;
    }

    public BooleanFilter includeServiceDetails() {
        if (includeServiceDetails == null) {
            includeServiceDetails = new BooleanFilter();
        }
        return includeServiceDetails;
    }

    public void setIncludeServiceDetails(BooleanFilter includeServiceDetails) {
        this.includeServiceDetails = includeServiceDetails;
    }

    public BooleanFilter getIncludeMessageOfTheDay() {
        return includeMessageOfTheDay;
    }

    public BooleanFilter includeMessageOfTheDay() {
        if (includeMessageOfTheDay == null) {
            includeMessageOfTheDay = new BooleanFilter();
        }
        return includeMessageOfTheDay;
    }

    public void setIncludeMessageOfTheDay(BooleanFilter includeMessageOfTheDay) {
        this.includeMessageOfTheDay = includeMessageOfTheDay;
    }

    public BooleanFilter getIncludeTreasuryQuote() {
        return includeTreasuryQuote;
    }

    public BooleanFilter includeTreasuryQuote() {
        if (includeTreasuryQuote == null) {
            includeTreasuryQuote = new BooleanFilter();
        }
        return includeTreasuryQuote;
    }

    public void setIncludeTreasuryQuote(BooleanFilter includeTreasuryQuote) {
        this.includeTreasuryQuote = includeTreasuryQuote;
    }

    public ZonedDateTimeFilter getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTimeFilter createdAt() {
        if (createdAt == null) {
            createdAt = new ZonedDateTimeFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTimeFilter createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTimeFilter getLastModifedAt() {
        return lastModifedAt;
    }

    public ZonedDateTimeFilter lastModifedAt() {
        if (lastModifedAt == null) {
            lastModifedAt = new ZonedDateTimeFilter();
        }
        return lastModifedAt;
    }

    public void setLastModifedAt(ZonedDateTimeFilter lastModifedAt) {
        this.lastModifedAt = lastModifedAt;
    }

    public StringFilter getPersonaName() {
        return personaName;
    }

    public StringFilter personaName() {
        if (personaName == null) {
            personaName = new StringFilter();
        }
        return personaName;
    }

    public void setPersonaName(StringFilter personaName) {
        this.personaName = personaName;
    }

    public LongFilter getCreatedById() {
        return createdById;
    }

    public LongFilter createdById() {
        if (createdById == null) {
            createdById = new LongFilter();
        }
        return createdById;
    }

    public void setCreatedById(LongFilter createdById) {
        this.createdById = createdById;
    }

    public LongFilter getLastModifiedById() {
        return lastModifiedById;
    }

    public LongFilter lastModifiedById() {
        if (lastModifiedById == null) {
            lastModifiedById = new LongFilter();
        }
        return lastModifiedById;
    }

    public void setLastModifiedById(LongFilter lastModifiedById) {
        this.lastModifiedById = lastModifiedById;
    }

    public LongFilter getContributorId() {
        return contributorId;
    }

    public LongFilter contributorId() {
        if (contributorId == null) {
            contributorId = new LongFilter();
        }
        return contributorId;
    }

    public void setContributorId(LongFilter contributorId) {
        this.contributorId = contributorId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SalesReceiptEmailPersonaCriteria that = (SalesReceiptEmailPersonaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(emailIdentifier, that.emailIdentifier) &&
            Objects.equals(mainEmail, that.mainEmail) &&
            Objects.equals(clearCopyEmail, that.clearCopyEmail) &&
            Objects.equals(blindCopyEmail, that.blindCopyEmail) &&
            Objects.equals(languageKeyCode, that.languageKeyCode) &&
            Objects.equals(preferredGreeting, that.preferredGreeting) &&
            Objects.equals(preferredGreetingDesignation, that.preferredGreetingDesignation) &&
            Objects.equals(preferredPrefix, that.preferredPrefix) &&
            Objects.equals(preferredSuffix, that.preferredSuffix) &&
            Objects.equals(timeBasedGreetings, that.timeBasedGreetings) &&
            Objects.equals(sloganBasedGreeting, that.sloganBasedGreeting) &&
            Objects.equals(addPrefix, that.addPrefix) &&
            Objects.equals(addSuffix, that.addSuffix) &&
            Objects.equals(preferredSignature, that.preferredSignature) &&
            Objects.equals(preferredSignatureDesignation, that.preferredSignatureDesignation) &&
            Objects.equals(includeServiceDetails, that.includeServiceDetails) &&
            Objects.equals(includeMessageOfTheDay, that.includeMessageOfTheDay) &&
            Objects.equals(includeTreasuryQuote, that.includeTreasuryQuote) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(lastModifedAt, that.lastModifedAt) &&
            Objects.equals(personaName, that.personaName) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(lastModifiedById, that.lastModifiedById) &&
            Objects.equals(contributorId, that.contributorId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            emailIdentifier,
            mainEmail,
            clearCopyEmail,
            blindCopyEmail,
            languageKeyCode,
            preferredGreeting,
            preferredGreetingDesignation,
            preferredPrefix,
            preferredSuffix,
            timeBasedGreetings,
            sloganBasedGreeting,
            addPrefix,
            addSuffix,
            preferredSignature,
            preferredSignatureDesignation,
            includeServiceDetails,
            includeMessageOfTheDay,
            includeTreasuryQuote,
            createdAt,
            lastModifedAt,
            personaName,
            createdById,
            lastModifiedById,
            contributorId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesReceiptEmailPersonaCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (emailIdentifier != null ? "emailIdentifier=" + emailIdentifier + ", " : "") +
            (mainEmail != null ? "mainEmail=" + mainEmail + ", " : "") +
            (clearCopyEmail != null ? "clearCopyEmail=" + clearCopyEmail + ", " : "") +
            (blindCopyEmail != null ? "blindCopyEmail=" + blindCopyEmail + ", " : "") +
            (languageKeyCode != null ? "languageKeyCode=" + languageKeyCode + ", " : "") +
            (preferredGreeting != null ? "preferredGreeting=" + preferredGreeting + ", " : "") +
            (preferredGreetingDesignation != null ? "preferredGreetingDesignation=" + preferredGreetingDesignation + ", " : "") +
            (preferredPrefix != null ? "preferredPrefix=" + preferredPrefix + ", " : "") +
            (preferredSuffix != null ? "preferredSuffix=" + preferredSuffix + ", " : "") +
            (timeBasedGreetings != null ? "timeBasedGreetings=" + timeBasedGreetings + ", " : "") +
            (sloganBasedGreeting != null ? "sloganBasedGreeting=" + sloganBasedGreeting + ", " : "") +
            (addPrefix != null ? "addPrefix=" + addPrefix + ", " : "") +
            (addSuffix != null ? "addSuffix=" + addSuffix + ", " : "") +
            (preferredSignature != null ? "preferredSignature=" + preferredSignature + ", " : "") +
            (preferredSignatureDesignation != null ? "preferredSignatureDesignation=" + preferredSignatureDesignation + ", " : "") +
            (includeServiceDetails != null ? "includeServiceDetails=" + includeServiceDetails + ", " : "") +
            (includeMessageOfTheDay != null ? "includeMessageOfTheDay=" + includeMessageOfTheDay + ", " : "") +
            (includeTreasuryQuote != null ? "includeTreasuryQuote=" + includeTreasuryQuote + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (lastModifedAt != null ? "lastModifedAt=" + lastModifedAt + ", " : "") +
            (personaName != null ? "personaName=" + personaName + ", " : "") +
            (createdById != null ? "createdById=" + createdById + ", " : "") +
            (lastModifiedById != null ? "lastModifiedById=" + lastModifiedById + ", " : "") +
            (contributorId != null ? "contributorId=" + contributorId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
