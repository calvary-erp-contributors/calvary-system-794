package io.github.calvary.service.mapper;

import io.github.calvary.domain.Dealer;
import io.github.calvary.domain.DealerType;
import io.github.calvary.domain.SalesReceiptEmailPersona;
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.dto.DealerTypeDTO;
import io.github.calvary.service.dto.SalesReceiptEmailPersonaDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Dealer} and its DTO {@link DealerDTO}.
 */
@Mapper(componentModel = "spring")
public interface DealerMapper extends EntityMapper<DealerDTO, Dealer> {
    @Mapping(target = "dealerType", source = "dealerType", qualifiedByName = "dealerTypeName")
    @Mapping(
        target = "salesReceiptEmailPersonas",
        source = "salesReceiptEmailPersonas",
        qualifiedByName = "salesReceiptEmailPersonaPreferredGreetingDesignationSet"
    )
    DealerDTO toDto(Dealer s);

    @Mapping(target = "removeSalesReceiptEmailPersona", ignore = true)
    Dealer toEntity(DealerDTO dealerDTO);

    @Named("dealerTypeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DealerTypeDTO toDtoDealerTypeName(DealerType dealerType);

    @Named("salesReceiptEmailPersonaPreferredGreetingDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "preferredGreetingDesignation", source = "preferredGreetingDesignation")
    SalesReceiptEmailPersonaDTO toDtoSalesReceiptEmailPersonaPreferredGreetingDesignation(
        SalesReceiptEmailPersona salesReceiptEmailPersona
    );

    @Named("salesReceiptEmailPersonaPreferredGreetingDesignationSet")
    default Set<SalesReceiptEmailPersonaDTO> toDtoSalesReceiptEmailPersonaPreferredGreetingDesignationSet(
        Set<SalesReceiptEmailPersona> salesReceiptEmailPersona
    ) {
        return salesReceiptEmailPersona
            .stream()
            .map(this::toDtoSalesReceiptEmailPersonaPreferredGreetingDesignation)
            .collect(Collectors.toSet());
    }
}
