package io.github.calvary.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.github.calvary.domain.TransactionClass;
import io.github.calvary.repository.TransactionClassRepository;
import io.github.calvary.repository.search.TransactionClassSearchRepository;
import io.github.calvary.service.TransactionClassService;
import io.github.calvary.service.dto.TransactionClassDTO;
import io.github.calvary.service.mapper.TransactionClassMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransactionClass}.
 */
@Service
@Transactional
public class TransactionClassServiceImpl implements TransactionClassService {

    private final Logger log = LoggerFactory.getLogger(TransactionClassServiceImpl.class);

    private final TransactionClassRepository transactionClassRepository;

    private final TransactionClassMapper transactionClassMapper;

    private final TransactionClassSearchRepository transactionClassSearchRepository;

    public TransactionClassServiceImpl(
        TransactionClassRepository transactionClassRepository,
        TransactionClassMapper transactionClassMapper,
        TransactionClassSearchRepository transactionClassSearchRepository
    ) {
        this.transactionClassRepository = transactionClassRepository;
        this.transactionClassMapper = transactionClassMapper;
        this.transactionClassSearchRepository = transactionClassSearchRepository;
    }

    @Override
    public TransactionClassDTO save(TransactionClassDTO transactionClassDTO) {
        log.debug("Request to save TransactionClass : {}", transactionClassDTO);
        TransactionClass transactionClass = transactionClassMapper.toEntity(transactionClassDTO);
        transactionClass = transactionClassRepository.save(transactionClass);
        TransactionClassDTO result = transactionClassMapper.toDto(transactionClass);
        transactionClassSearchRepository.index(transactionClass);
        return result;
    }

    @Override
    public TransactionClassDTO update(TransactionClassDTO transactionClassDTO) {
        log.debug("Request to update TransactionClass : {}", transactionClassDTO);
        TransactionClass transactionClass = transactionClassMapper.toEntity(transactionClassDTO);
        transactionClass = transactionClassRepository.save(transactionClass);
        TransactionClassDTO result = transactionClassMapper.toDto(transactionClass);
        transactionClassSearchRepository.index(transactionClass);
        return result;
    }

    @Override
    public Optional<TransactionClassDTO> partialUpdate(TransactionClassDTO transactionClassDTO) {
        log.debug("Request to partially update TransactionClass : {}", transactionClassDTO);

        return transactionClassRepository
            .findById(transactionClassDTO.getId())
            .map(existingTransactionClass -> {
                transactionClassMapper.partialUpdate(existingTransactionClass, transactionClassDTO);

                return existingTransactionClass;
            })
            .map(transactionClassRepository::save)
            .map(savedTransactionClass -> {
                transactionClassSearchRepository.save(savedTransactionClass);

                return savedTransactionClass;
            })
            .map(transactionClassMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionClassDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransactionClasses");
        return transactionClassRepository.findAll(pageable).map(transactionClassMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionClassDTO> findOne(Long id) {
        log.debug("Request to get TransactionClass : {}", id);
        return transactionClassRepository.findById(id).map(transactionClassMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TransactionClass : {}", id);
        transactionClassRepository.deleteById(id);
        transactionClassSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionClassDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransactionClasses for query {}", query);
        return transactionClassSearchRepository.search(query, pageable).map(transactionClassMapper::toDto);
    }
}
