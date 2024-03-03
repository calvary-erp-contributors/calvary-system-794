package io.github.calvary.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.github.calvary.domain.TransferItem;
import io.github.calvary.repository.TransferItemRepository;
import io.github.calvary.repository.search.TransferItemSearchRepository;
import io.github.calvary.service.TransferItemService;
import io.github.calvary.service.dto.TransferItemDTO;
import io.github.calvary.service.mapper.TransferItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransferItem}.
 */
@Service
@Transactional
public class TransferItemServiceImpl implements TransferItemService {

    private final Logger log = LoggerFactory.getLogger(TransferItemServiceImpl.class);

    private final TransferItemRepository transferItemRepository;

    private final TransferItemMapper transferItemMapper;

    private final TransferItemSearchRepository transferItemSearchRepository;

    public TransferItemServiceImpl(
        TransferItemRepository transferItemRepository,
        TransferItemMapper transferItemMapper,
        TransferItemSearchRepository transferItemSearchRepository
    ) {
        this.transferItemRepository = transferItemRepository;
        this.transferItemMapper = transferItemMapper;
        this.transferItemSearchRepository = transferItemSearchRepository;
    }

    @Override
    public TransferItemDTO save(TransferItemDTO transferItemDTO) {
        log.debug("Request to save TransferItem : {}", transferItemDTO);
        TransferItem transferItem = transferItemMapper.toEntity(transferItemDTO);
        transferItem = transferItemRepository.save(transferItem);
        TransferItemDTO result = transferItemMapper.toDto(transferItem);
        transferItemSearchRepository.index(transferItem);
        return result;
    }

    @Override
    public TransferItemDTO update(TransferItemDTO transferItemDTO) {
        log.debug("Request to update TransferItem : {}", transferItemDTO);
        TransferItem transferItem = transferItemMapper.toEntity(transferItemDTO);
        transferItem = transferItemRepository.save(transferItem);
        TransferItemDTO result = transferItemMapper.toDto(transferItem);
        transferItemSearchRepository.index(transferItem);
        return result;
    }

    @Override
    public Optional<TransferItemDTO> partialUpdate(TransferItemDTO transferItemDTO) {
        log.debug("Request to partially update TransferItem : {}", transferItemDTO);

        return transferItemRepository
            .findById(transferItemDTO.getId())
            .map(existingTransferItem -> {
                transferItemMapper.partialUpdate(existingTransferItem, transferItemDTO);

                return existingTransferItem;
            })
            .map(transferItemRepository::save)
            .map(savedTransferItem -> {
                transferItemSearchRepository.save(savedTransferItem);

                return savedTransferItem;
            })
            .map(transferItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransferItems");
        return transferItemRepository.findAll(pageable).map(transferItemMapper::toDto);
    }

    public Page<TransferItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return transferItemRepository.findAllWithEagerRelationships(pageable).map(transferItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransferItemDTO> findOne(Long id) {
        log.debug("Request to get TransferItem : {}", id);
        return transferItemRepository.findOneWithEagerRelationships(id).map(transferItemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TransferItem : {}", id);
        transferItemRepository.deleteById(id);
        transferItemSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferItemDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransferItems for query {}", query);
        return transferItemSearchRepository.search(query, pageable).map(transferItemMapper::toDto);
    }
}
