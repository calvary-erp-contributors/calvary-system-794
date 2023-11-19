package io.github.calvary.service.mapper;

import io.github.calvary.domain.TransactionClass;
import io.github.calvary.service.dto.TransactionClassDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransactionClass} and its DTO {@link TransactionClassDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionClassMapper extends EntityMapper<TransactionClassDTO, TransactionClass> {}
