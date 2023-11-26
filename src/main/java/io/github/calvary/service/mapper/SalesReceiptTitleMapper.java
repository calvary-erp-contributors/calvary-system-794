package io.github.calvary.service.mapper;

import io.github.calvary.domain.SalesReceiptTitle;
import io.github.calvary.service.dto.SalesReceiptTitleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SalesReceiptTitle} and its DTO {@link SalesReceiptTitleDTO}.
 */
@Mapper(componentModel = "spring")
public interface SalesReceiptTitleMapper extends EntityMapper<SalesReceiptTitleDTO, SalesReceiptTitle> {}
