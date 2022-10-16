package com.example.service.rest;

import com.example.model.dao.TransactionDAO;
import com.example.model.dto.TransactionDTO;
import com.example.model.mapper.TransactionMapper;
import com.example.resources.AerospikeReader;
import com.example.resources.HypermediaCreator;
import com.example.util.AerospikeDB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.ArrayList;

@Path("transactions")
public class TransactionResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTransactions(@Context UriInfo uriInfo){
        AerospikeReader<TransactionDAO> recordSet = new AerospikeReader<>(TransactionDAO.class);
        ArrayList<TransactionDAO> transactionSet = recordSet.getSet(AerospikeDB.NAMESPACE,  AerospikeDB.TRANSACTION, "date");
        ArrayList<TransactionDTO> transactions = TransactionMapper.INSTANCE.transactionListDaoToDto(transactionSet);
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        transactions.forEach(
                transactionDTO -> transactionDTO.setLinks(
                        hypermediaCreator.baseUri("/owners/" + transactionDTO.getBuyer(), "buyer")
                                .baseUri("/owners/" + transactionDTO.getSeller(), "seller")
                                .baseUri("/properties/" + transactionDTO.getProperty(), "property")
                                .build()
                )
        );
        return Response.ok(transactions).links(hypermediaCreator.makeSelfLink()).build();
    }
}
