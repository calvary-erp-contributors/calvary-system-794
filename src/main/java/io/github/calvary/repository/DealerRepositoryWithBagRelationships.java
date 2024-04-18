package io.github.calvary.repository;

import io.github.calvary.domain.Dealer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface DealerRepositoryWithBagRelationships {
    Optional<Dealer> fetchBagRelationships(Optional<Dealer> dealer);

    List<Dealer> fetchBagRelationships(List<Dealer> dealers);

    Page<Dealer> fetchBagRelationships(Page<Dealer> dealers);
}
