package io.github.calvary.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import io.github.calvary.domain.BalanceSheetItemType;
import io.github.calvary.repository.BalanceSheetItemTypeRepository;
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
 * Spring Data Elasticsearch repository for the {@link BalanceSheetItemType} entity.
 */
public interface BalanceSheetItemTypeSearchRepository
    extends ElasticsearchRepository<BalanceSheetItemType, Long>, BalanceSheetItemTypeSearchRepositoryInternal {}

interface BalanceSheetItemTypeSearchRepositoryInternal {
    Page<BalanceSheetItemType> search(String query, Pageable pageable);

    Page<BalanceSheetItemType> search(Query query);

    void index(BalanceSheetItemType entity);
}

class BalanceSheetItemTypeSearchRepositoryInternalImpl implements BalanceSheetItemTypeSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final BalanceSheetItemTypeRepository repository;

    BalanceSheetItemTypeSearchRepositoryInternalImpl(
        ElasticsearchRestTemplate elasticsearchTemplate,
        BalanceSheetItemTypeRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<BalanceSheetItemType> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<BalanceSheetItemType> search(Query query) {
        SearchHits<BalanceSheetItemType> searchHits = elasticsearchTemplate.search(query, BalanceSheetItemType.class);
        List<BalanceSheetItemType> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(BalanceSheetItemType entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
