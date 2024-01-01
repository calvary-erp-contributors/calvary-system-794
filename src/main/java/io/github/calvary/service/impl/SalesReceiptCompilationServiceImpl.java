package io.github.calvary.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.github.calvary.domain.SalesReceiptCompilation;
import io.github.calvary.repository.SalesReceiptCompilationRepository;
import io.github.calvary.repository.search.SalesReceiptCompilationSearchRepository;
import io.github.calvary.service.SalesReceiptCompilationService;
import io.github.calvary.service.dto.SalesReceiptCompilationDTO;
import io.github.calvary.service.mapper.SalesReceiptCompilationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SalesReceiptCompilation}.
 */
@Service
@Transactional
public class SalesReceiptCompilationServiceImpl implements SalesReceiptCompilationService {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptCompilationServiceImpl.class);

    private final SalesReceiptCompilationRepository salesReceiptCompilationRepository;

    private final SalesReceiptCompilationMapper salesReceiptCompilationMapper;

    private final SalesReceiptCompilationSearchRepository salesReceiptCompilationSearchRepository;

    public SalesReceiptCompilationServiceImpl(
        SalesReceiptCompilationRepository salesReceiptCompilationRepository,
        SalesReceiptCompilationMapper salesReceiptCompilationMapper,
        SalesReceiptCompilationSearchRepository salesReceiptCompilationSearchRepository
    ) {
        this.salesReceiptCompilationRepository = salesReceiptCompilationRepository;
        this.salesReceiptCompilationMapper = salesReceiptCompilationMapper;
        this.salesReceiptCompilationSearchRepository = salesReceiptCompilationSearchRepository;
    }

    @Override
    public SalesReceiptCompilationDTO save(SalesReceiptCompilationDTO salesReceiptCompilationDTO) {
        log.debug("Request to save SalesReceiptCompilation : {}", salesReceiptCompilationDTO);
        SalesReceiptCompilation salesReceiptCompilation = salesReceiptCompilationMapper.toEntity(salesReceiptCompilationDTO);
        salesReceiptCompilation = salesReceiptCompilationRepository.save(salesReceiptCompilation);
        SalesReceiptCompilationDTO result = salesReceiptCompilationMapper.toDto(salesReceiptCompilation);
        salesReceiptCompilationSearchRepository.index(salesReceiptCompilation);
        return result;
    }

    @Override
    public SalesReceiptCompilationDTO update(SalesReceiptCompilationDTO salesReceiptCompilationDTO) {
        log.debug("Request to update SalesReceiptCompilation : {}", salesReceiptCompilationDTO);
        SalesReceiptCompilation salesReceiptCompilation = salesReceiptCompilationMapper.toEntity(salesReceiptCompilationDTO);
        salesReceiptCompilation = salesReceiptCompilationRepository.save(salesReceiptCompilation);
        SalesReceiptCompilationDTO result = salesReceiptCompilationMapper.toDto(salesReceiptCompilation);
        salesReceiptCompilationSearchRepository.index(salesReceiptCompilation);
        return result;
    }

    @Override
    public Optional<SalesReceiptCompilationDTO> partialUpdate(SalesReceiptCompilationDTO salesReceiptCompilationDTO) {
        log.debug("Request to partially update SalesReceiptCompilation : {}", salesReceiptCompilationDTO);

        return salesReceiptCompilationRepository
            .findById(salesReceiptCompilationDTO.getId())
            .map(existingSalesReceiptCompilation -> {
                salesReceiptCompilationMapper.partialUpdate(existingSalesReceiptCompilation, salesReceiptCompilationDTO);

                return existingSalesReceiptCompilation;
            })
            .map(salesReceiptCompilationRepository::save)
            .map(savedSalesReceiptCompilation -> {
                salesReceiptCompilationSearchRepository.save(savedSalesReceiptCompilation);

                return savedSalesReceiptCompilation;
            })
            .map(salesReceiptCompilationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesReceiptCompilationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SalesReceiptCompilations");
        return salesReceiptCompilationRepository.findAll(pageable).map(salesReceiptCompilationMapper::toDto);
    }

    public Page<SalesReceiptCompilationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return salesReceiptCompilationRepository.findAllWithEagerRelationships(pageable).map(salesReceiptCompilationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SalesReceiptCompilationDTO> findOne(Long id) {
        log.debug("Request to get SalesReceiptCompilation : {}", id);
        return salesReceiptCompilationRepository.findOneWithEagerRelationships(id).map(salesReceiptCompilationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SalesReceiptCompilation : {}", id);
        salesReceiptCompilationRepository.deleteById(id);
        salesReceiptCompilationSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesReceiptCompilationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SalesReceiptCompilations for query {}", query);
        return salesReceiptCompilationSearchRepository.search(query, pageable).map(salesReceiptCompilationMapper::toDto);
    }
}
