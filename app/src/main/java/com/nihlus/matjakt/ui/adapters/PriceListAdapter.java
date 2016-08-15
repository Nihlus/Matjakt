/**
 *  PriceListAdapter.java
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

package com.nihlus.matjakt.ui.adapters;

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
                // TODO: Turn this into something a little more visually appealing
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