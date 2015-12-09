package com.nihlus.matjakt.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nihlus.matjakt.R;
import com.nihlus.matjakt.database.containers.MatjaktPrice;
import com.nihlus.matjakt.database.inserters.DeletePriceTask;
import com.nihlus.matjakt.database.retrievers.RetrievePricesTask;
import com.nihlus.matjakt.outpan.OutpanProduct;
import com.nihlus.matjakt.services.GPSService;
import com.nihlus.matjakt.ui.ModifyPriceDialogFragment;
import com.nihlus.matjakt.ui.ViewProductActivity;
import com.nihlus.matjakt.ui.adapters.PriceListAdapter;

import java.util.ArrayList;
import java.util.List;

public class PriceListFragment extends Fragment
{
    private final static int CONTEXT_DELETE = 0;
    private final static int CONTEXT_EDIT = 1;

    private final ArrayList<MatjaktPrice> priceList = new ArrayList<>();
    private PriceListAdapter priceListAdapter;

    private View rootView;

    public PriceListFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        priceListAdapter = new PriceListAdapter((ViewProductActivity)getActivity(), priceList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_price_list, container, false);

        setupListView();

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo)
    {
        if (view.getId() == R.id.priceListView)
        {
            if (!getString(R.string.matjakt_managementkey).isEmpty())
            {
                menu.add(0, CONTEXT_EDIT, 0, getString(R.string.ui_general_edit));
                menu.add(0, CONTEXT_DELETE, 1, getString(R.string.ui_general_delete));
            }
            else
            {
                menu.add(0, CONTEXT_EDIT, 0, getString(R.string.ui_general_edit));
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case CONTEXT_DELETE:
            {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                MatjaktPrice contextPrice = priceListAdapter.getItem(info.position);
                // Send a delete request to the server

                if (!getString(R.string.matjakt_managementkey).isEmpty())
                {
                    DeletePriceTask deletePriceTask = new DeletePriceTask((ViewProductActivity)getActivity(), contextPrice,
                            getString(R.string.matjakt_managementkey));

                    deletePriceTask.execute();
                }
                else
                {
                    Toast.makeText(getActivity(), getString(R.string.management_unauthorized), Toast.LENGTH_SHORT).show();
                }

                return true;
            }
            case CONTEXT_EDIT:
            {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                MatjaktPrice currentPrice = priceListAdapter.getItem(info.position);

                ModifyPriceDialogFragment addPriceDialog = new ModifyPriceDialogFragment((ViewProductActivity)getActivity(),
                        ((ViewProductActivity)getActivity()).product,
                        currentPrice,
                        getGPS().getCurrentLocation());

                addPriceDialog.show(getActivity().getFragmentManager(), "PRICEDIALOG");
                return true;
            }
            default:
            {
                break;
            }
        }
        return false;
    }

    private void setupListView()
    {
        ListView priceView = getPriceListView();
        if (priceView != null)
        {
            priceView.setOnItemClickListener(new onPriceClickedListener());
            registerForContextMenu(priceView);
            resetListViewAdapter();
        }

        getSwipeContainer().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                // Load new priceList from a new location
                if (getGPS() != null)
                {
                    loadPricesAsync();
                }
            }
        });

        getSwipeContainer().setColorSchemeResources(
                R.color.holo_blue_bright,
                R.color.holo_green_light,
                R.color.holo_orange_light,
                R.color.holo_red_light);
    }

    public void loadPricesAsync()
    {
        getSwipeContainer().setRefreshing(true);
        RetrievePricesTask pricesTask = new RetrievePricesTask((ViewProductActivity)getActivity(), getProduct().ean,
                getGPS().getCurrentLocation());

        pricesTask.execute();
    }


    private void resetListViewAdapter()
    {
        ListView priceView = getPriceListView();
        if (priceView != null)
        {
            priceView.setAdapter(priceListAdapter);

            //probably not needed
            priceListAdapter.notifyDataSetChanged();
        }
    }

    private class onPriceClickedListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
        {
            boolean isAddEntry = priceListAdapter.getItem(position).isAddEntry;
            if (isAddEntry)
            {
                // Retrieve the local stores, and then show an add price dialog
                ModifyPriceDialogFragment addPriceDialog = new ModifyPriceDialogFragment((ViewProductActivity)getActivity(),
                        ((ViewProductActivity)getActivity()).product,
                        getGPS().getCurrentLocation());

                addPriceDialog.show(getActivity().getFragmentManager(), "PRICEDIALOG");
            }
            else
            {
                ((ViewProductActivity)getActivity()).showPriceInfo(priceListAdapter.getItem(position));
            }
        }
    }

    public void onPricesRetrieved(List<MatjaktPrice> prices)
    {
        clearPrices();

        //add the prices
        for (MatjaktPrice price : prices)
        {
            addPriceItem(price);
        }

        addPlusItem();
        getSwipeContainer().setRefreshing(false);
    }

    private void addPriceItem(MatjaktPrice entry)
    {
        if (priceListAdapter != null && priceList != null)
        {
            priceList.add(entry);

            resetListViewAdapter();
        }
    }

    private void addPlusItem()
    {
        priceList.add(MatjaktPrice.getAddPriceEntry());
        resetListViewAdapter();
    }

    private void clearPrices()
    {
        if (priceListAdapter != null)
        {
            priceList.clear();
            resetListViewAdapter();
        }
    }

    private GPSService getGPS()
    {
        return ((ViewProductActivity)getActivity()).GPS;
    }

    private OutpanProduct getProduct()
    {
        return ((ViewProductActivity)getActivity()).product;
    }

    private SwipeRefreshLayout getSwipeContainer()
    {
        return (SwipeRefreshLayout)rootView.findViewById(R.id.swipeContainer);
    }

    private ListView getPriceListView()
    {
        return (ListView) rootView.findViewById(R.id.priceListView);
    }
}
