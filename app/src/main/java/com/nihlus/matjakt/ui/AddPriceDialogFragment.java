package com.nihlus.matjakt.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.nihlus.matjakt.database.containers.MatjaktStore;
import com.nihlus.matjakt.R;

import java.util.ArrayList;
import java.util.List;


public class AddPriceDialogFragment extends DialogFragment
{
    private final Activity ParentActivity;
    private final List<MatjaktStore> Stores;
    private final double Latitude;
    private final double Longitude;

    private List<String> StoreNames;

    @SuppressWarnings("ValidFragment")
    public AddPriceDialogFragment(Activity InActivity, List<MatjaktStore> InStores, double InLatitude, double InLongitude)
    {
        this.ParentActivity = InActivity;
        this.Stores = InStores;
        this.Latitude = InLatitude;
        this.Longitude = InLongitude;
    }

    public AddPriceDialogFragment()
    {
        // Required empty public constructor
        this.ParentActivity = null;
        this.Stores = null;
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

        builder.setTitle(getActivity().getResources().getString(R.string.ui_addPriceTitle));

        //LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = View.inflate(ParentActivity, R.layout.fragment_add_price_dialog, null);

        Spinner storeSpinner = (Spinner)view.findViewById(R.id.storesSpinner);

        StoreNames = new ArrayList<>();
        for (MatjaktStore Store: Stores)
        {
            StoreNames.add(Store.ID + " - " + Store.Chain + " " + Store.Name);
        }

        // TODO: Replace with proper adapter, containing the ID as well. HashMaps!
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, StoreNames);

        storeSpinner.setAdapter(adapter);

        Button addStoreButton = (Button)view.findViewById(R.id.addStoreButton);
        addStoreButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AddStoreDialogFragment storeDialogFragment = new AddStoreDialogFragment(ParentActivity,
                        AddPriceDialogFragment.this,
                        Latitude,
                        Longitude);

                storeDialogFragment.show(ParentActivity.getFragmentManager(), "ADDSTOREDIALOG");
            }
        });

        builder.setView(view);
        builder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO: Send results
                dialog.cancel();
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

    public void onStoreInserted(boolean Success, MatjaktStore InsertedStore)
    {
        if (Success)
        {
            // TODO: Replace with proper adapter. HashMaps!
            String storeEntry = InsertedStore.ID + " - " + InsertedStore.Chain + " " + InsertedStore.Name;
            StoreNames.add(storeEntry);
        }
    }
}
