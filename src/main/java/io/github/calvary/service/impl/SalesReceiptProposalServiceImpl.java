package io.github.calvary.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.github.calvary.domain.SalesReceiptProposal;
import io.github.calvary.repository.SalesReceiptProposalRepository;
import io.github.calvary.repository.search.SalesReceiptProposalSearchRepository;
import io.github.calvary.service.SalesReceiptProposalService;
import io.github.calvary.service.dto.SalesReceiptProposalDTO;
import io.github.calvary.service.mapper.SalesReceiptProposalMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SalesReceiptProposal}.
 */
@Service
@Transactional
public class SalesReceiptProposalServiceImpl implements SalesReceiptProposalService {

    private final Logger log = LoggerFactory.getLogger(SalesReceiptProposalServiceImpl.class);

    private final SalesReceiptProposalRepository salesReceiptProposalRepository;

    private final SalesReceiptProposalMapper salesReceiptProposalMapper;

    private final SalesReceiptProposalSearchRepository salesReceiptProposalSearchRepository;

    public SalesReceiptProposalServiceImpl(
        SalesReceiptProposalRepository salesReceiptProposalRepository,
        SalesReceiptProposalMapper salesReceiptProposalMapper,
        SalesReceiptProposalSearchRepository salesReceiptProposalSearchRepository
    ) {
        this.salesReceiptProposalRepository = salesReceiptProposalRepository;
        this.salesReceiptProposalMapper = salesReceiptProposalMapper;
        this.salesReceiptProposalSearchRepository = salesReceiptProposalSearchRepository;
    }

    @Override
    public SalesReceiptProposalDTO save(SalesReceiptProposalDTO salesReceiptProposalDTO) {
        log.debug("Request to save SalesReceiptProposal : {}", salesReceiptProposalDTO);
        SalesReceiptProposal salesReceiptProposal = salesReceiptProposalMapper.toEntity(salesReceiptProposalDTO);
        salesReceiptProposal = salesReceiptProposalRepository.save(salesReceiptProposal);
        SalesReceiptProposalDTO result = salesReceiptProposalMapper.toDto(salesReceiptProposal);
        salesReceiptProposalSearchRepository.index(salesReceiptProposal);
        return result;
    }

    @Override
    public SalesReceiptProposalDTO update(SalesReceiptProposalDTO salesReceiptProposalDTO) {
        log.debug("Request to update SalesReceiptProposal : {}", salesReceiptProposalDTO);
        SalesReceiptProposal salesReceiptProposal = salesReceiptProposalMapper.toEntity(salesReceiptProposalDTO);
        salesReceiptProposal = salesReceiptProposalRepository.save(salesReceiptProposal);
        SalesReceiptProposalDTO result = salesReceiptProposalMapper.toDto(salesReceiptProposal);
        salesReceiptProposalSearchRepository.index(salesReceiptProposal);
        return result;
    }

    @Override
    public Optional<SalesReceiptProposalDTO> partialUpdate(SalesReceiptProposalDTO salesReceiptProposalDTO) {
        log.debug("Request to partially update SalesReceiptProposal : {}", salesReceiptProposalDTO);

        return salesReceiptProposalRepository
            .findById(salesReceiptProposalDTO.getId())
            .map(existingSalesReceiptProposal -> {
                salesReceiptProposalMapper.partialUpdate(existingSalesReceiptProposal, salesReceiptProposalDTO);

                return existingSalesReceiptProposal;
            })
            .map(salesReceiptProposalRepository::save)
            .map(savedSalesReceiptProposal -> {
                salesReceiptProposalSearchRepository.save(savedSalesReceiptProposal);

                return savedSalesReceiptProposal;
            })
            .map(salesReceiptProposalMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesReceiptProposalDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SalesReceiptProposals");
        return salesReceiptProposalRepository.findAll(pageable).map(salesReceiptProposalMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SalesReceiptProposalDTO> findOne(Long id) {
        log.debug("Request to get SalesReceiptProposal : {}", id);
        return salesReceiptProposalRepository.findById(id).map(salesReceiptProposalMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SalesReceiptProposal : {}", id);
        salesReceiptProposalRepository.deleteById(id);
        salesReceiptProposalSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesReceiptProposalDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SalesReceiptProposals for query {}", query);
        return salesReceiptProposalSearchRepository.search(query, pageable).map(salesReceiptProposalMapper::toDto);
    }
}
