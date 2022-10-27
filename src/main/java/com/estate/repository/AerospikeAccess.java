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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The AerospikeAccess is a generic class that enables doing operations on any object type stored in aerospike.
 *
 * @param <E> the type parameter is the class of the object corresponding to a specific set in the aerospike namespace.
 */
public class AerospikeAccess<E> {
    /**
     * The java client to aerospike database running on docker.
     */
    private static AerospikeClient client;
    /**
     * An aerospike object mapper to map java objects to database entities.
     */
    private static AeroMapper mapper;

    /**
     * The aerospike namespace where all the project sets are stored inside.
     */
    public static final String NAMESPACE = "test";
    /**
     * The aerospike set name where all properties records stored.
     */
    public static final String PROPERTY = "property";
    /**
     * The aerospike set name where all owners records stored.
     */
    public static final String OWNERSHIP = "owner";
    /**
     * The aerospike set name where all transactions records stored.
     */
    public static final String TRANSACTION = "transaction";

//    //Initializing aerospike java client and mapper
    static{
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            //use properties values
            client = new AerospikeClient(
                    prop.getProperty("db.hostname"),
                    Integer.parseInt(prop.getProperty("db.port"))
            );
            mapper = new AeroMapper.Builder(client).build();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * A Map that maps each class type to the name of it's set inside the database.
     */
    private static final Map<Class<?>, String> setName = new HashMap<>();
    static{
        setName.put(PropertyDAO.class, PROPERTY);
        setName.put(OwnerDAO.class, OWNERSHIP);
        setName.put(TransactionDAO.class, TRANSACTION);
    }

    /**
     * A constant primary Key Map that maps each class type to it's used primary key in the database.
     * These primary keys are the names of the attributes annotated with @AerospikeKey in the DAO classes.
     */
    private static final Map<Class<?>, String> primaryKeyName = new HashMap<>();
    static{
        primaryKeyName.put(PropertyDAO.class, "propertyId");
        primaryKeyName.put(OwnerDAO.class, "userName");
        primaryKeyName.put(TransactionDAO.class, "date");
    }

    private final Class<E> type;

    /**
     * Instantiates a new Aerospike access.
     *
     * @param type is the class of the object that corresponds to its database set
     */
    public AerospikeAccess(Class<E> type) {
        this.type = type;
    }

    /**
     * Get all records of a database set in the form of array list.
     *
     * @return the array list of retrieved records
     */
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

    /**
     * Get a single record from the database.
     *
     * @param id the unique id of the record
     * @return the record in the corresponding class object
     */
    public E getRecord(Object id){
        return mapper.read(type, id);
    }

    /**
     * Save a new record into the set corresponding to the class type.
     *
     * @param recordObject the object representing the record to be added
     */
    public void saveRecord(E recordObject){
        mapper.save(recordObject);
    }

    /**
     * Delete a list of a given records from the corresponding set.
     *
     * @param records the list of objects representing the records to be deleted
     */
    public void deleteRecords(@NotNull List<E> records){
        if(!records.isEmpty())
            for(E aerospikeRecord : records)
                deleteRecord(aerospikeRecord);
    }

    /**
     * Delete a single given record from the corresponding set.
     *
     * @param aerospikeRecord the object representing the record to be deleted
     */
    public void deleteRecord(E aerospikeRecord){
        mapper.delete(aerospikeRecord);
    }

    /**
     * Update a single record in the corresponding set.
     *
     * @param aerospikeRecord the object representing the new data record to be updated
     * @param bins            the bins to apply the update to
     */
    public void updateRecord(E aerospikeRecord ,String... bins){
        mapper.update(aerospikeRecord, bins);
    }

    /**
     * Truncate/reset the database.
     */
    public static void truncateDatabase(){
        client.truncate(null, NAMESPACE, null, null);
    }
}
