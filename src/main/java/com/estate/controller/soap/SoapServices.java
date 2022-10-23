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
import com.estate.service.OwnerService;
import com.estate.service.PropertyService;
import com.estate.service.TransactionService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

/**
 * This class represents the Soap web service of the project.
 */
@WebService
public class SoapServices {
    /**
     * Add new owner to the database.
     *
     * @param userName  the username
     * @param firstName the first name
     * @param lastName  the last name
     * @param balance   the balance
     * @return the owner in dto presentation
     * @throws DataAlreadyExistsException the data already exists exception
     */
    @WebMethod(operationName = "add_new_owner")
        public OwnerDTO addOwner(String userName, String firstName, String lastName, Long balance) throws DataAlreadyExistsException {
        OwnerDAO ownerDAO = OwnerService.getInstance().addOwner(userName, firstName, lastName, balance);
        return OwnerMapper.INSTANCE.ownerDaoToDto(ownerDAO);
    }

    /**
     * Add new property to the database. The system assumes that a property can't exist without having an owner.
     *
     * @param userName        the owner username
     * @param propertyAddress the property address
     * @param cost            the cost
     * @return the property in dto presentation
     * @throws DataNotFoundException the data not found exception
     */
    @WebMethod(operationName = "add_new_property")
    public PropertyDTO addProperty(String userName, String propertyAddress, long cost) throws DataNotFoundException {
        PropertyDAO propertyDAO = PropertyService.getInstance().addProperty(userName, propertyAddress, cost);
        return PropertyMapper.INSTANCE.propertyDaoToDto(propertyDAO);
    }

    /**
     * Add balance to existing owner.
     *
     * @param userName the owner username
     * @param balance  the balance
     * @return the owner with the new balance in dto presentation
     * @throws DataNotFoundException the data not found exception
     */
    @WebMethod(operationName = "add_balance_to_owner")
    public OwnerDTO addBalance(String userName, int balance) throws DataNotFoundException {
        OwnerDAO ownerDAO = OwnerService.getInstance().getOwner(userName);
        ownerDAO = OwnerService.getInstance().addToOwnerBalance(ownerDAO, balance);
        return OwnerMapper.INSTANCE.ownerDaoToDto(ownerDAO);
    }

    /**
     * Buy property transaction dto.
     *
     * @param buyerID    the buyer id
     * @param propertyID the property id
     * @return the transaction in dto presentation
     * @throws DataNotFoundException  the data not found exception
     * @throws RequestFailedException the request failed exception
     */
    @WebMethod(operationName = "buy_property")
    public TransactionDTO buyProperty(String buyerID, int propertyID) throws DataNotFoundException, RequestFailedException {
        TransactionDAO transactionDAO = TransactionService.getInstance().makeTransaction(buyerID, propertyID);
        return TransactionMapper.INSTANCE.transactionDaoToDto(transactionDAO);
    }
}
