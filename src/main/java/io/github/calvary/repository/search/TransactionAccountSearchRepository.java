package io.github.calvary.repository.search;

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

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.repository.TransactionAccountRepository;
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
 * Spring Data Elasticsearch repository for the {@link TransactionAccount} entity.
 */
public interface TransactionAccountSearchRepository
    extends ElasticsearchRepository<TransactionAccount, Long>, TransactionAccountSearchRepositoryInternal {}

interface TransactionAccountSearchRepositoryInternal {
    Page<TransactionAccount> search(String query, Pageable pageable);

    Page<TransactionAccount> search(Query query);

    void index(TransactionAccount entity);
}

class TransactionAccountSearchRepositoryInternalImpl implements TransactionAccountSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TransactionAccountRepository repository;

    TransactionAccountSearchRepositoryInternalImpl(
        ElasticsearchRestTemplate elasticsearchTemplate,
        TransactionAccountRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TransactionAccount> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<TransactionAccount> search(Query query) {
        SearchHits<TransactionAccount> searchHits = elasticsearchTemplate.search(query, TransactionAccount.class);
        List<TransactionAccount> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TransactionAccount entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
