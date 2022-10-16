package com.example.model.mapper;

import com.example.model.dao.OwnerDAO;
import com.example.model.dao.TransactionDAO;
import com.example.model.dto.OwnerDTO;
import com.example.model.dto.TransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    TransactionDAO transactionDtoToDao(TransactionDTO transactionDTO);
    TransactionDTO transactionDaoToDto(TransactionDAO transactionDAO);
    ArrayList<TransactionDTO> transacitonListDaoToDto(ArrayList<TransactionDAO> transactionDAOS);
}
