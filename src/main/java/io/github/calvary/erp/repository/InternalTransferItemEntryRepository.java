package io.github.calvary.erp.repository;

import io.github.calvary.domain.TransferItemEntry;
import io.github.calvary.repository.TransferItemEntryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransferItemEntry entity.
 */
@Repository
public interface InternalTransferItemEntryRepository
    extends JpaRepository<TransferItemEntry, Long>, JpaSpecificationExecutor<TransferItemEntry> {
    default Optional<TransferItemEntry> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TransferItemEntry> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TransferItemEntry> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct transferItemEntry from TransferItemEntry transferItemEntry left join fetch transferItemEntry.transferItem",
        countQuery = "select count(distinct transferItemEntry) from TransferItemEntry transferItemEntry"
    )
    Page<TransferItemEntry> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct transferItemEntry from TransferItemEntry transferItemEntry left join fetch transferItemEntry.transferItem")
    List<TransferItemEntry> findAllWithToOneRelationships();

    @Query(
        "select transferItemEntry from TransferItemEntry transferItemEntry left join fetch transferItemEntry.transferItem where transferItemEntry.id =:id"
    )
    Optional<TransferItemEntry> findOneWithToOneRelationships(@Param("id") Long id);

    /**
     * Get list of transaction-items related to a given sales-receipt item
     *
     * @param salesReceiptId Parent owning the transaction-items we seek
     * @return Optional list of related transaction-items
     */
    Optional<List<TransferItemEntry>> findAllBySalesReceiptId(Long salesReceiptId);
}
