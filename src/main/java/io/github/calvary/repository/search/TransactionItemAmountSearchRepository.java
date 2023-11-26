package io.github.calvary.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import io.github.calvary.domain.TransactionItemAmount;
import io.github.calvary.repository.TransactionItemAmountRepository;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link TransactionItemAmount} entity.
 */
public interface TransactionItemAmountSearchRepository
    extends ElasticsearchRepository<TransactionItemAmount, Long>, TransactionItemAmountSearchRepositoryInternal {}

interface TransactionItemAmountSearchRepositoryInternal {
    Page<TransactionItemAmount> search(String query, Pageable pageable);

    Page<TransactionItemAmount> search(Query query);

    void index(TransactionItemAmount entity);
}

class TransactionItemAmountSearchRepositoryInternalImpl implements TransactionItemAmountSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TransactionItemAmountRepository repository;

    TransactionItemAmountSearchRepositoryInternalImpl(
        ElasticsearchRestTemplate elasticsearchTemplate,
        TransactionItemAmountRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TransactionItemAmount> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<TransactionItemAmount> search(Query query) {
        SearchHits<TransactionItemAmount> searchHits = elasticsearchTemplate.search(query, TransactionItemAmount.class);
        List<TransactionItemAmount> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TransactionItemAmount entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
