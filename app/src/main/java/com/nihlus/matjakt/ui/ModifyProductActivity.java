/**
 *  ModifyProductActivity.java
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

package com.nihlus.matjakt.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.nihlus.matjakt.R;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.inserters.ModifyProductTask;
import com.nihlus.matjakt.outpan.OutpanProduct;
import com.nihlus.matjakt.ui.adapters.BrandNameAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class ModifyProductActivity extends AppCompatActivity
{
    public OutpanProduct product;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        // Detect if the product is a by-weight product and needs special layout
        product = intent.getParcelableExtra(Constants.PRODUCT_PARCEL);

        if (product.isSoldByWeight())
        {
            setContentView(R.layout.activity_modify_product_by_weight);
        }
        else
        {
            setContentView(R.layout.activity_modify_product);
        }

        setVisibleProduct((OutpanProduct)intent.getParcelableExtra(Constants.PRODUCT_PARCEL));

        int modifyType = intent.getIntExtra(Constants.MODIFY_INTENT_TYPE, -1);
        if (modifyType == Constants.INSERT_NEW_PRODUCT)
        {
            setTitle(getResources().getString(R.string.title_activity_new_product));
        }
        else if (modifyType == Constants.MODIFY_EXISTING_PRODUCT)
        {
            setTitle(getResources().getString(R.string.title_activity_edit_product));
        }
        else
        {
            setTitle(getResources().getString(R.string.debug_howDidYouGetHere));
        }

        // Setup Brand autocomplete
        setupBrandAutocomplete();
    }

    private void setupBrandAutocomplete()
    {
        final AutoCompleteTextView brandEdit = (AutoCompleteTextView)findViewById(R.id.brandEdit);
        if (brandEdit != null)
        {
            brandEdit.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    brandEdit.setText(((TextView) view).getText().toString());
                }
            });

            ArrayAdapter<String> autocompleteAdapter = new BrandNameAdapter(this, BrandNameAdapter.getStoredBrands(getApplicationContext()));
            brandEdit.setAdapter(autocompleteAdapter);

        }
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    public void onClickOKButton(View view)
    {
        //verify data
        if (haveFieldsChanged())
        {
            // If we've altered any data, send it to the server
            if (areRequiredFieldsFilledOut())
            {
                //bundle up the data needed by the server
                Bundle newProductData = new Bundle();

                EditText brandName = (EditText) findViewById(R.id.brandEdit);
                newProductData.putString(Constants.PRODUCT_BRAND_ATTRIBUTE, brandName.getText().toString());

                EditText productTitle = (EditText) findViewById(R.id.productNameEdit);
                newProductData.putString(Constants.PRODUCT_TITLE_ATTRIBUTE, productTitle.getText().toString());

                EditText productAmount = (EditText) findViewById(R.id.productAmountEdit);
                Spinner amountSpinner = (Spinner) findViewById(R.id.amountSpinner);
                newProductData.putString(Constants.PRODUCT_AMOUNT_ATTRIBUTE, productAmount.getText().toString()
                        + amountSpinner.getSelectedItem().toString());


                // Set up by-weight products
                if (product.isSoldByWeight())
                {
                    newProductData.putBoolean(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE, true);

                    Spinner productByWeightUnit = (Spinner) findViewById(R.id.priceWeightUnitSpinner);
                    newProductData.putString(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE,
                            productByWeightUnit.getSelectedItem().toString());
                }

                // Only include the checkboxes if they have been checked
                CheckBox isOrganic = (CheckBox)findViewById(R.id.isOrganicCheckbox);
                if (isOrganic.isChecked())
                {
                    newProductData.putBoolean(Constants.PRODUCT_ORGANIC_ATTRIBUTE, true);
                }

                CheckBox isFairtrade = (CheckBox)findViewById(R.id.isFairtradeCheckbox);
                if (isFairtrade.isChecked())
                {
                    newProductData.putBoolean(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE, true);
                }

                // Create a new product to send to the server
                OutpanProduct newProduct = new OutpanProduct(product.ean, newProductData);

                //create the task and send it to the server, close the modify activity when done
                ModifyProductTask modifyProduct = new ModifyProductTask(this, newProduct);
                modifyProduct.execute();
            }
        }
        else
        {
            // We didn't change anything, so we canceled the edit
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (haveFieldsChanged())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getResources().getString(R.string.dialog_confirm));
            builder.setMessage(getResources().getString(R.string.dialog_productDataChanged));

            builder.setPositiveButton(getResources().getString(R.string.dialog_Yes), new DialogInterface.OnClickListener()
            {

                public void onClick(DialogInterface dialog, int which)
                {
                    // Do nothing but close the dialog
                    dialog.dismiss();
                    setResult(RESULT_CANCELED);
                    ModifyProductActivity.this.finish();
                }

            });

            builder.setNegativeButton(getResources().getString(R.string.dialog_No), new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            // We didn't change anything, so we canceled the edit
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constants.PRODUCT_PARCEL, product);

            setResult(RESULT_CANCELED, resultIntent);
            finish();

            super.onBackPressed();
        }
    }

    private void setVisibleProduct(OutpanProduct InProduct)
    {
        // Replace the member product data
        this.product = InProduct;

        // Load the data again
        // Brand
        if (product.attributes.containsKey(Constants.PRODUCT_BRAND_ATTRIBUTE))
        {
            EditText brandNameEdit = (EditText) findViewById(R.id.brandEdit);
            brandNameEdit.setText(product.attributes.get(Constants.PRODUCT_BRAND_ATTRIBUTE));
        }

        // Name
        if (product.attributes.containsKey(Constants.PRODUCT_TITLE_ATTRIBUTE))
        {

            EditText productTitleEdit = (EditText) findViewById(R.id.productNameEdit);
            productTitleEdit.setText(product.attributes.get(Constants.PRODUCT_TITLE_ATTRIBUTE));
        }

        // Amount
        if (product.attributes.containsKey(Constants.PRODUCT_AMOUNT_ATTRIBUTE))
        {
            EditText productAmountEdit = (EditText) findViewById(R.id.productAmountEdit);
            Spinner productAmountSpinner = (Spinner) findViewById(R.id.amountSpinner);
            HashMap<String, String> amountValues = splitAmountValue(product.attributes.get(Constants.PRODUCT_AMOUNT_ATTRIBUTE));

            productAmountEdit.setText(amountValues.get(Constants.SPLITMAP_NUMBER));
            productAmountSpinner.setSelection(getUnitIndex(amountValues.get(Constants.SPLITMAP_UNIT)));
        }

        // By weight amount, if applicable
        if (product.isSoldByWeight() && product.attributes.containsKey(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE))
        {
            Spinner productWeightUnitSpinner = (Spinner) findViewById(R.id.priceWeightUnitSpinner);
            String weightUnit = product.attributes.get(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE);

            productWeightUnitSpinner.setSelection(getUnitIndex(weightUnit));
        }

        // Is it organic?
        if (product.attributes.containsKey(Constants.PRODUCT_ORGANIC_ATTRIBUTE))
        {
            CheckBox isOrganicCheckBox = (CheckBox)findViewById(R.id.isOrganicCheckbox);
            boolean isOrganic = Boolean.valueOf(product.attributes.get(Constants.PRODUCT_ORGANIC_ATTRIBUTE));
            isOrganicCheckBox.setChecked(isOrganic);
        }

        // Is it fairtrade?
        if (product.attributes.containsKey(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE))
        {
            CheckBox isFairtradeCheckBox = (CheckBox)findViewById(R.id.isFairtradeCheckbox);
            boolean isFairtrade = Boolean.valueOf(product.attributes.get(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE));
            isFairtradeCheckBox.setChecked(isFairtrade);
        }
    }

    public HashMap<String, String> splitAmountValue(String inString)
    {
        HashMap<String, String> list = new HashMap<>();

        String tempNumbers  = "";
        String tempChars = "";

        if (inString != null)
        {
            for (int i = 0; i < inString.length(); ++i)
            {
                char c = inString.charAt(i);
                if (Character.isDigit(c))
                {
                    tempNumbers += c;
                }
                else
                {
                    tempChars += c;
                }
            }
        }

        list.put(Constants.SPLITMAP_UNIT, tempChars);
        list.put(Constants.SPLITMAP_NUMBER, tempNumbers);

        return list;
    }

    private boolean areRequiredFieldsFilledOut()
    {
        boolean bHasIncompleteFields = false;

        EditText productBrand = (EditText) findViewById(R.id.brandEdit);
        EditText productTitle = (EditText) findViewById(R.id.productNameEdit);
        EditText productAmount = (EditText) findViewById(R.id.productAmountEdit);

        if (productBrand.getText().toString().isEmpty())
        {
            productBrand.setError(getResources().getString(R.string.prompt_fillOutField));
            bHasIncompleteFields = true;
        }

        if (productTitle.getText().toString().isEmpty())
        {
            productTitle.setError(getResources().getString(R.string.prompt_fillOutField));
            bHasIncompleteFields = true;
        }

        boolean isProductAmountNotFilledOut = productAmount.getText().toString().isEmpty();

        if (isProductAmountNotFilledOut)
        {
            productAmount.setError(getResources().getString(R.string.prompt_fillOutField));
            bHasIncompleteFields = true;
        }

        return !bHasIncompleteFields;
    }

    private boolean haveFieldsChanged()
    {
        boolean fieldsHaveChanged = false;

        EditText productBrandEdit = (EditText) findViewById(R.id.brandEdit);
        EditText productTitleEdit = (EditText) findViewById(R.id.productNameEdit);
        EditText productAmountEdit = (EditText) findViewById(R.id.productAmountEdit);
        Spinner productAmountSpinner = (Spinner) findViewById(R.id.amountSpinner);

        CheckBox isOrganicCheckBox = (CheckBox) findViewById(R.id.isOrganicCheckbox);
        CheckBox isFairtradeCheckBox = (CheckBox) findViewById(R.id.isFairtradeCheckbox);

        if (!productBrandEdit.getText().toString().equals(product.attributes.get(Constants.PRODUCT_BRAND_ATTRIBUTE)))
        {
            fieldsHaveChanged = true;
        }

        if (!productTitleEdit.getText().toString().equals(product.attributes.get(Constants.PRODUCT_TITLE_ATTRIBUTE)))
        {
            fieldsHaveChanged = true;
        }

        String productAmount = productAmountEdit.getText().toString() + productAmountSpinner.getSelectedItem().toString();
        if (!productAmount.equals(product.attributes.get(Constants.PRODUCT_AMOUNT_ATTRIBUTE)))
        {
            fieldsHaveChanged = true;
        }

        HashMap<String, String> amountMap = splitAmountValue(product.attributes.get(Constants.PRODUCT_AMOUNT_ATTRIBUTE));
        if (amountMap.size() > 0)
        {
            int indexOfAmountUnit = getUnitIndex(amountMap.get(Constants.SPLITMAP_UNIT));
            if ((productAmountSpinner.getSelectedItemPosition() != indexOfAmountUnit))
            {
                fieldsHaveChanged = true;
            }
        }

        boolean isProductOrganic = Boolean.valueOf(product.attributes.get(Constants.PRODUCT_ORGANIC_ATTRIBUTE));
        if (isOrganicCheckBox.isChecked() != isProductOrganic)
        {
            fieldsHaveChanged = true;
        }

        boolean isProductFairtrade = Boolean.valueOf(product.attributes.get(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE));
        if (isFairtradeCheckBox.isChecked() != isProductFairtrade)
        {
            fieldsHaveChanged = true;
        }

        return fieldsHaveChanged;
    }

    private int getUnitIndex(String InUnit)
    {
        if (!InUnit.isEmpty())
        {
            String[] unitTypes = getResources().getStringArray(R.array.ui_amount_types);

            int i = 0;
            for (String unitType : unitTypes)
            {
                if (unitType.equals(InUnit))
                {
                    return i;
                }
                ++i;
            }
        }

        return 0;
    }

    public void addStoredBrand(String brand)
    {
        ArrayList<String> storedBrands = BrandNameAdapter.getStoredBrands(getApplicationContext());
        if (!storedBrands.contains(brand) && !brand.isEmpty())
        {
            storedBrands.add(brand);
            JSONArray brandArray = new JSONArray(storedBrands);

            SharedPreferences preferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(Constants.PREF_BRANDARRAY, brandArray.toString());
            editor.apply();
        }
    }
}
