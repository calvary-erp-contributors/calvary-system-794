package io.github.calvary.repository;

import io.github.calvary.domain.TransactionItemEntry;
import io.github.calvary.domain.TransferItemEntry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransactionItemEntry entity.
 */
@Repository
public interface TransactionItemEntryRepository
    extends JpaRepository<TransactionItemEntry, Long>, JpaSpecificationExecutor<TransactionItemEntry> {
    default Optional<TransactionItemEntry> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TransactionItemEntry> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TransactionItemEntry> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct transactionItemEntry from TransactionItemEntry transactionItemEntry left join fetch transactionItemEntry.transactionItem",
        countQuery = "select count(distinct transactionItemEntry) from TransactionItemEntry transactionItemEntry"
    )
    Page<TransactionItemEntry> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct transactionItemEntry from TransactionItemEntry transactionItemEntry left join fetch transactionItemEntry.transactionItem"
    )
    List<TransactionItemEntry> findAllWithToOneRelationships();

    @Query(
        "select transactionItemEntry from TransactionItemEntry transactionItemEntry left join fetch transactionItemEntry.transactionItem where transactionItemEntry.id =:id"
    )
    Optional<TransactionItemEntry> findOneWithToOneRelationships(@Param("id") Long id);

    List<TransactionItemEntry> findAllBySalesReceiptId(Long salesReceiptId);
}
