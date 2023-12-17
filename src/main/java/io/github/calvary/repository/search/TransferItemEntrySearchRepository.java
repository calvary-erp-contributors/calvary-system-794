package io.github.calvary.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import io.github.calvary.domain.TransferItemEntry;
import io.github.calvary.repository.TransferItemEntryRepository;
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
 * Spring Data Elasticsearch repository for the {@link TransferItemEntry} entity.
 */
public interface TransferItemEntrySearchRepository
    extends ElasticsearchRepository<TransferItemEntry, Long>, TransferItemEntrySearchRepositoryInternal {}

interface TransferItemEntrySearchRepositoryInternal {
    Page<TransferItemEntry> search(String query, Pageable pageable);

    Page<TransferItemEntry> search(Query query);

    void index(TransferItemEntry entity);
}

class TransferItemEntrySearchRepositoryInternalImpl implements TransferItemEntrySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TransferItemEntryRepository repository;

    TransferItemEntrySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, TransferItemEntryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TransferItemEntry> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<TransferItemEntry> search(Query query) {
        SearchHits<TransferItemEntry> searchHits = elasticsearchTemplate.search(query, TransferItemEntry.class);
        List<TransferItemEntry> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TransferItemEntry entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
