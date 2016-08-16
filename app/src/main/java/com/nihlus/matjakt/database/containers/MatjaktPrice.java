/**
 *  MatjaktPrice.java
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

package com.nihlus.matjakt.database.containers;

import android.content.Context;

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
    private Context context;

    public int ID;
    public EAN ean;

    public double price;
    public String currency;
    public boolean isOffer;

    public int storeID;
    public MatjaktStore store;

    public Date timestamp;
    public boolean isAddEntry;

    public MatjaktPrice(JSONObject InObject, Context inContext)
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

    public MatjaktPrice(Context inContext)
    {
        this.context = inContext;
    }

    public static MatjaktPrice getAddPriceEntry(Context inContext)
    {
        MatjaktPrice addEntry = new MatjaktPrice(inContext);
        addEntry.isAddEntry = true;
        return addEntry;
    }

    public HashMap<String, String> getHashMap()
    {
        HashMap<String, String> hashMap = new HashMap<>();

        if (isAddEntry)
        {
            hashMap.put(Constants.PRICEMAPID_STORE, context.getResources().getString(R.string.ui_price_addnewprice));
        }
        else
        {
            hashMap.put(Constants.PRICEMAPID_STORE, store.storePlace.getName().toString());

            if (isOffer)
            {
                hashMap.put(Constants.PRICEMAPID_OFFER, context.getResources().
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
            hashMap.put(Constants.PRICEMAPID_ADDRESS, store.storePlace.getAddress().toString());
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
