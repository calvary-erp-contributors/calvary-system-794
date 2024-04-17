package io.github.calvary.repository;

import io.github.calvary.domain.SalesReceiptEmailPersona;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalesReceiptEmailPersona entity.
 */
@Repository
public interface SalesReceiptEmailPersonaRepository
    extends JpaRepository<SalesReceiptEmailPersona, Long>, JpaSpecificationExecutor<SalesReceiptEmailPersona> {
    default Optional<SalesReceiptEmailPersona> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SalesReceiptEmailPersona> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SalesReceiptEmailPersona> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct salesReceiptEmailPersona from SalesReceiptEmailPersona salesReceiptEmailPersona left join fetch salesReceiptEmailPersona.createdBy left join fetch salesReceiptEmailPersona.lastModifiedBy left join fetch salesReceiptEmailPersona.contributor",
        countQuery = "select count(distinct salesReceiptEmailPersona) from SalesReceiptEmailPersona salesReceiptEmailPersona"
    )
    Page<SalesReceiptEmailPersona> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct salesReceiptEmailPersona from SalesReceiptEmailPersona salesReceiptEmailPersona left join fetch salesReceiptEmailPersona.createdBy left join fetch salesReceiptEmailPersona.lastModifiedBy left join fetch salesReceiptEmailPersona.contributor"
    )
    List<SalesReceiptEmailPersona> findAllWithToOneRelationships();

    @Query(
        "select salesReceiptEmailPersona from SalesReceiptEmailPersona salesReceiptEmailPersona left join fetch salesReceiptEmailPersona.createdBy left join fetch salesReceiptEmailPersona.lastModifiedBy left join fetch salesReceiptEmailPersona.contributor where salesReceiptEmailPersona.id =:id"
    )
    Optional<SalesReceiptEmailPersona> findOneWithToOneRelationships(@Param("id") Long id);
}
