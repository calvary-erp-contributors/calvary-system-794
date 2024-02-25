package io.github.calvary.repository;

import io.github.calvary.domain.SalesReceiptProposal;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalesReceiptProposal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SalesReceiptProposalRepository
    extends JpaRepository<SalesReceiptProposal, Long>, JpaSpecificationExecutor<SalesReceiptProposal> {}
