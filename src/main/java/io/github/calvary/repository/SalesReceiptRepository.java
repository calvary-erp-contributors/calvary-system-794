package io.github.calvary.repository;

import io.github.calvary.domain.SalesReceipt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalesReceipt entity.
 *
 * When extending this class, extend SalesReceiptRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface SalesReceiptRepository
    extends SalesReceiptRepositoryWithBagRelationships, JpaRepository<SalesReceipt, Long>, JpaSpecificationExecutor<SalesReceipt> {
    default Optional<SalesReceipt> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<SalesReceipt> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<SalesReceipt> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct salesReceipt from SalesReceipt salesReceipt left join fetch salesReceipt.transactionClass left join fetch salesReceipt.dealer left join fetch salesReceipt.salesReceiptTitle",
        countQuery = "select count(distinct salesReceipt) from SalesReceipt salesReceipt"
    )
    Page<SalesReceipt> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct salesReceipt from SalesReceipt salesReceipt left join fetch salesReceipt.transactionClass left join fetch salesReceipt.dealer left join fetch salesReceipt.salesReceiptTitle"
    )
    List<SalesReceipt> findAllWithToOneRelationships();

    @Query(
        "select salesReceipt from SalesReceipt salesReceipt left join fetch salesReceipt.transactionClass left join fetch salesReceipt.dealer left join fetch salesReceipt.salesReceiptTitle where salesReceipt.id =:id"
    )
    Optional<SalesReceipt> findOneWithToOneRelationships(@Param("id") Long id);
}
