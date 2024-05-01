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
 * Criteria class for the {@link io.github.calvary.domain.ApplicationUser} entity. This class is used
 * in {@link io.github.calvary.web.rest.ApplicationUserResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /application-users?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApplicationUserCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter applicationIdentity;

    private ZonedDateTimeFilter lastLoginTime;

    private ZonedDateTimeFilter timeOfCreation;

    private ZonedDateTimeFilter lastTimeOfModification;

    private UUIDFilter userIdentifier;

    private LongFilter createdById;

    private LongFilter lastModifiedById;

    private LongFilter systemIdentityId;

    private Boolean distinct;

    public ApplicationUserCriteria() {}

    public ApplicationUserCriteria(ApplicationUserCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.applicationIdentity = other.applicationIdentity == null ? null : other.applicationIdentity.copy();
        this.lastLoginTime = other.lastLoginTime == null ? null : other.lastLoginTime.copy();
        this.timeOfCreation = other.timeOfCreation == null ? null : other.timeOfCreation.copy();
        this.lastTimeOfModification = other.lastTimeOfModification == null ? null : other.lastTimeOfModification.copy();
        this.userIdentifier = other.userIdentifier == null ? null : other.userIdentifier.copy();
        this.createdById = other.createdById == null ? null : other.createdById.copy();
        this.lastModifiedById = other.lastModifiedById == null ? null : other.lastModifiedById.copy();
        this.systemIdentityId = other.systemIdentityId == null ? null : other.systemIdentityId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ApplicationUserCriteria copy() {
        return new ApplicationUserCriteria(this);
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

    public StringFilter getApplicationIdentity() {
        return applicationIdentity;
    }

    public StringFilter applicationIdentity() {
        if (applicationIdentity == null) {
            applicationIdentity = new StringFilter();
        }
        return applicationIdentity;
    }

    public void setApplicationIdentity(StringFilter applicationIdentity) {
        this.applicationIdentity = applicationIdentity;
    }

    public ZonedDateTimeFilter getLastLoginTime() {
        return lastLoginTime;
    }

    public ZonedDateTimeFilter lastLoginTime() {
        if (lastLoginTime == null) {
            lastLoginTime = new ZonedDateTimeFilter();
        }
        return lastLoginTime;
    }

    public void setLastLoginTime(ZonedDateTimeFilter lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public ZonedDateTimeFilter getTimeOfCreation() {
        return timeOfCreation;
    }

    public ZonedDateTimeFilter timeOfCreation() {
        if (timeOfCreation == null) {
            timeOfCreation = new ZonedDateTimeFilter();
        }
        return timeOfCreation;
    }

    public void setTimeOfCreation(ZonedDateTimeFilter timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public ZonedDateTimeFilter getLastTimeOfModification() {
        return lastTimeOfModification;
    }

    public ZonedDateTimeFilter lastTimeOfModification() {
        if (lastTimeOfModification == null) {
            lastTimeOfModification = new ZonedDateTimeFilter();
        }
        return lastTimeOfModification;
    }

    public void setLastTimeOfModification(ZonedDateTimeFilter lastTimeOfModification) {
        this.lastTimeOfModification = lastTimeOfModification;
    }

    public UUIDFilter getUserIdentifier() {
        return userIdentifier;
    }

    public UUIDFilter userIdentifier() {
        if (userIdentifier == null) {
            userIdentifier = new UUIDFilter();
        }
        return userIdentifier;
    }

    public void setUserIdentifier(UUIDFilter userIdentifier) {
        this.userIdentifier = userIdentifier;
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

    public LongFilter getSystemIdentityId() {
        return systemIdentityId;
    }

    public LongFilter systemIdentityId() {
        if (systemIdentityId == null) {
            systemIdentityId = new LongFilter();
        }
        return systemIdentityId;
    }

    public void setSystemIdentityId(LongFilter systemIdentityId) {
        this.systemIdentityId = systemIdentityId;
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
        final ApplicationUserCriteria that = (ApplicationUserCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(applicationIdentity, that.applicationIdentity) &&
            Objects.equals(lastLoginTime, that.lastLoginTime) &&
            Objects.equals(timeOfCreation, that.timeOfCreation) &&
            Objects.equals(lastTimeOfModification, that.lastTimeOfModification) &&
            Objects.equals(userIdentifier, that.userIdentifier) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(lastModifiedById, that.lastModifiedById) &&
            Objects.equals(systemIdentityId, that.systemIdentityId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            applicationIdentity,
            lastLoginTime,
            timeOfCreation,
            lastTimeOfModification,
            userIdentifier,
            createdById,
            lastModifiedById,
            systemIdentityId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplicationUserCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (applicationIdentity != null ? "applicationIdentity=" + applicationIdentity + ", " : "") +
            (lastLoginTime != null ? "lastLoginTime=" + lastLoginTime + ", " : "") +
            (timeOfCreation != null ? "timeOfCreation=" + timeOfCreation + ", " : "") +
            (lastTimeOfModification != null ? "lastTimeOfModification=" + lastTimeOfModification + ", " : "") +
            (userIdentifier != null ? "userIdentifier=" + userIdentifier + ", " : "") +
            (createdById != null ? "createdById=" + createdById + ", " : "") +
            (lastModifiedById != null ? "lastModifiedById=" + lastModifiedById + ", " : "") +
            (systemIdentityId != null ? "systemIdentityId=" + systemIdentityId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
