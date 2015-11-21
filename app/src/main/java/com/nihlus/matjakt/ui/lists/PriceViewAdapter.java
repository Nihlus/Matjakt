package com.nihlus.matjakt.ui.lists;

/**
 * Adapter for the columnar ListView for prices
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PriceViewAdapter extends BaseAdapter
{

    private final ArrayList<HashMap<String, String>> list;
    private final Activity ParentActivity;
    private TextView txtFirst;
    private TextView txtSecond;
    private TextView txtThird;

    public PriceViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list)
    {
        super();
        this.ParentActivity = activity;
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
        //TODO: Have another look at this if the ListView stops working, passing parent instead of null
        LayoutInflater inflater = ParentActivity.getLayoutInflater();

        if (convertView == null)
        {

            convertView = inflater.inflate(R.layout.price_row, parent);

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