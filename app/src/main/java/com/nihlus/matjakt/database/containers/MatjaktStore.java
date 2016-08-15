/**
 *  MatjaktStore.java
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
