package com.nihlus.matjakt.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.database.containers.EAN;


/**
 * Dialog asking the user if they would like to add a new item to the database.
 */
public class RepairProductDialogFragment extends DialogFragment
{
    private final EAN ean;
    private final Activity ParentActivity;
    private final Bundle ProductData;

    @SuppressWarnings("ValidFragment")
    public RepairProductDialogFragment(Activity InParentActivity, EAN InEAN, Bundle InProductData)
    {
        this.ean = InEAN;
        this.ParentActivity = InParentActivity;
        this.ProductData = InProductData;
    }

    public RepairProductDialogFragment()
    {
        // Required empty public constructor
        this.ean = null;
        this.ParentActivity = null;
        this.ProductData = null;
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
                //   new UpdateOutpanProduct().execute(userInput);
                //}

                Intent intent = new Intent(ParentActivity, ModifyProductActivity.class);
                intent.putExtra(Constants.GENERIC_INTENT_ID, Constants.MODIFY_EXISTING_PRODUCT);
                intent.putExtra(Constants.PRODUCT_EAN, ean);
                intent.putExtra(Constants.PRODUCT_BUNDLE_EXTRA, ProductData);

                ParentActivity.startActivityForResult(intent, Constants.MODIFY_EXISTING_PRODUCT);

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
