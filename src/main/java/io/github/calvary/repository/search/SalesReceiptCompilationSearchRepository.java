package io.github.calvary.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import io.github.calvary.domain.SalesReceiptCompilation;
import io.github.calvary.repository.SalesReceiptCompilationRepository;
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
 * Spring Data Elasticsearch repository for the {@link SalesReceiptCompilation} entity.
 */
public interface SalesReceiptCompilationSearchRepository
    extends ElasticsearchRepository<SalesReceiptCompilation, Long>, SalesReceiptCompilationSearchRepositoryInternal {}

interface SalesReceiptCompilationSearchRepositoryInternal {
    Page<SalesReceiptCompilation> search(String query, Pageable pageable);

    Page<SalesReceiptCompilation> search(Query query);

    void index(SalesReceiptCompilation entity);
}

class SalesReceiptCompilationSearchRepositoryInternalImpl implements SalesReceiptCompilationSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final SalesReceiptCompilationRepository repository;

    SalesReceiptCompilationSearchRepositoryInternalImpl(
        ElasticsearchRestTemplate elasticsearchTemplate,
        SalesReceiptCompilationRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<SalesReceiptCompilation> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<SalesReceiptCompilation> search(Query query) {
        SearchHits<SalesReceiptCompilation> searchHits = elasticsearchTemplate.search(query, SalesReceiptCompilation.class);
        List<SalesReceiptCompilation> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(SalesReceiptCompilation entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
