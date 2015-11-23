package com.nihlus.matjakt;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.outpan.OutpanAPI2;
import com.nihlus.matjakt.outpan.OutpanProduct;
import com.nihlus.matjakt.services.GPSService;
import com.nihlus.matjakt.ui.AddProductDialogFragment;
import com.nihlus.matjakt.ui.MainAboutFragment;
import com.nihlus.matjakt.ui.MainSettingsFragment;
import com.nihlus.matjakt.ui.RepairProductDialogFragment;
import com.nihlus.matjakt.ui.ViewProductActivity;

import java.util.Currency;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    /// GPS Service Binding ///

    private boolean isGPSBound;
    private GPSService GPS;
    private ServiceConnection GPSConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            isGPSBound = true;
            GPSService.GPSBinder Binder = (GPSService.GPSBinder) service;
            GPS = Binder.getService();
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

        setupLeftDrawer();

        if (!hasStartedBefore())
        {
            doFirstTimeSetup();
        }

        setFragment(new MainActivityFragment(), Constants.SCANFRAGMENT_ID);

        bindGPS();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
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

        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menuItems));
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        preferenceEditor.commit();
    }

    public void onScanButtonClicked(View view)
    {
        // Will fail if there's no network available
        initiateScan();
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void initiateScan()
    {
        if (isNetworkAvailable())
        {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt(getResources().getString(R.string.prompt_ScanACode));
            integrator.setBeepEnabled(false);
            integrator.setOrientationLocked(false);

            integrator.initiateScan();
        }
        else
        {
            Toast.makeText(getApplication(), getResources().getString(R.string.debug_noInternet), Toast.LENGTH_LONG).show();
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
        else if (requestCode == Constants.INSERT_NEW_PRODUCT && resultCode == RESULT_OK)
        {
            // We inserted a new product
            Intent intent = new Intent(this, ViewProductActivity.class);

            intent.putExtra(Constants.PRODUCT_NAME, data.getStringExtra(Constants.PRODUCT_NAME));
            intent.putExtra(Constants.PRODUCT_EAN, data.getStringExtra(Constants.PRODUCT_EAN));

            startActivityForResult(intent, Constants.VIEW_EXISTING_PRODUCT);
        }
        else if (requestCode == Constants.VIEW_EXISTING_PRODUCT && resultCode == RESULT_OK)
        {
            int request = data.getIntExtra(Constants.GENERIC_INTENT_ID, -1);
            if (request == Constants.REQUEST_BARCODE_SCAN)
            {
                initiateScan();
            }
        }
        else if (requestCode == Constants.MODIFY_EXISTING_PRODUCT && resultCode == RESULT_OK)
        {
            int request = data.getIntExtra(Constants.GENERIC_INTENT_ID, -1);
            if (request == Constants.REQUEST_BARCODE_SCAN)
            {
                initiateScan();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class RetrieveProductTask extends AsyncTask<EAN, Integer, OutpanProduct>
    {
        private final Activity ParentActivity;
        private EAN ean;

        private ProgressDialog progressDialog;

        public RetrieveProductTask(Activity InParentActivity)
        {
            this.ParentActivity = InParentActivity;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(MainActivity.this, "", getResources().getString(R.string.dialog_RetrievingProduct));
        }

        @Override
        protected OutpanProduct doInBackground(EAN... inEANs)
        {
            this.ean = inEANs[0];

            OutpanAPI2 api = new OutpanAPI2(Constants.OutpanAPIKey);
            return api.getProduct(inEANs[0]);
        }

        @Override
        protected void onPostExecute(OutpanProduct result)
        {
            if (progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }

            //launch product view activity
            if (!result.isNameValid())
            {
                //ask the user if they want to add a new product
                AddProductDialogFragment dialog = new AddProductDialogFragment(ParentActivity, ean);
                dialog.show(getFragmentManager(), Constants.DIALOG_ADDPRODUCTFRAGMENT_ID);
            }
            else if (!result.isValid())
            {
                //broken product, ask if the user wants to edit it
                RepairProductDialogFragment dialog = new RepairProductDialogFragment(ParentActivity, ean, result.getBundle());
                dialog.show(getFragmentManager(), Constants.DIALOG_REPAIRPRODUCTFRAGMENT_ID);
            }
            else
            {
                Intent intent = new Intent(ParentActivity, ViewProductActivity.class);

                intent.putExtra(Constants.PRODUCT_BUNDLE_EXTRA, result.getBundle());

                startActivityForResult(intent, Constants.VIEW_EXISTING_PRODUCT);
            }
        }
    }
}
