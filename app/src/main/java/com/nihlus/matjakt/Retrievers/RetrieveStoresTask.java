package com.nihlus.matjakt.Retrievers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.Containers.MatjaktStore;
import com.nihlus.matjakt.R;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jarl on 2015-08-20.
 *
 * Retrieves and sorts stores from the database
 */
public class RetrieveStoresTask extends AsyncTask<Void, Void, List<MatjaktStore>>
{
    final Activity parentActivity;
    final String chain;
    final double latitude;
    final double longitude;
    final double distance;

    final ProgressDialog dialog;


    public RetrieveStoresTask(Activity activity, String chain, double latitude, double longitude, double distance)
    {
        this.parentActivity = activity;
        this.chain = chain;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;

        dialog = new ProgressDialog(parentActivity);
    }

    public RetrieveStoresTask(Activity activity, double latitude, double longitude, double distance)
    {
        this.parentActivity = activity;
        this.chain = "";
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;

        dialog = new ProgressDialog(parentActivity);
    }

    @Override
    protected void onPreExecute()
    {
        //set first list item to show a loading message
        super.onPreExecute();
        this.dialog.setMessage(parentActivity.getResources().getString(R.string.dialog_loadingStoresAndPrices));
        this.dialog.show();
    }

    // TODO: 9/8/15 Stub function - implement loading and sorting of stores
    @Override
    protected List<MatjaktStore> doInBackground(Void... nothing)
    {
        List<MatjaktStore> retrievedStores = new ArrayList<MatjaktStore>();

        JSONArray jsonResult = new JSONArray();

        try
        {
            URL url = null;

            // Get stores in the desired distance
            if (chain.isEmpty())
            {
                url = new URL(Constants.MatjaktAPIURL + Constants.GETSTORES + "?" +
                        Constants.API_PARAM_LAT + "=" + String.valueOf(latitude) + "&" +
                        Constants.API_PARAM_LON + "=" + String.valueOf(longitude) + "&" +
                        Constants.API_PARAM_DISTANCE + "=" + String.valueOf(distance));
            }
            else
            {
                url = new URL(Constants.MatjaktAPIURL + Constants.GETSTORES + "?" +
                        Constants.API_PARAM_LAT + "=" + String.valueOf(latitude) + "&" +
                        Constants.API_PARAM_LON + "=" + String.valueOf(longitude) + "&" +
                        Constants.API_PARAM_DISTANCE + "=" + String.valueOf(distance) + "&" +
                        Constants.API_PARAM_CHAIN + "=" + chain);
            }

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
                retrievedStores.add(new MatjaktStore(jsonResult.getJSONObject(i)));
            }

            // If the list is empty, extend the search distance 10x
            if (retrievedStores.size() < 1)
            {
                // Get stores in the desired distance
                if (chain.isEmpty())
                {
                    url = new URL(Constants.MatjaktAPIURL + Constants.GETSTORES + "?" +
                            Constants.API_PARAM_LAT + "=" + String.valueOf(latitude) + "&" +
                            Constants.API_PARAM_LON + "=" + String.valueOf(longitude) + "&" +
                            Constants.API_PARAM_DISTANCE + "=" + String.valueOf(distance * 10));
                }
                else
                {
                    url = new URL(Constants.MatjaktAPIURL + Constants.GETSTORES + "?" +
                            Constants.API_PARAM_LAT + "=" + String.valueOf(latitude) + "&" +
                            Constants.API_PARAM_LON + "=" + String.valueOf(longitude) + "&" +
                            Constants.API_PARAM_DISTANCE + "=" + String.valueOf(distance * 10) + "&" +
                            Constants.API_PARAM_CHAIN + "=" + chain);
                }

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
                    retrievedStores.add(new MatjaktStore(jsonResult.getJSONObject(i)));
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

        return retrievedStores;
    }

    @Override
    protected void onPostExecute(List<MatjaktStore> result)
    {
        //update list with the retrieved stores
        if (parentActivity instanceof ViewProductActivity)
        {
            dialog.dismiss();
            ((ViewProductActivity)parentActivity).onStoresLoaded(result);
        }
    }
}
