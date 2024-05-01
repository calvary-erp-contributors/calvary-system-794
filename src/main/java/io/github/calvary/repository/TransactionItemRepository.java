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

import io.github.calvary.domain.TransactionItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransactionItem entity.
 */
@Repository
public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long>, JpaSpecificationExecutor<TransactionItem> {
    default Optional<TransactionItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TransactionItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TransactionItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct transactionItem from TransactionItem transactionItem left join fetch transactionItem.transactionClass left join fetch transactionItem.transactionAccount",
        countQuery = "select count(distinct transactionItem) from TransactionItem transactionItem"
    )
    Page<TransactionItem> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct transactionItem from TransactionItem transactionItem left join fetch transactionItem.transactionClass left join fetch transactionItem.transactionAccount"
    )
    List<TransactionItem> findAllWithToOneRelationships();

    @Query(
        "select transactionItem from TransactionItem transactionItem left join fetch transactionItem.transactionClass left join fetch transactionItem.transactionAccount where transactionItem.id =:id"
    )
    Optional<TransactionItem> findOneWithToOneRelationships(@Param("id") Long id);
}
