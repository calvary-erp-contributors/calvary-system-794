package io.github.calvary.repository;

import io.github.calvary.domain.SalesReceipt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface SalesReceiptRepositoryWithBagRelationships {
    Optional<SalesReceipt> fetchBagRelationships(Optional<SalesReceipt> salesReceipt);

    List<SalesReceipt> fetchBagRelationships(List<SalesReceipt> salesReceipts);

    Page<SalesReceipt> fetchBagRelationships(Page<SalesReceipt> salesReceipts);
}
