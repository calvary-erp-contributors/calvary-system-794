package io.github.calvary.service.mapper;

import io.github.calvary.domain.ApplicationUser;
import io.github.calvary.domain.SalesReceiptCompilation;
import io.github.calvary.service.dto.ApplicationUserDTO;
import io.github.calvary.service.dto.SalesReceiptCompilationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SalesReceiptCompilation} and its DTO {@link SalesReceiptCompilationDTO}.
 */
@Mapper(componentModel = "spring")
public interface SalesReceiptCompilationMapper extends EntityMapper<SalesReceiptCompilationDTO, SalesReceiptCompilation> {
    @Mapping(target = "compiledBy", source = "compiledBy", qualifiedByName = "applicationUserApplicationIdentity")
    SalesReceiptCompilationDTO toDto(SalesReceiptCompilation s);

    @Named("applicationUserApplicationIdentity")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "applicationIdentity", source = "applicationIdentity")
    ApplicationUserDTO toDtoApplicationUserApplicationIdentity(ApplicationUser applicationUser);
}
