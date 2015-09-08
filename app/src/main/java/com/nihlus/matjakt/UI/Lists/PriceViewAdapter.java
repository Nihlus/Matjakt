package com.nihlus.matjakt.UI.Lists;

/**
 * Created by jarl on 9/7/15.
 *
 * Adapter for the columnar listview for prices
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PriceViewAdapter extends BaseAdapter
{

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtThird;

    public PriceViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list)
    {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public HashMap<String, String> getItem(int position)
    {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub


        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null)
        {

            convertView = inflater.inflate(R.layout.price_row, null);

            txtFirst = (TextView) convertView.findViewById(R.id.chain);
            txtSecond = (TextView) convertView.findViewById(R.id.extra);
            txtThird = (TextView) convertView.findViewById(R.id.price);
        }

        HashMap<String, String> map = list.get(position);
        txtFirst.setText(map.get(Constants.PRICEMAPID_CHAIN));
        txtSecond.setText(map.get(Constants.PRICEMAPID_EXTRA));
        txtThird.setText(map.get(Constants.PRICEMAPID_PRICE));

        return convertView;
    }

}