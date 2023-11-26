package io.github.calvary.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.github.calvary.domain.TransactionItemAmount;
import io.github.calvary.repository.TransactionItemAmountRepository;
import io.github.calvary.repository.search.TransactionItemAmountSearchRepository;
import io.github.calvary.service.TransactionItemAmountService;
import io.github.calvary.service.dto.TransactionItemAmountDTO;
import io.github.calvary.service.mapper.TransactionItemAmountMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransactionItemAmount}.
 */
@Service
@Transactional
public class TransactionItemAmountServiceImpl implements TransactionItemAmountService {

    private final Logger log = LoggerFactory.getLogger(TransactionItemAmountServiceImpl.class);

    private final TransactionItemAmountRepository transactionItemAmountRepository;

    private final TransactionItemAmountMapper transactionItemAmountMapper;

    private final TransactionItemAmountSearchRepository transactionItemAmountSearchRepository;

    public TransactionItemAmountServiceImpl(
        TransactionItemAmountRepository transactionItemAmountRepository,
        TransactionItemAmountMapper transactionItemAmountMapper,
        TransactionItemAmountSearchRepository transactionItemAmountSearchRepository
    ) {
        this.transactionItemAmountRepository = transactionItemAmountRepository;
        this.transactionItemAmountMapper = transactionItemAmountMapper;
        this.transactionItemAmountSearchRepository = transactionItemAmountSearchRepository;
    }

    @Override
    public TransactionItemAmountDTO save(TransactionItemAmountDTO transactionItemAmountDTO) {
        log.debug("Request to save TransactionItemAmount : {}", transactionItemAmountDTO);
        TransactionItemAmount transactionItemAmount = transactionItemAmountMapper.toEntity(transactionItemAmountDTO);
        transactionItemAmount = transactionItemAmountRepository.save(transactionItemAmount);
        TransactionItemAmountDTO result = transactionItemAmountMapper.toDto(transactionItemAmount);
        transactionItemAmountSearchRepository.index(transactionItemAmount);
        return result;
    }

    @Override
    public TransactionItemAmountDTO update(TransactionItemAmountDTO transactionItemAmountDTO) {
        log.debug("Request to update TransactionItemAmount : {}", transactionItemAmountDTO);
        TransactionItemAmount transactionItemAmount = transactionItemAmountMapper.toEntity(transactionItemAmountDTO);
        transactionItemAmount = transactionItemAmountRepository.save(transactionItemAmount);
        TransactionItemAmountDTO result = transactionItemAmountMapper.toDto(transactionItemAmount);
        transactionItemAmountSearchRepository.index(transactionItemAmount);
        return result;
    }

    @Override
    public Optional<TransactionItemAmountDTO> partialUpdate(TransactionItemAmountDTO transactionItemAmountDTO) {
        log.debug("Request to partially update TransactionItemAmount : {}", transactionItemAmountDTO);

        return transactionItemAmountRepository
            .findById(transactionItemAmountDTO.getId())
            .map(existingTransactionItemAmount -> {
                transactionItemAmountMapper.partialUpdate(existingTransactionItemAmount, transactionItemAmountDTO);

                return existingTransactionItemAmount;
            })
            .map(transactionItemAmountRepository::save)
            .map(savedTransactionItemAmount -> {
                transactionItemAmountSearchRepository.save(savedTransactionItemAmount);

                return savedTransactionItemAmount;
            })
            .map(transactionItemAmountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionItemAmountDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransactionItemAmounts");
        return transactionItemAmountRepository.findAll(pageable).map(transactionItemAmountMapper::toDto);
    }

    public Page<TransactionItemAmountDTO> findAllWithEagerRelationships(Pageable pageable) {
        return transactionItemAmountRepository.findAllWithEagerRelationships(pageable).map(transactionItemAmountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionItemAmountDTO> findOne(Long id) {
        log.debug("Request to get TransactionItemAmount : {}", id);
        return transactionItemAmountRepository.findOneWithEagerRelationships(id).map(transactionItemAmountMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TransactionItemAmount : {}", id);
        transactionItemAmountRepository.deleteById(id);
        transactionItemAmountSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionItemAmountDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransactionItemAmounts for query {}", query);
        return transactionItemAmountSearchRepository.search(query, pageable).map(transactionItemAmountMapper::toDto);
    }
}
