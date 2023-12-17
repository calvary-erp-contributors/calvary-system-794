package io.github.calvary.repository;

import io.github.calvary.domain.TransferItemEntry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransferItemEntry entity.
 */
@Repository
public interface TransferItemEntryRepository extends JpaRepository<TransferItemEntry, Long>, JpaSpecificationExecutor<TransferItemEntry> {
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
        value = "select distinct transferItemEntry from TransferItemEntry transferItemEntry left join fetch transferItemEntry.transactionItem",
        countQuery = "select count(distinct transferItemEntry) from TransferItemEntry transferItemEntry"
    )
    Page<TransferItemEntry> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct transferItemEntry from TransferItemEntry transferItemEntry left join fetch transferItemEntry.transactionItem")
    List<TransferItemEntry> findAllWithToOneRelationships();

    @Query(
        "select transferItemEntry from TransferItemEntry transferItemEntry left join fetch transferItemEntry.transactionItem where transferItemEntry.id =:id"
    )
    Optional<TransferItemEntry> findOneWithToOneRelationships(@Param("id") Long id);
}
