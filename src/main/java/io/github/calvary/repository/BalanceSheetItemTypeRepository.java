package io.github.calvary.repository;

import io.github.calvary.domain.BalanceSheetItemType;
import io.github.calvary.domain.TransactionAccount;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BalanceSheetItemType entity.
 */
@Repository
public interface BalanceSheetItemTypeRepository
    extends JpaRepository<BalanceSheetItemType, Long>, JpaSpecificationExecutor<BalanceSheetItemType> {
    default Optional<BalanceSheetItemType> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BalanceSheetItemType> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BalanceSheetItemType> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct balanceSheetItemType from BalanceSheetItemType balanceSheetItemType left join fetch balanceSheetItemType.transactionAccount left join fetch balanceSheetItemType.parentItem",
        countQuery = "select count(distinct balanceSheetItemType) from BalanceSheetItemType balanceSheetItemType"
    )
    Page<BalanceSheetItemType> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct balanceSheetItemType from BalanceSheetItemType balanceSheetItemType left join fetch balanceSheetItemType.transactionAccount left join fetch balanceSheetItemType.parentItem"
    )
    List<BalanceSheetItemType> findAllWithToOneRelationships();

    @Query(
        "select balanceSheetItemType from BalanceSheetItemType balanceSheetItemType left join fetch balanceSheetItemType.transactionAccount left join fetch balanceSheetItemType.parentItem where balanceSheetItemType.id =:id"
    )
    Optional<BalanceSheetItemType> findOneWithToOneRelationships(@Param("id") Long id);

    List<BalanceSheetItemType> findBalanceSheetItemTypeByTransactionAccountEquals(@NotNull TransactionAccount transactionAccount);
}
