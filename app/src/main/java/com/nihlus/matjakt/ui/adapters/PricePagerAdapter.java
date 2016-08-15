/**
 *  PricePagerAdapter.java
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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nihlus.matjakt.ui.fragments.PriceInfoFragment;
import com.nihlus.matjakt.ui.fragments.PriceListFragment;

public class PricePagerAdapter extends FragmentPagerAdapter
{
    public static final int PAGE_PRICELIST = 0;
    public static final int PAGE_PRICEINFO = 1;

    private final PriceListFragment priceListFragment;
    private final PriceInfoFragment priceInfoFragment;

    public PricePagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);

        priceListFragment = new PriceListFragment();
        priceInfoFragment = new PriceInfoFragment();
    }

    @Override
    public Fragment getItem(int i)
    {
        switch(i)
        {
            case PAGE_PRICELIST:
            {
                return priceListFragment;
            }
            case PAGE_PRICEINFO:
            {
                return priceInfoFragment;
            }
            default:
            {
                return null;
            }
        }
    }

    @Override
    public int getCount()
    {
        // We'll only ever have two pages
        return 2;
    }

    public PriceListFragment getPriceListFragment()
    {
        return priceListFragment;
    }

    public PriceInfoFragment getPriceInfoFragment()
    {
        return priceInfoFragment;
    }
}
