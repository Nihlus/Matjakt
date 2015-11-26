package com.nihlus.matjakt;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

/**
 * Handler class for running new product scans.
 */
public class ProductScan
{
    public static void initiate(Activity InParentActivity)
    {
        if (isNetworkAvailable(InParentActivity))
        {
            IntentIntegrator integrator = new IntentIntegrator(InParentActivity);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt(InParentActivity.getResources().getString(R.string.prompt_ScanACode));
            integrator.setBeepEnabled(false);
            integrator.setOrientationLocked(false);

            integrator.initiateScan();
        }
        else
        {
            Toast.makeText(InParentActivity, InParentActivity.getResources().getString(R.string.debug_noInternet), Toast.LENGTH_LONG).show();
        }
    }

    private static boolean isNetworkAvailable(Activity InParentActivity)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) InParentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
