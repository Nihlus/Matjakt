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
public class ModifyProductDialogFragment extends DialogFragment
{
    private final Activity parentActivity;
    private final OutpanProduct TemplateProduct;
    private final boolean isRepairing;

    @SuppressWarnings("ValidFragment")
    public ModifyProductDialogFragment(Activity InParentActivity, OutpanProduct InTemplateProduct, boolean InIsRepairing)
    {
        this.parentActivity = InParentActivity;
        this.TemplateProduct = InTemplateProduct;
        this.isRepairing = InIsRepairing;
    }

    public ModifyProductDialogFragment()
    {
        // Required empty public constructor
        this.parentActivity = null;
        this.TemplateProduct = null;
        this.isRepairing = false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (isRepairing)
        {
            builder.setTitle(R.string.dialog_BrokenProduct);
            builder.setMessage(R.string.dialog_brokenProductBody);

            builder.setPositiveButton(R.string.dialog_Yes, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int which)
                {

                    Intent intent = new Intent(parentActivity, ModifyProductActivity.class);
                    intent.putExtra(Constants.MODIFY_INTENT_TYPE, Constants.MODIFY_EXISTING_PRODUCT);
                    intent.putExtra(Constants.PRODUCT_PARCEL, TemplateProduct);

                    parentActivity.startActivityForResult(intent, Constants.MODIFY_EXISTING_PRODUCT);

                }
            });
        }
        else
        {
            builder.setTitle(R.string.dialog_NoProductFound);
            builder.setMessage(R.string.dialog_newProductNameText);

            builder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int which)
                {

                    Intent intent = new Intent(parentActivity, ModifyProductActivity.class);
                    intent.putExtra(Constants.MODIFY_INTENT_TYPE, Constants.INSERT_NEW_PRODUCT);
                    intent.putExtra(Constants.PRODUCT_PARCEL, TemplateProduct);

                    parentActivity.startActivityForResult(intent, Constants.INSERT_NEW_PRODUCT);

                }
            });
        }

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
