package com.nihlus.matjakt.constants;

/**
 * Created by jarl on 9/8/15.
 *
 * Contains constants used across the application to unify access and output.
 */

@SuppressWarnings("HardCodedStringLiteral")
public final class Constants
{
    public static final int INSERT_NEW_PRODUCT = 2;
    public static final int MODIFY_EXISTING_PRODUCT = 3;
    public static final int VIEW_EXISTING_PRODUCT = 4;
    public static final int REQUEST_BARCODE_SCAN = 5;

    public static final String SHARED_PREFERENCES = "MATJAKTPREFS";
    public static final String PREF_HASSTARTEDBEFORE = "bHasStartedBefore";
    public static final String PREF_USEDARKTHEME = "bUseDarkTheme";
    public static final String PREF_PREFERREDSTOREDISTANCE = "preferredStoreDistance";
    public static final String PREF_MAXSTOREDISTANCE ="maxStoreDistance";
    public static final String PREF_USERCURRENCY = "userCurrency";

    public static final String GENERIC_INTENT_ID = "INTENT";
    public static final String SCANFRAGMENT_ID = "SCANFRAGMENT";
    public static final String SETTINGSFRAGMENT_ID = "SETTINGSFRAGMENT";
    public static final String ABOUTFRAGMENT_ID = "ABOUTFRAGMENT";
    public static final String DIALOG_ADDPRODUCTFRAGMENT_ID = "ADD_PRODUCT_DIALOG";
    public static final String DIALOG_REPAIRPRODUCTFRAGMENT_ID = "REPAIR_PRODUCT_DIALOG";

    public static final String PRODUCT_NAME = "PRODUCT_TITLE";
    public static final String PRODUCT_EAN = "EAN";
    public static final String PRODUCT_BRAND_ATTRIBUTE = "Brand";
    public static final String PRODUCT_TITLE_ATTRIBUTE = "Title";
    public static final String PRODUCT_AMOUNT_ATTRIBUTE = "Amount";
    public static final String PRODUCT_FAIRTRADE_ATTRIBUTE = "Fairtrade";
    public static final String PRODUCT_ORGANIC_ATTRIBUTE = "Organic";
    public static final String PRODUCT_BUNDLE_EXTRA = "PRODUCTDATA";

    public static final String OutpanAPIKey = "6ce3d3614125cd1976dedca9b48e69e5";
    public static final String OutpanBaseURLv2 = "https://api.outpan.com/v2/products/";
    public static final String OutpanLegacyAPI_EditName = "http://www.outpan.com/api/edit-name.php";
    public static final String OutpanLegacyAPI_EditAttribute = "http://www.outpan.com/api/edit-attr.php";

    public static final String MatjaktAPIURL = "http://192.168.1.120/api/v1/";
    public static final String GETSTORES = "matjakt_getstores.php";
    public static final String GETSTORE = "matjakt_getstore.php";
    public static final String GETPRICES = "matjakt_getprices.php";
    public static final String ADDSTORE = "matjakt_addstore.php";
    public static final String ADDPRICE = "matjakt_addprice.php";

    public static final String API_PARAM_ID = "id";
    public static final String API_PARAM_EAN = "ean";
    public static final String API_PARAM_LAT = "latitude";
    public static final String API_PARAM_LON = "longitude";
    public static final String API_PARAM_DISTANCE = "distance";
    public static final String API_PARAM_CHAIN = "chain";
    public static final String API_PARAM_COUNT = "count";
    public static final String API_PARAM_PRICE = "price";
    public static final String API_PARAM_CURRENCY = "currency";
    public static final String API_PARAM_STORE = "store";
    public static final String API_PARAM_OFFER = "isOffer";
    public static final String API_PARAM_NAME = "name";
    public static final String API_PARAM_TIMESTAMP = "timestamp";
    public static final String API_PARAM_STOREID = "storeid";

    public static final String PRICEMAPID_CHAIN = "CHAIN";
    public static final String PRICEMAPID_EXTRA = "EXTRA";
    public static final String PRICEMAPID_PRICE = "PRICE";
    public static final String PRICEMAPID_LAT = "LAT";
    public static final String PRICEMAPID_LON = "LON";
    public static final String PRICEMAPID_LOC = "LOC";
    public static final String PRICEMAPID_TIMESTAMP = "NIXTIME";

    public static final String PRODUCTKEY_EAN = "gtin";
    public static final String PRODUCTKEY_OUTPANURL = "outpan_url";
    public static final String PRODUCTKEY_NAME = "name";
    public static final String PRODUCTKEY_ATTRIBUTES = "attributes";
    public static final String PRODUCTKEY_IMAGES = "images";
    public static final String PRODUCTKEY_VIDEOS = "videos";
    public static final String PRODUCTKEY_CATEGORIES = "categories";

    public static final String SPLITMAP_UNIT = "LETTER";
    public static final String SPLITMAP_NUMBER = "NUMBER";

    public static final int DRAWERITEM_SCAN = 0;
    public static final int DRAWERITEM_SETTINGS = 1;
    public static final int DRAWERITEM_ABOUT = 2;

}
