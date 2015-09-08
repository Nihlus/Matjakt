package com.nihlus.matjakt.Retrievers;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * Created by Jarl on 2015-08-20.
 *
 * Retrieves and sorts stores from the database
 */
public class RetrieveStoresTask extends AsyncTask<Void, Void, HashMap<String, String>>
{
    @Override
    protected void onPreExecute()
    {
        //set first list item to show a loading message
    }

    // TODO: 9/8/15 Stub function - implement loading and sorting of stores
    @Override
    protected HashMap<String, String> doInBackground(Void... nothing)
    {
        //HashMap<String, String> list = new HashMap<>();

        return null;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> result)
    {
        //update list with the retrieved stores
    }
}
