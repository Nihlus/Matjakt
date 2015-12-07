package com.nihlus.matjakt.database.retrievers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.database.containers.MatjaktPrice;
import com.nihlus.matjakt.database.containers.MatjaktStore;
import com.nihlus.matjakt.database.inserters.InsertPriceTask;
import com.nihlus.matjakt.ui.ViewProductActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Retrieves and sorts prices from the database
 */
public class RetrievePricesTask extends AsyncTask<Void, Void, List<MatjaktPrice>>
{
    private final Activity ParentActivity;
    private final EAN ean;
    private final double latitude;
    private final double longitude;

    public RetrievePricesTask(Activity activity, EAN ean, double latitude, double longitude)
    {
        this.ParentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected void onPreExecute()
    {

    }

    @Override
    protected List<MatjaktPrice> doInBackground(Void... nothing)
    {
        List<MatjaktPrice> retrievedPrices = fetchPrices(false);

        // Perform an extended search if needed
        if (!(retrievedPrices.size() > 0) && !areSearchDistancesEqual())
        {
            retrievedPrices = fetchPrices(true);
        }

        return retrievedPrices;
    }

    private List<MatjaktPrice> fetchPrices(Boolean bIsExtendedSearch)
    {
        List<MatjaktPrice> retrievedPrices = new ArrayList<>();

        try
        {
            String rawUrl = Constants.MATJAKT_API_URL + Constants.API_GETPRICES + "?" +
                    Constants.API_PARAM_EAN + "=" + ean.getCode() + "&" +
                    Constants.API_PARAM_LAT + "=" + String.valueOf(latitude) + "&" +
                    Constants.API_PARAM_LON + "=" + String.valueOf(longitude) + "&" +
                    Constants.API_PARAM_DISTANCE + "=" + String.valueOf(getStoreSearchDistance(bIsExtendedSearch));

            JSONArray Result = Utility.getRemoteJSONArray(new URL(rawUrl));
            if (Result != null)
            {
                for (int i = 0; i < Result.length(); ++i)
                {
                    MatjaktPrice newPrice = new MatjaktPrice(Result.getJSONObject(i));
                    newPrice.Store = getStore(newPrice.StoreID, ((ViewProductActivity)ParentActivity).getGoogleApiClient());

                    retrievedPrices.add(newPrice);
                }
            }
        }
        catch (MalformedURLException mex)
        {
            //TODO: Create global exception handler
            mex.printStackTrace();
        }
        catch (JSONException jex)
        {
            //TODO: Create global exception handler
            jex.printStackTrace();
        }

        return retrievedPrices;
    }

    private double getStoreSearchDistance(boolean maxAllowedDistance)
    {
        SharedPreferences preferences = ParentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (maxAllowedDistance)
        {
            return preferences.getFloat(Constants.PREF_MAXSTOREDISTANCE, 2.0f);
        }
        else
        {
            return preferences.getFloat(Constants.PREF_PREFERREDSTOREDISTANCE, 2.0f);
        }
    }

    private boolean areSearchDistancesEqual()
    {
        return getStoreSearchDistance(true) == getStoreSearchDistance(false);
    }

    @Override
    protected void onPostExecute(List<MatjaktPrice> retrievedPrices)
    {
        //update list with the retrieved prices
        if (ParentActivity instanceof ViewProductActivity)
        {
            // TODO: Sort list based on price
            Collections.sort(retrievedPrices, MatjaktPrice.LOWEST_FIRST);
            ((ViewProductActivity) ParentActivity).onPricesRetrieved(retrievedPrices);
        }
    }

    private static MatjaktStore getStore(int id, GoogleApiClient inApiClient)
    {
        JSONObject Result = null;
        Place storePlace = null;
        try
        {
            String rawUrl = Constants.MATJAKT_API_URL + Constants.API_GETSTORE + "?" +
                    Constants.API_PARAM_STOREID + "=" + String.valueOf(id);

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

            Result = new JSONObject(responseContent);

            String placeID = Result.getString(Constants.API_PARAM_PLACEID);
            storePlace = InsertPriceTask.getStorePlaceByID(inApiClient, placeID);
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

        return new MatjaktStore(Result, storePlace);
    }
}
