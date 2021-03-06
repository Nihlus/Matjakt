/**
 *  ModifyProductTask.java
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

package com.nihlus.matjakt.database.inserters;

import android.accounts.NetworkErrorException;
import android.app.Activity;
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
import com.nihlus.matjakt.ui.ModifyProductActivity;

import java.util.HashMap;

public class ModifyProductTask extends AsyncTask<Void, Void, OutpanProduct>
{
    private final ModifyProductActivity modifyProductActivity;
    private final OutpanProduct newProduct;

    private final ProgressDialog dialog;

    public ModifyProductTask(ModifyProductActivity InParentActivity, OutpanProduct InProductData)
    {
        this.modifyProductActivity = InParentActivity;
        this.newProduct = InProductData;

        dialog = new ProgressDialog(InParentActivity);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        this.dialog.setMessage(modifyProductActivity.getString(R.string.dialog_updatingProductInfo));
        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                cancel(true);
            }
        });

        this.dialog.show();
    }

    protected OutpanProduct doInBackground(Void... inputs)
    {
        OutpanAPI2 api = new OutpanAPI2();
        EAN productEAN = newProduct.ean;

        try
        {
            api.setProductName(productEAN, getFinalProductString());

            api.setProductAttribute(productEAN,
                    Constants.PRODUCT_BRAND_ATTRIBUTE,
                    newProduct.attributes.get(Constants.PRODUCT_BRAND_ATTRIBUTE));

            // Store the brand for autocomplete purposes
            modifyProductActivity.addStoredBrand(newProduct.attributes.get(Constants.PRODUCT_BRAND_ATTRIBUTE));

            api.setProductAttribute(productEAN,
                    Constants.PRODUCT_TITLE_ATTRIBUTE,
                    newProduct.attributes.get(Constants.PRODUCT_TITLE_ATTRIBUTE));


            if (newProduct.attributes.containsKey(Constants.PRODUCT_AMOUNT_ATTRIBUTE))
            {
                boolean isEmpty = modifyProductActivity.splitAmountValue(newProduct.attributes.get(Constants.PRODUCT_AMOUNT_ATTRIBUTE))
                        .get(Constants.SPLITMAP_NUMBER).isEmpty();

                if (!isEmpty)
                {
                    api.setProductAttribute(productEAN,
                            Constants.PRODUCT_AMOUNT_ATTRIBUTE,
                            newProduct.attributes.get(Constants.PRODUCT_AMOUNT_ATTRIBUTE));
                }
            }

            if (newProduct.attributes.containsKey(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE))
            {
                api.setProductAttribute(productEAN,
                        Constants.PRODUCT_BYWEIGHT_ATTRIBUTE,
                        newProduct.attributes.get(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE));
            }

            if (newProduct.attributes.containsKey(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE))
            {
                api.setProductAttribute(productEAN,
                        Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE,
                        newProduct.attributes.get(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE));
            }

            if (newProduct.attributes.containsKey(Constants.PRODUCT_ORGANIC_ATTRIBUTE))
            {
                api.setProductAttribute(productEAN,
                        Constants.PRODUCT_ORGANIC_ATTRIBUTE,
                        newProduct.attributes.get(Constants.PRODUCT_ORGANIC_ATTRIBUTE));
            }
            else if (modifyProductActivity.product.attributes.containsKey(Constants.PRODUCT_ORGANIC_ATTRIBUTE))
            {
                // Remove the attribute by setting it to empty
                api.deleteProductAttribute(productEAN,
                        Constants.PRODUCT_ORGANIC_ATTRIBUTE);
            }

            if (newProduct.attributes.containsKey(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE))
            {
                api.setProductAttribute(productEAN,
                        Constants.PRODUCT_FAIRTRADE_ATTRIBUTE,
                        newProduct.attributes.get(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE));
            }
            else if (modifyProductActivity.product.attributes.containsKey(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE))
            {
                // Remove the attribute by setting it to empty
                api.deleteProductAttribute(productEAN,
                        Constants.PRODUCT_FAIRTRADE_ATTRIBUTE);
            }
        }
        catch (NetworkErrorException nex)
        {
            nex.printStackTrace();
        }

        return api.getProduct(productEAN);
    }

    protected void onPostExecute(OutpanProduct result)
    {
        super.onPostExecute(result);
        if (this.dialog.isShowing() && result != null)
        {
            this.dialog.dismiss();

            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constants.PRODUCT_PARCEL, result);

            modifyProductActivity.setResult(Activity.RESULT_OK, resultIntent);
            modifyProductActivity.finish();
        }
        else
        {
            modifyProductActivity.setResult(Activity.RESULT_CANCELED);
            this.dialog.dismiss();

            Toast.makeText(modifyProductActivity, modifyProductActivity.getString(R.string.prompt_productUpdateFailed), Toast.LENGTH_LONG).show();
        }
    }

    private String getFinalProductString()
    {
        String productBrand = newProduct.attributes.get(Constants.PRODUCT_BRAND_ATTRIBUTE);
        String productTitle = newProduct.attributes.get(Constants.PRODUCT_TITLE_ATTRIBUTE);

        HashMap<String, String> amountValues = modifyProductActivity.splitAmountValue(newProduct.attributes.get(Constants.PRODUCT_AMOUNT_ATTRIBUTE));
        String productAmount = amountValues.get(Constants.SPLITMAP_NUMBER);
        String productAmountUnit = amountValues.get(Constants.SPLITMAP_UNIT);

        return productBrand + " " +
                productTitle + " " +
                productAmount + productAmountUnit;
    }
}