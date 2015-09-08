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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.R;

import io.github.johncipponeri.outpanapi.OutpanAPI;

public class EditProductActivity extends AppCompatActivity
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
        }
        else if (bIsModifyingProduct)
        {
            setTitle(getResources().getString(R.string.title_activity_edit_product));
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
        // as you specify a parent activity in AndroidManifest.xml.
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
            //display wait spinner
            //return to scan screen when done

            UpdateProductTitle update = new UpdateProductTitle(this, ean);
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
                    EditProductActivity.this.finish();
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

    private boolean verifyRequiredFields()
    {
        boolean bHasIncompleteFields = false;

        EditText manufacturerName = (EditText) findViewById(R.id.manufacturerEdit);
        EditText productTitle = (EditText) findViewById(R.id.productNameEdit);
        EditText productWeight = (EditText) findViewById(R.id.productWeightEdit);

        if (manufacturerName.getText().toString().isEmpty())
        {
            manufacturerName.setError(getResources().getString(R.string.prompt_fillOutField));
            bHasIncompleteFields = true;
        }

        if (productTitle.getText().toString().isEmpty())
        {
            productTitle.setError(getResources().getString(R.string.prompt_fillOutField));
            bHasIncompleteFields = true;
        }

        if (productWeight.getText().toString().isEmpty())
        {
            productWeight.setError(getResources().getString(R.string.prompt_fillOutField));
            bHasIncompleteFields = true;
        }

        if (bHasIncompleteFields)
        {
            return false;
        }
        return true;
    }

    private boolean haveFieldsChanged()
    {
        boolean bFieldsHaveChanged = false;

        EditText manufacturerName = (EditText) findViewById(R.id.manufacturerEdit);
        EditText productTitle = (EditText) findViewById(R.id.productNameEdit);
        EditText productWeight = (EditText) findViewById(R.id.productWeightEdit);
        Spinner weightTypeSpinner = (Spinner) findViewById(R.id.weightSpinner);

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

        if (bFieldsHaveChanged)
        {
            return true;
        }
        return false;
    }

    private String getFinalProductString()
    {
        EditText manufacturerName = (EditText) findViewById(R.id.manufacturerEdit);
        EditText productTitle = (EditText) findViewById(R.id.productNameEdit);
        EditText productWeight = (EditText) findViewById(R.id.productWeightEdit);
        Spinner weightTypeSpinner = (Spinner) findViewById(R.id.weightSpinner);

        String finalProductString = manufacturerName.getText().toString() + " " +
                productTitle.getText().toString() + " " +
                productWeight.getText().toString() +
                weightTypeSpinner.getSelectedItem().toString();

        return finalProductString;
    }

    //async posting of the update to the database
    class UpdateProductTitle extends AsyncTask<String, Void, String>
    {
        private Activity activity;
        private String inEan = "";

        private ProgressDialog dialog;

        UpdateProductTitle(Activity activity, String ean)
        {
            this.activity = activity;
            this.inEan = ean;

            dialog = new ProgressDialog(activity);
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
            api.setProductName(inEan, inputs[0]);

            return api.getProductName(inEan).name;
        }

        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if (this.dialog.isShowing() && !result.isEmpty())
            {
                this.dialog.dismiss();

                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.PRODUCT_TITLE_EXTRA, result);
                resultIntent.putExtra(Constants.PRODUCT_EAN_EXTRA, ean);

                setResult(RESULT_OK, resultIntent);
                activity.finish();
            }
            else
            {
                setResult(RESULT_CANCELED);
                this.dialog.dismiss();

                Toast.makeText(activity, getResources().getString(R.string.prompt_productUpdateFailed), Toast.LENGTH_LONG).show();
            }
        }

    }
}
