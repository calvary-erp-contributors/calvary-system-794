package io.github.calvary.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ApplicationUser.
 */
@Entity
@Table(name = "application_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "applicationuser")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApplicationUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "application_identity", nullable = false, unique = true)
    private String applicationIdentity;

    @Column(name = "last_login_time")
    private ZonedDateTime lastLoginTime;

    @NotNull
    @Column(name = "time_of_creation", nullable = false)
    private ZonedDateTime timeOfCreation;

    @Column(name = "last_time_of_modification")
    private ZonedDateTime lastTimeOfModification;

    @NotNull
    @Column(name = "user_identifier", nullable = false, unique = true)
    private UUID userIdentifier;

    @ManyToOne
    @JsonIgnoreProperties(value = { "createdBy", "lastModifiedBy", "systemIdentity" }, allowSetters = true)
    private ApplicationUser createdBy;

    @ManyToOne
    @JsonIgnoreProperties(value = { "createdBy", "lastModifiedBy", "systemIdentity" }, allowSetters = true)
    private ApplicationUser lastModifiedBy;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User systemIdentity;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ApplicationUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationIdentity() {
        return this.applicationIdentity;
    }

    public ApplicationUser applicationIdentity(String applicationIdentity) {
        this.setApplicationIdentity(applicationIdentity);
        return this;
    }

    public void setApplicationIdentity(String applicationIdentity) {
        this.applicationIdentity = applicationIdentity;
    }

    public ZonedDateTime getLastLoginTime() {
        return this.lastLoginTime;
    }

    public ApplicationUser lastLoginTime(ZonedDateTime lastLoginTime) {
        this.setLastLoginTime(lastLoginTime);
        return this;
    }

    public void setLastLoginTime(ZonedDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public ZonedDateTime getTimeOfCreation() {
        return this.timeOfCreation;
    }

    public ApplicationUser timeOfCreation(ZonedDateTime timeOfCreation) {
        this.setTimeOfCreation(timeOfCreation);
        return this;
    }

    public void setTimeOfCreation(ZonedDateTime timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public ZonedDateTime getLastTimeOfModification() {
        return this.lastTimeOfModification;
    }

    public ApplicationUser lastTimeOfModification(ZonedDateTime lastTimeOfModification) {
        this.setLastTimeOfModification(lastTimeOfModification);
        return this;
    }

    public void setLastTimeOfModification(ZonedDateTime lastTimeOfModification) {
        this.lastTimeOfModification = lastTimeOfModification;
    }

    public UUID getUserIdentifier() {
        return this.userIdentifier;
    }

    public ApplicationUser userIdentifier(UUID userIdentifier) {
        this.setUserIdentifier(userIdentifier);
        return this;
    }

    public void setUserIdentifier(UUID userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public ApplicationUser getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(ApplicationUser applicationUser) {
        this.createdBy = applicationUser;
    }

    public ApplicationUser createdBy(ApplicationUser applicationUser) {
        this.setCreatedBy(applicationUser);
        return this;
    }

    public ApplicationUser getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public void setLastModifiedBy(ApplicationUser applicationUser) {
        this.lastModifiedBy = applicationUser;
    }

    public ApplicationUser lastModifiedBy(ApplicationUser applicationUser) {
        this.setLastModifiedBy(applicationUser);
        return this;
    }

    public User getSystemIdentity() {
        return this.systemIdentity;
    }

    public void setSystemIdentity(User user) {
        this.systemIdentity = user;
    }

    public ApplicationUser systemIdentity(User user) {
        this.setSystemIdentity(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationUser)) {
            return false;
        }
        return id != null && id.equals(((ApplicationUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplicationUser{" +
            "id=" + getId() +
            ", applicationIdentity='" + getApplicationIdentity() + "'" +
            ", lastLoginTime='" + getLastLoginTime() + "'" +
            ", timeOfCreation='" + getTimeOfCreation() + "'" +
            ", lastTimeOfModification='" + getLastTimeOfModification() + "'" +
            ", userIdentifier='" + getUserIdentifier() + "'" +
            "}";
    }
}
