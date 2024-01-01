package io.github.calvary.repository;

import io.github.calvary.domain.SalesReceiptCompilation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalesReceiptCompilation entity.
 */
@Repository
public interface SalesReceiptCompilationRepository
    extends JpaRepository<SalesReceiptCompilation, Long>, JpaSpecificationExecutor<SalesReceiptCompilation> {
    default Optional<SalesReceiptCompilation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SalesReceiptCompilation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SalesReceiptCompilation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct salesReceiptCompilation from SalesReceiptCompilation salesReceiptCompilation left join fetch salesReceiptCompilation.compiledBy",
        countQuery = "select count(distinct salesReceiptCompilation) from SalesReceiptCompilation salesReceiptCompilation"
    )
    Page<SalesReceiptCompilation> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct salesReceiptCompilation from SalesReceiptCompilation salesReceiptCompilation left join fetch salesReceiptCompilation.compiledBy"
    )
    List<SalesReceiptCompilation> findAllWithToOneRelationships();

    @Query(
        "select salesReceiptCompilation from SalesReceiptCompilation salesReceiptCompilation left join fetch salesReceiptCompilation.compiledBy where salesReceiptCompilation.id =:id"
    )
    Optional<SalesReceiptCompilation> findOneWithToOneRelationships(@Param("id") Long id);
}
