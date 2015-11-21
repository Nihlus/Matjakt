package com.nihlus.matjakt.UI;

import android.content.Context;

import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.Containers.MatjaktPrice;
import com.nihlus.matjakt.R;

import java.util.HashMap;
import java.util.InputMismatchException;

/**
 * Created by jarl on 9/7/15.
 *
 * Represents a retrieved entry from the price database
 */
@SuppressWarnings("HardCodedStringLiteral")
public class PriceEntry
{
    public String Chain;
    public String Extra;
    public double Latitude;
    public double Longitude;
    public String Location;
    public double Price;
    public String Timestamp;

    public static PriceEntry getDefaultEntry()
    {
        PriceEntry entry = new PriceEntry();

        entry.Chain = "N/A";
        entry.Extra = "N/A";
        entry.Latitude = 0;
        entry.Longitude = 0;
        entry.Location = "N/A";
        entry.Price = 0;
        entry.Timestamp = "0";

        return entry;
    }

    public static PriceEntry getExampleEntry()
    {
        PriceEntry entry = new PriceEntry();
        entry.Chain = "EXAMPLE: ICA";
        entry.Extra = "Maxi";
        entry.Latitude = 0;
        entry.Longitude = 0;
        entry.Location = "Nyköping";
        entry.Price = 22.90;
        entry.Timestamp = "0";

        return entry;
    }

    public static PriceEntry getExampleEntry2()
    {
        PriceEntry entry = new PriceEntry();
        entry.Chain = "EXAMPLE: COOP";
        entry.Extra = "Maxi";
        entry.Latitude = 0;
        entry.Longitude = 0;
        entry.Location = "Nyköping";
        entry.Price = 22.90;
        entry.Timestamp = "0";

        return entry;
    }

    public static PriceEntry getAddEntry()
    {
        PriceEntry entry = new PriceEntry();
        entry.Chain = "+";
        entry.Extra = "";
        entry.Latitude = -1;
        entry.Longitude = -1;
        entry.Location = "";
        entry.Price = 0;
        entry.Timestamp = "0";

        return entry;
    }

    public static PriceEntry getLoadingEntry(Context context)
    {
        PriceEntry entry = new PriceEntry();
        entry.Chain = context.getResources().getString(R.string.ui_pricesLoading);
        entry.Extra = "";
        entry.Latitude = -1;
        entry.Longitude = -1;
        entry.Location = "";
        entry.Price = 0;
        entry.Timestamp = "0";

        return entry;
    }

    public HashMap<String, String> getHashMap()
    {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.PRICEMAPID_CHAIN, Chain);

        //if the entry is an "Add" entry (i.e, just a plus sign) or Loading, don't add the price or the comma
        if (Latitude == -1 || Longitude == -1)
        {
            hashMap.put(Constants.PRICEMAPID_EXTRA, Extra + ", " + Location);
            hashMap.put(Constants.PRICEMAPID_PRICE, Double.toString(Price));
        }
        else
        {
            hashMap.put(Constants.PRICEMAPID_EXTRA, "");
            hashMap.put(Constants.PRICEMAPID_PRICE, "");
        }

        hashMap.put(Constants.PRICEMAPID_LAT, String.valueOf(Latitude));
        hashMap.put(Constants.PRICEMAPID_LON, String.valueOf(Longitude));
        hashMap.put(Constants.PRICEMAPID_LOC, Location);
        hashMap.put(Constants.PRICEMAPID_TIMESTAMP, Timestamp);

        return hashMap;
    }
}
