package com.nihlus.matjakt.Retrievers;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.Containers.MatjaktPrice;
import com.nihlus.matjakt.Containers.MatjaktStore;
import com.nihlus.matjakt.UI.PriceEntry;
import com.nihlus.matjakt.UI.ViewProductActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jarl on 2015-08-20.
 *
 * Retrieves and sorts prices from the database
 */
public class RetrievePricesTask extends AsyncTask<Void, Void, List<MatjaktPrice>>
{
    final Activity parentActivity;
    final String ean;
    final double latitude;
    final double longitude;
    final double distance;
    final String chain;
    final int count;

    public RetrievePricesTask(Activity activity, String ean, double latitude, double longitude, double distance)
    {
        this.parentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.chain = "";
        this.count = 0;
    }

    public RetrievePricesTask(Activity activity, String ean, double latitude, double longitude, double distance, String chain, int count)
    {
        this.parentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.chain = chain;
        this.count = count;
    }

    public RetrievePricesTask(Activity activity, String ean, double latitude, double longitude, double distance, String chain)
    {
        this.parentActivity = activity;
        this.ean = ean;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.chain = chain;
        this.count = 0;
    }

    public RetrievePricesTask(Activity activity, String ean, double latitude, double longitude, double distance, int count)
    {
        this.parentActivity = activity;
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
        if (parentActivity instanceof ViewProductActivity)
        {
            ((ViewProductActivity) parentActivity).setListStatusLoading();
        }
    }

    // TODO: 9/8/15 Stub class - retrieve and sort prices
    @Override
    protected List<MatjaktPrice> doInBackground(Void... nothing)
    {
        List<MatjaktPrice> retrievedPrices = new ArrayList<MatjaktPrice>();

        JSONArray jsonResult = new JSONArray();

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

            URL url = new URL(rawUrl);
            URLConnection uc = url.openConnection();

            InputStream in = uc.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();

            while ((numCharsRead = isr.read(charArray)) > 0)
            {
                sb.append(charArray, 0, numCharsRead);
            }

            jsonResult = new JSONArray(sb.toString());

            for (int i = 0; i < jsonResult.length(); ++i)
            {
                if (jsonResult.getJSONObject(i).has("type"))
                {
                    Log.w(Constants.MATJAKT_LOG_ID, "Error in price retrieval - array might be empty?");
                }
                else
                {
                    MatjaktPrice newPrice = new MatjaktPrice(jsonResult.getJSONObject(i));
                    newPrice.Store = getStore(newPrice.StoreID);

                    retrievedPrices.add(newPrice);
                }
            }

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

                url = new URL(rawUrl);
                uc = url.openConnection();

                in = uc.getInputStream();
                isr = new InputStreamReader(in);

                numCharsRead = 0;
                charArray = new char[1024];
                sb = new StringBuffer();

                while ((numCharsRead = isr.read(charArray)) > 0)
                {
                    sb.append(charArray, 0, numCharsRead);
                }


                jsonResult = new JSONArray(sb.toString());

                for (int i = 0; i < jsonResult.length(); ++i)
                {
                    MatjaktPrice newPrice = new MatjaktPrice(jsonResult.getJSONObject(i));
                    newPrice.Store = getStore(newPrice.StoreID);

                    retrievedPrices.add(newPrice);
                }
            }
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

        return retrievedPrices;
    }

    @Override
    protected void onPostExecute(List<MatjaktPrice> result)
    {
        //update list with the retrieved prices
        if (parentActivity instanceof ViewProductActivity)
        {
            //send the results
            ((ViewProductActivity) parentActivity).addPrices(result);
        }
    }

    public static MatjaktStore getStore(int id)
    {
        JSONObject Result = null;
        try
        {
            String rawUrl = Constants.MatjaktAPIURL + Constants.GETSTORE + "?" +
                    Constants.API_PARAM_STOREID + "=" + String.valueOf(id);

            URL url = new URL(rawUrl);
            URLConnection uc = url.openConnection();

            InputStream in = uc.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();

            while ((numCharsRead = isr.read(charArray)) > 0)
            {
                sb.append(charArray, 0, numCharsRead);
            }

            Result = new JSONObject(sb.toString());
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
