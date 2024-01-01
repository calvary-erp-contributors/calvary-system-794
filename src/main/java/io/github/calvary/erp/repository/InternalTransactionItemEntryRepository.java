package io.github.calvary.erp.repository;

import io.github.calvary.domain.TransactionItemEntry;
import io.github.calvary.repository.TransactionItemEntryRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InternalTransactionItemEntryRepository
    extends JpaRepository<TransactionItemEntry, Long>, JpaSpecificationExecutor<TransactionItemEntry> {
    List<TransactionItemEntry> findAllBySalesReceiptId(Long salesReceiptId);
}
