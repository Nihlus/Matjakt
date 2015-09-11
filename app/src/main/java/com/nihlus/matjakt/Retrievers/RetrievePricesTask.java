package com.nihlus.matjakt.Retrievers;

import android.app.Activity;
import android.os.AsyncTask;

import com.nihlus.matjakt.UI.PriceEntry;
import com.nihlus.matjakt.UI.ViewProductActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jarl on 2015-08-20.
 *
 * Retrieves and sorts prices from the database
 */
public class RetrievePricesTask extends AsyncTask<Void, Void, List<PriceEntry>>
{
    final Activity parentActivity;

    public RetrievePricesTask(Activity activity)
    {
        parentActivity = activity;
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
    protected List<PriceEntry> doInBackground(Void... nothing)
    {
        //HashMap<String, String> list = new HashMap<>();

        return null;
    }

    @Override
    protected void onPostExecute(List<PriceEntry> result)
    {
        //update list with the retrieved prices
        if (parentActivity instanceof ViewProductActivity)
        {
            //send the results
            ((ViewProductActivity) parentActivity).addPrices(result);
        }
    }
}
