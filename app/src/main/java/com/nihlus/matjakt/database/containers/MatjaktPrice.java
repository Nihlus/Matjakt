package com.nihlus.matjakt.database.containers;

import com.nihlus.matjakt.MainActivity;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Represents a price from the Matjakt database.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MatjaktPrice
{
    public int ID;
    public String EAN;
    public double Price;
    public String Currency;
    public int StoreID;
    public MatjaktStore Store;
    public boolean isOffer;
    public Date Timestamp;
    public Boolean isAddEntry;

    public MatjaktPrice(JSONObject InObject)
    {
        try
        {
            this.ID = InObject.getInt(Constants.API_PARAM_ID);
            this.EAN = InObject.getString(Constants.API_PARAM_EAN);
            this.Price = InObject.getDouble(Constants.API_PARAM_PRICE);
            this.Currency = InObject.getString(Constants.API_PARAM_CURRENCY);
            this.StoreID = InObject.getInt(Constants.API_PARAM_STORE);
            this.isOffer = InObject.getBoolean(Constants.API_PARAM_OFFER);

            this.isAddEntry = false;

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            this.Timestamp = formatter.parse(InObject.getString(Constants.API_PARAM_TIMESTAMP));
        }
        catch (JSONException jex)
        {
            jex.printStackTrace();
        }
        catch (ParseException pex)
        {
            pex.printStackTrace();
        }
    }

    public MatjaktPrice()
    {

    }

    public static MatjaktPrice getAddPriceEntry()
    {
        MatjaktPrice addEntry = new MatjaktPrice();
        addEntry.isAddEntry = true;
        return addEntry;
    }

    public HashMap<String, String> getHashMap()
    {
        HashMap<String, String> hashMap = new HashMap<>();

        if (isAddEntry)
        {
            hashMap.put(Constants.PRICEMAPID_STORE, MainActivity.getStaticContext().getResources().getString(R.string.ui_price_addnewprice));
        }
        else
        {
            hashMap.put(Constants.PRICEMAPID_STORE, Store.storePlace.getName().toString());

            if (isOffer)
            {
                hashMap.put(Constants.PRICEMAPID_OFFER, MainActivity.getStaticContext().getResources().
                        getString(R.string.ui_pricelist_isoffer));
            }
            else
            {
                hashMap.put(Constants.PRICEMAPID_OFFER, "");
            }

            hashMap.put(Constants.PRICEMAPID_PRICE, getPriceString(Price, Currency));

            hashMap.put(Constants.PRICEMAPID_LAT, String.valueOf(Store.Latitude));
            hashMap.put(Constants.PRICEMAPID_LON, String.valueOf(Store.Longitude));

            //TODO: Add city reverse Geooding here
            //hashMap.put(Constants.PRICEMAPID_LOC, Location);
            hashMap.put(Constants.PRICEMAPID_TIMESTAMP, Timestamp.toString());
            hashMap.put(Constants.PRICEMAPID_ISADDENTRY, String.valueOf(isAddEntry));

        }

        return hashMap;
    }

    private String getPriceString(double InPrice, String InCurrency)
    {

        DecimalFormat currencyFormat = new DecimalFormat("0.00");
        String numberString = currencyFormat.format(InPrice);

        numberString += " " + InCurrency;
        return numberString;
    }
}
