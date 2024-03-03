package io.github.calvary.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.github.calvary.domain.ReceiptEmailRequest;
import io.github.calvary.repository.ReceiptEmailRequestRepository;
import io.github.calvary.repository.search.ReceiptEmailRequestSearchRepository;
import io.github.calvary.service.ReceiptEmailRequestService;
import io.github.calvary.service.dto.ReceiptEmailRequestDTO;
import io.github.calvary.service.mapper.ReceiptEmailRequestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ReceiptEmailRequest}.
 */
@Service
@Transactional
public class ReceiptEmailRequestServiceImpl implements ReceiptEmailRequestService {

    private final Logger log = LoggerFactory.getLogger(ReceiptEmailRequestServiceImpl.class);

    private final ReceiptEmailRequestRepository receiptEmailRequestRepository;

    private final ReceiptEmailRequestMapper receiptEmailRequestMapper;

    private final ReceiptEmailRequestSearchRepository receiptEmailRequestSearchRepository;

    public ReceiptEmailRequestServiceImpl(
        ReceiptEmailRequestRepository receiptEmailRequestRepository,
        ReceiptEmailRequestMapper receiptEmailRequestMapper,
        ReceiptEmailRequestSearchRepository receiptEmailRequestSearchRepository
    ) {
        this.receiptEmailRequestRepository = receiptEmailRequestRepository;
        this.receiptEmailRequestMapper = receiptEmailRequestMapper;
        this.receiptEmailRequestSearchRepository = receiptEmailRequestSearchRepository;
    }

    @Override
    public ReceiptEmailRequestDTO save(ReceiptEmailRequestDTO receiptEmailRequestDTO) {
        log.debug("Request to save ReceiptEmailRequest : {}", receiptEmailRequestDTO);
        ReceiptEmailRequest receiptEmailRequest = receiptEmailRequestMapper.toEntity(receiptEmailRequestDTO);
        receiptEmailRequest = receiptEmailRequestRepository.save(receiptEmailRequest);
        ReceiptEmailRequestDTO result = receiptEmailRequestMapper.toDto(receiptEmailRequest);
        receiptEmailRequestSearchRepository.index(receiptEmailRequest);
        return result;
    }

    @Override
    public ReceiptEmailRequestDTO update(ReceiptEmailRequestDTO receiptEmailRequestDTO) {
        log.debug("Request to update ReceiptEmailRequest : {}", receiptEmailRequestDTO);
        ReceiptEmailRequest receiptEmailRequest = receiptEmailRequestMapper.toEntity(receiptEmailRequestDTO);
        receiptEmailRequest = receiptEmailRequestRepository.save(receiptEmailRequest);
        ReceiptEmailRequestDTO result = receiptEmailRequestMapper.toDto(receiptEmailRequest);
        receiptEmailRequestSearchRepository.index(receiptEmailRequest);
        return result;
    }

    @Override
    public Optional<ReceiptEmailRequestDTO> partialUpdate(ReceiptEmailRequestDTO receiptEmailRequestDTO) {
        log.debug("Request to partially update ReceiptEmailRequest : {}", receiptEmailRequestDTO);

        return receiptEmailRequestRepository
            .findById(receiptEmailRequestDTO.getId())
            .map(existingReceiptEmailRequest -> {
                receiptEmailRequestMapper.partialUpdate(existingReceiptEmailRequest, receiptEmailRequestDTO);

                return existingReceiptEmailRequest;
            })
            .map(receiptEmailRequestRepository::save)
            .map(savedReceiptEmailRequest -> {
                receiptEmailRequestSearchRepository.save(savedReceiptEmailRequest);

                return savedReceiptEmailRequest;
            })
            .map(receiptEmailRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceiptEmailRequestDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ReceiptEmailRequests");
        return receiptEmailRequestRepository.findAll(pageable).map(receiptEmailRequestMapper::toDto);
    }

    public Page<ReceiptEmailRequestDTO> findAllWithEagerRelationships(Pageable pageable) {
        return receiptEmailRequestRepository.findAllWithEagerRelationships(pageable).map(receiptEmailRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReceiptEmailRequestDTO> findOne(Long id) {
        log.debug("Request to get ReceiptEmailRequest : {}", id);
        return receiptEmailRequestRepository.findOneWithEagerRelationships(id).map(receiptEmailRequestMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ReceiptEmailRequest : {}", id);
        receiptEmailRequestRepository.deleteById(id);
        receiptEmailRequestSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceiptEmailRequestDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ReceiptEmailRequests for query {}", query);
        return receiptEmailRequestSearchRepository.search(query, pageable).map(receiptEmailRequestMapper::toDto);
    }
}
