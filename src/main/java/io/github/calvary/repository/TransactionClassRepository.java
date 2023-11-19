package io.github.calvary.repository;

import io.github.calvary.domain.TransactionClass;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransactionClass entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionClassRepository extends JpaRepository<TransactionClass, Long>, JpaSpecificationExecutor<TransactionClass> {}
