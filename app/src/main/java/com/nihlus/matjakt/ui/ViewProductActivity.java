package com.nihlus.matjakt.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nihlus.matjakt.ProductScan;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.database.containers.MatjaktPrice;
import com.nihlus.matjakt.database.containers.MatjaktStore;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.database.retrievers.RetrievePricesTask;
import com.nihlus.matjakt.database.retrievers.RetrieveProductTask;
import com.nihlus.matjakt.database.retrievers.RetrieveStoresTask;
import com.nihlus.matjakt.services.GPSService;
import com.nihlus.matjakt.ui.lists.PriceEntry;
import com.nihlus.matjakt.ui.lists.PriceViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewProductActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener

{
    private final ArrayList<HashMap<String, String>> priceList = new ArrayList<>();
    private final PriceViewAdapter adapter = new PriceViewAdapter(this, priceList);

    private Bundle ProductData;

    private SwipeRefreshLayout swipeContainer;
    private GoogleApiClient googleApiClient;


    private boolean isGPSBound;
    private GPSService GPS;
    private final ServiceConnection GPSConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            isGPSBound = true;
            GPSService.GPSBinder Binder = (GPSService.GPSBinder)service;
            GPS = Binder.getService();

            onGPSConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            isGPSBound = false;
        }
    };

    private void bindGPS()
    {
        Intent intent = new Intent(this, GPSService.class);
        bindService(intent, GPSConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindGPS()
    {
        if (isGPSBound)
        {
            unbindService(GPSConnection);
            isGPSBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Setup main activity content
        setContentView(R.layout.activity_view_product);
        setTitle(getResources().getString(R.string.title_activity_scanned_product));
        setVisibleProduct(getIntent().getBundleExtra(Constants.PRODUCT_BUNDLE));

        // Setup the Google Api
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Setup ListView and its associated swipe container
        setupPriceView();
        setListStatusLoading();

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                // Load new prices from a new location
                if (GPS != null)
                {
                    loadPricesAsync();
                }
                else
                {
                    bindGPS();
                }
            }
        });

        swipeContainer.setColorSchemeResources(
                R.color.holo_blue_bright,
                R.color.holo_green_light,
                R.color.holo_orange_light,
                R.color.holo_red_light);

        // Start up the GPS and wait for that. Execution continues in onGPSConnected()
        bindGPS();
    }

    private void onGPSConnected()
    {
        // Load the prices using the available location
        if (GPS != null)
        {
            loadPricesAsync();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint)
    {

    }

    @Override
    public void onConnectionSuspended(int cause)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {

    }

    public GoogleApiClient getGoogleApiClient()
    {
        return googleApiClient;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        bindGPS();
        googleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        googleApiClient.disconnect();
        unbindGPS();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        if (id == R.id.action_edit)
        {
            Intent intent = new Intent(this, ModifyProductActivity.class);
            intent.putExtra(Constants.GENERIC_INTENT_ID, Constants.MODIFY_EXISTING_PRODUCT);
            intent.putExtra(Constants.PRODUCT_BUNDLE, ProductData);

            startActivityForResult(intent, Constants.MODIFY_EXISTING_PRODUCT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            // We scanned an EAN code!
            if (result.getContents() != null)
            {
                //retrieve data and show it to the user if the product exists
                new RetrieveProductTask(this).execute(new EAN(result.getContents()));
            }
        }
        else if (requestCode == Constants.MODIFY_EXISTING_PRODUCT && resultCode == RESULT_OK)
        {
            if (data.getIntExtra(Constants.GENERIC_INTENT_ID, -1) != Constants.REQUEST_BARCODE_SCAN)
            {
                //update from bundle
                this.ProductData = data.getBundleExtra(Constants.PRODUCT_BUNDLE);

                setVisibleProductTitle(getFinalProductString(ProductData));
            }
        }
    }

    public void setVisibleProduct(Bundle InProductBundle)
    {
        if (InProductBundle != null)
        {
            this.ProductData = InProductBundle;

            setVisibleProductTitle(ProductData.getString(Constants.PRODUCT_NAME));

            if (GPS != null)
            {
                loadPricesAsync();
            }
        }
    }

    private String getFinalProductString(Bundle inProductData)
    {
        return inProductData.getString(Constants.PRODUCT_BRAND_ATTRIBUTE) + " " +
                inProductData.getString(Constants.PRODUCT_TITLE_ATTRIBUTE) + " " +
                inProductData.getString(Constants.PRODUCT_AMOUNT_ATTRIBUTE);
    }

    private void setVisibleProductTitle(String title)
    {
        TextView productTitle = (TextView) findViewById(R.id.textView_ProductTitle);
        if (productTitle != null && !title.isEmpty())
        {
            productTitle.setText(title);
        }
    }

    private void setupPriceView()
    {
        ListView priceView = (ListView)findViewById(R.id.listView_Prices);
        if (priceView != null)
        {
            priceView.setOnItemClickListener(new onPriceClickedListener());

            resetListViewAdapter();
        }
    }

    public void loadPricesAsync()
    {
        swipeContainer.setRefreshing(true);
        RetrievePricesTask pricesTask = new RetrievePricesTask(this, (EAN)ProductData.getParcelable(Constants.PRODUCT_EAN),
                GPS.getCurrentLocation().getLatitude(),
                GPS.getCurrentLocation().getLongitude());

        pricesTask.execute();
    }


    public void onPricesRetrieved(List<MatjaktPrice> entries)
    {
        clearPrices();

        //add the prices
        for (MatjaktPrice entry : entries)
        {
            addPriceItem(entry);
        }

        addPlusItem();
        swipeContainer.setRefreshing(false);
    }

    private void addPriceItem(MatjaktPrice entry)
    {
        if (adapter != null && priceList != null)
        {
            priceList.add(entry.getHashMap());

            resetListViewAdapter();
        }
    }

    public void setListStatusLoading()
    {
        clearPrices();
        resetListViewAdapter();
    }

    private void addPlusItem()
    {
        priceList.add(PriceEntry.getAddEntry().getHashMap());
        resetListViewAdapter();
    }

    private void clearPrices()
    {
        if (adapter != null)
        {
            priceList.clear();

            resetListViewAdapter();
        }
    }

    private void resetListViewAdapter()
    {
        ListView priceView = (ListView)findViewById(R.id.listView_Prices);
        if (priceView != null)
        {
            priceView.setAdapter(adapter);

            //probably not needed
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    public void onScanButtonClicked(View view)
    {
        ProductScan.initiate(this);
    }

    private class onPriceClickedListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
        {
            String storeName = adapter.getItem(position).get(Constants.PRICEMAPID_STORE);
            if (storeName.equals("+"))
            {
                //add new price
                Toast.makeText(ViewProductActivity.this, getResources().getString(R.string.prompt_addNewPrice), Toast.LENGTH_LONG).show();

                // Retrieve the local stores, and then show an add price dialog
                RetrieveStoresTask retrieveStoresTask = new RetrieveStoresTask(ViewProductActivity.this,
                        GPS.getCurrentLocation().getLatitude(),
                        GPS.getCurrentLocation().getLongitude());

                retrieveStoresTask.execute();
            }
        }
    }

    public void onStoresLoaded(List<MatjaktStore> Stores)
    {
        AddPriceDialogFragment addPriceDialog = new AddPriceDialogFragment(this,
                Stores,
                ProductData,
                GPS.getCurrentLocation().getLatitude(),
                GPS.getCurrentLocation().getLongitude());

        addPriceDialog.show(getFragmentManager(), "PRICEDIALOG");
    }
}
