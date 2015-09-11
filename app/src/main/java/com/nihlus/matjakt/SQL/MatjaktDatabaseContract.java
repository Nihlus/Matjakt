package com.nihlus.matjakt.SQL;

import android.provider.BaseColumns;

/**
 * Created by Jarl on 2015-08-19.
 * <p/>
 * This class defines database tables for Matjakt, prices and stores.
 */
final class MatjaktDatabaseContract
{
    //Empty constructor. This class should never be instantiated.
    public MatjaktDatabaseContract()
    {

    }

    @SuppressWarnings("HardCodedStringLiteral")
    public static abstract class PriceEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "prices";
        public static final String COLUMN_NAME_EAN = "ean"; //EAN as string
        public static final String COLUMN_NAME_PRICE = "price"; //price as double
        public static final String COLUMN_NAME_CURRENCY = "currency"; //currency as string
        public static final String COLUMN_NAME_STORE = "store"; //store as cross-table index (integer)
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp"; //timestamp as UTC string. Converts to local time when read or written.

        private static final String TEXT_TYPE = " TEXT";
        private static final String REAL_TYPE = " REAL";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String COMMA_SEPARATOR = ",";

        private static final String SQL_QUERY_CREATE_TABLE =
                "CREATE TABLE" + PriceEntry.TABLE_NAME + " (" +
                        PriceEntry._ID + " INTEGER PRIMARY KEY," +
                        PriceEntry.COLUMN_NAME_EAN + TEXT_TYPE + COMMA_SEPARATOR +
                        PriceEntry.COLUMN_NAME_PRICE + REAL_TYPE + COMMA_SEPARATOR +
                        PriceEntry.COLUMN_NAME_CURRENCY + TEXT_TYPE + COMMA_SEPARATOR +
                        PriceEntry.COLUMN_NAME_STORE + INTEGER_TYPE + COMMA_SEPARATOR +
                        PriceEntry.COLUMN_NAME_TIMESTAMP + TEXT_TYPE + COMMA_SEPARATOR +
                        " )";

        private static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + PriceEntry.TABLE_NAME;
    }

    @SuppressWarnings("HardCodedStringLiteral")
    public static abstract class StoreEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "stores";
        public static final String COLUMN_NAME_CHAIN = "chain"; //chain as string (ICA, Coop, etc)
        public static final String COLUMN_NAME_NAME = "name"; //extra name for chain (ICA Nära, where Nära is the extra) as string
        public static final String COLUMN_NAME_LATITUDE = "latitude"; //latitude as double
        public static final String COLUMN_NAME_LONGITUDE = "longitude"; //longitude as double

        private static final String TEXT_TYPE = " TEXT";
        private static final String REAL_TYPE = " REAL";
        private static final String COMMA_SEPARATOR = ",";

        private static final String SQL_QUERY_CREATE_TABLE =
                "CREATE TABLE " + StoreEntry.TABLE_NAME + " (" +
                        StoreEntry._ID + " INTEGER PRIMARY KEY," +
                        StoreEntry.COLUMN_NAME_CHAIN + TEXT_TYPE + COMMA_SEPARATOR +
                        StoreEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEPARATOR +
                        StoreEntry.COLUMN_NAME_LATITUDE + REAL_TYPE + COMMA_SEPARATOR +
                        StoreEntry.COLUMN_NAME_LONGITUDE + REAL_TYPE + COMMA_SEPARATOR +
                        " )";

        private static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + StoreEntry.TABLE_NAME;
    }
}
