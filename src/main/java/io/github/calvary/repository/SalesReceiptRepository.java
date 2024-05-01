package io.github.calvary.repository;

/*-
 * Copyright © 2023 - 2024 Calvary ERP Contributors (mailnjeru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.github.calvary.domain.SalesReceipt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalesReceipt entity.
 */
@Repository
public interface SalesReceiptRepository extends JpaRepository<SalesReceipt, Long>, JpaSpecificationExecutor<SalesReceipt> {
    default Optional<SalesReceipt> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SalesReceipt> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SalesReceipt> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct salesReceipt from SalesReceipt salesReceipt left join fetch salesReceipt.transactionClass left join fetch salesReceipt.dealer left join fetch salesReceipt.salesReceiptTitle",
        countQuery = "select count(distinct salesReceipt) from SalesReceipt salesReceipt"
    )
    Page<SalesReceipt> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct salesReceipt from SalesReceipt salesReceipt left join fetch salesReceipt.transactionClass left join fetch salesReceipt.dealer left join fetch salesReceipt.salesReceiptTitle"
    )
    List<SalesReceipt> findAllWithToOneRelationships();

    @Query(
        "select salesReceipt from SalesReceipt salesReceipt left join fetch salesReceipt.transactionClass left join fetch salesReceipt.dealer left join fetch salesReceipt.salesReceiptTitle where salesReceipt.id =:id"
    )
    Optional<SalesReceipt> findOneWithToOneRelationships(@Param("id") Long id);
}
