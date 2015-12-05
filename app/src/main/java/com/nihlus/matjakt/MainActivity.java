package com.nihlus.matjakt;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
import com.nihlus.matjakt.ui.SettingsActivity;
import com.nihlus.matjakt.ui.ViewProductActivity;

import java.util.Currency;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private ActionBarDrawerToggle drawerToggle;

    private static Context context;

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
        context = this;

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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);

            actionBar.setHomeButtonEnabled(true);
        }

        String[] menuItems = getResources().getStringArray(R.array.ui_sidebar_menu_items);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.ui_drawer_open,
                R.string.ui_drawer_close
                )
                {
                    @Override
                    public void onDrawerClosed(View drawerView)
                    {
                        invalidateOptionsMenu();
                    }

                    @Override
                    public void onDrawerOpened(View drawerView)
                    {
                        invalidateOptionsMenu();
                    }
                };
        drawerLayout.setDrawerListener(drawerToggle);

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
                        //setFragment(new MainSettingsFragment(), Constants.SETTINGSFRAGMENT_ID);
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    }
                    case Constants.DRAWERITEM_ABOUT:
                    {
                        setFragment(new MainAboutFragment(), Constants.ABOUTFRAGMENT_ID);
                        break;
                    }
                    case Constants.DRAWERITEM_BUGREPORT:
                    {
                        Intent bugReportEmail = new Intent(Intent.ACTION_SENDTO);
                        bugReportEmail.setType("message/rfc822");
                        bugReportEmail.setData(Uri.parse("mailto:" + Constants.DEVELOPEREMAIL));
                        bugReportEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.DEVELOPEREMAIL});
                        bugReportEmail.putExtra(Intent.EXTRA_SUBJECT, Constants.EMAIL_BUGREPORT_SUBJECT);
                        bugReportEmail.putExtra(Intent.EXTRA_TEXT, Constants.EMAIL_BUGREPORT_BODY);

                        if (bugReportEmail.resolveActivity(getPackageManager()) != null)
                        {
                            startActivity(Intent.createChooser(bugReportEmail, getResources().getString(R.string.ui_sendbugreport)));
                        }

                        drawerLayout.closeDrawer(drawerList);
                        break;
                    }
                    case Constants.DRAWERITEM_REQUESTFEATURE:
                    {
                        Intent featureRequestEmail = new Intent(Intent.ACTION_SENDTO);
                        featureRequestEmail.setType("text/plain");
                        featureRequestEmail.setData(Uri.parse("mailto:" + Constants.DEVELOPEREMAIL));
                        featureRequestEmail.putExtra(Intent.EXTRA_EMAIL, Constants.DEVELOPEREMAIL);
                        featureRequestEmail.putExtra(Intent.EXTRA_SUBJECT, Constants.EMAIL_FEATUREREQUEST_SUBJECT);
                        featureRequestEmail.putExtra(Intent.EXTRA_TEXT, Constants.EMAIL_FEATUREREQUEST_BODY);

                        if (featureRequestEmail.resolveActivity(getPackageManager()) != null)
                        {
                            startActivity(featureRequestEmail);
                        }

                        drawerLayout.closeDrawer(drawerList);
                        break;
                    }
                }
            }
        });


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null)
        {
            drawerToggle.syncState();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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
        else if (requestCode == Constants.MODIFY_EXISTING_PRODUCT ||
                requestCode == Constants.INSERT_NEW_PRODUCT &&
                resultCode == RESULT_OK)
        {
            // We inserted a new product
            Intent intent = new Intent(this, ViewProductActivity.class);
            intent.putExtra(Constants.PRODUCT_BUNDLE, data.getBundleExtra(Constants.PRODUCT_BUNDLE));

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

    public static Context getStaticContext()
    {
        return context;
    }
}
