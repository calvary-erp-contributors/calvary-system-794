package io.github.calvary.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.calvary.domain.ApplicationUser} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApplicationUserDTO implements Serializable {

    private Long id;

    @NotNull
    private String applicationIdentity;

    private ZonedDateTime lastLoginTime;

    @NotNull
    private ZonedDateTime timeOfCreation;

    private ZonedDateTime lastTimeOfModification;

    @NotNull
    private UUID userIdentifier;

    private ApplicationUserDTO createdBy;

    private ApplicationUserDTO lastModifiedBy;

    private UserDTO systemIdentity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationIdentity() {
        return applicationIdentity;
    }

    public void setApplicationIdentity(String applicationIdentity) {
        this.applicationIdentity = applicationIdentity;
    }

    public ZonedDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(ZonedDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public ZonedDateTime getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(ZonedDateTime timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public ZonedDateTime getLastTimeOfModification() {
        return lastTimeOfModification;
    }

    public void setLastTimeOfModification(ZonedDateTime lastTimeOfModification) {
        this.lastTimeOfModification = lastTimeOfModification;
    }

    public UUID getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(UUID userIdentifier) {
        this.userIdentifier = userIdentifier;
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

    public UserDTO getSystemIdentity() {
        return systemIdentity;
    }

    public void setSystemIdentity(UserDTO systemIdentity) {
        this.systemIdentity = systemIdentity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationUserDTO)) {
            return false;
        }

        ApplicationUserDTO applicationUserDTO = (ApplicationUserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, applicationUserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplicationUserDTO{" +
            "id=" + getId() +
            ", applicationIdentity='" + getApplicationIdentity() + "'" +
            ", lastLoginTime='" + getLastLoginTime() + "'" +
            ", timeOfCreation='" + getTimeOfCreation() + "'" +
            ", lastTimeOfModification='" + getLastTimeOfModification() + "'" +
            ", userIdentifier='" + getUserIdentifier() + "'" +
            ", createdBy=" + getCreatedBy() +
            ", lastModifiedBy=" + getLastModifiedBy() +
            ", systemIdentity=" + getSystemIdentity() +
            "}";
    }
}
