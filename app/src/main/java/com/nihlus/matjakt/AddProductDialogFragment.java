package com.nihlus.matjakt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.UI.ModifyProductActivity;


/**
 * Dialog asking the user if they would like to add a new item to the database.
 */
public class AddProductDialogFragment extends DialogFragment
{
    private String ean = "";
    private Activity activity;

    @SuppressWarnings("ValidFragment")
    public AddProductDialogFragment(Activity activity, String ean)
    {
        this.ean = ean;
        this.activity = activity;
    }

    public AddProductDialogFragment()
    {
        // Required empty public constructor
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
                //   new UpdateProductTitle().execute(userInput);
                //}

                Intent intent = new Intent(activity, ModifyProductActivity.class);
                intent.putExtra(Constants.GENERIC_INTENT_ID, Constants.INSERT_NEW_PRODUCT);
                intent.putExtra(Constants.PRODUCT_EAN_EXTRA, ean);

                activity.startActivityForResult(intent, Constants.INSERT_NEW_PRODUCT);

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
