package com.nihlus.matjakt.ui;

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

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.EAN;
import com.nihlus.matjakt.outpan.OutpanAPI2;
import com.nihlus.matjakt.outpan.OutpanProduct;
import com.nihlus.matjakt.R;

import java.util.HashMap;

public class ModifyProductActivity extends AppCompatActivity
{
    private String ean = "";
    private Bundle productData = new Bundle();

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
        }
        else if (bIsModifyingProduct)
        {
            setTitle(getResources().getString(R.string.title_activity_edit_product));

            this.productData = intent.getBundleExtra(Constants.PRODUCT_BUNDLE_EXTRA);

            EditText brandName = (EditText) findViewById(R.id.brandEdit);
            EditText productTitle = (EditText) findViewById(R.id.productNameEdit);
            EditText productAmount = (EditText) findViewById(R.id.productAmountEdit);

            Spinner productAmountSpinner = (Spinner) findViewById(R.id.amountSpinner);

            CheckBox isOrganic = (CheckBox)findViewById(R.id.isOrganicCheckbox);
            CheckBox isFairtrade = (CheckBox)findViewById(R.id.isFairtradeCheckbox);

            //load input data from the intent

            // Brand
            brandName.setText(productData.getString(Constants.PRODUCT_BRAND_ATTRIBUTE));

            // Name
            productTitle.setText(productData.getString(Constants.PRODUCT_TITLE_ATTRIBUTE));

            // Amount
            if (productData.containsKey(Constants.PRODUCT_AMOUNT_ATTRIBUTE))
            {
                HashMap<String, String> splitValue = splitAmountValue(productData.getString(Constants.PRODUCT_AMOUNT_ATTRIBUTE));
                productAmount.setText(splitValue.get(Constants.SPLITMAP_NUMBER));
                productAmountSpinner.setSelection(((ArrayAdapter<String>) productAmountSpinner.getAdapter()).getPosition(splitValue.get(Constants.SPLITMAP_LETTER)));
            }

            // Is it organic?
            isOrganic.setChecked(productData.getBoolean(Constants.PRODUCT_ORGANIC_ATTRIBUTE, false));
            // Is it fairtrade?
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Constants.REQUEST_BARCODE_SCAN && resultCode == RESULT_OK)
        {
            if (data.getIntExtra(Constants.GENERIC_INTENT_ID, -1) == Constants.REQUEST_BARCODE_SCAN)
            {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.GENERIC_INTENT_ID, Constants.REQUEST_BARCODE_SCAN);

                setResult(RESULT_OK, resultIntent);
                this.finish();
            }
        }
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
            EditText productAmount = (EditText) findViewById(R.id.productAmountEdit);

            Spinner amountSpinner = (Spinner) findViewById(R.id.amountSpinner);

            CheckBox isOrganic = (CheckBox)findViewById(R.id.isOrganicCheckbox);
            CheckBox isFairtrade = (CheckBox)findViewById(R.id.isFairtradeCheckbox);

            //bundle up the data needed by the server
            Bundle productData = new Bundle();
            productData.putString(Constants.PRODUCT_BRAND_ATTRIBUTE, brandName.getText().toString());
            productData.putString(Constants.PRODUCT_TITLE_ATTRIBUTE, productTitle.getText().toString());

            productData.putString(Constants.PRODUCT_AMOUNT_ATTRIBUTE, productAmount.getText().toString()
                    + amountSpinner.getSelectedItem().toString());


            // Only include the checkboxes if they have been checked
            if (isOrganic.isChecked())
            {
                productData.putBoolean(Constants.PRODUCT_ORGANIC_ATTRIBUTE, isOrganic.isChecked());
            }

            if (isFairtrade.isChecked())
            {
                productData.putBoolean(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE, isFairtrade.isChecked());
            }

            //create the task and send it to the server, close parentActivity when done
            UpdateProductTitle update = new UpdateProductTitle(this, ean, productData);
            update.execute(getFinalProductString());
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

        list.put(Constants.SPLITMAP_LETTER, tempChars);
        list.put(Constants.SPLITMAP_NUMBER, tempNumbers);

        return list;
    }

    private boolean verifyRequiredFields()
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

    // TODO: 9/11/15 Update to reflect new Bundle model (match values with Bundle instead of default values
    private boolean haveFieldsChanged()
    {
        boolean bFieldsHaveChanged = false;

        EditText productBrand = (EditText) findViewById(R.id.brandEdit);
        EditText productTitle = (EditText) findViewById(R.id.productNameEdit);
        EditText productAmount = (EditText) findViewById(R.id.productAmountEdit);
        Spinner productAmountSpinner = (Spinner) findViewById(R.id.amountSpinner);

        if (!productBrand.getText().toString().equals(productData.getString(Constants.PRODUCT_BRAND_ATTRIBUTE)))
        {
            bFieldsHaveChanged = true;
        }

        if (!productTitle.getText().toString().equals(productData.getString(Constants.PRODUCT_TITLE_ATTRIBUTE)))
        {
            bFieldsHaveChanged = true;
        }

        if (!productAmount.getText().toString().equals(productData.getString(Constants.PRODUCT_AMOUNT_ATTRIBUTE)))
        {
            bFieldsHaveChanged = true;
        }

        if ((productAmountSpinner.getSelectedItemPosition() != 0))
        {
            bFieldsHaveChanged = true;
        }

        return bFieldsHaveChanged;
    }

    private String getFinalProductString()
    {
        EditText manufacturerName = (EditText) findViewById(R.id.brandEdit);
        EditText productTitle = (EditText) findViewById(R.id.productNameEdit);

        EditText productAmount = (EditText) findViewById(R.id.productAmountEdit);
        Spinner productAmountSpinner = (Spinner) findViewById(R.id.amountSpinner);

        return manufacturerName.getText().toString() + " " +
                productTitle.getText().toString() + " " +
                productAmount.getText().toString() +
                productAmountSpinner.getSelectedItem().toString();
    }

    //async posting of the update to the database
    class UpdateProductTitle extends AsyncTask<String, Void, OutpanProduct>
    {
        private final Activity parentActivity;
        private final String ean;
        private final Bundle data;

        private final ProgressDialog dialog;

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

        protected OutpanProduct doInBackground(String... inputs)
        {
            OutpanAPI2 api = new OutpanAPI2(Constants.OutpanAPIKey);
            api.setProductName(ean, inputs[0]);

            api.setProductAttribute(ean, Constants.PRODUCT_BRAND_ATTRIBUTE, data.getString(Constants.PRODUCT_BRAND_ATTRIBUTE));
            api.setProductAttribute(ean, Constants.PRODUCT_TITLE_ATTRIBUTE, data.getString(Constants.PRODUCT_TITLE_ATTRIBUTE));

            if (data.containsKey(Constants.PRODUCT_AMOUNT_ATTRIBUTE))
            {
                boolean isEmpty = splitAmountValue(data.getString(Constants.PRODUCT_AMOUNT_ATTRIBUTE)).get(Constants.SPLITMAP_NUMBER).isEmpty();

                if (!isEmpty)
                {
                    api.setProductAttribute(ean, Constants.PRODUCT_AMOUNT_ATTRIBUTE, data.getString(Constants.PRODUCT_AMOUNT_ATTRIBUTE));
                }
            }

            if (data.containsKey(Constants.PRODUCT_ORGANIC_ATTRIBUTE))
            {
                api.setProductAttribute(ean, Constants.PRODUCT_ORGANIC_ATTRIBUTE, String.valueOf(data.getBoolean(Constants.PRODUCT_ORGANIC_ATTRIBUTE)));
            }

            if (data.containsKey(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE))
            {
                api.setProductAttribute(ean, Constants.PRODUCT_FAIRTRADE_ATTRIBUTE, String.valueOf(data.getBoolean(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE)));
            }

            return api.getProduct(new EAN(ean));
        }

        protected void onPostExecute(OutpanProduct result)
        {
            super.onPostExecute(result);
            if (this.dialog.isShowing() && result != null)
            {
                this.dialog.dismiss();

                Intent resultIntent = new Intent(parentActivity, ViewProductActivity.class);
                resultIntent.putExtra(Constants.PRODUCT_TITLE_EXTRA, result.Name);
                resultIntent.putExtra(Constants.PRODUCT_EAN_EXTRA, ModifyProductActivity.this.ean);
                resultIntent.putExtra(Constants.PRODUCT_BUNDLE_EXTRA, result.getBundle());

                parentActivity.setResult(RESULT_OK, resultIntent);
                parentActivity.startActivity(resultIntent);
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
