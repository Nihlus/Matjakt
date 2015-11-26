package com.nihlus.matjakt.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.nihlus.matjakt.R;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;


/**
 * Dialog asking the user if they would like to add a new item to the database.
 */
public class AddProductDialogFragment extends DialogFragment
{
    private EAN ean;
    private Activity ParentActivity;

    @SuppressWarnings("ValidFragment")
    public AddProductDialogFragment(Activity InParentActivity, EAN InEAN)
    {
        this.ean = InEAN;
        this.ParentActivity = InParentActivity;
    }

    public AddProductDialogFragment()
    {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        //final EditText input = new EditText(getActivity());
        //input.setInputType(InputType.TYPE_CLASS_TEXT);
        //builder.setView(input);

        builder.setTitle(R.string.dialog_NoProductFound);
        builder.setMessage(R.string.dialog_newProductNameText);

        builder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener()
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
                intent.putExtra(Constants.GENERIC_INTENT_ID, Constants.INSERT_NEW_PRODUCT);
                intent.putExtra(Constants.PRODUCT_EAN, ean);

                ParentActivity.startActivityForResult(intent, Constants.INSERT_NEW_PRODUCT);

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
