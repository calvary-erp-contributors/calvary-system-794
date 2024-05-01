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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class SalesReceiptRepositoryWithBagRelationshipsImpl implements SalesReceiptRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<SalesReceipt> fetchBagRelationships(Optional<SalesReceipt> salesReceipt) {
        return salesReceipt.map(this::fetchTransactionItemEntries);
    }

    @Override
    public Page<SalesReceipt> fetchBagRelationships(Page<SalesReceipt> salesReceipts) {
        return new PageImpl<>(
            fetchBagRelationships(salesReceipts.getContent()),
            salesReceipts.getPageable(),
            salesReceipts.getTotalElements()
        );
    }

    @Override
    public List<SalesReceipt> fetchBagRelationships(List<SalesReceipt> salesReceipts) {
        return Optional.of(salesReceipts).map(this::fetchTransactionItemEntries).orElse(Collections.emptyList());
    }

    SalesReceipt fetchTransactionItemEntries(SalesReceipt result) {
        return entityManager
            .createQuery(
                "select salesReceipt from SalesReceipt salesReceipt left join fetch salesReceipt.transactionItemEntries where salesReceipt is :salesReceipt",
                SalesReceipt.class
            )
            .setParameter("salesReceipt", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<SalesReceipt> fetchTransactionItemEntries(List<SalesReceipt> salesReceipts) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, salesReceipts.size()).forEach(index -> order.put(salesReceipts.get(index).getId(), index));
        List<SalesReceipt> result = entityManager
            .createQuery(
                "select distinct salesReceipt from SalesReceipt salesReceipt left join fetch salesReceipt.transactionItemEntries where salesReceipt in :salesReceipts",
                SalesReceipt.class
            )
            .setParameter("salesReceipts", salesReceipts)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
