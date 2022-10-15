package com.example.util;


import com.aerospike.client.AerospikeClient;
import com.aerospike.mapper.tools.AeroMapper;

public class AerospikeDB {

    public final static String NAMESPACE = "test";
    public final static String PROPERTY = "property";
    public final static String OWNERSHIP = "owner";
    public final static String TRANSACTION = "transaction";
    public static AerospikeClient client = new AerospikeClient("localhost", 3000);
    public static AeroMapper mapper = new AeroMapper.Builder(AerospikeDB.client).build();

    public static void save(Object classType, Object className){
        mapper.save(classType,className);
    }
}
