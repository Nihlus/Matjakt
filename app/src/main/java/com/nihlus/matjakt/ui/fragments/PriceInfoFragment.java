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
