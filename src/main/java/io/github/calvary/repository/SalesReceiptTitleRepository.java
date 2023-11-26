package io.github.calvary.repository;

import io.github.calvary.domain.SalesReceiptTitle;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalesReceiptTitle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SalesReceiptTitleRepository extends JpaRepository<SalesReceiptTitle, Long>, JpaSpecificationExecutor<SalesReceiptTitle> {}
