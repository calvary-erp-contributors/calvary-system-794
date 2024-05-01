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

import io.github.calvary.domain.AccountingEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AccountingEvent entity.
 */
@Repository
public interface AccountingEventRepository extends JpaRepository<AccountingEvent, Long>, JpaSpecificationExecutor<AccountingEvent> {
    default Optional<AccountingEvent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AccountingEvent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AccountingEvent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct accountingEvent from AccountingEvent accountingEvent left join fetch accountingEvent.eventType left join fetch accountingEvent.dealer",
        countQuery = "select count(distinct accountingEvent) from AccountingEvent accountingEvent"
    )
    Page<AccountingEvent> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct accountingEvent from AccountingEvent accountingEvent left join fetch accountingEvent.eventType left join fetch accountingEvent.dealer"
    )
    List<AccountingEvent> findAllWithToOneRelationships();

    @Query(
        "select accountingEvent from AccountingEvent accountingEvent left join fetch accountingEvent.eventType left join fetch accountingEvent.dealer where accountingEvent.id =:id"
    )
    Optional<AccountingEvent> findOneWithToOneRelationships(@Param("id") Long id);
}
