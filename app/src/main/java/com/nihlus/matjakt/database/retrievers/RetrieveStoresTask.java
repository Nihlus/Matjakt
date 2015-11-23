package com.nihlus.matjakt.database.retrievers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.MatjaktStore;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.ui.ViewProductActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Retrieves and sorts stores from the database
 */
public class RetrieveStoresTask extends AsyncTask<Void, Void, List<MatjaktStore>>
{
    private final Activity ParentActivity;
    private final String chain;
    private final double latitude;
    private final double longitude;

    private final ProgressDialog dialog;


    public RetrieveStoresTask(Activity activity, String chain, double latitude, double longitude)
    {
        this.ParentActivity = activity;
        this.chain = chain;
        this.latitude = latitude;
        this.longitude = longitude;

        dialog = new ProgressDialog(ParentActivity);
    }

    public RetrieveStoresTask(Activity activity, double latitude, double longitude)
    {
        this.ParentActivity = activity;
        this.chain = "";
        this.latitude = latitude;
        this.longitude = longitude;

        dialog = new ProgressDialog(ParentActivity);
    }

    @Override
    protected void onPreExecute()
    {
        //set first list item to show a loading message
        super.onPreExecute();
        this.dialog.setMessage(ParentActivity.getResources().getString(R.string.dialog_loadingStoresAndPrices));
        this.dialog.show();
    }

    // TODO: Refactor - redundant and repetitive code
    @Override
    protected List<MatjaktStore> doInBackground(Void... nothing)
    {
        List<MatjaktStore> retrievedStores = new ArrayList<>();

        try
        {
            JSONArray Result = Utility.getRemoteJSONArray(buildURL(chain.isEmpty(), false));
            if (Result != null)
            {
                for (int i = 0; i < Result.length(); ++i)
                {
                    retrievedStores.add(new MatjaktStore(Result.getJSONObject(i)));
                }
            }

            // If the list is empty, extend the search distance 10x
            if (retrievedStores.size() < 1)
            {

                Result = Utility.getRemoteJSONArray(buildURL(chain.isEmpty(), true));
                if (Result != null)
                {
                    for (int i = 0; i < Result.length(); ++i)
                    {
                        retrievedStores.add(new MatjaktStore(Result.getJSONObject(i)));
                    }
                }
            }
        }
        catch (JSONException jex)
        {
            jex.printStackTrace();
        }

        return retrievedStores;
    }

    //TODO: Load extended search radius from settings instead of a hardcoded value
    private URL buildURL(boolean bIsChainEmpty, boolean bIsExtendedSearch)
    {
        URL url = null;

        try
        {
            if (bIsChainEmpty)
            {
                url = new URL(Constants.MatjaktAPIURL + Constants.GETSTORES + "?" +
                        Constants.API_PARAM_LAT + "=" + String.valueOf(latitude) + "&" +
                        Constants.API_PARAM_LON + "=" + String.valueOf(longitude) + "&" +
                        Constants.API_PARAM_DISTANCE + "=" + String.valueOf(getStoreSearchDistance(bIsExtendedSearch)));
            }
            else
            {
                url = new URL(Constants.MatjaktAPIURL + Constants.GETSTORES + "?" +
                        Constants.API_PARAM_LAT + "=" + String.valueOf(latitude) + "&" +
                        Constants.API_PARAM_LON + "=" + String.valueOf(longitude) + "&" +
                        Constants.API_PARAM_DISTANCE + "=" + String.valueOf(getStoreSearchDistance(bIsExtendedSearch)) + "&" +
                        Constants.API_PARAM_CHAIN + "=" + chain);
            }
        }
        catch (MalformedURLException mex)
        {
            //TODO: Create global exception handler
            mex.printStackTrace();
        }

        return url;
    }

    @Override
    protected void onPostExecute(List<MatjaktStore> result)
    {
        //update list with the retrieved stores
        if (ParentActivity instanceof ViewProductActivity)
        {
            dialog.dismiss();
            ((ViewProductActivity) ParentActivity).onStoresLoaded(result);
        }
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
}
