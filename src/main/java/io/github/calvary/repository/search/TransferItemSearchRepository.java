package io.github.calvary.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import io.github.calvary.domain.TransferItem;
import io.github.calvary.repository.TransferItemRepository;
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
 * Spring Data Elasticsearch repository for the {@link TransferItem} entity.
 */
public interface TransferItemSearchRepository extends ElasticsearchRepository<TransferItem, Long>, TransferItemSearchRepositoryInternal {}

interface TransferItemSearchRepositoryInternal {
    Page<TransferItem> search(String query, Pageable pageable);

    Page<TransferItem> search(Query query);

    void index(TransferItem entity);
}

class TransferItemSearchRepositoryInternalImpl implements TransferItemSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TransferItemRepository repository;

    TransferItemSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, TransferItemRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TransferItem> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<TransferItem> search(Query query) {
        SearchHits<TransferItem> searchHits = elasticsearchTemplate.search(query, TransferItem.class);
        List<TransferItem> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TransferItem entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
