package io.github.calvary.repository;

import io.github.calvary.domain.TransferItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransferItem entity.
 */
@Repository
public interface TransferItemRepository extends JpaRepository<TransferItem, Long>, JpaSpecificationExecutor<TransferItem> {
    default Optional<TransferItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TransferItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TransferItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct transferItem from TransferItem transferItem left join fetch transferItem.transactionClass left join fetch transferItem.transactionAccount",
        countQuery = "select count(distinct transferItem) from TransferItem transferItem"
    )
    Page<TransferItem> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct transferItem from TransferItem transferItem left join fetch transferItem.transactionClass left join fetch transferItem.transactionAccount"
    )
    List<TransferItem> findAllWithToOneRelationships();

    @Query(
        "select transferItem from TransferItem transferItem left join fetch transferItem.transactionClass left join fetch transferItem.transactionAccount where transferItem.id =:id"
    )
    Optional<TransferItem> findOneWithToOneRelationships(@Param("id") Long id);
}
