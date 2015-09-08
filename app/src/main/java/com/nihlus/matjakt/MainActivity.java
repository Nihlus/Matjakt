package com.nihlus.matjakt;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.Outpan.OutpanAPI2;
import com.nihlus.matjakt.UI.ViewProductActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.johncipponeri.outpanapi.OutpanAPI;
import io.github.johncipponeri.outpanapi.OutpanObject;

public class MainActivity extends AppCompatActivity
{
    /**
     * Location listener for the application. Saves the current latitude and longitude into prefs.
     */
    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_file_key),
                    Context.MODE_MULTI_PROCESS);

            SharedPreferences.Editor editor = preferences.edit();

            editor.putLong(Constants.LATITUDE_ID, Double.doubleToLongBits(location.getLatitude()));
            editor.putLong(Constants.LONGITUDE_ID, Double.doubleToLongBits(location.getLongitude()));

            editor.commit();

            long latitude = 0;
            preferences.getLong(Constants.LATITUDE_ID, latitude);

            // TODO: 9/8/15 Debug logging, remove
            Log.d(Constants.MATJAKT_LOG_ID, getResources().getString(R.string.debug_locationFromGPS)
                    + String.valueOf(location.getLatitude()));
            Log.d(Constants.MATJAKT_LOG_ID, getResources().getString(R.string.debug_locationFromStorage)
                    + String.valueOf(Double.longBitsToDouble(latitude)));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

        @Override
        public void onProviderEnabled(String provider)
        {

        }

        @Override
        public void onProviderDisabled(String provider)
        {

        }
    };
    private LocationManager locationManager;
    private String currentPhotoPath;
    private EAN currentEAN;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (locationManager == null)
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), 0, 1, locationListener);
        }
        else
        {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), 0, 1, locationListener);
        }

        if (savedInstanceState == null)
        {
            FragmentManager manager = getFragmentManager();
            Fragment currentFragment = manager.findFragmentByTag(Constants.CURRENTFRAGMENT_ID);
            if (currentFragment != null)
            {
                setFragment(currentFragment);
            }
            else
            {
                setFragment(new MainActivityFragment());
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(bestProvider, 2000, 1, locationListener);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    public void setFragment(Fragment fragment)
    {
        if (fragment != null)
        {
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.content_frame, fragment, Constants.CURRENTFRAGMENT_ID).commit();
        }
        else
        {
            Log.e(Constants.MATJAKT_LOG_ID, getResources().getString(R.string.debug_fragmentNullLog));
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

    public void OnScanButtonClicked(View view)
    {
        InitiateScan();
    }

    private File createImageFile() throws IOException
    {
        String timestamep = new SimpleDateFormat(Constants.DATEFORMAT_EUR).format(new Date());
        String imageFileName = Constants.MATJAKT_IMAGE_PREFIX + timestamep;

        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, Constants.JPEGSUFFIX, storageDirectory);

        //currentPhotoPath = "file:" + image.getAbsolutePath();
        currentPhotoPath = image.getAbsolutePath();

        //add the image to the gallery
        MediaScannerConnection.scanFile(this, new String[]{image.getPath()}, new String[]{Constants.WEBFORMAT_JPEG}, null);

        return image;
    }

    private void InitiateScan()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt(getResources().getString(R.string.prompt_ScanACode));
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(false);

        integrator.initiateScan();
    }

    private void EnableLocationUpdates()
    {

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
                new AsyncProductResolver(this).execute(new EAN(result.getContents(), result.getFormatName()));
            }
        }
        else if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            // We took a picture of the product
            new AsyncImageUploader().execute(currentPhotoPath);
        }
        else if (requestCode == Constants.INSERT_NEW_PRODUCT && resultCode == RESULT_OK)
        {
            // We inserted a new product
            Intent intent = new Intent(this, ViewProductActivity.class);

            // TODO: 9/8/15 Implement static names for extras
            intent.putExtra(Constants.PRODUCT_TITLE_EXTRA, data.getStringExtra(Constants.PRODUCT_TITLE_EXTRA));
            intent.putExtra(Constants.PRODUCT_EAN_EXTRA, data.getStringExtra(Constants.PRODUCT_EAN_EXTRA));

            startActivityForResult(intent, Constants.VIEW_EXISTING_PRODUCT);
        }
        else if (requestCode == Constants.VIEW_EXISTING_PRODUCT && resultCode == RESULT_OK)
        {
            int request = data.getIntExtra(Constants.GENERIC_INTENT_ID, -1);
            if (request == Constants.REQUEST_BARCODE_SCAN)
            {
                InitiateScan();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class AsyncProductResolver extends AsyncTask<EAN, Integer, EANProduct>
    {
        private Activity activity;
        private String ean;
        private String type;

        private ProgressDialog progressDialog;

        public AsyncProductResolver(Activity activity)
        {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(MainActivity.this, "", getResources().getString(R.string.dialog_RetrievingProduct));
        }

        @Override
        protected EANProduct doInBackground(EAN... inEans)
        {
            this.ean = inEans[0].getCode();
            this.type = inEans[0].getType();

            EANProduct Product = new EANProduct(inEans[0]);
            OutpanAPI api = new OutpanAPI(Constants.OutpanAPIKey);
            OutpanObject outpanEntry = api.getProduct(ean);

            if (isNameValid(outpanEntry.name))
            {
                Product.setProductName(outpanEntry.name);
            }
            else
            {
                Product.setProductName(getResources().getString(R.string.dialog_NoProductFound));
            }
            return Product;
        }

        @Override
        protected void onPostExecute(EANProduct result)
        {
            if (progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }

            //launch product view activity
            if (result.getProductName() != getResources().getString(R.string.dialog_NoProductFound))
            {
                Intent intent = new Intent(activity, ViewProductActivity.class);

                intent.putExtra(Constants.PRODUCT_TITLE_EXTRA, result.getProductName());
                intent.putExtra(Constants.PRODUCT_EAN_EXTRA, ean);

                startActivityForResult(intent, Constants.VIEW_EXISTING_PRODUCT);
            }
            else
            {
                //ask the user if they want to add a new product
                AddProductDialogFragment dialog = new AddProductDialogFragment(activity, ean);
                dialog.show(getFragmentManager(), Constants.ADDPRODUCTFRAGMENT_ID);
            }
        }

        private boolean isNameValid(String name)
        {
            if (name.isEmpty() || name == "null" || name == null)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    private class AsyncImageUploader extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Boolean doInBackground(String... inFiles)
        {
            try
            {
                return OutpanAPI2.UploadImage(new String[]{inFiles[0]}, currentEAN.getCode());
            } catch (IOException iex)
            {
                iex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success)
        {
            if (!success)
            {
                Toast.makeText(getApplication(), getResources().getString(R.string.debug_imageUploadFailed), Toast.LENGTH_LONG).show();
            }
        }
    }
}
