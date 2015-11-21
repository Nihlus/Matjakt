package com.nihlus.matjakt.Inserters;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.AsyncTask;

import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.Containers.MatjaktStore;
import com.nihlus.matjakt.Retrievers.RetrievePricesTask;
import com.nihlus.matjakt.UI.AddPriceDialogFragment;
import com.nihlus.matjakt.UI.AddStoreDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jarl on 11/7/15.
 */
public class InsertStoreTask extends AsyncTask<Void, Void, Boolean>
{
    final Activity ParentActivity;
    final DialogFragment ParentDialog;
    final String Chain;
    final String Name;
    final double Latitude;
    final double Longitude;

    private MatjaktStore InsertedStore;

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

    }

    // TODO: 9/8/15 Stub class - retrieve and sort prices
    @Override
    protected Boolean doInBackground(Void... nothing)
    {
        JSONObject jsonResult = new JSONObject();

        try
        {
            String rawUrl = Constants.MatjaktAPIURL + Constants.ADDSTORE + "?" +
                    Constants.API_PARAM_CHAIN + "=" + Chain + "&" +
                    Constants.API_PARAM_NAME + "=" + Name + "&" +
                    Constants.API_PARAM_LAT + "=" + String.valueOf(Latitude) + "&" +
                    Constants.API_PARAM_LON + "=" + String.valueOf(Longitude);

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

            jsonResult = new JSONObject(sb.toString());

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
        if (ParentDialog instanceof AddPriceDialogFragment)
        {
            ((AddPriceDialogFragment)ParentDialog).onStoreInserted(success, InsertedStore);
        }
    }
}
