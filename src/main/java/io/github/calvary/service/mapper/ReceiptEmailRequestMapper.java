package io.github.calvary.service.mapper;

import io.github.calvary.domain.ApplicationUser;
import io.github.calvary.domain.ReceiptEmailRequest;
import io.github.calvary.service.dto.ApplicationUserDTO;
import io.github.calvary.service.dto.ReceiptEmailRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReceiptEmailRequest} and its DTO {@link ReceiptEmailRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReceiptEmailRequestMapper extends EntityMapper<ReceiptEmailRequestDTO, ReceiptEmailRequest> {
    @Mapping(target = "requestedBy", source = "requestedBy", qualifiedByName = "applicationUserApplicationIdentity")
    ReceiptEmailRequestDTO toDto(ReceiptEmailRequest s);

    @Named("applicationUserApplicationIdentity")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "applicationIdentity", source = "applicationIdentity")
    ApplicationUserDTO toDtoApplicationUserApplicationIdentity(ApplicationUser applicationUser);
}
