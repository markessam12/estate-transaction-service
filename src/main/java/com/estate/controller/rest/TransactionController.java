package com.estate.controller.rest;

import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.TransactionDTO;
import com.estate.model.mapper.TransactionMapper;
import com.estate.repository.HypermediaCreator;
import com.estate.service.TransactionService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.ArrayList;

@Path("transactions")
public class TransactionController {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTransactions(@Context UriInfo uriInfo){
        ArrayList<TransactionDAO> transactionsDAO = TransactionService.getInstance().getTransactions();
        ArrayList<TransactionDTO> transactionsDTO = TransactionMapper.INSTANCE.transactionListDaoToDto(transactionsDAO);
        transactionsDTO = TransactionService.getInstance().addHypermediaToTransactions(transactionsDTO, uriInfo);
        return Response.ok(transactionsDTO).links(new HypermediaCreator(uriInfo).makeSelfLink()).build();
    }
}
