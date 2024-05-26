package io.github.calvary.erp.repository;

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

import io.github.calvary.domain.TransactionEntry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransactionEntry entity.
 */
@Repository
public interface InternalTransactionEntryRepository
    extends JpaRepository<TransactionEntry, Long>, JpaSpecificationExecutor<TransactionEntry> {
    default Optional<TransactionEntry> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TransactionEntry> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TransactionEntry> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct transactionEntry from TransactionEntry transactionEntry left join fetch transactionEntry.transactionAccount left join fetch transactionEntry.accountTransaction",
        countQuery = "select count(distinct transactionEntry) from TransactionEntry transactionEntry"
    )
    Page<TransactionEntry> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct transactionEntry from TransactionEntry transactionEntry left join fetch transactionEntry.transactionAccount left join fetch transactionEntry.accountTransaction"
    )
    List<TransactionEntry> findAllWithToOneRelationships();

    @Query(
        "select transactionEntry from TransactionEntry transactionEntry left join fetch transactionEntry.transactionAccount left join fetch transactionEntry.accountTransaction where transactionEntry.id =:id"
    )
    Optional<TransactionEntry> findOneWithToOneRelationships(@Param("id") Long id);
}
