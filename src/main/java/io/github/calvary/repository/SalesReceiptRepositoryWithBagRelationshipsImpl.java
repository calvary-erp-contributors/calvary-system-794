package io.github.calvary.repository;

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
        return salesReceipt.map(this::fetchTransactionItemAmounts);
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
        return Optional.of(salesReceipts).map(this::fetchTransactionItemAmounts).orElse(Collections.emptyList());
    }

    SalesReceipt fetchTransactionItemAmounts(SalesReceipt result) {
        return entityManager
            .createQuery(
                "select salesReceipt from SalesReceipt salesReceipt left join fetch salesReceipt.transactionItemAmounts where salesReceipt is :salesReceipt",
                SalesReceipt.class
            )
            .setParameter("salesReceipt", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<SalesReceipt> fetchTransactionItemAmounts(List<SalesReceipt> salesReceipts) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, salesReceipts.size()).forEach(index -> order.put(salesReceipts.get(index).getId(), index));
        List<SalesReceipt> result = entityManager
            .createQuery(
                "select distinct salesReceipt from SalesReceipt salesReceipt left join fetch salesReceipt.transactionItemAmounts where salesReceipt in :salesReceipts",
                SalesReceipt.class
            )
            .setParameter("salesReceipts", salesReceipts)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
