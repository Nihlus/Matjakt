package com.nihlus.matjakt;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
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
import com.nihlus.matjakt.Services.GPSService;
import com.nihlus.matjakt.UI.RepairProductDialogFragment;
import com.nihlus.matjakt.UI.ViewProductActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.johncipponeri.outpanapi.OutpanAPI;
import io.github.johncipponeri.outpanapi.OutpanObject;

public class MainActivity extends AppCompatActivity
{
    private String currentPhotoPath;
    private EAN currentEAN;

    private boolean isGPSBound;
    private boolean isGPSConnected;
    private GPSService GPS;

    private ServiceConnection GPSConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            GPS = ((GPSService.GPSBinder)service).getService();
            isGPSConnected = true;

            onGPSConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            GPS = null;
            isGPSConnected = false;
        }
    };

    private void bindGPS()
    {
        bindService(new Intent(this, GPSService.class), GPSConnection, Context.BIND_AUTO_CREATE);
        isGPSBound = true;
    }

    private void unbindGPS()
    {
        if (isGPSBound)
        {
            unbindService(GPSConnection);
            isGPSBound = false;
        }
    }

    private void onGPSConnected()
    {
        //GPS.startService(new Intent(this, GPSService.class));
        GPS.startService(new Intent(this, GPSService.class));

        Location someLocation = GPS.getCurrentLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindGPS();

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

        if (!isGPSBound)
        {
            bindGPS();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (isGPSBound)
        {
            unbindGPS();
        }
    }

    private void setFragment(Fragment fragment)
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
        // Will fail if there's no network available
        InitiateScan();
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
        else if (requestCode == Constants.MODIFY_EXISTING_PRODUCT && resultCode == RESULT_OK)
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

    //HACK: This should be in its own class or in OutpanObject as getBundle()
    public static Bundle getOutpanBundle(OutpanObject outpanObject)
    {
        Bundle productData = new Bundle();

        if (outpanObject.attributes.containsKey(Constants.PRODUCT_BRAND_ATTRIBUTE))
        {
            productData.putString(Constants.PRODUCT_BRAND_ATTRIBUTE, outpanObject.attributes.get(Constants.PRODUCT_BRAND_ATTRIBUTE));
        }

        if (outpanObject.attributes.containsKey(Constants.PRODUCT_TITLE_ATTRIBUTE))
        {
            productData.putString(Constants.PRODUCT_TITLE_ATTRIBUTE, outpanObject.attributes.get(Constants.PRODUCT_TITLE_ATTRIBUTE));
        }

        if (outpanObject.attributes.containsKey(Constants.PRODUCT_AMOUNT_ATTRIBUTE))
        {
            productData.putString(Constants.PRODUCT_AMOUNT_ATTRIBUTE, outpanObject.attributes.get(Constants.PRODUCT_AMOUNT_ATTRIBUTE));
        }

        if (outpanObject.attributes.containsKey(Constants.PRODUCT_ORGANIC_ATTRIBUTE))
        {
            productData.putBoolean(Constants.PRODUCT_ORGANIC_ATTRIBUTE, Boolean.valueOf(outpanObject.attributes.get(Constants.PRODUCT_ORGANIC_ATTRIBUTE)));
        }

        if (outpanObject.attributes.containsKey(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE))
        {
            productData.putBoolean(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE, Boolean.valueOf(outpanObject.attributes.get(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE)));
        }

        return productData;
    }

    private class AsyncProductResolver extends AsyncTask<EAN, Integer, OutpanObject>
    {
        private final Activity activity;
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
        protected OutpanObject doInBackground(EAN... inEans)
        {
            this.ean = inEans[0].getCode();
            this.type = inEans[0].getType();

            OutpanAPI api = new OutpanAPI(Constants.OutpanAPIKey);
            return api.getProduct(ean);
        }

        @Override
        protected void onPostExecute(OutpanObject result)
        {
            if (progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }

            //launch product view activity
            if (isProductValid(result))
            {
                Intent intent = new Intent(activity, ViewProductActivity.class);

                intent.putExtra(Constants.PRODUCT_TITLE_EXTRA, result.name);
                intent.putExtra(Constants.PRODUCT_EAN_EXTRA, ean);
                intent.putExtra(Constants.PRODUCT_BUNDLE_EXTRA, getOutpanBundle(result));

                startActivityForResult(intent, Constants.VIEW_EXISTING_PRODUCT);
            }
            else if (isNameValid(result.name))
            {
                //broken product, ask if the user wants to edit it
                RepairProductDialogFragment dialog = new RepairProductDialogFragment(activity, ean, getOutpanBundle(result));
                dialog.show(getFragmentManager(), Constants.DIALOG_REPAIRPRODUCTFRAGMENT_ID);
            }
            else
            {
                //ask the user if they want to add a new product
                AddProductDialogFragment dialog = new AddProductDialogFragment(activity, ean);
                dialog.show(getFragmentManager(), Constants.DIALOG_ADDPRODUCTFRAGMENT_ID);
            }
        }

        private boolean isNameValid(String name)
        {
            //if name is empty or says null, return false
            boolean nameIsEmpty = name.isEmpty();
            boolean nameIsNull = name.equals("null");


            if (nameIsEmpty || nameIsNull)
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        private boolean isProductValid(OutpanObject outpanObject)
        {
            boolean isMissingRequiredAttributes = false;

            if (!isNameValid(outpanObject.name))
            {
                isMissingRequiredAttributes = true;
            }

            if (!outpanObject.attributes.containsKey(Constants.PRODUCT_BRAND_ATTRIBUTE))
            {
                isMissingRequiredAttributes = true;
            }

            if (!outpanObject.attributes.containsKey(Constants.PRODUCT_TITLE_ATTRIBUTE))
            {
                isMissingRequiredAttributes = true;
            }

            if (!outpanObject.attributes.containsKey(Constants.PRODUCT_AMOUNT_ATTRIBUTE))
            {
                isMissingRequiredAttributes = true;
            }

            return !isMissingRequiredAttributes;
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
