/**
 *  ViewProductActivity.java
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

package com.nihlus.matjakt.ui;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nihlus.matjakt.ProductScan;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.database.containers.MatjaktPrice;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.database.retrievers.RetrieveProductTask;
import com.nihlus.matjakt.outpan.OutpanProduct;
import com.nihlus.matjakt.services.GPSService;
import com.nihlus.matjakt.ui.adapters.PricePagerAdapter;

import java.util.List;

public class ViewProductActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener

{
    public OutpanProduct product;

    private GoogleApiClient googleApiClient;

    // Price list and info tabs //
    private PricePagerAdapter pricePagerAdapter;
    private ViewPager pricePager;

    // GPS //
    private boolean isGPSBound;
    public GPSService GPS;
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
        setVisibleProduct((OutpanProduct)getIntent().getParcelableExtra(Constants.PRODUCT_PARCEL));

        // Setup the Google Api
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Setup the pager for the price information -i.e, the tabbed container
        setupPricePager();

        // Start up the GPS and wait for that. Execution continues in onGPSConnected()
        bindGPS();
    }

    private void onGPSConnected()
    {
        // Load the prices using the available location
        if (GPS != null && product != null)
        {
            showPriceList();
            pricePagerAdapter.getPriceListFragment().loadPricesAsync();
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

        if (id == R.id.action_edit)
        {
            Intent intent = new Intent(this, ModifyProductActivity.class);
            intent.putExtra(Constants.MODIFY_INTENT_TYPE, Constants.MODIFY_EXISTING_PRODUCT);
            intent.putExtra(Constants.PRODUCT_PARCEL, product);

            startActivityForResult(intent, Constants.MODIFY_EXISTING_PRODUCT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            // We scanned an EAN code!
            if (result.getContents() != null)
            {
                //retrieve data and show it to the user if the product exists
                new RetrieveProductTask(this, new EAN(result.getContents())).execute();
            }
        }
        else if ((requestCode == Constants.MODIFY_EXISTING_PRODUCT ||
                 requestCode == Constants.INSERT_NEW_PRODUCT) &&
                 resultCode == RESULT_OK)
        {
            //update from bundle
            this.product = data.getParcelableExtra(Constants.PRODUCT_PARCEL);

            setVisibleProductTitle(product.getCompositeProductTitle());
            loadPricesAsync();
        }
    }

    public void setVisibleProduct(OutpanProduct InProduct)
    {
        if (InProduct != null)
        {
            this.product = InProduct;
            setVisibleProductTitle(product.getCompositeProductTitle());

            if (GPS != null)
            {
                showPriceList();
                pricePagerAdapter.getPriceListFragment().loadPricesAsync();
            }
        }
    }

    private void setVisibleProductTitle(String title)
    {
        TextView productTitle = (TextView) findViewById(R.id.textView_ProductTitle);
        if (productTitle != null && !title.isEmpty())
        {
            productTitle.setText(title);
        }
    }

    private void setupPricePager()
    {
        pricePagerAdapter = new PricePagerAdapter(getSupportFragmentManager());
        pricePager = (ViewPager)findViewById(R.id.pricePageContainer);
        pricePager.setAdapter(pricePagerAdapter);
    }

    private void showPriceList()
    {
        if (pricePager != null)
        {
            pricePager.setCurrentItem(PricePagerAdapter.PAGE_PRICELIST, true);
        }
    }

    public void showPriceInfo(MatjaktPrice price)
    {
        if (pricePager != null)
        {
            pricePagerAdapter.getPriceInfoFragment().setVisibleInfo(price);
            pricePager.setCurrentItem(PricePagerAdapter.PAGE_PRICEINFO, true);
        }
    }

    public void loadPricesAsync()
    {
        showPriceList();
        pricePagerAdapter.getPriceListFragment().loadPricesAsync();
    }

    public void onPricesRetrieved(List<MatjaktPrice> prices)
    {
        showPriceList();
        pricePagerAdapter.getPriceListFragment().onPricesRetrieved(prices);
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    public void onScanButtonClicked(View view)
    {
        ProductScan.initiate(this);
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    public void onEditPriceButtonClicked(View view)
    {
        MatjaktPrice currentPrice = pricePagerAdapter.getPriceInfoFragment().getCurrentPrice();

        ModifyPriceDialogFragment addPriceDialog = new ModifyPriceDialogFragment(this,
                product,
                currentPrice,
                GPS.getCurrentLocation());

        addPriceDialog.show(getFragmentManager(), "PRICEDIALOG");
    }
}
