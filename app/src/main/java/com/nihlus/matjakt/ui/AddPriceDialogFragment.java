package com.nihlus.matjakt.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.database.containers.MatjaktStore;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.database.inserters.InsertPriceTask;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;


public class AddPriceDialogFragment extends DialogFragment
{
    private final Activity ParentActivity;
    private final List<MatjaktStore> Stores;
    private final Bundle ProductData;
    private final double Latitude;
    private final double Longitude;

    private Spinner storeSpinner;
    private List<String> StoreNames;

    @SuppressWarnings("ValidFragment")
    public AddPriceDialogFragment(Activity InActivity, List<MatjaktStore> InStores, Bundle InProductData, double InLatitude, double InLongitude)
    {
        this.ParentActivity = InActivity;
        this.Stores = InStores;
        this.ProductData = InProductData;
        this.Latitude = InLatitude;
        this.Longitude = InLongitude;
    }

    public AddPriceDialogFragment()
    {
        // Required empty public constructor
        this.ParentActivity = null;
        this.Stores = null;
        this.ProductData = null;
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
        final View view = View.inflate(ParentActivity, R.layout.fragment_add_price_dialog, null);

        storeSpinner = (Spinner)view.findViewById(R.id.storesSpinner);

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
                EditText priceEntry = (EditText)view.findViewById(R.id.priceEntry);
                double inPrice = Double.valueOf(priceEntry.getText().toString());
                int numberEnd = ((String)storeSpinner.getSelectedItem()).indexOf('-') - 1;
                int storeID = Integer.valueOf(((String) storeSpinner.getSelectedItem()).substring(0, numberEnd));


                InsertPriceTask insertPriceTask = new InsertPriceTask(ParentActivity,
                        (EAN)ProductData.getParcelable(Constants.PRODUCT_EAN),
                        inPrice,
                        getUserCurrency(),
                        storeID,
                        false);

                insertPriceTask.execute();
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

    private String getUserCurrency()
    {
        SharedPreferences preferences = ParentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(Constants.PREF_USERCURRENCY, Currency.getInstance(Locale.getDefault()).getCurrencyCode());
    }

    public void onStoreInserted(boolean Success, MatjaktStore InsertedStore)
    {
        if (Success)
        {
            // TODO: Replace with proper adapter. HashMaps!
            String storeEntry = InsertedStore.ID + " - " + InsertedStore.Chain + " " + InsertedStore.Name;
            if (!StoreNames.contains(storeEntry))
            {
                StoreNames.add(storeEntry);
            }

            int storeIndex = StoreNames.indexOf(storeEntry);
            storeSpinner.setSelection(storeIndex, true);
        }
    }
}
