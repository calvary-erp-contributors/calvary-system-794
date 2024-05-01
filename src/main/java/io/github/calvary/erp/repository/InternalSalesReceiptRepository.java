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

import io.github.calvary.domain.SalesReceipt;
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
 * Spring Data JPA repository for the SalesReceipt entity.
 */
@Repository
public interface InternalSalesReceiptRepository extends JpaRepository<SalesReceipt, Long>, JpaSpecificationExecutor<SalesReceipt> {
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

    @Query(
        nativeQuery = true,
        value = "" +
        "SELECT  " +
        "   id,  " +
        "   description,  " +
        "   transaction_class_id,  " +
        "   dealer_id,  " +
        "   has_been_emailed,  " +
        "   has_been_proposed,  " +
        "   transaction_date,  " +
        "   should_be_emailed,  " +
        "   sales_receipt_title_id " +
        "FROM public.sales_receipt " +
        "WHERE should_be_emailed = TRUE AND  " +
        "has_been_emailed = FALSE;",
        countQuery = "" +
        "SELECT  " +
        "   id,  " +
        "   description,  " +
        "   transaction_class_id,  " +
        "   dealer_id,  " +
        "   has_been_emailed,  " +
        "   has_been_proposed,  " +
        "   transaction_date,  " +
        "   should_be_emailed,  " +
        "   sales_receipt_title_id " +
        "FROM public.sales_receipt " +
        "WHERE should_be_emailed = TRUE AND  " +
        "has_been_emailed = FALSE;"
    )
    Optional<List<SalesReceipt>> findSalesReceiptDueForNotification();
}
