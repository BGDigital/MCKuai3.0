package com.litl.leveldb;

/**
 * Created by kyly on 2015/7/6.
 */
public class DatabaseCorruptException extends LevelDBException {
    private static final long serialVersionUID = -2110293580518875321L;

    public DatabaseCorruptException() {
    }

    public DatabaseCorruptException(String error) {
        super(error);
    }
}
