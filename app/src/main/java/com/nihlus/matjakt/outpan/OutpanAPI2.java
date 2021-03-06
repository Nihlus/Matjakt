/**
 *  OutpanAPI2.java
 *
 *  Author:
 *       Jarl Gullberg <jarl.gullberg@gmail.com>
 *
 *  Copyright (c) 2016 Jarl Gullberg
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nihlus.matjakt.outpan;


import android.accounts.NetworkErrorException;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.database.retrievers.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author Jarl Gullberg - jarl.gullberg@gmail.com
 *
 * This class connects to the Outpan product database, and allows reading and writing of product data
 * there. A valid API key must be retrieved from Outpan.com before this class can be used.
 */
public class OutpanAPI2
{
    /**
     * Retrieves a product from the database by its EAN code. The product may be invalid (i.e,
     * missing data) if the EAN has never been scanned before or doesn't have any data attached
     * to it.
     *
     * @param inEAN The EAN of the product to be retrieved.
     * @return A product object representing the product in the Outpan database
     */
    public OutpanProduct getProduct(EAN inEAN)
    {
        OutpanProduct OutProduct = null;

        JSONObject responseObject = Utility.getRemoteJSONObject(buildRequestURL(inEAN, OutpanRequestType.Product));

        if (responseObject != null)
        {
            OutProduct = new OutpanProduct(responseObject);
        }

        return OutProduct;
    }

    /**
     * Sets the name of a product in the database, identified by its EAN code. If the product
     * already has a name, it will be replaced.
     *
     * @param inEAN The EAN of the product.
     * @param inNewName The new name of the product.
     */
    public void setProductName(EAN inEAN, String inNewName) throws NetworkErrorException
    {
        try
        {
            URL requestURL = buildRequestURL(inEAN, OutpanRequestType.Name);
            HttpsURLConnection connection = (HttpsURLConnection)requestURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("name", inNewName);

            try(OutputStream os = connection.getOutputStream())
            {
                try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")))
                {
                    bw.write(getPostDataParameterString(parameters));
                    bw.flush();
                }
            }

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpsURLConnection.HTTP_OK)
            {
                if (responseCode == HttpsURLConnection.HTTP_BAD_REQUEST)
                {
                    // Read the error object
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8")))
                    {
                        String responseContent = "";
                        String currentLine;

                        while ((currentLine = br.readLine()) != null)
                        {
                            responseContent += currentLine;
                        }

                        if (!responseContent.isEmpty())
                        {
                            try
                            {
                                JSONObject jsonErrorObject = new JSONObject(responseContent);

                                OutpanError error = new OutpanError(jsonErrorObject);

                                if (error.ErrorType == OutpanError.OutpanErrorType.Unknown)
                                {
                                    throw new NetworkErrorException("The server responded with an unknown error code.");
                                }
                            }
                            catch (JSONException jex)
                            {
                                jex.printStackTrace();
                            }
                        }
                    }
                }
                else
                {
                    throw new NetworkErrorException("The server responded with a response code other than HTTP_OK or HTTP_BAD_REQUEST.");
                }
            }
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

    /**
     * Sets an attribute on a product in the database, identified by its EAN code. If the attribute
     * already exists, it will be replaced.
     *
     * If you want to delete attributes, use {@link #deleteProductAttribute(EAN, String)} instead.
     *
     * @param inEAN The EAN of the product.
     * @param inAttributeKey The key of the attribute. Must be a valid string.
     * @param inAttributeValue The new value of the attribute.
     */
    public void setProductAttribute(EAN inEAN, String inAttributeKey, String inAttributeValue) throws NetworkErrorException
    {
        try
        {
            URL requestURL = buildRequestURL(inEAN, OutpanRequestType.Attribute);
            HttpsURLConnection connection = (HttpsURLConnection)requestURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("name", inAttributeKey);
            parameters.put("value", inAttributeValue);

            try(OutputStream os = connection.getOutputStream())
            {
                try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")))
                {
                    bw.write(getPostDataParameterString(parameters));
                    bw.flush();
                }
            }

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpsURLConnection.HTTP_OK)
            {
                if (responseCode == HttpsURLConnection.HTTP_BAD_REQUEST)
                {
                    // Read the error object
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8")))
                    {
                        String responseContent = "";
                        String currentLine;

                        while ((currentLine = br.readLine()) != null)
                        {
                            responseContent += currentLine;
                        }

                        if (!responseContent.isEmpty())
                        {
                            try
                            {
                                JSONObject jsonErrorObject = new JSONObject(responseContent);

                                OutpanError error = new OutpanError(jsonErrorObject);

                                if (error.ErrorType == OutpanError.OutpanErrorType.Unknown)
                                {
                                    throw new NetworkErrorException("The server responded with an unknown error code.");
                                }
                            }
                            catch (JSONException jex)
                            {
                                jex.printStackTrace();
                            }
                        }
                    }
                }
                else
                {
                    throw new NetworkErrorException("The server responded with a response code other than HTTP_OK or HTTP_BAD_REQUEST.");
                }
            }
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

    /**
     * Helper function to delete attributes on a product, identified by its EAN code.
     * Passes an empty attributes value to {@link #setProductAttribute(EAN, String, String)},
     * deleting the attribute.
     *
     * @param inEAN The EAN of the product.
     * @param inAttributeKey The key of the attribute. Must be a valid string.
     */
    public void deleteProductAttribute(EAN inEAN, String inAttributeKey) throws NetworkErrorException
    {
        setProductAttribute(inEAN, inAttributeKey, "");
    }

    /**
     * Creates a complete request URL from a given input EAN, allowing the API to retrieve a product
     * from the database.
     *
     * @param inEAN The EAN of the product.
     * @param requestType The type of request URL to build.
     * @return A valid API URL to a product.
     */
    private URL buildRequestURL(EAN inEAN, OutpanRequestType requestType)
    {
        URL OutURL = null;
        String rawURL = Constants.OutpanBaseURLv2 + inEAN.getCode();

        switch (requestType)
        {

            case Attribute:
            {
                rawURL += "/attribute";
                break;
            }
            case Name:
            {
                rawURL += "/name";
                break;
            }
            case Product:
            {
                break;
            }
        }

        rawURL += "?apikey=" + Constants.OutpanAPIKey;

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

    /**
     * Creates a HTTP POST request body from a set of key-value pairs.
     *
     * @param parameters A HashMap containing the parameters as key-value pairs.
     * @return A POST body string.
     */
    private String getPostDataParameterString(HashMap<String, String> parameters)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        try
        {
            for (Map.Entry<String, String> entry : parameters.entrySet())
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    sb.append("&");
                }

                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        }
        catch (UnsupportedEncodingException uex)
        {
            uex.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * Different types of requests the API can perform.
     */
    private enum OutpanRequestType
    {
        Attribute,
        Name,
        Product
    }
}
