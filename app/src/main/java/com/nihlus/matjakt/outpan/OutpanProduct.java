package com.nihlus.matjakt.outpan;

import android.os.Bundle;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.containers.EAN;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jarl on 2015-08-21.
 *
 * Represents a response object from the Outpan database.
 */

@SuppressWarnings("WeakerAccess")
public class OutpanProduct
{
    public EAN ean;
    public URL OutpanURL;
    public String Name;
    public HashMap<String, String> Attributes;
    public List<URL> Images;
    public List<URL> Videos;
    public List<String> Categories;

    public OutpanProduct(JSONObject InJSON)
    {
        try
        {
            if (!isErrorMessage(InJSON))
            {
                this.ean = new EAN(InJSON.getString(Constants.PRODUCTKEY_EAN));
                this.OutpanURL = new URL(InJSON.getString(Constants.PRODUCTKEY_OUTPANURL));
                this.Name = InJSON.getString(Constants.PRODUCTKEY_NAME);
                this.Attributes = getAttributesFromJSON(InJSON.getJSONObject(Constants.PRODUCTKEY_ATTRIBUTES));
                this.Images = getURLListFromJSON(InJSON.getJSONArray(Constants.PRODUCTKEY_IMAGES));
                this.Videos = getURLListFromJSON(InJSON.getJSONArray(Constants.PRODUCTKEY_VIDEOS));
                this.Categories = getStringListFromJSON(InJSON.getJSONArray(Constants.PRODUCTKEY_CATEGORIES));
            }
            else
            {
                throw new IllegalArgumentException("The provided product was an error message.");
            }
        }
        catch (JSONException jex)
        {
            //TODO: Create global exception handler
            jex.printStackTrace();
        }
        catch (MalformedURLException mex)
        {
            //TODO: Create global exception handler
            mex.printStackTrace();
        }
    }

    public Bundle getBundle()
    {
        Bundle productData = new Bundle();

        if (Attributes.containsKey(Constants.PRODUCT_BRAND_ATTRIBUTE))
        {
            productData.putString(Constants.PRODUCT_BRAND_ATTRIBUTE, Attributes.get(Constants.PRODUCT_BRAND_ATTRIBUTE));
        }

        if (Attributes.containsKey(Constants.PRODUCT_TITLE_ATTRIBUTE))
        {
            productData.putString(Constants.PRODUCT_TITLE_ATTRIBUTE, Attributes.get(Constants.PRODUCT_TITLE_ATTRIBUTE));
        }

        if (Attributes.containsKey(Constants.PRODUCT_AMOUNT_ATTRIBUTE))
        {
            productData.putString(Constants.PRODUCT_AMOUNT_ATTRIBUTE, Attributes.get(Constants.PRODUCT_AMOUNT_ATTRIBUTE));
        }

        if (Attributes.containsKey(Constants.PRODUCT_ORGANIC_ATTRIBUTE))
        {
            productData.putBoolean(Constants.PRODUCT_ORGANIC_ATTRIBUTE, Boolean.valueOf(Attributes.get(Constants.PRODUCT_ORGANIC_ATTRIBUTE)));
        }

        if (Attributes.containsKey(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE))
        {
            productData.putBoolean(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE, Boolean.valueOf(Attributes.get(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE)));
        }

        return productData;
    }

    private boolean isErrorMessage(JSONObject InJSON)
    {
        return InJSON.has("error");
    }

    private HashMap<String, String> getAttributesFromJSON(JSONObject InJSON)
    {
        HashMap<String, String> OutMap = new HashMap<>();

        if (InJSON != null)
        {
            Iterator<?> Attributes = InJSON.keys();

            while (Attributes.hasNext())
            {
                try
                {
                    String AttributeKey = (String)Attributes.next();
                    String AttributeValue = InJSON.getString(AttributeKey);

                    OutMap.put(AttributeKey, AttributeValue);
                }
                catch (JSONException jex)
                {
                    //TODO: Create global exception handler
                    jex.printStackTrace();
                }
            }

        }

        return OutMap;
    }

    private ArrayList<URL> getURLListFromJSON(JSONArray InJSON)
    {
        ArrayList<URL> OutArray = new ArrayList<>();

        if (InJSON != null)
        {
            try
            {
                for (int i = 0; i < InJSON.length(); ++i)
                {
                    OutArray.add(new URL(InJSON.getString(i)));
                }
            }
            catch (JSONException jex)
            {
                //TODO: Create global exception handler
                jex.printStackTrace();
            }
            catch (MalformedURLException mex)
            {
                //TODO: Create global exception handler
                mex.printStackTrace();
            }
        }

        return OutArray;
    }

    private ArrayList<String> getStringListFromJSON(JSONArray InJSON)
    {
        ArrayList<String> OutArray = new ArrayList<>();

        if (InJSON != null)
        {
            try
            {
                for (int i = 0; i < InJSON.length(); ++i)
                {
                    OutArray.add(InJSON.getString(i));
                }
            }
            catch (JSONException jex)
            {
                //TODO: Create global exception handler
                jex.printStackTrace();
            }
        }

        return OutArray;
    }
}
