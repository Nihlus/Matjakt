package com.nihlus.matjakt.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.R;


/**
 * Dialog asking the user if they would like to add a new item to the database.
 */
public class RepairProductDialogFragment extends DialogFragment
{
    private final String ean;
    private final Activity parentActivity;
    private final Bundle productData;

    @SuppressWarnings("ValidFragment")
    public RepairProductDialogFragment(Activity activity, String inEan, Bundle inProductData)
    {
        this.ean = inEan;
        this.parentActivity = activity;
        this.productData = inProductData;
    }

    public RepairProductDialogFragment()
    {
        // Required empty public constructor
        this.ean = null;
        this.parentActivity = null;
        this.productData = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        //final EditText input = new EditText(getActivity());
        //input.setInputType(InputType.TYPE_CLASS_TEXT);
        //builder.setView(input);

        builder.setTitle(R.string.dialog_BrokenProduct);
        builder.setMessage(R.string.dialog_brokenProductBody);

        builder.setPositiveButton(R.string.dialog_Yes, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                //userInput = input.getText().toString();

                //if (!userInput.isEmpty())
                //{
                //   new UpdateProductTitle().execute(userInput);
                //}

                Intent intent = new Intent(parentActivity, ModifyProductActivity.class);
                intent.putExtra(Constants.GENERIC_INTENT_ID, Constants.MODIFY_EXISTING_PRODUCT);
                intent.putExtra(Constants.PRODUCT_EAN_EXTRA, ean);
                intent.putExtra(Constants.PRODUCT_BUNDLE_EXTRA, productData);

                parentActivity.startActivityForResult(intent, Constants.MODIFY_EXISTING_PRODUCT);

            }
        });

        builder.setNegativeButton(R.string.dialog_CancelText, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        return builder.create();
    }
}
