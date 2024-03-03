package io.github.calvary.repository;

import io.github.calvary.domain.ReceiptEmailRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReceiptEmailRequest entity.
 */
@Repository
public interface ReceiptEmailRequestRepository
    extends JpaRepository<ReceiptEmailRequest, Long>, JpaSpecificationExecutor<ReceiptEmailRequest> {
    default Optional<ReceiptEmailRequest> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ReceiptEmailRequest> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ReceiptEmailRequest> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct receiptEmailRequest from ReceiptEmailRequest receiptEmailRequest left join fetch receiptEmailRequest.requestedBy",
        countQuery = "select count(distinct receiptEmailRequest) from ReceiptEmailRequest receiptEmailRequest"
    )
    Page<ReceiptEmailRequest> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct receiptEmailRequest from ReceiptEmailRequest receiptEmailRequest left join fetch receiptEmailRequest.requestedBy"
    )
    List<ReceiptEmailRequest> findAllWithToOneRelationships();

    @Query(
        "select receiptEmailRequest from ReceiptEmailRequest receiptEmailRequest left join fetch receiptEmailRequest.requestedBy where receiptEmailRequest.id =:id"
    )
    Optional<ReceiptEmailRequest> findOneWithToOneRelationships(@Param("id") Long id);
}
