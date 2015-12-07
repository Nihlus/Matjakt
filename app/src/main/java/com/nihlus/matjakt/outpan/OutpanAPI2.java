package com.nihlus.matjakt.outpan;


import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.database.retrievers.Utility;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class OutpanAPI2
{
    private final String APIKey;

    @SuppressWarnings("SameParameterValue")
    public OutpanAPI2(String InAPIKey)
    {
        this.APIKey = InAPIKey;
    }

    public OutpanProduct getProduct(EAN InEAN)
    {
        OutpanProduct OutProduct = null;

        JSONObject responseObject = Utility.getRemoteJSONObject(buildRequestURL(InEAN));

        if (responseObject != null)
        {
            OutProduct = new OutpanProduct(responseObject);
        }

        return OutProduct;
    }

    //Unofficial API, does not return an object.
    public void setProductName(EAN InEAN, String newName)
    {
        try
        {
            URL requestURL = new URL(Constants.OutpanLegacyAPI_EditName +
                    "?apikey=" + URLParameterEncoder.encode(APIKey) +
                    "&barcode=" + URLParameterEncoder.encode(InEAN.getCode()) +
                    "&name=" + URLParameterEncoder.encode(newName));

            URLConnection uc = requestURL.openConnection();
            uc.getInputStream();

        }
        catch (MalformedURLException mex)
        {
            //TODO: Create global exception handler
            mex.printStackTrace();
        }
        catch (IOException iex)
        {
            //TODO: Create global exception handler
            iex.printStackTrace();
        }
    }

    //Unofficial API, does not return an object.
    public void setProductAttribute(EAN InEAN, String attributeKey, String attributeValue)
    {
        try
        {
            URL requestURL = new URL(Constants.OutpanLegacyAPI_EditAttribute +
                    "?apikey=" + URLParameterEncoder.encode(APIKey) +
                    "&barcode=" + URLParameterEncoder.encode(InEAN.getCode()) +
                    "&attr_name=" + URLParameterEncoder.encode(attributeKey) +
                    "&attr_val=" + URLParameterEncoder.encode(attributeValue));

            URLConnection uc = requestURL.openConnection();
            uc.getInputStream();

        }
        catch (MalformedURLException mex)
        {
            //TODO: Create global exception handler
            mex.printStackTrace();
        }
        catch (IOException iex)
        {
            //TODO: Create global exception handler
            iex.printStackTrace();
        }
    }

    private URL buildRequestURL(EAN InEAN)
    {
        URL OutURL = null;
        String rawURL = Constants.OutpanBaseURLv2 + InEAN.getCode() + "?apikey=" + Constants.OutpanAPIKey;

        try
        {
            OutURL = new URL(rawURL);
        }
        catch (MalformedURLException mex)
        {
            //TODO: Create global exception handler
            mex.printStackTrace();
        }

        return OutURL;
    }
}
