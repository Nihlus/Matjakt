/**
 *  PriceInfoFragment.java
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

package com.nihlus.matjakt.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nihlus.matjakt.R;
import com.nihlus.matjakt.database.containers.MatjaktPrice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PriceInfoFragment extends Fragment
{
    private View rootView;
    private MatjaktPrice currentPrice;
    public PriceInfoFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_price_info, container, false);
        return rootView;
    }

    public void setVisibleInfo(MatjaktPrice price)
    {
        this.currentPrice = price;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = df.format(price.timestamp);

        setDateText(dateString);

        if (price.isOffer)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(price.timestamp);
            calendar.add(Calendar.HOUR, 24);

            String expirationString = df.format(calendar.getTime());
            setExpirationDate(expirationString);
        }
        else
        {
            setExpirationDate(getResources().getString(R.string.ui_expires_never));
        }

        String rawStoreAddress = price.store.storePlace.getAddress().toString();
        String[] addressParts = rawStoreAddress.split(",");
        setStoreAddress(buildAddressString(addressParts));
    }

    private String buildAddressString(String[] parts)
    {
        String outString = "";
        for (int i = 0; (i < parts.length) && !(i > 1); i++)
        {
            outString += parts[i];

            // Add commas between the parts except the last one
            if (i < parts.length - 2)
            {
                outString += ", ";
            }
        }

        return outString;
    }

    private void setDateText(String newDate)
    {
        TextView dateText = (TextView)rootView.findViewById(R.id.dateText);
        if (dateText != null)
        {
            dateText.setText(newDate);

        }
    }

    private void setExpirationDate(String newDate)
    {
        TextView expirationText = (TextView)rootView.findViewById(R.id.expirationDateText);
        if (expirationText != null)
        {
            expirationText.setText(newDate);

        }
    }

    private void setStoreAddress(String newAddress)
    {
        TextView storeAddressText = (TextView)rootView.findViewById(R.id.storeAddressText);
        if (storeAddressText != null)
        {
            storeAddressText.setText(newAddress);

        }
    }

    public MatjaktPrice getCurrentPrice()
    {
        return currentPrice;
    }
}
