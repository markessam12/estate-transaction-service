package com.example.resources;

import com.example.model.Property;
import com.example.util.AerospikeDB;

public class RowReader<E> {
    private final Class<E> type;

    public RowReader(Class<E> type)
    {
        this.type = type;
    }

    public E getRaw(int id){
        E row = AerospikeDB.mapper.read(type, id);
        return row;
    }
}
