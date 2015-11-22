package com.nihlus.matjakt.database.containers;

import com.nihlus.matjakt.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a store from the Matjakt database.
 */
public class MatjaktStore
{
    public int ID;
    public String Chain;
    public String Name;
    public double Latitude;
    public double Longitude;

    public MatjaktStore(JSONObject InObject)
    {
        try
        {
            this.ID = InObject.getInt(Constants.API_PARAM_ID);
            this.Chain = InObject.getString(Constants.API_PARAM_CHAIN);
            this.Name = InObject.getString(Constants.API_PARAM_NAME);
            this.Latitude = InObject.getDouble(Constants.API_PARAM_LAT);
            this.Longitude = InObject.getDouble(Constants.API_PARAM_LON);
        }
        catch (JSONException jex)
        {
            jex.printStackTrace();
        }
    }
}
