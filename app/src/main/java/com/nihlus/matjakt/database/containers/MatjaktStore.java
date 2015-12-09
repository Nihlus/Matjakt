package com.nihlus.matjakt.database.containers;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
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
    public String placeID;
    public Place storePlace;
    public LatLng location;

    public MatjaktStore(JSONObject InObject, Place InStorePlace)
    {
        try
        {
            this.ID = InObject.getInt(Constants.API_PARAM_ID);
            this.placeID = InObject.getString(Constants.API_PARAM_PLACEID);
            this.storePlace = InStorePlace;

            double Latitude = InObject.getDouble(Constants.API_PARAM_LAT);
            double Longitude = InObject.getDouble(Constants.API_PARAM_LON);
            this.location = new LatLng(Latitude, Longitude);
        }
        catch (JSONException jex)
        {
            jex.printStackTrace();
        }
    }
}
