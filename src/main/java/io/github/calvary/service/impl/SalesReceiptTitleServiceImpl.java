package io.github.calvary.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.github.calvary.domain.SalesReceiptTitle;
import io.github.calvary.repository.SalesReceiptTitleRepository;
import io.github.calvary.repository.search.SalesReceiptTitleSearchRepository;
import io.github.calvary.service.SalesReceiptTitleService;
import io.github.calvary.service.dto.SalesReceiptTitleDTO;
import io.github.calvary.service.mapper.SalesReceiptTitleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SalesReceiptTitle}.
 */
@Service
@Transactional
public class SalesReceiptTitleServiceImpl implements SalesReceiptTitleService {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptTitleServiceImpl.class);

    private final SalesReceiptTitleRepository salesReceiptTitleRepository;

    private final SalesReceiptTitleMapper salesReceiptTitleMapper;

    private final SalesReceiptTitleSearchRepository salesReceiptTitleSearchRepository;

    public SalesReceiptTitleServiceImpl(
        SalesReceiptTitleRepository salesReceiptTitleRepository,
        SalesReceiptTitleMapper salesReceiptTitleMapper,
        SalesReceiptTitleSearchRepository salesReceiptTitleSearchRepository
    ) {
        this.salesReceiptTitleRepository = salesReceiptTitleRepository;
        this.salesReceiptTitleMapper = salesReceiptTitleMapper;
        this.salesReceiptTitleSearchRepository = salesReceiptTitleSearchRepository;
    }

    @Override
    public SalesReceiptTitleDTO save(SalesReceiptTitleDTO salesReceiptTitleDTO) {
        log.debug("Request to save SalesReceiptTitle : {}", salesReceiptTitleDTO);
        SalesReceiptTitle salesReceiptTitle = salesReceiptTitleMapper.toEntity(salesReceiptTitleDTO);
        salesReceiptTitle = salesReceiptTitleRepository.save(salesReceiptTitle);
        SalesReceiptTitleDTO result = salesReceiptTitleMapper.toDto(salesReceiptTitle);
        salesReceiptTitleSearchRepository.index(salesReceiptTitle);
        return result;
    }

    @Override
    public SalesReceiptTitleDTO update(SalesReceiptTitleDTO salesReceiptTitleDTO) {
        log.debug("Request to update SalesReceiptTitle : {}", salesReceiptTitleDTO);
        SalesReceiptTitle salesReceiptTitle = salesReceiptTitleMapper.toEntity(salesReceiptTitleDTO);
        salesReceiptTitle = salesReceiptTitleRepository.save(salesReceiptTitle);
        SalesReceiptTitleDTO result = salesReceiptTitleMapper.toDto(salesReceiptTitle);
        salesReceiptTitleSearchRepository.index(salesReceiptTitle);
        return result;
    }

    @Override
    public Optional<SalesReceiptTitleDTO> partialUpdate(SalesReceiptTitleDTO salesReceiptTitleDTO) {
        log.debug("Request to partially update SalesReceiptTitle : {}", salesReceiptTitleDTO);

        return salesReceiptTitleRepository
            .findById(salesReceiptTitleDTO.getId())
            .map(existingSalesReceiptTitle -> {
                salesReceiptTitleMapper.partialUpdate(existingSalesReceiptTitle, salesReceiptTitleDTO);

                return existingSalesReceiptTitle;
            })
            .map(salesReceiptTitleRepository::save)
            .map(savedSalesReceiptTitle -> {
                salesReceiptTitleSearchRepository.save(savedSalesReceiptTitle);

                return savedSalesReceiptTitle;
            })
            .map(salesReceiptTitleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesReceiptTitleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SalesReceiptTitles");
        return salesReceiptTitleRepository.findAll(pageable).map(salesReceiptTitleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SalesReceiptTitleDTO> findOne(Long id) {
        log.debug("Request to get SalesReceiptTitle : {}", id);
        return salesReceiptTitleRepository.findById(id).map(salesReceiptTitleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SalesReceiptTitle : {}", id);
        salesReceiptTitleRepository.deleteById(id);
        salesReceiptTitleSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesReceiptTitleDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SalesReceiptTitles for query {}", query);
        return salesReceiptTitleSearchRepository.search(query, pageable).map(salesReceiptTitleMapper::toDto);
    }
}
