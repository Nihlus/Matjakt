package com.nihlus.matjakt;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.database.retrievers.RetrieveProductTask;
import com.nihlus.matjakt.services.GPSService;
import com.nihlus.matjakt.ui.MainAboutFragment;
import com.nihlus.matjakt.ui.MainSettingsFragment;
import com.nihlus.matjakt.ui.ViewProductActivity;

import java.util.Currency;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    /// GPS Service Binding ///

    private boolean isGPSBound;
    private final ServiceConnection GPSConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            isGPSBound = true;
            GPSService.GPSBinder Binder = (GPSService.GPSBinder) service;
            Binder.getService();
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

    /// Main Class ///
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasStartedBefore())
        {
            doFirstTimeSetup();
        }

        setupLeftDrawer();
        setFragment(new MainActivityFragment(), Constants.SCANFRAGMENT_ID);
        bindGPS();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        unbindGPS();
    }

    @Override
    public void onBackPressed()
    {
        int fragmentCount = getFragmentManager().getBackStackEntryCount();
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.content_frame);
        if (fragmentCount <= 1 || currentFragment instanceof MainActivityFragment)
        {
            super.onBackPressed();
        }
        else
        {
            getFragmentManager().popBackStack();
        }
    }

    private void setupLeftDrawer()
    {
        String[] menuItems = getResources().getStringArray(R.array.ui_sidebar_menu_items);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, menuItems));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case Constants.DRAWERITEM_SCAN:
                    {
                        setFragment(new MainActivityFragment(), Constants.SCANFRAGMENT_ID);
                        break;
                    }
                    case Constants.DRAWERITEM_SETTINGS:
                    {
                        setFragment(new MainSettingsFragment(), Constants.SETTINGSFRAGMENT_ID);
                        break;
                    }
                    case Constants.DRAWERITEM_ABOUT:
                    {
                        setFragment(new MainAboutFragment(), Constants.ABOUTFRAGMENT_ID);
                        break;
                    }
                }
            }
        });


    }

    private void setFragment(Fragment InFragment, String FragmentID)
    {
        if (InFragment != null)
        {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, InFragment, FragmentID)
                    .addToBackStack(null)
                    .commit();

            drawerLayout.closeDrawer(drawerList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.content_frame);
        if (id == R.id.action_settings && !(currentFragment instanceof MainSettingsFragment))
        {
            setFragment(new MainSettingsFragment(), Constants.SETTINGSFRAGMENT_ID);
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
        else if (requestCode == Constants.INSERT_NEW_PRODUCT && resultCode == RESULT_OK)
        {
            // We inserted a new product
            Intent intent = new Intent(this, ViewProductActivity.class);

            intent.putExtra(Constants.PRODUCT_NAME, data.getStringExtra(Constants.PRODUCT_NAME));
            intent.putExtra(Constants.PRODUCT_EAN, data.getStringExtra(Constants.PRODUCT_EAN));

            startActivityForResult(intent, Constants.VIEW_EXISTING_PRODUCT);
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private boolean hasStartedBefore()
    {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(Constants.PREF_HASSTARTEDBEFORE, false);
    }

    private void doFirstTimeSetup()
    {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        preferenceEditor.putBoolean(Constants.PREF_HASSTARTEDBEFORE, true);
        preferenceEditor.putBoolean(Constants.PREF_USEDARKTHEME, false);
        preferenceEditor.putFloat(Constants.PREF_PREFERREDSTOREDISTANCE, 2.0f);
        preferenceEditor.putFloat(Constants.PREF_MAXSTOREDISTANCE, 10.0f);
        preferenceEditor.putString(Constants.PREF_USERCURRENCY, Currency.getInstance(Locale.getDefault()).getCurrencyCode());

        preferenceEditor.apply();
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    public void onScanButtonClicked(View view)
    {
        // Will fail if there's no network available
        ProductScan.initiate(this);
    }
}
