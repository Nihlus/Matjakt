package com.nihlus.matjakt.database.retrievers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.database.containers.MatjaktPrice;
import com.nihlus.matjakt.database.containers.MatjaktStore;
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
    private final String chain;
    private final int count;

    public RetrievePricesTask(Activity activity, EAN ean, double latitude, double longitude)
    {
        this.ParentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
        this.chain = "";
        this.count = 0;
    }

    public RetrievePricesTask(Activity activity, EAN ean, double latitude, double longitude, String chain, int count)
    {
        this.ParentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
        this.chain = chain;
        this.count = count;
    }

    public RetrievePricesTask(Activity activity, EAN ean, double latitude, double longitude, String chain)
    {
        this.ParentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
        this.chain = chain;
        this.count = 0;
    }

    public RetrievePricesTask(Activity activity, EAN ean, double latitude, double longitude, int count)
    {
        this.ParentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
        this.chain = "";
        this.count = count;
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
        if (retrievedPrices.size() <= 0 && !areSearchDistancesEqual())
        {
            fetchPrices(true);
        }

        return retrievedPrices;
    }

    private List<MatjaktPrice> fetchPrices(Boolean bIsExtendedSearch)
    {
        List<MatjaktPrice> retrievedPrices = new ArrayList<>();

        try
        {
            String rawUrl = Constants.MatjaktAPIURL + Constants.GETPRICES + "?" +
                    Constants.API_PARAM_EAN + "=" + ean.getCode() + "&" +
                    Constants.API_PARAM_LAT + "=" + String.valueOf(latitude) + "&" +
                    Constants.API_PARAM_LON + "=" + String.valueOf(longitude) + "&" +
                    Constants.API_PARAM_DISTANCE + "=" + String.valueOf(getStoreSearchDistance(bIsExtendedSearch));

            if (!chain.isEmpty())
            {
                rawUrl += "&" + Constants.API_PARAM_CHAIN + "=" + chain;
            }

            if (count > 0)
            {
                rawUrl += "&" + Constants.API_PARAM_COUNT + "=" + String.valueOf(count);
            }

            JSONArray Result = Utility.getRemoteJSONArray(new URL(rawUrl));
            if (Result != null)
            {
                for (int i = 0; i < Result.length(); ++i)
                {
                    MatjaktPrice newPrice = new MatjaktPrice(Result.getJSONObject(i));
                    newPrice.Store = getStore(newPrice.StoreID);

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

    public static MatjaktStore getStore(int id)
    {
        JSONObject Result = null;
        try
        {
            String rawUrl = Constants.MatjaktAPIURL + Constants.GETSTORE + "?" +
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

        return new MatjaktStore(Result);
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
            ((ViewProductActivity) ParentActivity).onPricesRetrieved(retrievedPrices);
        }
    }
}
