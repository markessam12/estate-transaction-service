package com.example.resources;

import com.aerospike.client.Record;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.example.util.AerospikeDB;

import java.util.ArrayList;

public class TableReader<E> {
    private final Class<E> type;
    public TableReader(Class<E> type) {
        this.type = type;
    }
    public ArrayList<E> getAllSet(String nameSpace, String set, String keyName){
        ArrayList<E> setRecords = new ArrayList<>();
        Statement stmt = new Statement();
        stmt.setNamespace(nameSpace);
        stmt.setSetName(set);
        try (RecordSet recordSet = AerospikeDB.client.query(null, stmt)) {
            while (recordSet.next()) {
                Record record = recordSet.getRecord();
                Object key = record.bins.get(keyName);
                setRecords.add(AerospikeDB.mapper.read(type, key));
            }
        }
        return setRecords;
    }
}
