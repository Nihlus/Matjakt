package com.nihlus.matjakt.retrievers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.containers.MatjaktPrice;
import com.nihlus.matjakt.containers.MatjaktStore;
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
    private final String ean;
    private final double latitude;
    private final double longitude;
    private final double distance;
    private final String chain;
    private final int count;

    public RetrievePricesTask(Activity activity, String ean, double latitude, double longitude, double distance)
    {
        this.ParentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.chain = "";
        this.count = 0;
    }

    public RetrievePricesTask(Activity activity, String ean, double latitude, double longitude, double distance, String chain, int count)
    {
        this.ParentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.chain = chain;
        this.count = count;
    }

    public RetrievePricesTask(Activity activity, String ean, double latitude, double longitude, double distance, String chain)
    {
        this.ParentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.chain = chain;
        this.count = 0;
    }

    public RetrievePricesTask(Activity activity, String ean, double latitude, double longitude, double distance, int count)
    {
        this.ParentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.chain = "";
        this.count = count;
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

    // TODO: Refactor - redundant and repetitive code
    @Override
    protected List<MatjaktPrice> doInBackground(Void... nothing)
    {
        List<MatjaktPrice> retrievedPrices = new ArrayList<>();

        try
        {
            String rawUrl = Constants.MatjaktAPIURL + Constants.GETPRICES + "?" +
                    Constants.API_PARAM_EAN + "=" + ean + "&" +
                    Constants.API_PARAM_LAT + "=" + String.valueOf(latitude) + "&" +
                    Constants.API_PARAM_LON + "=" + String.valueOf(longitude) + "&" +
                    Constants.API_PARAM_DISTANCE + "=" + String.valueOf(distance);

            if (!chain.isEmpty())
            {
                rawUrl += "&" + Constants.API_PARAM_CHAIN + "=" + chain;
            }

            if (count > 0)
            {
                rawUrl += "&" + Constants.API_PARAM_COUNT + "=" + String.valueOf(count);
            }

            JSONArray Result = Utility.getRemoteJSONArray(new URL(rawUrl));

            for (int i = 0; i < Result.length(); ++i)
            {
                if (Result.getJSONObject(i).has("type"))
                {
                    Log.w(Constants.MATJAKT_LOG_ID, "Error in price retrieval - array might be empty?");
                }
                else
                {
                    MatjaktPrice newPrice = new MatjaktPrice(Result.getJSONObject(i));
                    newPrice.Store = getStore(newPrice.StoreID);

                    retrievedPrices.add(newPrice);
                }
            }

            //TODO: Load extended search radius from settings instead of a hardcoded value
            // No prices? Extend the search 10x
            if (!(retrievedPrices.size() > 0))
            {
                rawUrl = Constants.MatjaktAPIURL + Constants.GETPRICES + "?" +
                        Constants.API_PARAM_EAN + "=" + ean + "&" +
                        Constants.API_PARAM_LAT + "=" + String.valueOf(latitude) + "&" +
                        Constants.API_PARAM_LON + "=" + String.valueOf(longitude) + "&" +
                        Constants.API_PARAM_DISTANCE + "=" + String.valueOf(distance * 10);

                if (!chain.isEmpty())
                {
                    rawUrl += "&" + Constants.API_PARAM_CHAIN + "=" + chain;
                }

                if (count > 0)
                {
                    rawUrl += "&" + Constants.API_PARAM_COUNT + "=" + String.valueOf(count);
                }

                Result = Utility.getRemoteJSONArray(new URL(rawUrl));

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

    @Override
    protected void onPostExecute(List<MatjaktPrice> result)
    {
        //update list with the retrieved prices
        if (ParentActivity instanceof ViewProductActivity)
        {
            //send the results
            ((ViewProductActivity) ParentActivity).addPrices(result);
        }
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
}
