package com.estate.controller;

import com.estate.controller.rest.OwnerController;
import com.estate.controller.soap.SoapServices;
import com.estate.exception.DataAlreadyExistsException;
import com.estate.repository.AerospikeAccess;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class OwnerPresentationLayerTest {
    static SoapServices soapServices;
    static OwnerController ownerController;

    @BeforeAll
    static void setUp() {
        AerospikeAccess.truncateDatabase();
        soapServices = new SoapServices();
        ownerController = new OwnerController();
    }

    @AfterAll
    static void tearDown() {
        AerospikeAccess.truncateDatabase();
    }

    @Test
    void Test01_addOwnerUsingSoapMethod(){
        assertDoesNotThrow(() ->
                soapServices.addOwner("user1", "firstName", "lastName", 0L));
    }

    @Test
    void Test02_addExistingOwnerUsingSoapMethod() {
        assertThrows(DataAlreadyExistsException.class, () ->
                soapServices.addOwner("user1", "firstName", "lastName", 0L));
    }

//    @Test
//    void Test03_getOwnerUsingRestApi() throws IOException {
//        HttpClient client = new HttpClient(URI.create("https://api.github.com/users/vogella").toURL(),"hello",1);
//        OwnerController ownerController1 = new OwnerController();
//        ownerController1.getOwner("user1");
//        assertEquals(Response.Status.OK,
//                ownerController.getOwner("user1"));
//    }
//
//    @Test
//    void deleteOwner() {
//    }
//
//    @Test
//    void updateOwner() {
//    }
//
//    @Test
//    void getAllOwnerProperties() {
//    }
}