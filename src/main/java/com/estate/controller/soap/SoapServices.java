package com.estate.controller.soap;

import com.estate.exception.DataAlreadyExistsException;
import com.estate.exception.DataNotFoundException;
import com.estate.exception.RequestFailedException;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.OwnerDTO;
import com.estate.model.dto.PropertyDTO;
import com.estate.model.dto.TransactionDTO;
import com.estate.model.mapper.OwnerMapper;
import com.estate.model.mapper.PropertyMapper;
import com.estate.model.mapper.TransactionMapper;
import com.estate.service.OwnerServiceImp;
import com.estate.service.PropertyServiceImp;
import com.estate.service.TransactionServiceImp;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public class SoapServices {
    @WebMethod
    public OwnerDTO addUser(String userName, String firstName, String lastName, Long balance) throws DataAlreadyExistsException {
        OwnerDAO ownerDAO = OwnerServiceImp.getInstance().addOwner(userName, firstName, lastName, balance);
        return OwnerMapper.INSTANCE.ownerDaoToDto(ownerDAO);
    }

    @WebMethod
    public PropertyDTO addProperty(String userName, String propertyAddress, long cost) throws DataNotFoundException {
        PropertyDAO propertyDAO = PropertyServiceImp.getInstance().addProperty(userName, propertyAddress, cost);
        return PropertyMapper.INSTANCE.propertyDaoToDto(propertyDAO);
    }

    @WebMethod
    public OwnerDTO addBalance(String userName, int balance) throws DataNotFoundException {
        OwnerDAO ownerDAO = OwnerServiceImp.getInstance().addToOwnerBalance(
            OwnerServiceImp.getInstance().getOwner(userName),
            balance);
        return OwnerMapper.INSTANCE.ownerDaoToDto(ownerDAO);
    }

    @WebMethod
    public TransactionDTO buyProperty(String buyerID, int propertyID) throws DataNotFoundException, RequestFailedException {
        TransactionDAO transactionDAO = TransactionServiceImp.getInstance().makeTransaction(buyerID, propertyID);
        return TransactionMapper.INSTANCE.transactionDaoToDto(transactionDAO);
    }
}
