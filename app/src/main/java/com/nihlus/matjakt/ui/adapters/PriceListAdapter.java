package com.nihlus.matjakt.ui.adapters;

/**
 * Adapter for the columnar ListView for prices
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.database.containers.MatjaktPrice;
import com.nihlus.matjakt.ui.ViewProductActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class PriceListAdapter extends BaseAdapter
{

    private final ArrayList<MatjaktPrice> list;
    private final ViewProductActivity parentActivity;
    private TextView storeChainText;
    private TextView storeNameText;
    private TextView priceValueText;

    public PriceListAdapter(ViewProductActivity InViewProductActivity, ArrayList<MatjaktPrice> InPriceList)
    {
        super();
        this.parentActivity = InViewProductActivity;
        this.list = InPriceList;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public MatjaktPrice getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return list.get(position).ID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = parentActivity.getLayoutInflater();

        if (convertView == null)
        {

            convertView = inflater.inflate(R.layout.price_row, null);

            storeChainText = (TextView) convertView.findViewById(R.id.chain);
            storeNameText = (TextView) convertView.findViewById(R.id.extra);
            priceValueText = (TextView) convertView.findViewById(R.id.price);
        }

        HashMap<String, String> map = list.get(position).getHashMap();
        if (map.containsKey(Constants.PRICEMAPID_STORE))
        {
            storeChainText.setText(map.get(Constants.PRICEMAPID_STORE));
        }

        if (map.containsKey(Constants.PRICEMAPID_OFFER))
        {
            storeNameText.setText(map.get(Constants.PRICEMAPID_OFFER));
        }

        if (map.containsKey(Constants.PRICEMAPID_PRICE))
        {
            if (parentActivity.product.isSoldByWeight())
            {
                // TODO: Clean this crap up
                String basePriceString = map.get(Constants.PRICEMAPID_PRICE);
                String finalPriceString = basePriceString + "/" + parentActivity.product.getWeightUnit();
                priceValueText.setText(finalPriceString);
            }
            else
            {
                priceValueText.setText(map.get(Constants.PRICEMAPID_PRICE));
            }
        }

        return convertView;
    }

}