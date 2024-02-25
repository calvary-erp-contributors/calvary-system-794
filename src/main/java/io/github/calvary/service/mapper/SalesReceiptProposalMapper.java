package io.github.calvary.service.mapper;

import io.github.calvary.domain.SalesReceiptProposal;
import io.github.calvary.service.dto.SalesReceiptProposalDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SalesReceiptProposal} and its DTO {@link SalesReceiptProposalDTO}.
 */
@Mapper(componentModel = "spring")
public interface SalesReceiptProposalMapper extends EntityMapper<SalesReceiptProposalDTO, SalesReceiptProposal> {}
