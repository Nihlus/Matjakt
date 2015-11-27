package com.nihlus.matjakt.database.inserters;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.database.retrievers.RetrieveStoresTask;
import com.nihlus.matjakt.ui.ViewProductActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Inserts a price into the Matjakt database, based on EAN and store ID.
 */
public class InsertPriceTask extends AsyncTask<Void, Void, Boolean>
{
    private final Activity ParentActivity;
    private final EAN ean;
    private final double Price;
    private final String Currency;
    private final String PlaceID;
    private final boolean isOffer;

    public InsertPriceTask(Activity InActivity, EAN InEAN, double InPrice, String InCurrency, String inPlaceID, boolean InIsOffer)
    {
        this.ParentActivity = InActivity;
        this.ean = InEAN;
        this.Price = InPrice;
        this.Currency = InCurrency;
        this.PlaceID = inPlaceID;
        this.isOffer = InIsOffer;
    }

    @Override
    protected void onPreExecute()
    {
        //set first list item to show a loading message
        if (ParentActivity instanceof ViewProductActivity)
        {
            ((ViewProductActivity) ParentActivity).setListStatusLoading();
        }
    }

    @Override
    protected Boolean doInBackground(Void... nothing)
    {
        try
        {
            // Get the store ID from the database
            if (!RetrieveStoresTask.isPlaceIDRegisteredByMatjakt(PlaceID))
            {
                // Take the place ID and put it into the Matjakt database so we can search based on distance
                registerPlaceID(((ViewProductActivity)ParentActivity).getGoogleApiClient(), PlaceID);
            }

            int StoreID = RetrieveStoresTask.getStoreIDByPlaceID(PlaceID);

            String rawUrl = Constants.MatjaktAPIURL + Constants.ADDPRICE + "?" +
                    Constants.API_PARAM_EAN + "=" + ean.getCode() + "&" +
                    Constants.API_PARAM_PRICE + "=" + String.valueOf(Price) + "&" +
                    Constants.API_PARAM_CURRENCY + "=" + Currency + "&" +
                    Constants.API_PARAM_STORE + "=" + String.valueOf(StoreID) + "&" +
                    Constants.API_PARAM_OFFER + "=" + String.valueOf(isOffer);

            String responseContent = "";
            URL url = new URL(rawUrl);
            URLConnection requestConnection = url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(requestConnection.getInputStream()));

            // Read the entire input stream
            String currentLine;
            while ((currentLine = br.readLine()) != null)
            {
                responseContent += currentLine;
            }

            JSONObject Result = new JSONObject(responseContent);

            return Result.getInt("result") == 0;
        }
        catch (MalformedURLException mex)
        {
            mex.printStackTrace();
        }
        catch (IOException iex)
        {
            iex.printStackTrace();
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
        if (ParentActivity instanceof ViewProductActivity)
        {
            //send the results
            ((ViewProductActivity) ParentActivity).loadPricesAsync();
        }
    }

    public static void registerPlaceID(GoogleApiClient apiClient, String InPlaceID)
    {
        Place storePlace = RetrieveStoresTask.getStorePlaceByID(apiClient, InPlaceID);

        try
        {
            String rawUrl = Constants.MatjaktAPIURL + Constants.ADDSTORE + "?" +
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
}
