package com.estate.repository;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Record;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.aerospike.mapper.tools.AeroMapper;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dao.TransactionDAO;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AerospikeAccess<E> {
    public static final AerospikeClient client = new AerospikeClient("localhost", 3000);
    public static final AeroMapper mapper = new AeroMapper.Builder(client).build();

    public static final String NAMESPACE = "test";
    public static final String PROPERTY = "property";
    public static final String OWNERSHIP = "owner";
    public static final String TRANSACTION = "transaction";

    private static final Map<Class<?>, String> setName = new HashMap<>();
    static{
        setName.put(PropertyDAO.class, PROPERTY);
        setName.put(OwnerDAO.class, OWNERSHIP);
        setName.put(TransactionDAO.class, TRANSACTION);
    }

    /**
     * A constant primary Key Map that maps each class type to it's primary key in the datab;ase.
     * These primary keys are the names of the attributes annotated with @AerospikeKey in the classes.
     */
    private static final Map<Class<?>, String> primaryKeyName = new HashMap<>();
    static{
        primaryKeyName.put(PropertyDAO.class, "propertyId");
        primaryKeyName.put(OwnerDAO.class, "userName");
        primaryKeyName.put(TransactionDAO.class, "date");
    }

    private final Class<E> type;
    public AerospikeAccess(Class<E> type) {
        this.type = type;
    }

    public ArrayList<E> getSet(){
        ArrayList<E> setRecords = new ArrayList<>();
        Statement stmt = new Statement();
        stmt.setNamespace(NAMESPACE);
        stmt.setSetName(setName.get(type));
        try (RecordSet recordSet = client.query(null, stmt)) {
            while (recordSet.next()) {
                Record aerospikeRecord = recordSet.getRecord();
                Object key = aerospikeRecord.bins.get(primaryKeyName.get(type));
                setRecords.add(getRecord(key));
            }
        }
        return setRecords;
    }

    public E getRecord(Object id){
        return mapper.read(type, id);
    }

    public void saveRecord(E recordObject){
        mapper.save(recordObject);
    }

    public void deleteRecords(@NotNull List<E> records){
        if(!records.isEmpty())
            for(E aerospikeRecord : records)
                deleteRecord(aerospikeRecord);
    }

    public void deleteRecord(E aerospikeRecord){
        mapper.delete(aerospikeRecord);
    }

    public void updateRecord(E aerospikeRecord ,String... bins){
        mapper.update(aerospikeRecord, bins);
    }
}
