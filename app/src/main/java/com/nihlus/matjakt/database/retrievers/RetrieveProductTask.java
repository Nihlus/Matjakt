package com.nihlus.matjakt.database.retrievers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.nihlus.matjakt.R;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.outpan.OutpanAPI2;
import com.nihlus.matjakt.outpan.OutpanProduct;
import com.nihlus.matjakt.ui.AddProductDialogFragment;
import com.nihlus.matjakt.ui.RepairProductDialogFragment;
import com.nihlus.matjakt.ui.ViewProductActivity;

/**
 * Retrieves a product from the Outpan database by its ean
 */
public class RetrieveProductTask extends AsyncTask<Void, Integer, OutpanProduct>
{
    private final Activity ParentActivity;
    private EAN ean;

    private ProgressDialog progressDialog;

    public RetrieveProductTask(Activity InParentActivity, EAN inEAN)
    {
        this.ParentActivity = InParentActivity;
        this.ean = inEAN;
    }

    @Override
    protected void onPreExecute()
    {
        progressDialog = ProgressDialog.show(ParentActivity, "", ParentActivity.getResources().getString(R.string.dialog_RetrievingProduct));
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                cancel(true);
            }
        });
    }

    @Override
    protected OutpanProduct doInBackground(Void... Nothing)
    {
        OutpanAPI2 api = new OutpanAPI2(Constants.OutpanAPIKey);
        return api.getProduct(ean);
    }

    @Override
    protected void onPostExecute(OutpanProduct result)
    {
        if (progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }

        if (result != null)
        {
            //launch product view activity
            if (!result.isNameValid())
            {
                //ask the user if they want to add a new product
                AddProductDialogFragment dialog = new AddProductDialogFragment(ParentActivity, ean);
                dialog.show(ParentActivity.getFragmentManager(), Constants.DIALOG_ADDPRODUCTFRAGMENT_ID);
            }
            else if (!result.isValid())
            {
                //broken product, ask if the user wants to edit it
                RepairProductDialogFragment dialog = new RepairProductDialogFragment(ParentActivity, result.getBundle());
                dialog.show(ParentActivity.getFragmentManager(), Constants.DIALOG_REPAIRPRODUCTFRAGMENT_ID);
            }
            else
            {
                if (ParentActivity instanceof ViewProductActivity)
                {
                    ((ViewProductActivity) ParentActivity).setVisibleProduct(result.getBundle());
                }
                else
                {
                    // First scan from MainActivity
                    Intent intent = new Intent(ParentActivity, ViewProductActivity.class);
                    intent.putExtra(Constants.PRODUCT_BUNDLE, result.getBundle());
                    ParentActivity.startActivity(intent);
                }
            }
        }
        else
        {
            Toast.makeText(ParentActivity, ParentActivity.getResources().getString(R.string.ui_warning_noresult), Toast.LENGTH_SHORT).show();
        }
    }
}
