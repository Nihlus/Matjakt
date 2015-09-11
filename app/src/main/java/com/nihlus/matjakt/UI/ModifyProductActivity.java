package com.nihlus.matjakt.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.R;

import java.util.HashMap;

import io.github.johncipponeri.outpanapi.OutpanAPI;

public class ModifyProductActivity extends AppCompatActivity
{
    private String ean = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_product);

        Intent intent = getIntent();

        int incomingIntent = intent.getIntExtra(Constants.GENERIC_INTENT_ID, -1);
        this.ean = intent.getStringExtra(Constants.PRODUCT_EAN_EXTRA);

        boolean bIsNewProduct = incomingIntent == Constants.INSERT_NEW_PRODUCT;
        boolean bIsModifyingProduct = incomingIntent == Constants.MODIFY_EXISTING_PRODUCT;
        if (bIsNewProduct)
        {
            setTitle(getResources().getString(R.string.title_activity_new_product));

            EditText fluidVolumeEdit = (EditText)findViewById(R.id.productFluidVolumeEdit);
            Spinner fluidTypeSpinner = (Spinner)findViewById(R.id.fluidVolumeSpinner);

            if (fluidVolumeEdit != null && fluidTypeSpinner != null)
            {
                if (!isFluidChecked())
                {
                    fluidVolumeEdit.setEnabled(false);
                    fluidTypeSpinner.setEnabled(false);
                }
            }
        }
        else if (bIsModifyingProduct)
        {
            setTitle(getResources().getString(R.string.title_activity_edit_product));

            Bundle productData = intent.getBundleExtra(Constants.PRODUCT_BUNDLE_EXTRA);

            EditText brandName = (EditText) findViewById(R.id.brandEdit);
            EditText productTitle = (EditText) findViewById(R.id.productNameEdit);
            EditText productNetWeight = (EditText) findViewById(R.id.productNetWeightEdit);
            EditText productGrossWeight = (EditText) findViewById(R.id.productGrossWeightEdit);
            EditText productFluidVolume = (EditText)findViewById(R.id.productFluidVolumeEdit);

            Spinner netWeightTypeSpinner = (Spinner) findViewById(R.id.netWeightSpinner);
            Spinner grossWeightTypeSpinner = (Spinner) findViewById(R.id.grossWeightSpinner);
            Spinner fluidVolumeTypeSpinner = (Spinner) findViewById(R.id.fluidVolumeSpinner);

            CheckBox isOrganic = (CheckBox)findViewById(R.id.isOrganicCheckbox);
            CheckBox isFairtrade = (CheckBox)findViewById(R.id.isFairtradeCheckbox);

            //load input data from the intent

            //brand
            brandName.setText(productData.getString(Constants.PRODUCT_BRAND_ATTRIBUTE));
            //name
            productTitle.setText(productData.getString(Constants.PRODUCT_TITLE_ATTRIBUTE));
            //weight or volume
            if (productData.containsKey(Constants.PRODUCT_NET_WEIGHT_ATTRIBUTE))
            {
                HashMap<String, String> splitValue = splitAmountValue(productData.getString(Constants.PRODUCT_NET_WEIGHT_ATTRIBUTE));
                productNetWeight.setText(splitValue.get(Constants.SPLITMAP_NUMBER));
                netWeightTypeSpinner.setSelection(((ArrayAdapter)netWeightTypeSpinner.getAdapter()).getPosition(splitValue.get(Constants.SPLITMAP_LETTER)));
            }

            if (productData.containsKey(Constants.PRODUCT_GROSS_WEIGHT_ATTRIBUTE))
            {
                HashMap<String, String> splitValue = splitAmountValue(productData.getString(Constants.PRODUCT_GROSS_WEIGHT_ATTRIBUTE));
                productGrossWeight.setText(splitValue.get(Constants.SPLITMAP_NUMBER));
                grossWeightTypeSpinner.setSelection(((ArrayAdapter)grossWeightTypeSpinner.getAdapter()).getPosition(splitValue.get(Constants.SPLITMAP_LETTER)));
            }

            if (productData.containsKey(Constants.PRODUCT_FLUID_ATTRIBUTE))
            {
                HashMap<String, String> splitValue = splitAmountValue(productData.getString(Constants.PRODUCT_VOLUME_ATTRIBUTE));
                productFluidVolume.setText(splitValue.get(Constants.SPLITMAP_NUMBER));
                fluidVolumeTypeSpinner.setSelection(((ArrayAdapter)fluidVolumeTypeSpinner.getAdapter()).getPosition(splitValue.get(Constants.SPLITMAP_LETTER)));
            }

            //organic
            isOrganic.setChecked(productData.getBoolean(Constants.PRODUCT_ORGANIC_ATTRIBUTE, false));
            //fairtrade
            isFairtrade.setChecked(productData.getBoolean(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE, false));
        }
        else
        {
            setTitle(getResources().getString(R.string.debug_howDidYouGetHere));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent parentActivity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickOKButton(View view)
    {
        //verify data
        if (verifyRequiredFields())
        {
            //Toast.makeText(this, getFinalProductString(), Toast.LENGTH_LONG).show();

            //if OK, send to server
            EditText brandName = (EditText) findViewById(R.id.brandEdit);
            EditText productTitle = (EditText) findViewById(R.id.productNameEdit);
            EditText productNetWeight = (EditText) findViewById(R.id.productNetWeightEdit);
            EditText productGrossWeight = (EditText) findViewById(R.id.productGrossWeightEdit);
            EditText productFluidVolume = (EditText)findViewById(R.id.productFluidVolumeEdit);

            Spinner netWeightTypeSpinner = (Spinner) findViewById(R.id.netWeightSpinner);
            Spinner grossWeightTypeSpinner = (Spinner) findViewById(R.id.grossWeightSpinner);
            Spinner fluidVolumeTypeSpinner = (Spinner) findViewById(R.id.fluidVolumeSpinner);

            CheckBox isOrganic = (CheckBox)findViewById(R.id.isOrganicCheckbox);
            CheckBox isFairtrade = (CheckBox)findViewById(R.id.isFairtradeCheckbox);

            //bundle up the data needed by the server
            Bundle productData = new Bundle();
            productData.putString(Constants.PRODUCT_BRAND_ATTRIBUTE, brandName.getText().toString());
            productData.putString(Constants.PRODUCT_TITLE_ATTRIBUTE, productTitle.getText().toString());

            productData.putString(Constants.PRODUCT_NET_WEIGHT_ATTRIBUTE, productNetWeight.getText().toString()
                    + netWeightTypeSpinner.getSelectedItem().toString());

            productData.putString(Constants.PRODUCT_GROSS_WEIGHT_ATTRIBUTE, productGrossWeight.getText().toString()
                    + grossWeightTypeSpinner.getSelectedItem().toString());

            if (isFluidChecked())
            {
                productData.putBoolean(Constants.PRODUCT_FLUID_ATTRIBUTE, isFluidChecked());
                productData.putString(Constants.PRODUCT_VOLUME_ATTRIBUTE, productFluidVolume.getText().toString()
                        + fluidVolumeTypeSpinner.getSelectedItem().toString());
            }

            productData.putBoolean(Constants.PRODUCT_ORGANIC_ATTRIBUTE, isOrganic.isChecked());
            productData.putBoolean(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE, isFairtrade.isChecked());

            //create the task and send it to the server, close parentActivity when done
            UpdateProductTitle update = new UpdateProductTitle(this, ean, productData);
            update.execute(getFinalProductString());
        }
    }

    public void onIsFluidToggle(View view)
    {
        EditText productFluidVolume = (EditText)findViewById(R.id.productFluidVolumeEdit);
        Spinner fluidTypeSpinner = (Spinner)findViewById(R.id.fluidVolumeSpinner);

        if (productFluidVolume != null && fluidTypeSpinner != null)
        {
            productFluidVolume.setEnabled(isFluidChecked());
            fluidTypeSpinner.setEnabled(isFluidChecked());
        }

        //clear any errors previously set
        EditText productNetWeight = (EditText) findViewById(R.id.productNetWeightEdit);
        EditText productGrossWeight = (EditText) findViewById(R.id.productGrossWeightEdit);

        if (productNetWeight != null && productGrossWeight != null && productFluidVolume != null)
        {
            productNetWeight.setError(null);
            productGrossWeight.setError(null);
            productFluidVolume.setError(null);
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
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    private HashMap<String, String> splitAmountValue(String inString)
    {
        HashMap<String, String> list = new HashMap<>();

        String tempNums  = "";
        String tempChars = "";

        for (int i = 0; i < inString.length(); ++i)
        {
            char c = inString.charAt(i);
            if (Character.isDigit(c))
            {
                tempNums += c;
            }
            else
            {
                tempChars += c;
            }
        }

        list.put(Constants.SPLITMAP_LETTER, tempChars);
        list.put(Constants.SPLITMAP_NUMBER, tempNums);

        return list;
    }

    private boolean isFluidChecked()
    {

        CheckBox isFluid = (CheckBox)findViewById(R.id.isFluidCheckbox);
        if (isFluid != null)
        {
            return isFluid.isChecked();
        }
        return false;
    }

    private boolean verifyRequiredFields()
    {
        boolean bHasIncompleteFields = false;

        EditText productBrand = (EditText) findViewById(R.id.brandEdit);
        EditText productTitle = (EditText) findViewById(R.id.productNameEdit);
        EditText productNetWeight = (EditText) findViewById(R.id.productNetWeightEdit);
        EditText productGrossWeight = (EditText) findViewById(R.id.productGrossWeightEdit);
        EditText productFluidVolume = (EditText) findViewById(R.id.productFluidVolumeEdit);

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

        if (isFluidChecked())
        {
            //require the fluid field, other two not neccesary
            if (productFluidVolume.getText().toString().isEmpty())
            {
                productFluidVolume.setError(getResources().getString(R.string.prompt_fillOutField));
                bHasIncompleteFields = true;
            }
        }
        else
        {
            boolean isNetWeightNotFilledOut = productNetWeight.getText().toString().isEmpty();
            boolean isGrossWeightNotFilledOut = productNetWeight.getText().toString().isEmpty();

            if (isNetWeightNotFilledOut && isGrossWeightNotFilledOut)
            {
                productNetWeight.setError(getResources().getString(R.string.prompt_fillOutAtLeastOneField));
                productGrossWeight.setError(getResources().getString(R.string.prompt_fillOutAtLeastOneField));
                bHasIncompleteFields = true;
            }
        }

        return !bHasIncompleteFields;
    }

    private boolean haveFieldsChanged()
    {
        boolean bFieldsHaveChanged = false;

        EditText manufacturerName = (EditText) findViewById(R.id.brandEdit);
        EditText productTitle = (EditText) findViewById(R.id.productNameEdit);
        EditText productWeight = (EditText) findViewById(R.id.productNetWeightEdit);
        Spinner weightTypeSpinner = (Spinner) findViewById(R.id.netWeightSpinner);

        if (!manufacturerName.getText().toString().isEmpty())
        {
            bFieldsHaveChanged = true;
        }

        if (!productTitle.getText().toString().isEmpty())
        {
            bFieldsHaveChanged = true;
        }

        if (!productWeight.getText().toString().isEmpty())
        {
            bFieldsHaveChanged = true;
        }

        if ((weightTypeSpinner.getSelectedItemPosition() != 0))
        {
            bFieldsHaveChanged = true;
        }

        return bFieldsHaveChanged;
    }

    private String getFinalProductString()
    {
        EditText manufacturerName = (EditText) findViewById(R.id.brandEdit);
        EditText productTitle = (EditText) findViewById(R.id.productNameEdit);

        if (isFluidChecked())
        {
            //use the fluid volume for the final title
            EditText fluidVolume = (EditText) findViewById(R.id.productFluidVolumeEdit);
            Spinner fluidVolumeType = (Spinner) findViewById(R.id.fluidVolumeSpinner);

            return manufacturerName.getText().toString() + " " +
                    productTitle.getText().toString() + " " +
                    fluidVolume.getText().toString() +
                    fluidVolumeType.getSelectedItem().toString();
        }
        else
        {
            //use the gross weight for the final title
            EditText productGrossWeight = (EditText) findViewById(R.id.productGrossWeightEdit);
            Spinner grossWeightTypeSpinner = (Spinner) findViewById(R.id.grossWeightSpinner);

            return manufacturerName.getText().toString() + " " +
                    productTitle.getText().toString() + " " +
                    productGrossWeight.getText().toString() +
                    grossWeightTypeSpinner.getSelectedItem().toString();
        }
    }

    //async posting of the update to the database
    class UpdateProductTitle extends AsyncTask<String, Void, String>
    {
        private final Activity parentActivity;
        private final String ean;
        private final Bundle data;

        private ProgressDialog dialog;

        UpdateProductTitle(Activity parentActivity, String inEan, Bundle inData)
        {
            this.parentActivity = parentActivity;
            this.ean = inEan;
            this.data = inData;

            dialog = new ProgressDialog(parentActivity);
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            this.dialog.setMessage(getResources().getString(R.string.dialog_updatingProductInfo));
            this.dialog.show();
        }

        protected String doInBackground(String... inputs)
        {
            OutpanAPI api = new OutpanAPI(Constants.OutpanAPIKey);
            api.setProductName(ean, inputs[0]);

            api.setProductAttribute(ean, Constants.PRODUCT_BRAND_ATTRIBUTE, data.getString(Constants.PRODUCT_BRAND_ATTRIBUTE));
            api.setProductAttribute(ean, Constants.PRODUCT_TITLE_ATTRIBUTE, data.getString(Constants.PRODUCT_TITLE_ATTRIBUTE));

            if (data.containsKey(Constants.PRODUCT_FLUID_ATTRIBUTE))
            {
                api.setProductAttribute(ean, Constants.PRODUCT_VOLUME_ATTRIBUTE, data.getString(Constants.PRODUCT_VOLUME_ATTRIBUTE));
                api.setProductAttribute(ean, Constants.PRODUCT_FLUID_ATTRIBUTE, String.valueOf(data.getBoolean(Constants.PRODUCT_FLUID_ATTRIBUTE)));
            }

            if (data.containsKey(Constants.PRODUCT_NET_WEIGHT_ATTRIBUTE))
            {
                api.setProductAttribute(ean, Constants.PRODUCT_NET_WEIGHT_ATTRIBUTE, data.getString(Constants.PRODUCT_NET_WEIGHT_ATTRIBUTE));
            }

            if (data.containsKey(Constants.PRODUCT_GROSS_WEIGHT_ATTRIBUTE))
            {
                api.setProductAttribute(ean, Constants.PRODUCT_GROSS_WEIGHT_ATTRIBUTE, data.getString(Constants.PRODUCT_GROSS_WEIGHT_ATTRIBUTE));
            }

            api.setProductAttribute(ean, Constants.PRODUCT_ORGANIC_ATTRIBUTE, String.valueOf(data.getBoolean(Constants.PRODUCT_ORGANIC_ATTRIBUTE)));
            api.setProductAttribute(ean, Constants.PRODUCT_FAIRTRADE_ATTRIBUTE, String.valueOf(data.getBoolean(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE)));

            return api.getProductName(ean).name;
        }

        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if (this.dialog.isShowing() && !result.isEmpty())
            {
                this.dialog.dismiss();

                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.PRODUCT_TITLE_EXTRA, result);
                resultIntent.putExtra(Constants.PRODUCT_EAN_EXTRA, ModifyProductActivity.this.ean);

                parentActivity.setResult(RESULT_OK, resultIntent);
                parentActivity.finish();
            }
            else
            {
                parentActivity.setResult(RESULT_CANCELED);
                this.dialog.dismiss();

                Toast.makeText(parentActivity, getResources().getString(R.string.prompt_productUpdateFailed), Toast.LENGTH_LONG).show();
            }
        }

    }
}
