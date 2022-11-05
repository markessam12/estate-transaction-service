package com.estate.model.mapper;

import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dto.PropertyDTO;
import com.estate.repository.AerospikeAccess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface PropertyMapper {
    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);

    @Mapping(target = "links", ignore = true)
    PropertyDTO propertyDaoToDto(PropertyDAO propertyDAO);
    PropertyDAO propertyDtoToDao(PropertyDTO propertyDTO);
    ArrayList<PropertyDTO> propertyListDaoToDto(ArrayList<PropertyDAO> propertyDAOS);

    default String map(OwnerDAO value){
        return value.getUserName();
    }

    default OwnerDAO map(String userName){
        AerospikeAccess<OwnerDAO> reader = new AerospikeAccess<>(OwnerDAO.class);
        return reader.getRecord(userName);
    }


}
