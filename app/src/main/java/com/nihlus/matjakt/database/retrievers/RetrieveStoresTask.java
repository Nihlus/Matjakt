package com.nihlus.matjakt.database.retrievers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.MatjaktStore;
import com.nihlus.matjakt.R;
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
import java.util.concurrent.TimeUnit;

/**
 * Retrieves and sorts stores from the database
 */
public class RetrieveStoresTask extends AsyncTask<Void, Void, List<MatjaktStore>>
{
    private static final int NO_RESULT = -1;
    private final Activity ParentActivity;
    private final double Latitude;
    private final double Longitude;

    private final ProgressDialog progressDialog;


    public RetrieveStoresTask(Activity InParentActivity, double InLatitude, double InLongitude)
    {
        this.ParentActivity = InParentActivity;
        this.Latitude = InLatitude;
        this.Longitude = InLongitude;

        progressDialog = new ProgressDialog(ParentActivity);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                cancel(true);
            }
        });
    }

    @Override
    protected void onPreExecute()
    {
        //set first list item to show a loading message
        super.onPreExecute();
        this.progressDialog.setMessage(ParentActivity.getResources().getString(R.string.dialog_loadingStoresAndPrices));
        this.progressDialog.show();
    }

    // TODO: Refactor - redundant and repetitive code
    @Override
    protected List<MatjaktStore> doInBackground(Void... nothing)
    {
        List<MatjaktStore> retrievedStores = new ArrayList<>();

        try
        {
            JSONArray Result = Utility.getRemoteJSONArray(buildURL(false));
            if (Result != null)
            {
                for (int i = 0; i < Result.length(); ++i)
                {
                    GoogleApiClient apiClient = ((ViewProductActivity)ParentActivity).getGoogleApiClient();
                    String placeID = Result.getJSONObject(i).getString(Constants.API_PARAM_PLACEID);
                    retrievedStores.add(new MatjaktStore(Result.getJSONObject(i), getStorePlaceByID(apiClient, placeID)));
                }
            }

            // If the list is empty, extend the search distance
            if (retrievedStores.size() < 1)
            {

                Result = Utility.getRemoteJSONArray(buildURL(true));
                if (Result != null)
                {
                    for (int i = 0; i < Result.length(); ++i)
                    {
                        GoogleApiClient apiClient = ((ViewProductActivity)ParentActivity).getGoogleApiClient();
                        String placeID = Result.getJSONObject(i).getString(Constants.API_PARAM_PLACEID);
                        retrievedStores.add(new MatjaktStore(Result.getJSONObject(i), getStorePlaceByID(apiClient, placeID)));
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
    private URL buildURL(boolean bIsExtendedSearch)
    {
        URL url = null;

        try
        {
            url = new URL(Constants.MatjaktAPIURL + Constants.GETSTORES + "?" +
                    Constants.API_PARAM_LAT + "=" + String.valueOf(Latitude) + "&" +
                    Constants.API_PARAM_LON + "=" + String.valueOf(Longitude) + "&" +
                    Constants.API_PARAM_DISTANCE + "=" + String.valueOf(getStoreSearchDistance(bIsExtendedSearch)));
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
            progressDialog.dismiss();
            ((ViewProductActivity) ParentActivity).onStoresLoaded(result);
        }
    }

    private double getStoreSearchDistance(boolean maxAllowedDistance)
    {
        SharedPreferences preferences = ParentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (maxAllowedDistance)
        {
            return preferences.getFloat(Constants.PREF_MAXSTOREDISTANCE, 10.0f);
        }
        else
        {
            return preferences.getFloat(Constants.PREF_PREFERREDSTOREDISTANCE, 2.0f);
        }
    }

    public static MatjaktStore getStore(int id, GoogleApiClient inApiClient)
    {
        JSONObject Result = null;
        Place storePlace = null;
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

            String placeID = Result.getString(Constants.API_PARAM_PLACEID);
            storePlace = getStorePlaceByID(inApiClient, placeID);
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

    public static int getStoreIDByPlaceID(String placeID)
    {
        int storeID = NO_RESULT;
        try
        {
            String rawUrl = Constants.MatjaktAPIURL + Constants.GETSTORE + "?" +
                    Constants.API_PARAM_PLACEID + "=" + placeID;

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
            storeID = Result.getInt(Constants.API_PARAM_ID);
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

        return storeID;
    }

    public static boolean isPlaceIDRegisteredByMatjakt(String placeID)
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
