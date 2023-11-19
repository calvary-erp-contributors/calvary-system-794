package io.github.calvary.repository;

import io.github.calvary.domain.TransactionAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransactionAccount entity.
 */
@Repository
public interface TransactionAccountRepository
    extends JpaRepository<TransactionAccount, Long>, JpaSpecificationExecutor<TransactionAccount> {
    default Optional<TransactionAccount> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TransactionAccount> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TransactionAccount> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct transactionAccount from TransactionAccount transactionAccount left join fetch transactionAccount.parentAccount left join fetch transactionAccount.transactionAccountType left join fetch transactionAccount.transactionCurrency",
        countQuery = "select count(distinct transactionAccount) from TransactionAccount transactionAccount"
    )
    Page<TransactionAccount> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct transactionAccount from TransactionAccount transactionAccount left join fetch transactionAccount.parentAccount left join fetch transactionAccount.transactionAccountType left join fetch transactionAccount.transactionCurrency"
    )
    List<TransactionAccount> findAllWithToOneRelationships();

    @Query(
        "select transactionAccount from TransactionAccount transactionAccount left join fetch transactionAccount.parentAccount left join fetch transactionAccount.transactionAccountType left join fetch transactionAccount.transactionCurrency where transactionAccount.id =:id"
    )
    Optional<TransactionAccount> findOneWithToOneRelationships(@Param("id") Long id);
}
