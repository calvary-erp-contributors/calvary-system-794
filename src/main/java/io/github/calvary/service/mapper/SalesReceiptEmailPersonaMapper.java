package io.github.calvary.service.mapper;

import io.github.calvary.domain.ApplicationUser;
import io.github.calvary.domain.Dealer;
import io.github.calvary.domain.SalesReceiptEmailPersona;
import io.github.calvary.service.dto.ApplicationUserDTO;
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.dto.SalesReceiptEmailPersonaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SalesReceiptEmailPersona} and its DTO {@link SalesReceiptEmailPersonaDTO}.
 */
@Mapper(componentModel = "spring")
public interface SalesReceiptEmailPersonaMapper extends EntityMapper<SalesReceiptEmailPersonaDTO, SalesReceiptEmailPersona> {
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "applicationUserApplicationIdentity")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy", qualifiedByName = "applicationUserApplicationIdentity")
    @Mapping(target = "contributor", source = "contributor", qualifiedByName = "dealerName")
    SalesReceiptEmailPersonaDTO toDto(SalesReceiptEmailPersona s);

    @Named("applicationUserApplicationIdentity")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "applicationIdentity", source = "applicationIdentity")
    ApplicationUserDTO toDtoApplicationUserApplicationIdentity(ApplicationUser applicationUser);

    @Named("dealerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DealerDTO toDtoDealerName(Dealer dealer);
}
