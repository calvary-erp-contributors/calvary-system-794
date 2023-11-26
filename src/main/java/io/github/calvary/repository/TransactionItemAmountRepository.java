package io.github.calvary.repository;

import io.github.calvary.domain.TransactionItemAmount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransactionItemAmount entity.
 */
@Repository
public interface TransactionItemAmountRepository
    extends JpaRepository<TransactionItemAmount, Long>, JpaSpecificationExecutor<TransactionItemAmount> {
    default Optional<TransactionItemAmount> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TransactionItemAmount> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TransactionItemAmount> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct transactionItemAmount from TransactionItemAmount transactionItemAmount left join fetch transactionItemAmount.transactionItem",
        countQuery = "select count(distinct transactionItemAmount) from TransactionItemAmount transactionItemAmount"
    )
    Page<TransactionItemAmount> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct transactionItemAmount from TransactionItemAmount transactionItemAmount left join fetch transactionItemAmount.transactionItem"
    )
    List<TransactionItemAmount> findAllWithToOneRelationships();

    @Query(
        "select transactionItemAmount from TransactionItemAmount transactionItemAmount left join fetch transactionItemAmount.transactionItem where transactionItemAmount.id =:id"
    )
    Optional<TransactionItemAmount> findOneWithToOneRelationships(@Param("id") Long id);
}
