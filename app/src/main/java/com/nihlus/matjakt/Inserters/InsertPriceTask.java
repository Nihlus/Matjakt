package com.nihlus.matjakt.Inserters;

import android.app.Activity;
import android.os.AsyncTask;

import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.Containers.MatjaktPrice;
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

/**
 * Created by jarl on 11/7/15.
 */
public class InsertPriceTask extends AsyncTask<Void, Void, Boolean>
{
    final Activity ParentActivity;
    final String EAN;
    final double Price;
    final String Currency;
    final int Store;
    final boolean isOffer;

    public InsertPriceTask(Activity InActivity, String InEAN, double InPrice, String InCurrency, int InStore, boolean InIsOffer)
    {
        this.ParentActivity = InActivity;
        this.EAN = InEAN;
        this.Price = InPrice;
        this.Currency = InCurrency;
        this.Store = InStore;
        this.isOffer = InIsOffer;
    }

    @Override
    protected void onPreExecute()
    {
        //set first list item to show a loading message
        if (ParentActivity instanceof ViewProductActivity)
        {
            ((ViewProductActivity) ParentActivity).setListStatusLoading();
        }
    }

    // TODO: 9/8/15 Stub class - retrieve and sort prices
    @Override
    protected Boolean doInBackground(Void... nothing)
    {
        JSONObject jsonResult = new JSONObject();

        try
        {
            String rawUrl = Constants.MatjaktAPIURL + Constants.ADDPRICE + "?" +
                    Constants.API_PARAM_EAN + "=" + EAN + "&" +
                    Constants.API_PARAM_PRICE + "=" + String.valueOf(Price) + "&" +
                    Constants.API_PARAM_CURRENCY + "=" + Currency + "&" +
                    Constants.API_PARAM_STORE + "=" + String.valueOf(Store) + "&" +
                    Constants.API_PARAM_OFFER + "=" + String.valueOf(isOffer);

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

            if (jsonResult.getInt("result") == 0)
            {
                return true;
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
        //update list with the retrieved prices
        if (ParentActivity instanceof ViewProductActivity)
        {
            //send the results
            ((ViewProductActivity) ParentActivity).LoadPrices();
        }
    }
}
