package io.github.calvary.service.mapper;

import io.github.calvary.domain.ApplicationUser;
import io.github.calvary.domain.SalesReceiptProposal;
import io.github.calvary.service.dto.ApplicationUserDTO;
import io.github.calvary.service.dto.SalesReceiptProposalDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SalesReceiptProposal} and its DTO {@link SalesReceiptProposalDTO}.
 */
@Mapper(componentModel = "spring")
public interface SalesReceiptProposalMapper extends EntityMapper<SalesReceiptProposalDTO, SalesReceiptProposal> {
    @Mapping(target = "proposedBy", source = "proposedBy", qualifiedByName = "applicationUserApplicationIdentity")
    SalesReceiptProposalDTO toDto(SalesReceiptProposal s);

    @Named("applicationUserApplicationIdentity")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "applicationIdentity", source = "applicationIdentity")
    ApplicationUserDTO toDtoApplicationUserApplicationIdentity(ApplicationUser applicationUser);
}
