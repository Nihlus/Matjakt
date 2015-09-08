package com.nihlus.matjakt.Retrievers;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * Created by Jarl on 2015-08-20.
 */
public class RetrievePricesTask extends AsyncTask<Void, Void, HashMap<String, String>>
{
    @Override
    protected void onPreExecute()
    {
        //set first list item to show a loading message
    }

    @Override
    protected HashMap<String, String> doInBackground(Void... nothing)
    {
        HashMap<String, String> list = new HashMap<>();

        return list;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> result)
    {
        //update list with the retrieved prices
    }
}
