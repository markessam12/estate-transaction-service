package com.estate.controller.rest;

import com.estate.exception.DataNotFoundException;
import com.estate.model.ErrorMessage;
import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.TransactionDTO;
import com.estate.model.mapper.TransactionMapper;
import com.estate.service.HypermediaAdder;
import com.estate.service.TransactionService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.ArrayList;

/**
 * Transaction Controller is the presentation layer of the application.
 * It exposes the rest APIs related to transactions
 */
@Path("transactions")
public class TransactionController {
    /**
     * Gets all transactions in the database.
     *
     * @param uriInfo the uri info
     * @return the all transactions in dto presentation
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTransactions(@Context UriInfo uriInfo) {
        ArrayList<TransactionDAO> transactionsDAO;
        try {
            transactionsDAO = TransactionService.getInstance().getTransactions();
        } catch (DataNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage(), 404))
                    .build();
        }
        ArrayList<TransactionDTO> transactionsDTO = TransactionMapper.INSTANCE.transactionListDaoToDto(transactionsDAO);
        TransactionService.getInstance().addHypermediaToTransactions(transactionsDTO, uriInfo);
        return Response.created(HypermediaAdder.getSelfLink(uriInfo).getUri())
                .entity(transactionsDTO)
                .build();
    }
}
