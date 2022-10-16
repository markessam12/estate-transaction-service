package com.example.model.mapper;

import com.example.model.dao.OwnerDAO;
import com.example.model.dto.OwnerDTO;
import com.example.resources.AerospikeReader;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import javax.validation.groups.Default;
import java.util.ArrayList;

@Mapper
public interface OwnerMapper {
    OwnerMapper INSTANCE = Mappers.getMapper(OwnerMapper.class);

    OwnerDAO ownerDtoToDao(OwnerDTO ownerDTO);
    OwnerDTO ownerDaoToDto(OwnerDAO ownerDAO);
    ArrayList<OwnerDTO> ownerListDaoToDto(ArrayList<OwnerDAO> ownerDAOS);

    default OwnerDAO map(String userName){
        AerospikeReader<OwnerDAO> reader = new AerospikeReader<OwnerDAO>(OwnerDAO.class);
        return reader.getRow(userName);
    }

    default String map(OwnerDAO owner){
        return owner.getUserName();
    }
}
