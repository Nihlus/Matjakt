package com.nihlus.matjakt.database.inserters;

import android.app.Activity;
import android.os.AsyncTask;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.MatjaktPrice;
import com.nihlus.matjakt.ui.ViewProductActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Inserts a price into the Matjakt database, based on EAN and store ID.
 */
public class DeletePriceTask extends AsyncTask<Void, Void, Boolean>
{
    private final Activity ParentActivity;
    private final MatjaktPrice PriceToDelete;
    private final String ManagementKey;

    public DeletePriceTask(Activity InActivity, MatjaktPrice InPrice, String InManagementKey)
    {
        this.ParentActivity = InActivity;
        this.PriceToDelete = InPrice;
        this.ManagementKey = InManagementKey;
    }


    @Override
    protected void onPreExecute()
    {

    }

    @Override
    protected Boolean doInBackground(Void... nothing)
    {
        try
        {
            String rawUrl = Constants.MATJAKT_API_URL + Constants.API_DELETEPRICE + "?" +
                    Constants.API_PARAM_ID + "=" + String.valueOf(PriceToDelete.ID) + "&" +
                    Constants.API_PARAM_KEY + "=" + ManagementKey;

            URL url = new URL(rawUrl);
            URLConnection requestConnection = url.openConnection();
            requestConnection.getInputStream();

            return true;
        }
        catch (MalformedURLException mex)
        {
            mex.printStackTrace();
        }
        catch (IOException iex)
        {
            iex.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean success)
    {
        //update list with the retrieved prices
        if (ParentActivity instanceof ViewProductActivity)
        {
            //send the results
            ((ViewProductActivity) ParentActivity).loadPricesAsync();
        }
    }
}
