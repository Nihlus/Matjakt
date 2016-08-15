/**
 *  RetrieveProductTask.java
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
import com.nihlus.matjakt.ui.ModifyProductDialogFragment;
import com.nihlus.matjakt.ui.ViewProductActivity;

/**
 * Retrieves a product from the Outpan database by its ean
 */
public class RetrieveProductTask extends AsyncTask<Void, Integer, OutpanProduct>
{
    private final Activity parentActivity;
    private final EAN ean;

    private OutpanProduct byWeightProduct;
    private ProgressDialog progressDialog;

    public RetrieveProductTask(Activity InParentActivity, EAN InEAN)
    {
        this.parentActivity = InParentActivity;
        this.ean = InEAN;
    }

    @Override
    protected void onPreExecute()
    {
        progressDialog = ProgressDialog.show(parentActivity, "", parentActivity.getResources().getString(R.string.dialog_RetrievingProduct));
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
    protected OutpanProduct doInBackground(Void... params)
    {
        OutpanAPI2 api = new OutpanAPI2(Constants.OutpanAPIKey);

        if (ean.isInternalCode())
        {
            byWeightProduct = api.getProduct(ean.getEmbeddedPriceEAN());
            if (byWeightProduct != null)
            {
                byWeightProduct.setIsSoldByWeight(true);
            }
        }

        return api.getProduct(ean);
    }

    // TODO: Clean this crap up
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
                updateViewActivity(byWeightProduct);
            }
            else
            {
                AlertDialog.Builder isProductAByWeightProductDialog = new AlertDialog.Builder(parentActivity);

                isProductAByWeightProductDialog.setTitle(parentActivity.getString(R.string.dialog_mightBeByWeight));
                isProductAByWeightProductDialog.setMessage(parentActivity.getString(R.string.dialog_mightBeByWeight_body));
                isProductAByWeightProductDialog.setPositiveButton(parentActivity.getString(R.string.dialog_Yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (byWeightProduct != null)
                        {
                            //launch product view activity
                            if (!byWeightProduct.isValid())
                            {
                                addProduct(byWeightProduct);
                            }
                            else if (byWeightProduct.isMissingRequiredAttributes())
                            {
                                repairProduct(byWeightProduct);
                            }
                            else
                            {
                                updateViewActivity(byWeightProduct);
                            }
                        }
                        else
                        {
                            Toast.makeText(parentActivity, parentActivity.getResources().getString(R.string.ui_warning_noresult), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                isProductAByWeightProductDialog.setNegativeButton(parentActivity.getString(R.string.dialog_No), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (result != null)
                        {
                            //launch product view activity
                            if (!result.isValid())
                            {
                                addProduct(result);
                            }
                            else if (result.isMissingRequiredAttributes())
                            {
                                repairProduct(result);
                            }
                            else
                            {
                                updateViewActivity(result);
                            }
                        }
                        else
                        {
                            Toast.makeText(parentActivity, parentActivity.getResources().getString(R.string.ui_warning_noresult), Toast.LENGTH_SHORT).show();
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
                addProduct(result);
            }
            else if (result.isMissingRequiredAttributes())
            {
                repairProduct(result);
            }
            else
            {
                updateViewActivity(result);
            }
        }
        else
        {
            Toast.makeText(parentActivity, parentActivity.getResources().getString(R.string.ui_warning_noresult), Toast.LENGTH_SHORT).show();
        }
    }

    private void addProduct(OutpanProduct InTemplateProduct)
    {
        //ask the user if they want to add a new product
        ModifyProductDialogFragment dialog = new ModifyProductDialogFragment(parentActivity, InTemplateProduct, false);
        dialog.show(parentActivity.getFragmentManager(), Constants.DIALOG_ADDPRODUCTFRAGMENT_ID);
    }

    private void repairProduct(OutpanProduct InTemplateProduct)
    {
        //broken product, ask if the user wants to edit it
        ModifyProductDialogFragment dialog = new ModifyProductDialogFragment(parentActivity, InTemplateProduct, true);
        dialog.show(parentActivity.getFragmentManager(), Constants.DIALOG_REPAIRPRODUCTFRAGMENT_ID);
    }

    private void updateViewActivity(OutpanProduct InProduct)
    {
        if (parentActivity instanceof ViewProductActivity)
        {
            ((ViewProductActivity) parentActivity).setVisibleProduct(InProduct);
        }
        else
        {
            // First scan from MainActivity
            Intent intent = new Intent(parentActivity, ViewProductActivity.class);
            intent.putExtra(Constants.PRODUCT_PARCEL, InProduct);
            parentActivity.startActivity(intent);
        }
    }
}
