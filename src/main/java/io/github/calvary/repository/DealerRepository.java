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
