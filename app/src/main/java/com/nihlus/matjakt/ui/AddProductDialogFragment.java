package com.nihlus.matjakt.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.nihlus.matjakt.R;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.outpan.OutpanProduct;


/**
 * Dialog asking the user if they would like to add a new item to the database.
 */
public class AddProductDialogFragment extends DialogFragment
{
    private Activity parentActivity;
    private EAN ean;

    @SuppressWarnings("ValidFragment")
    public AddProductDialogFragment(Activity InParentActivity, EAN InEAN)
    {
        this.parentActivity = InParentActivity;
        this.ean = InEAN;
    }

    public AddProductDialogFragment()
    {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_NoProductFound);
        builder.setMessage(R.string.dialog_newProductNameText);

        builder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {

                Intent intent = new Intent(parentActivity, ModifyProductActivity.class);
                OutpanProduct newProduct = new OutpanProduct(ean, new Bundle());

                intent.putExtra(Constants.MODIFY_INTENT_TYPE, Constants.INSERT_NEW_PRODUCT);
                intent.putExtra(Constants.PRODUCT_PARCEL, newProduct);

                parentActivity.startActivityForResult(intent, Constants.INSERT_NEW_PRODUCT);

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

        Dialog finalDialog = builder.create();
        finalDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return finalDialog;
    }
}
