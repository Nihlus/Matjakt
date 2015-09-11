package com.nihlus.matjakt.UI;

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

import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.UI.Lists.PriceViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewProductActivity extends AppCompatActivity
{
    private final ArrayList<HashMap<String, String>> priceList = new ArrayList<>();
    private final PriceViewAdapter adapter = new PriceViewAdapter(this, priceList);

    private String ean;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        setTitle(getResources().getString(R.string.title_activity_scanned_product));

        Intent intent = getIntent();
        String productTitle = intent.getStringExtra(Constants.PRODUCT_TITLE_EXTRA);
        this.ean = intent.getStringExtra(Constants.PRODUCT_EAN_EXTRA);

        setVisibleProductTitle(productTitle);

        //add default <none> priceList item
        setupPriceView();

        //load new prices
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

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Constants.MODIFY_EXISTING_PRODUCT && resultCode == RESULT_OK)
        {

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

    private void setupPriceView()
    {
        ListView priceView = (ListView)findViewById(R.id.listView_Prices);
        if (priceView != null)
        {
            priceList.add(PriceEntry.getExampleEntry().getHashMap());
            priceList.add(PriceEntry.getAddEntry().getHashMap());

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

    // TODO: 9/7/15 Stub function - adds all relevant provided prices to the visible list
    public void addPrices(List<PriceEntry> entries)
    {
        clearPrices();

        //add the prices
        for (PriceEntry entry : entries)
        {
            addPriceItem(entry);
        }
    }

    public void addPriceItem(PriceEntry entry)
    {
        if (adapter != null && priceList != null)
        {
            if (priceList.size() > 1)
            {
                //get the last item and remove it (the add button)
                priceList.remove(priceList.get(priceList.size() - 1));
            }
            else
            {
                //the list is clean, so add in the values
                priceList.add(entry.getHashMap());
                addAddItem();

                resetListViewAdapter();
            }
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
            addAddItem();

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
                clearPrices();
                //addPriceItem(PriceEntry.getExampleEntry2());
            }
            else
            {
                //display timestamp
                String nixtime = priceList.get(position).get(Constants.PRICEMAPID_TIMESTAMP);
                Toast.makeText(ViewProductActivity.this, nixtime, Toast.LENGTH_LONG).show();
            }
        }
    }
}
