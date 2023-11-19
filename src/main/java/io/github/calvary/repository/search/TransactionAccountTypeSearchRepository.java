package io.github.calvary.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import io.github.calvary.domain.TransactionAccountType;
import io.github.calvary.repository.TransactionAccountTypeRepository;
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
 * Spring Data Elasticsearch repository for the {@link TransactionAccountType} entity.
 */
public interface TransactionAccountTypeSearchRepository
    extends ElasticsearchRepository<TransactionAccountType, Long>, TransactionAccountTypeSearchRepositoryInternal {}

interface TransactionAccountTypeSearchRepositoryInternal {
    Page<TransactionAccountType> search(String query, Pageable pageable);

    Page<TransactionAccountType> search(Query query);

    void index(TransactionAccountType entity);
}

class TransactionAccountTypeSearchRepositoryInternalImpl implements TransactionAccountTypeSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TransactionAccountTypeRepository repository;

    TransactionAccountTypeSearchRepositoryInternalImpl(
        ElasticsearchRestTemplate elasticsearchTemplate,
        TransactionAccountTypeRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TransactionAccountType> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<TransactionAccountType> search(Query query) {
        SearchHits<TransactionAccountType> searchHits = elasticsearchTemplate.search(query, TransactionAccountType.class);
        List<TransactionAccountType> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TransactionAccountType entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
