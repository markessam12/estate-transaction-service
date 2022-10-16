package com.example.model.mapper;

import com.example.model.dao.OwnerDAO;
import com.example.model.dao.PropertyDAO;
import com.example.model.dao.TransactionDAO;
import com.example.model.dto.TransactionDTO;
import org.luaj.vm2.ast.Str;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    TransactionDTO transactionDaoToDto(TransactionDAO transactionDAO);

    @Mapping(target = "buyer", source = "buyer")
    @Mapping(target = "seller", source = "seller")
    @Mapping(target = "property", source = "property")
    ArrayList<TransactionDTO> transactionListDaoToDto(ArrayList<TransactionDAO> transactionDAOS);

    default String map(OwnerDAO value){
        return value.getUserName();
    }

    default int map(PropertyDAO value){
        return value.getPropertyId();
    }
}
