package com.nihlus.matjakt.database.retrievers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

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
public class RetrieveProductTask extends AsyncTask<EAN, Integer, OutpanProduct>
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
        progressDialog = ProgressDialog.show(ParentActivity, "", ParentActivity.getResources().getString(R.string.dialog_RetrievingProduct));
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
            dialog.show(ParentActivity.getFragmentManager(), Constants.DIALOG_ADDPRODUCTFRAGMENT_ID);
        }
        else if (!result.isValid())
        {
            //broken product, ask if the user wants to edit it
            RepairProductDialogFragment dialog = new RepairProductDialogFragment(ParentActivity, ean, result.getBundle());
            dialog.show(ParentActivity.getFragmentManager(), Constants.DIALOG_REPAIRPRODUCTFRAGMENT_ID);
        }
        else
        {
            Intent intent = new Intent(ParentActivity, ViewProductActivity.class);
            intent.putExtra(Constants.PRODUCT_BUNDLE_EXTRA, result.getBundle());
            ParentActivity.startActivity(intent);

            if (ParentActivity instanceof ViewProductActivity)
            {
                ParentActivity.finish();
            }
        }
    }
}
