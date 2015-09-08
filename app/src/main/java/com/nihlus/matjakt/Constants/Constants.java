package com.nihlus.matjakt.Constants;

import android.annotation.SuppressLint;

/**
 * Created by jarl on 9/8/15.
 */

@SuppressWarnings("HardCodedStringLiteral")
public final class Constants
{
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int INSERT_NEW_PRODUCT = 2;
    public static final int MODIFY_EXISTING_PRODUCT = 3;
    public static final int VIEW_EXISTING_PRODUCT = 4;
    public static final int REQUEST_BARCODE_SCAN = 5;

    public static final String GENERIC_INTENT_ID = "INTENT";
    public static final String LATITUDE_ID = "LATITUDE";
    public static final String LONGITUDE_ID = "LONGITUDE";
    public static final String MATJAKT_LOG_ID = "MATJAKT";
    public static final String CURRENTFRAGMENT_ID = "CURRENTFRAGMENT";
    public static final String DATEFORMAT_EUR = "yyyy-MM-dd_HH-mm-ss";
    public static final String MATJAKT_IMAGE_PREFIX = "MAT_";
    public static final String JPEGSUFFIX = ".jpg";
    public static final String WEBFORMAT_JPEG = "image/jpeg";
    public static final String ADDPRODUCTFRAGMENT_ID = "PRODUCTDIALOG";

    public static final String PRODUCT_TITLE_EXTRA = "PRODUCT_TITLE";
    public static final String PRODUCT_EAN_EXTRA = "EAN";
    public static final String PRODUCT_EAN_TYPE_EXTRA = "EAN_TYPE";

    public static final String OutpanAPIKey = "6ce3d3614125cd1976dedca9b48e69e5";
    public static final String OutpanBaseURL = "https://api.outpan.com/v1/products/";
    public static final String OutpanFileUploadField = "field";

    public static final String UTF8 = "UTF-8";

    public static final String PRICEMAPID_CHAIN = "CHAIN";
    public static final String PRICEMAPID_EXTRA = "EXTRA";
    public static final String PRICEMAPID_PRICE = "PRICE";
    public static final String PRICEMAPID_LAT = "LAT";
    public static final String PRICEMAPID_LON = "LON";
    public static final String PRICEMAPID_LOC = "LOC";
    public static final String PRICEMAPID_TIMESTAMP = "NIXTIME";
}
