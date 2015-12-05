package com.nihlus.matjakt.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nihlus.matjakt.ui.fragments.PriceInfoFragment;
import com.nihlus.matjakt.ui.fragments.PriceListFragment;

/**
 * Created by jarl on 12/3/15.
 * Provides paging for information about prices
 */
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
