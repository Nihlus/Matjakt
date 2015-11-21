package com.nihlus.matjakt.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.nihlus.matjakt.Inserters.InsertStoreTask;
import com.nihlus.matjakt.R;

import java.util.jar.Attributes;


public class AddStoreDialogFragment extends DialogFragment
{

    private final Activity ParentActivity;
    private final DialogFragment ParentDialog;
    private final double Latitude;
    private final double Longitude;

    private EditText ChainEntry;
    private EditText NameEntry;

    @SuppressWarnings("ValidFragment")
    public AddStoreDialogFragment(Activity InActivity, DialogFragment InDialog, double InLatitude, double InLongitude)
    {
        this.ParentActivity = InActivity;
        this.ParentDialog = InDialog;
        this.Latitude = InLatitude;
        this.Longitude = InLongitude;
    }

    @SuppressWarnings("ValidFragment")
    public AddStoreDialogFragment()
    {
        this.ParentActivity = null;
        this.ParentDialog = null;
        this.Latitude = 0;
        this.Longitude = 0;
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

        builder.setTitle(getActivity().getResources().getString(R.string.ui_addStoreTitle));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_store_dialog, null);

        ChainEntry = (EditText)view.findViewById(R.id.chainEntry);
        NameEntry = (EditText)view.findViewById(R.id.nameEntry);

        builder.setView(view);
        builder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                InsertStoreTask insertStoreTask = new InsertStoreTask(ParentActivity, ParentDialog,
                        ChainEntry.getText().toString(),
                        NameEntry.getText().toString(),
                        Latitude,
                        Longitude);

                insertStoreTask.execute();
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
