package com.nihlus.matjakt.outpan;


import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.EAN;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class OutpanAPI2
{
    private final String APIKey;
    public OutpanAPI2(String InAPIKey)
    {
        this.APIKey = InAPIKey;
    }

    //TODO: Stub
    public OutpanProduct getProduct(EAN InEAN)
    {
        OutpanProduct OutProduct = null;

        try
        {
            String responseContent = "";
            URL requestURL = buildRequestURL(InEAN);
            URLConnection requestConnection = requestURL.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(requestConnection.getInputStream()));

            // Read the entire input stream
            String currentLine;
            while ((currentLine = br.readLine()) != null)
            {
                responseContent += currentLine;
            }

            if (!responseContent.isEmpty())
            {
                OutProduct = new OutpanProduct(new JSONObject(responseContent));
            }

        }
        catch (IOException iex)
        {
            //TODO: Create global exception handler
            iex.printStackTrace();
        }
        catch (JSONException jex)
        {
            //TODO: Create global exception handler
            jex.printStackTrace();
        }

        return OutProduct;
    }

    //Unofficial API, does not return an object.
    public void setProductName(String barcode, String newName)
    {
        try
        {
            URL requestURL = new URL(Constants.OutpanLegacyAPI_EditName +
                    "?apikey=" + URLParameterEncoder.encode(APIKey) +
                    "&barcode=" + URLParameterEncoder.encode(barcode) +
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
    public void setProductAttribute(String barcode, String attributeKey, String attributeValue)
    {
        try
        {
            URL requestURL = new URL(Constants.OutpanLegacyAPI_EditAttribute +
                    "?apikey=" + URLParameterEncoder.encode(APIKey) +
                    "&barcode=" + URLParameterEncoder.encode(barcode) +
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
