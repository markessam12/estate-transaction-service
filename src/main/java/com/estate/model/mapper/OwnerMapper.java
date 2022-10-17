package com.estate.model.mapper;

import com.estate.model.dao.OwnerDAO;
import com.estate.model.dto.OwnerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.ArrayList;

@Mapper
public interface OwnerMapper {
    OwnerMapper INSTANCE = Mappers.getMapper(OwnerMapper.class);

    OwnerDTO ownerDaoToDto(OwnerDAO ownerDAO);
    OwnerDAO ownerDtoToDao(OwnerDTO ownerDTO);
    ArrayList<OwnerDTO> ownerListDaoToDto(ArrayList<OwnerDAO> ownerDAOS);
}
