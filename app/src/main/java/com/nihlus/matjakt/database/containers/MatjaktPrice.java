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
import java.util.Comparator;
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
    public EAN ean;

    public double price;
    public String currency;
    public boolean isOffer;

    public int storeID;
    public MatjaktStore store;

    public Date timestamp;
    public boolean isAddEntry;

    public MatjaktPrice(JSONObject InObject)
    {
        try
        {
            this.ID = InObject.getInt(Constants.API_PARAM_ID);
            this.ean = new EAN(InObject.getString(Constants.API_PARAM_EAN));
            this.price = InObject.getDouble(Constants.API_PARAM_PRICE);
            this.currency = InObject.getString(Constants.API_PARAM_CURRENCY);
            this.storeID = InObject.getInt(Constants.API_PARAM_STORE);
            this.isOffer = InObject.getBoolean(Constants.API_PARAM_OFFER);

            this.isAddEntry = false;

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            this.timestamp = formatter.parse(InObject.getString(Constants.API_PARAM_TIMESTAMP));
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
            hashMap.put(Constants.PRICEMAPID_STORE, store.storePlace.getName().toString());

            if (isOffer)
            {
                hashMap.put(Constants.PRICEMAPID_OFFER, MainActivity.getStaticContext().getResources().
                        getString(R.string.ui_pricelist_isoffer));
            }
            else
            {
                hashMap.put(Constants.PRICEMAPID_OFFER, "");
            }

            hashMap.put(Constants.PRICEMAPID_PRICE, getPriceString(price, currency));

            hashMap.put(Constants.PRICEMAPID_LAT, String.valueOf(store.location.latitude));
            hashMap.put(Constants.PRICEMAPID_LON, String.valueOf(store.location.longitude));

            hashMap.put(Constants.PRICEMAPID_TIMESTAMP, timestamp.toString());
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

    public static final Comparator<MatjaktPrice> HIGHEST_FIRST = new Comparator<MatjaktPrice>()
    {
        @Override
        public int compare(MatjaktPrice lhs, MatjaktPrice rhs)
        {
            return Double.compare(rhs.price, lhs.price);
        }
    };

    public static final Comparator<MatjaktPrice> LOWEST_FIRST = new Comparator<MatjaktPrice>()
    {
        @Override
        public int compare(MatjaktPrice lhs, MatjaktPrice rhs)
        {
            return Double.compare(lhs.price, rhs.price);
        }
    };
}
