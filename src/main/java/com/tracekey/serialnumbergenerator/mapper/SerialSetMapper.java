package com.tracekey.serialnumbergenerator.mapper;

import com.tracekey.serialnumbergenerator.dto.SerialSetRequest;
import com.tracekey.serialnumbergenerator.dto.SerialSetResponse;
import com.tracekey.serialnumbergenerator.entity.SerialSet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SerialSetMapper {

    @Mappings({
            @Mapping(source = "serialSetRequest.serialLength", target = "serialLength"),
            @Mapping(source = "serialSetRequest.configuration", target = "configuration"),
            @Mapping(source = "serialSetRequest.number", target = "number"),
            @Mapping(source = "serialSetRequest.lowerCase", target = "lowerCase"),
            @Mapping(source = "serialSetRequest.upperCase", target = "upperCase"),
            @Mapping(source = "serialSetRequest.exclusions", target = "exclusions")
    })
    SerialSet mapRequestDtoToEntity(SerialSetRequest serialSetRequest);

    @Mappings({
            @Mapping(source = "serialSet.name", target = "name"),
            @Mapping(source = "serialSet.quantity", target = "quantity"),
            @Mapping(source = "serialSet.createdDate", target = "createdDate"),
            @Mapping(source = "serialSet.serialNumbers", target = "serialNumberResponseList")
    })
    SerialSetResponse mapEntityToResponseDto(SerialSet serialSet);


}
