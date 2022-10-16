package com.example.model.mapper;

import com.example.model.dao.OwnerDAO;
import com.example.model.dao.PropertyDAO;
import com.example.model.dto.OwnerDTO;
import com.example.model.dto.PropertyDTO;
import com.example.resources.AerospikeReader;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface PropertyMapper {
    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);

    PropertyDAO propertyDtoToDao(PropertyDTO propertyDTO);
    PropertyDTO propertyDaoToDto(PropertyDAO propertyDAO);
    ArrayList<PropertyDTO> propertyListDaoToDto(ArrayList<PropertyDAO> propertyDAOS);

    default String map(OwnerDAO value){
        return value.getUserName();
    }

    default OwnerDAO map(String userName){
        AerospikeReader<OwnerDAO> reader = new AerospikeReader<OwnerDAO>(OwnerDAO.class);
        return reader.getRow(userName);
    }
}
