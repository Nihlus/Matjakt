package com.nihlus.matjakt.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.outpan.OutpanProduct;


/**
 * Dialog asking the user if they would like to add a new item to the database.
 */
public class RepairProductDialogFragment extends DialogFragment
{
    private final Activity parentActivity;
    private final OutpanProduct productData;

    @SuppressWarnings("ValidFragment")
    public RepairProductDialogFragment(Activity InParentActivity, OutpanProduct InProduct)
    {
        this.parentActivity = InParentActivity;
        this.productData = InProduct;
    }

    public RepairProductDialogFragment()
    {
        // Required empty public constructor
        this.parentActivity = null;
        this.productData = null;
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

                Intent intent = new Intent(parentActivity, ModifyProductActivity.class);
                intent.putExtra(Constants.MODIFY_INTENT_TYPE, Constants.MODIFY_EXISTING_PRODUCT);
                intent.putExtra(Constants.PRODUCT_PARCEL, productData);

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

        Dialog finalDialog = builder.create();
        finalDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return finalDialog;
    }
}
