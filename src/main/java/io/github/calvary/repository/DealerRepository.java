package io.github.calvary.repository;

import io.github.calvary.domain.Dealer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Dealer entity.
 *
 * When extending this class, extend DealerRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface DealerRepository
    extends DealerRepositoryWithBagRelationships, JpaRepository<Dealer, Long>, JpaSpecificationExecutor<Dealer> {
    default Optional<Dealer> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Dealer> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Dealer> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct dealer from Dealer dealer left join fetch dealer.dealerType",
        countQuery = "select count(distinct dealer) from Dealer dealer"
    )
    Page<Dealer> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct dealer from Dealer dealer left join fetch dealer.dealerType")
    List<Dealer> findAllWithToOneRelationships();

    @Query("select dealer from Dealer dealer left join fetch dealer.dealerType where dealer.id =:id")
    Optional<Dealer> findOneWithToOneRelationships(@Param("id") Long id);
}
