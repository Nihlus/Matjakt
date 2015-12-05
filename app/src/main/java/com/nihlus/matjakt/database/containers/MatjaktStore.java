package com.nihlus.matjakt.database.containers;

import com.google.android.gms.location.places.Place;
import com.nihlus.matjakt.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a store from the Matjakt database.
 */
@SuppressWarnings("unused")
public class MatjaktStore
{
    public int ID;
    public String PlaceID;
    public Place storePlace;
    public double Latitude;
    public double Longitude;

    public MatjaktStore(JSONObject InObject, Place InStorePlace)
    {
        try
        {
            this.ID = InObject.getInt(Constants.API_PARAM_ID);
            this.PlaceID = InObject.getString(Constants.API_PARAM_PLACEID);
            this.storePlace = InStorePlace;
            this.Latitude = InObject.getDouble(Constants.API_PARAM_LAT);
            this.Longitude = InObject.getDouble(Constants.API_PARAM_LON);
        }
        catch (JSONException jex)
        {
            jex.printStackTrace();
        }
    }
}
