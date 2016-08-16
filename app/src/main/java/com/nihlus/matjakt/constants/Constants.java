/**
 *  Constants.java
 *
 *  Author:
 *       Jarl Gullberg <jarl.gullberg@gmail.com>
 *
 *  Copyright (c) 2016 Jarl Gullberg
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    public static final String SHARED_PREFERENCES = "MATJAKTPREFS";
    public static final String PREF_HASSTARTEDBEFORE = "bHasStartedBefore";
    public static final String PREF_PREFERREDSTOREDISTANCE = "preferredStoreDistance";
    public static final String PREF_MAXSTOREDISTANCE ="maxStoreDistance";
    public static final String PREF_USERCURRENCY = "userCurrency";
    public static final String PREF_STOREPLACEID = "storePlaceID";
    public static final String PREF_STOREPRIMARYTEXT = "storePrimaryText";
    public static final String PREF_BRANDARRAY = "brandArray";

    public static final String MODIFY_INTENT_TYPE = "INTENT";
    public static final String DIALOG_ADDPRODUCTFRAGMENT_ID = "ADD_PRODUCT_DIALOG";
    public static final String DIALOG_REPAIRPRODUCTFRAGMENT_ID = "REPAIR_PRODUCT_DIALOG";

    public static final String PRODUCT_NAME = "PRODUCT_TITLE";
    public static final String PRODUCT_EAN = "EAN";
    public static final String PRODUCT_BRAND_ATTRIBUTE = "Brand";
    public static final String PRODUCT_TITLE_ATTRIBUTE = "Title";
    public static final String PRODUCT_AMOUNT_ATTRIBUTE = "Amount";
    public static final String PRODUCT_FAIRTRADE_ATTRIBUTE = "Fairtrade";
    public static final String PRODUCT_ORGANIC_ATTRIBUTE = "Organic";
    public static final String PRODUCT_BYWEIGHT_ATTRIBUTE = "Sold by weight";
    public static final String PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE = "Weight Unit";
    public static final String PRODUCT_IMAGES_LIST = "images";
    public static final String PRODUCT_VIDEOS_LIST = "videos";
    public static final String PRODUCT_CATEGORIES_LIST = "categories";
    public static final String PRODUCT_PARCEL = "PRODUCTDATA";

    public static final String OutpanAPIKey = "6ce3d3614125cd1976dedca9b48e69e5";
    public static final String OutpanBaseURLv2 = "https://api.outpan.com/v2/products/";

    public static final String MATJAKT_API_URL = "http://directorate.asuscomm.com/api/v1/";
    public static final String API_GETSTORE = "matjakt_getstore.php";
    public static final String API_GETPRICES = "matjakt_getprices.php";
    public static final String API_ADDSTORE = "matjakt_addstore.php";
    public static final String API_ADDPRICE = "matjakt_addprice.php";
    public static final String API_DELETEPRICE = "matjakt_deleteprice.php";

    public static final String API_PARAM_ID = "id";
    public static final String API_PARAM_EAN = "ean";
    public static final String API_PARAM_LAT = "latitude";
    public static final String API_PARAM_LON = "longitude";
    public static final String API_PARAM_DISTANCE = "distance";
    public static final String API_PARAM_PRICE = "price";
    public static final String API_PARAM_CURRENCY = "currency";
    public static final String API_PARAM_STORE = "store";
    public static final String API_PARAM_OFFER = "isOffer";
    public static final String API_PARAM_TIMESTAMP = "timestamp";
    public static final String API_PARAM_STOREID = "storeid";
    public static final String API_PARAM_PLACEID = "placeID";
    public static final String API_PARAM_KEY = "key";

    public static final String PRICEMAPID_STORE = "STORE";
    public static final String PRICEMAPID_OFFER = "EXTRA";
    public static final String PRICEMAPID_PRICE = "PRICE";
    public static final String PRICEMAPID_ADDRESS = "CITY";
    public static final String PRICEMAPID_LAT = "LAT";
    public static final String PRICEMAPID_LON = "LON";
    public static final String PRICEMAPID_TIMESTAMP = "NIXTIME";
    public static final String PRICEMAPID_ISADDENTRY = "SPECIAL";

    public static final String PRODUCTKEY_EAN = "gtin";
    public static final String PRODUCTKEY_OUTPANURL = "outpan_url";
    public static final String PRODUCTKEY_NAME = "name";
    public static final String PRODUCTKEY_ATTRIBUTES = "attributes";
    public static final String PRODUCTKEY_IMAGES = "images";
    public static final String PRODUCTKEY_VIDEOS = "videos";
    public static final String PRODUCTKEY_CATEGORIES = "categories";

    public static final String SPLITMAP_UNIT = "LETTER";
    public static final String SPLITMAP_NUMBER = "NUMBER";

    public static final String DEVELOPEREMAIL = "jarl.gullberg@gmail.com";
    public static final String EMAIL_BUGREPORT_SUBJECT = "[Matjakt] Bug Report: ";
    public static final String EMAIL_FEATUREREQUEST_SUBJECT = "[Matjakt] Feature Request: ";

    public static final String EMAIL_BUGREPORT_BODY =
            "Please describe the issue in as much detail as possible - be specific!\n\n" +
            "" +
            "Your report should contain the following: \n\n" +
            "" +
            "* Steps to reproduce the issue. Don't assume anything - the more detail the better!\n" +
            "* What should happen? What happens instead?\n" +
            "* Attempted and/or successful workarounds" +
            "* Handset model (Note 3, LG, Nexus etc)\n\n" +
            "" +
            "Thank you for taking the time to fill this out! I really appreciate it ^.=.^\n" +
            "--------------\n\n" +
            "" +
            "";

    public static final String EMAIL_FEATUREREQUEST_BODY =
            "Please describe the feature you'd like in as much detail as possible.\n\n" +
                    "" +
                    "Your request should contain the following: \n\n" +
                    "" +
                    "* What the feature would do\n" +
                    "* What the feature would solve or provide that isn't already in the app\n" +
                    "* An alternative way of contacting you, if needed\n\n" +
                    "" +
                    "Thank you for taking the time to fill this out! I really appreciate it ^.=.^\n" +
                    "--------------\n\n" +
                    "" +
                    "";

    public static final int DRAWERITEM_SCAN = 0;
    public static final int DRAWERITEM_SETTINGS = 1;
    public static final int DRAWERITEM_BUGREPORT = 2;
    public static final int DRAWERITEM_REQUESTFEATURE = 3;
}
