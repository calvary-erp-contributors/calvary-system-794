package io.github.calvary.repository;

import io.github.calvary.domain.Dealer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class DealerRepositoryWithBagRelationshipsImpl implements DealerRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Dealer> fetchBagRelationships(Optional<Dealer> dealer) {
        return dealer.map(this::fetchSalesReceiptEmailPersonas);
    }

    @Override
    public Page<Dealer> fetchBagRelationships(Page<Dealer> dealers) {
        return new PageImpl<>(fetchBagRelationships(dealers.getContent()), dealers.getPageable(), dealers.getTotalElements());
    }

    @Override
    public List<Dealer> fetchBagRelationships(List<Dealer> dealers) {
        return Optional.of(dealers).map(this::fetchSalesReceiptEmailPersonas).orElse(Collections.emptyList());
    }

    Dealer fetchSalesReceiptEmailPersonas(Dealer result) {
        return entityManager
            .createQuery(
                "select dealer from Dealer dealer left join fetch dealer.salesReceiptEmailPersonas where dealer is :dealer",
                Dealer.class
            )
            .setParameter("dealer", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Dealer> fetchSalesReceiptEmailPersonas(List<Dealer> dealers) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, dealers.size()).forEach(index -> order.put(dealers.get(index).getId(), index));
        List<Dealer> result = entityManager
            .createQuery(
                "select distinct dealer from Dealer dealer left join fetch dealer.salesReceiptEmailPersonas where dealer in :dealers",
                Dealer.class
            )
            .setParameter("dealers", dealers)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
