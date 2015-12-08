package com.nihlus.matjakt.database.retrievers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

    private OutpanProduct byWeightProduct;
    private OutpanProduct resultProduct;
    private EAN resultEAN;

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

        if (ean.isInternalCode())
        {
            byWeightProduct = api.getProduct(ean.getEmbeddedPriceEAN());
        }

        return api.getProduct(ean);
    }

    @Override
    protected void onPostExecute(final OutpanProduct result)
    {
        if (progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }

        if (byWeightProduct != null && ean.isInternalCode() && !result.isValid())
        {
            if (byWeightProduct.isValid())
            {
                updateViewActivity(byWeightProduct.getBundle());
            }
            else
            {
                AlertDialog.Builder isProductAByWeightProductDialog = new AlertDialog.Builder(ParentActivity)
                        .setTitle(ParentActivity.getString(R.string.dialog_mightBeByWeight))
                        .setMessage(ParentActivity.getString(R.string.dialog_mightBeByWeight_body))
                        .setPositiveButton(ParentActivity.getString(R.string.dialog_Yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (byWeightProduct != null)
                                {
                                    //launch product view activity
                                    if (!byWeightProduct.isValid())
                                    {
                                        addProduct(ean.getEmbeddedPriceEAN());
                                    }
                                    else if (!byWeightProduct.isMissingRequiredAttributes())
                                    {
                                        repairProduct(byWeightProduct.getBundle());
                                    }
                                    else
                                    {
                                        updateViewActivity(byWeightProduct.getBundle());
                                    }
                                }
                                else
                                {
                                    Toast.makeText(ParentActivity, ParentActivity.getResources().getString(R.string.ui_warning_noresult), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(ParentActivity.getString(R.string.dialog_No), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (result != null)
                                {
                                    //launch product view activity
                                    if (!result.isValid())
                                    {
                                        addProduct(ean);
                                    }
                                    else if (!result.isMissingRequiredAttributes())
                                    {
                                        repairProduct(result.getBundle());
                                    }
                                    else
                                    {
                                        updateViewActivity(result.getBundle());
                                    }
                                }
                                else
                                {
                                    Toast.makeText(ParentActivity, ParentActivity.getResources().getString(R.string.ui_warning_noresult), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                isProductAByWeightProductDialog.show();
            }
        }
        else if (result != null)
        {
            //launch product view activity
            if (!result.isValid())
            {
                addProduct(ean);
            }
            else if (!result.isMissingRequiredAttributes())
            {
                repairProduct(result.getBundle());
            }
            else
            {
                updateViewActivity(result.getBundle());
            }
        }
        else
        {
            Toast.makeText(ParentActivity, ParentActivity.getResources().getString(R.string.ui_warning_noresult), Toast.LENGTH_SHORT).show();
        }
    }

    private void addProduct(EAN InEAN)
    {
        //ask the user if they want to add a new product
        AddProductDialogFragment dialog = new AddProductDialogFragment(ParentActivity, InEAN);
        dialog.show(ParentActivity.getFragmentManager(), Constants.DIALOG_ADDPRODUCTFRAGMENT_ID);
    }

    private void repairProduct(Bundle InProductData)
    {
        //broken product, ask if the user wants to edit it
        RepairProductDialogFragment dialog = new RepairProductDialogFragment(ParentActivity, InProductData);
        dialog.show(ParentActivity.getFragmentManager(), Constants.DIALOG_REPAIRPRODUCTFRAGMENT_ID);
    }

    private void updateViewActivity(Bundle InProductData)
    {
        if (ParentActivity instanceof ViewProductActivity)
        {
            ((ViewProductActivity) ParentActivity).setVisibleProduct(InProductData);
        }
        else
        {
            // First scan from MainActivity
            Intent intent = new Intent(ParentActivity, ViewProductActivity.class);
            intent.putExtra(Constants.PRODUCT_BUNDLE, InProductData);
            ParentActivity.startActivity(intent);
        }
    }
}
