/**
 *  InsertPriceTask.java
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

package com.nihlus.matjakt.database.inserters;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.database.retrievers.Utility;
import com.nihlus.matjakt.ui.ViewProductActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

/**
 * Inserts a price into the Matjakt database, based on EAN and store ID.
 */
public class InsertPriceTask extends AsyncTask<Void, Void, Boolean>
{
    private static final int NO_RESULT = -1;
    private static final int NEW_ID = -1;

    private final ViewProductActivity parentActivity;
    private final int IDToModify;
    private final EAN ean;
    private final double Price;
    private final String Currency;
    private final String PlaceID;
    private final boolean isOffer;

    public InsertPriceTask(ViewProductActivity InViewProductActivity, EAN InEAN, double InPrice, String InCurrency, String inPlaceID, boolean InIsOffer)
    {
        this.parentActivity = InViewProductActivity;
        this.IDToModify = NEW_ID;
        this.ean = InEAN;
        this.Price = InPrice;
        this.Currency = InCurrency;
        this.PlaceID = inPlaceID;
        this.isOffer = InIsOffer;
    }

    public InsertPriceTask(ViewProductActivity InViewProductActivity, int InIDToModify, EAN InEAN, double InPrice, String InCurrency, String inPlaceID, boolean InIsOffer)
    {
        this.parentActivity = InViewProductActivity;
        this.IDToModify = InIDToModify;
        this.ean = InEAN;
        this.Price = InPrice;
        this.Currency = InCurrency;
        this.PlaceID = inPlaceID;
        this.isOffer = InIsOffer;
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            // Get the store ID from the database
            if (!isPlaceIDRegisteredByMatjakt(PlaceID))
            {
                // Take the place ID and put it into the Matjakt database so we can search based on distance
                registerPlaceID(parentActivity.getGoogleApiClient(), PlaceID);
            }

            int StoreID = getStoreIDByPlaceID(PlaceID);

            String rawUrl = Constants.MATJAKT_API_URL + Constants.API_ADDPRICE + "?" +
                    Constants.API_PARAM_ID + "=" + String.valueOf(IDToModify) + "&" +
                    Constants.API_PARAM_EAN + "=" + ean.getCode() + "&" +
                    Constants.API_PARAM_PRICE + "=" + String.valueOf(Price) + "&" +
                    Constants.API_PARAM_CURRENCY + "=" + Currency + "&" +
                    Constants.API_PARAM_STORE + "=" + String.valueOf(StoreID) + "&" +
                    Constants.API_PARAM_OFFER + "=" + String.valueOf(isOffer);

            URL url = new URL(rawUrl);
            JSONObject Result = Utility.getRemoteJSONObject(url);

            return Result != null && Result.getInt("result") == 0;
        }
        catch (MalformedURLException mex)
        {
            mex.printStackTrace();
        }
        catch (JSONException jex)
        {
            jex.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean success)
    {
        //update list with the retrieved prices
        parentActivity.loadPricesAsync();
    }

    private static void registerPlaceID(GoogleApiClient apiClient, String InPlaceID)
    {
        Place storePlace = getStorePlaceByID(apiClient, InPlaceID);

        try
        {
            String rawUrl = Constants.MATJAKT_API_URL + Constants.API_ADDSTORE + "?" +
                    Constants.API_PARAM_PLACEID + "=" + InPlaceID + "&" +
                    Constants.API_PARAM_LAT + "=" + String.valueOf(storePlace.getLatLng().latitude) + "&" +
                    Constants.API_PARAM_LON + "=" + String.valueOf(storePlace.getLatLng().longitude);

            URL url = new URL(rawUrl);
            URLConnection requestConnection = url.openConnection();
            requestConnection.getInputStream();
        }
        catch (MalformedURLException mex)
        {
            mex.printStackTrace();
        }
        catch (IOException iex)
        {
            iex.printStackTrace();
        }
    }

    private static int getStoreIDByPlaceID(String placeID)
    {
        int storeID = NO_RESULT;
        try
        {
            String rawUrl = Constants.MATJAKT_API_URL + Constants.API_GETSTORE + "?" +
                    Constants.API_PARAM_PLACEID + "=" + placeID;

            URL url = new URL(rawUrl);
            JSONObject Result = Utility.getRemoteJSONObject(url);

            if (Result != null)
            {
                storeID = Result.getInt(Constants.API_PARAM_ID);
            }
        }
        catch (MalformedURLException mex)
        {
            mex.printStackTrace();
        }
        catch (JSONException jex)
        {
            jex.printStackTrace();
        }

        return storeID;
    }

    private static boolean isPlaceIDRegisteredByMatjakt(String placeID)
    {
        return getStoreIDByPlaceID(placeID) != NO_RESULT;
    }

    public static Place getStorePlaceByID(GoogleApiClient inApiClient, String placeID)
    {
        Place storePlace = null;

        // Load the place from the Google servers
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(inApiClient, placeID);

        PlaceBuffer placeBuffer = placeResult.await(60, TimeUnit.SECONDS);
        if (placeBuffer.getStatus().isSuccess())
        {
            storePlace = placeBuffer.get(0).freeze();
        }

        placeBuffer.release();

        return storePlace;
    }
}
