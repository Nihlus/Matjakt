package com.nihlus.matjakt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.containers.EAN;
import com.nihlus.matjakt.containers.MatjaktPrice;
import com.nihlus.matjakt.containers.MatjaktStore;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.retrievers.RetrievePricesTask;
import com.nihlus.matjakt.ui.lists.PriceViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewProductActivity extends AppCompatActivity
{
    private final ArrayList<HashMap<String, String>> priceList = new ArrayList<>();
    private final PriceViewAdapter adapter = new PriceViewAdapter(this, priceList);

    private Bundle ProductData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        setTitle(getResources().getString(R.string.title_activity_scanned_product));

        Intent intent = getIntent();

        this.ProductData = intent.getBundleExtra(Constants.PRODUCT_BUNDLE_EXTRA);

        setVisibleProductTitle(ProductData.getString(Constants.PRODUCT_NAME));

        //add default <none> priceList item
        setListStatusLoading();

        //load new prices
        LoadPrices();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
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
            intent.putExtra(Constants.PRODUCT_BUNDLE_EXTRA, ProductData);

            startActivityForResult(intent, Constants.MODIFY_EXISTING_PRODUCT);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Constants.MODIFY_EXISTING_PRODUCT && resultCode == RESULT_OK)
        {
            if (data.getIntExtra(Constants.GENERIC_INTENT_ID, -1) != Constants.REQUEST_BARCODE_SCAN)
            {
                //update from bundle
                this.ProductData = data.getBundleExtra(Constants.PRODUCT_BUNDLE_EXTRA);

                setVisibleProductTitle(getFinalProductString(ProductData));
            }
            else
            {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.GENERIC_INTENT_ID, Constants.REQUEST_BARCODE_SCAN);

                setResult(RESULT_OK, resultIntent);
                this.finish();
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
            //priceList.add(PriceEntry.getExampleEntry().getHashMap());
            LoadPrices();

            priceView.setOnItemClickListener(new onPriceClicked());

            resetListViewAdapter();
        }
    }

    public void setListStatusLoading()
    {
        clearPrices();
        priceList.add(PriceEntry.getLoadingEntry(this).getHashMap());
        resetListViewAdapter();
    }

    public void LoadPrices()
    {
        //TODO: Clean this crap up (GPS Service)
        //TODO: Use real values instead of debug point
        RetrievePricesTask pricesTask = new RetrievePricesTask(this, (EAN)ProductData.getParcelable(Constants.PRODUCT_EAN),
                58.770276,
                17.020926, 2);

        pricesTask.execute();
    }

    public void addPrices(List<MatjaktPrice> entries)
    {
        clearPrices();

        //add the prices
        for (MatjaktPrice entry : entries)
        {
            addPriceItem(entry);
        }

        addAddItem();
    }

    private void addPriceItem(MatjaktPrice entry)
    {
        if (adapter != null && priceList != null)
        {
            priceList.add(entry.getHashMap());

            resetListViewAdapter();
        }
        else
        {
            setupPriceView();
        }
    }

    private void addAddItem()
    {
        priceList.add(PriceEntry.getAddEntry().getHashMap());
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
        // HACK: 9/8/15 ListView needs to have its adapter reset manually to clear ghosting data
        ListView priceView = (ListView)findViewById(R.id.listView_Prices);
        if (priceView != null)
        {
            priceView.setAdapter(adapter);

            //probably not needed
            adapter.notifyDataSetChanged();
        }
    }

    public void onScanButtonClicked(View view)
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.GENERIC_INTENT_ID, Constants.REQUEST_BARCODE_SCAN);

        setResult(RESULT_OK, resultIntent);
        this.finish();
    }

    private class onPriceClicked implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
        {
            String chainName = adapter.getItem(position).get(Constants.PRICEMAPID_CHAIN);
            if (chainName.equals("+"))
            {
                //add new price
                Toast.makeText(ViewProductActivity.this, getResources().getString(R.string.prompt_addNewPrice), Toast.LENGTH_LONG).show();

                //TODO: Add InsertPriceTask handler here
            }
            else
            {
                //display timestamp
                String nixTime = priceList.get(position).get(Constants.PRICEMAPID_TIMESTAMP);
                Toast.makeText(ViewProductActivity.this, nixTime, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onStoresLoaded(List<MatjaktStore> Stores)
    {
        Stores.size();

        //TODO: Clean this crap up (GPS Service)
        //TODO: Use real values instead of debug point
        AddPriceDialogFragment addPriceDialog = new AddPriceDialogFragment(this, Stores,
                58.770276,
                17.020926);

        addPriceDialog.show(getFragmentManager(), "PRICEDIALOG");
        // If within a reasonable distance (~100m), preselect that store
        // Still nothing? Display store input dialog (chain, name)
        // Grab lat/lon and attach to store, display accuracy to user
        // Load stores again, then continue
        // Display a price input dialog (value spinners, currency, store spinner, Cancel/OK)
        // Save/load currency from user settings
        // Load prices again
    }
}
