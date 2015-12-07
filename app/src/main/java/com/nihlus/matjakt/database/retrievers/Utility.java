package com.nihlus.matjakt.database.retrievers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Remote JSON retrieval utility class
 */
public class Utility
{
    public static JSONObject getRemoteJSONObject(URL InURL)
    {
        try
        {
            String responseContent = "";
            URLConnection requestConnection = InURL.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(requestConnection.getInputStream()));

            // Read the entire input stream
            String currentLine;
            while ((currentLine = br.readLine()) != null)
            {
                responseContent += currentLine;
            }

            return new JSONObject(responseContent);
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

        return null;
    }

    public static JSONArray getRemoteJSONArray(URL InURL)
    {
        try
        {
            String responseContent = "";
            URLConnection requestConnection = InURL.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(requestConnection.getInputStream()));

            // Read the entire input stream
            String currentLine;
            while ((currentLine = br.readLine()) != null)
            {
                responseContent += currentLine;
            }

            return new JSONArray(responseContent);
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

        return null;
    }
}
