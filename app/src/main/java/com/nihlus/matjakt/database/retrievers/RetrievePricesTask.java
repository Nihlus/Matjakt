package com.nihlus.matjakt.database.retrievers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Retrieves and sorts prices from the database
 */
public class RetrievePricesTask extends AsyncTask<Void, Void, List<MatjaktPrice>>
{
    private final ViewProductActivity parentActivity;
    private final EAN ean;
    private final Location location;

    public RetrievePricesTask(ViewProductActivity InParentActivity, EAN InEAN, Location InLocation)
    {
        this.parentActivity = InParentActivity;
        this.ean = InEAN;
        this.location = InLocation;
    }

    @Override
    protected List<MatjaktPrice> doInBackground(Void... params)
    {
        List<MatjaktPrice> retrievedPrices = fetchPrices(false);

        // Perform an extended search if needed
        if (!(retrievedPrices.size() > 0) && !areSearchDistancesEqual())
        {
            retrievedPrices = fetchPrices(true);
        }

        return retrievedPrices;
    }

    private List<MatjaktPrice> fetchPrices(Boolean IsExtendedSearch)
    {
        List<MatjaktPrice> retrievedPrices = new ArrayList<>();

        try
        {
            String rawUrl = Constants.MATJAKT_API_URL + Constants.API_GETPRICES + "?" +
                    Constants.API_PARAM_EAN + "=" + ean.getCode() + "&" +
                    Constants.API_PARAM_LAT + "=" + String.valueOf(location.getLatitude()) + "&" +
                    Constants.API_PARAM_LON + "=" + String.valueOf(location.getLongitude()) + "&" +
                    Constants.API_PARAM_DISTANCE + "=" + String.valueOf(getStoreSearchDistance(IsExtendedSearch));

            JSONArray Result = Utility.getRemoteJSONArray(new URL(rawUrl));
            if (Result != null)
            {
                for (int i = 0; i < Result.length(); ++i)
                {
                    MatjaktPrice newPrice = new MatjaktPrice(Result.getJSONObject(i));
                    newPrice.store = getStore(newPrice.storeID, parentActivity.getGoogleApiClient());

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

    private double getStoreSearchDistance(boolean MaxAllowedDistance)
    {
        SharedPreferences preferences = parentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (MaxAllowedDistance)
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
    protected void onPostExecute(List<MatjaktPrice> InRetrievedPrices)
    {
        //update list with the retrieved prices
        Collections.sort(InRetrievedPrices, MatjaktPrice.LOWEST_FIRST);
        parentActivity.onPricesRetrieved(InRetrievedPrices);
    }

    private static MatjaktStore getStore(int InID, GoogleApiClient InAPIClient)
    {
        JSONObject Result = null;
        Place storePlace = null;
        try
        {
            String rawUrl = Constants.MATJAKT_API_URL + Constants.API_GETSTORE + "?" +
                    Constants.API_PARAM_STOREID + "=" + String.valueOf(InID);

            URL url = new URL(rawUrl);
            Result = Utility.getRemoteJSONObject(url);

            if (Result != null)
            {
                String placeID = Result.getString(Constants.API_PARAM_PLACEID);
                storePlace = InsertPriceTask.getStorePlaceByID(InAPIClient, placeID);
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

        return new MatjaktStore(Result, storePlace);
    }
}
