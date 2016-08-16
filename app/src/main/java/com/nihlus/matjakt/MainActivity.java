/**
 *  MainActivity.java
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

package com.nihlus.matjakt;

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
import com.nihlus.matjakt.ui.SettingsActivity;
import com.nihlus.matjakt.ui.ViewProductActivity;

import java.util.Currency;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private ActionBarDrawerToggle drawerToggle;

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
        bindGPS();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        unbindGPS();
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
                        super.onDrawerClosed(drawerView);
                        invalidateOptionsMenu();
                    }

                    @Override
                    public void onDrawerOpened(View drawerView)
                    {
                        super.onDrawerOpened(drawerView);
                        invalidateOptionsMenu();
                    }
                };
        drawerLayout.addDrawerListener(drawerToggle);

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
                        onScanButtonClicked(null);

                        drawerLayout.closeDrawer(drawerList);
                        break;
                    }
                    case Constants.DRAWERITEM_SETTINGS:
                    {
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));

                        drawerLayout.closeDrawer(drawerList);
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
        else if (requestCode == Constants.MODIFY_EXISTING_PRODUCT ||
                requestCode == Constants.INSERT_NEW_PRODUCT &&
                resultCode == RESULT_OK)
        {
            // We inserted a new product
            Intent intent = new Intent(this, ViewProductActivity.class);
            intent.putExtra(Constants.PRODUCT_PARCEL, data.getParcelableExtra(Constants.PRODUCT_PARCEL));

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
