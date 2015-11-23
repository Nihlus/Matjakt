package com.nihlus.matjakt.database.inserters;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.nihlus.matjakt.R;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.MatjaktStore;
import com.nihlus.matjakt.database.retrievers.RetrievePricesTask;
import com.nihlus.matjakt.ui.AddPriceDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Inserts a store into the Matjakt database, based on location and name.
 */
public class InsertStoreTask extends AsyncTask<Void, Void, Boolean>
{
    private final Activity ParentActivity;
    private final DialogFragment ParentDialog;
    private final String Chain;
    private final String Name;
    private final double Latitude;
    private final double Longitude;

    private MatjaktStore InsertedStore;
    private ProgressDialog progressDialog;

    public InsertStoreTask(Activity InActivity, DialogFragment InDialog, String InChain, String InName, double InLatitude, double InLongitude)
    {
        this.ParentActivity = InActivity;
        this.ParentDialog = InDialog;
        this.Chain = InChain;
        this.Name = InName;
        this.Latitude = InLatitude;
        this.Longitude = InLongitude;
    }

    @Override
    protected void onPreExecute()
    {
        progressDialog = ProgressDialog.show(ParentActivity, "", ParentActivity.getResources().getString(R.string.dialog_insertingStore));
    }

    // TODO: 9/8/15 Stub class - retrieve and sort prices
    @Override
    protected Boolean doInBackground(Void... nothing)
    {
        try
        {
            String rawUrl = Constants.MatjaktAPIURL + Constants.ADDSTORE + "?" +
                    Constants.API_PARAM_CHAIN + "=" + Chain + "&" +
                    Constants.API_PARAM_NAME + "=" + Name + "&" +
                    Constants.API_PARAM_LAT + "=" + String.valueOf(Latitude) + "&" +
                    Constants.API_PARAM_LON + "=" + String.valueOf(Longitude);

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

            JSONObject jsonResult = new JSONObject(responseContent);

            if (jsonResult.getInt("result") == 0 || jsonResult.getInt("result") == 2)
            {
                if (jsonResult.has("id"))
                {
                    InsertedStore = RetrievePricesTask.getStore(jsonResult.getInt("id"));
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
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

        return false;
    }

    @Override
    protected void onPostExecute(Boolean success)
    {
        progressDialog.cancel();
        if (ParentDialog instanceof AddPriceDialogFragment)
        {
            ((AddPriceDialogFragment)ParentDialog).onStoreInserted(success, InsertedStore);
        }
    }
}
