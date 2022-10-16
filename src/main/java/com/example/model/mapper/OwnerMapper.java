package com.example.model.mapper;

import com.example.model.dao.OwnerDAO;
import com.example.model.dto.OwnerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface OwnerMapper {
    OwnerMapper INSTANCE = Mappers.getMapper(OwnerMapper.class);

    OwnerDAO ownerDtoToDao(OwnerDTO ownerDTO);
    OwnerDTO ownerDaoToDto(OwnerDAO ownerDAO);
    ArrayList<OwnerDTO> ownerListDaoToDto(ArrayList<OwnerDAO> ownerDAOS);
}
