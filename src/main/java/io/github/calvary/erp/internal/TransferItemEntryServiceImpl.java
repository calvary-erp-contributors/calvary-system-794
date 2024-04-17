package io.github.calvary.erp.internal;

import io.github.calvary.domain.TransactionItemEntry;
import io.github.calvary.domain.TransferItemEntry;
import io.github.calvary.erp.repository.InternalTransferItemEntryRepository;
import io.github.calvary.repository.search.TransferItemEntrySearchRepository;
import io.github.calvary.service.dto.SalesReceiptDTO;
import io.github.calvary.service.dto.TransferItemEntryDTO;
import io.github.calvary.service.mapper.TransferItemEntryMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransferItemEntry}.
 */
@Service
@Transactional
public class TransferItemEntryServiceImpl implements InternalTransferItemEntryService {

    private final Logger log = LoggerFactory.getLogger(TransferItemEntryServiceImpl.class);

    private final InternalTransferItemEntryRepository transferItemEntryRepository;

    private final TransferItemEntryMapper transferItemEntryMapper;

    private final TransferItemEntrySearchRepository transferItemEntrySearchRepository;

    public TransferItemEntryServiceImpl(
        InternalTransferItemEntryRepository transferItemEntryRepository,
        TransferItemEntryMapper transferItemEntryMapper,
        TransferItemEntrySearchRepository transferItemEntrySearchRepository
    ) {
        this.transferItemEntryRepository = transferItemEntryRepository;
        this.transferItemEntryMapper = transferItemEntryMapper;
        this.transferItemEntrySearchRepository = transferItemEntrySearchRepository;
    }

    @Override
    public TransferItemEntryDTO save(TransferItemEntryDTO transferItemEntryDTO) {
        log.debug("Request to save TransferItemEntry : {}", transferItemEntryDTO);
        TransferItemEntry transferItemEntry = transferItemEntryMapper.toEntity(transferItemEntryDTO);
        transferItemEntry = transferItemEntryRepository.save(transferItemEntry);
        TransferItemEntryDTO result = transferItemEntryMapper.toDto(transferItemEntry);
        transferItemEntrySearchRepository.index(transferItemEntry);
        return result;
    }

    @Override
    public TransferItemEntryDTO update(TransferItemEntryDTO transferItemEntryDTO) {
        log.debug("Request to update TransferItemEntry : {}", transferItemEntryDTO);
        TransferItemEntry transferItemEntry = transferItemEntryMapper.toEntity(transferItemEntryDTO);
        transferItemEntry = transferItemEntryRepository.save(transferItemEntry);
        TransferItemEntryDTO result = transferItemEntryMapper.toDto(transferItemEntry);
        transferItemEntrySearchRepository.index(transferItemEntry);
        return result;
    }

    @Override
    public Optional<TransferItemEntryDTO> partialUpdate(TransferItemEntryDTO transferItemEntryDTO) {
        log.debug("Request to partially update TransferItemEntry : {}", transferItemEntryDTO);

        return transferItemEntryRepository
            .findById(transferItemEntryDTO.getId())
            .map(existingTransferItemEntry -> {
                transferItemEntryMapper.partialUpdate(existingTransferItemEntry, transferItemEntryDTO);

                return existingTransferItemEntry;
            })
            .map(transferItemEntryRepository::save)
            .map(savedTransferItemEntry -> {
                transferItemEntrySearchRepository.save(savedTransferItemEntry);

                return savedTransferItemEntry;
            })
            .map(transferItemEntryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferItemEntryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransferItemEntries");
        return transferItemEntryRepository.findAll(pageable).map(transferItemEntryMapper::toDto);
    }

    public Page<TransferItemEntryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return transferItemEntryRepository.findAllWithEagerRelationships(pageable).map(transferItemEntryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransferItemEntryDTO> findOne(Long id) {
        log.debug("Request to get TransferItemEntry : {}", id);
        return transferItemEntryRepository.findOneWithEagerRelationships(id).map(transferItemEntryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TransferItemEntry : {}", id);
        transferItemEntryRepository.deleteById(id);
        transferItemEntrySearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferItemEntryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransferItemEntries for query {}", query);
        return transferItemEntrySearchRepository.search(query, pageable).map(transferItemEntryMapper::toDto);
    }

    /**
     * Get list of transfer-items related to a given sales-receipt item
     *
     * @param salesReceipt Parent owning the transaction-items we seek
     * @return Optional list of related transaction-items
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<List<TransferItemEntryDTO>> findItemsRelatedToSalesReceipt(SalesReceiptDTO salesReceipt) {
        log.debug("Request to find transfer-items related to transfer-item-entries for sales-receipt id: {}", salesReceipt.getId());
        return transferItemEntryRepository.findAllBySalesReceiptId(salesReceipt.getId()).map(transferItemEntryMapper::toDto);
    }
}
