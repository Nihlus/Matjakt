package com.nihlus.matjakt.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;
import com.nihlus.matjakt.database.containers.MatjaktStore;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.database.inserters.InsertPriceTask;
import com.nihlus.matjakt.ui.places.PlaceAutocompleteAdapter;


import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class AddPriceDialogFragment extends DialogFragment
{
    private final Activity ParentActivity;
    private final List<MatjaktStore> Stores;
    private final Bundle ProductData;
    private final double Latitude;
    private final double Longitude;

    private AutoCompleteTextView autoCompleteStoreText;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;

    private String selectedPlaceID;

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
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getResources().getString(R.string.ui_addPriceTitle));

        final View view = View.inflate(ParentActivity, R.layout.fragment_add_price_dialog, null);

        if (ParentActivity instanceof ViewProductActivity)
        {
            final GoogleApiClient apiClient = ((ViewProductActivity) ParentActivity).getGoogleApiClient();
            if (apiClient != null)
            {
                autoCompleteStoreText = (AutoCompleteTextView) view.findViewById(R.id.storeEntry);
                if (autoCompleteStoreText != null)
                {
                    autoCompleteStoreText.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
                            selectedPlaceID = item.getPlaceId();
                        }
                    });

                    // Load the previous store data, if we have some
                    if (!getSavedPrimaryText().isEmpty())
                    {
                        autoCompleteStoreText.setText(getSavedPrimaryText());
                    }

                    if (!getSavedPlaceID().isEmpty())
                    {
                        selectedPlaceID = getSavedPlaceID();
                    }

                    ArrayList<Integer> filterTypes = new ArrayList<>();
                    filterTypes.add(Place.TYPE_ESTABLISHMENT);

                    AutocompleteFilter filter = AutocompleteFilter.create(filterTypes);
                    LatLngBounds bounds = getLatLngBoundsFromLocation(Latitude, Longitude,
                            getStoreSearchDistance(true));

                    placeAutocompleteAdapter = new PlaceAutocompleteAdapter(ParentActivity,
                            ((ViewProductActivity) ParentActivity).getGoogleApiClient(),
                            bounds, filter);

                    autoCompleteStoreText.setAdapter(placeAutocompleteAdapter);
                }
            }
        }


        builder.setView(view);

        builder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // Empty listener - is overridden later
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

        AlertDialog finalDialog = builder.create();
        finalDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return finalDialog;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();
        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (areRequiredFieldsFilledOut(v.getRootView()))
                {
                    EditText priceEntry = (EditText) v.getRootView().findViewById(R.id.priceEntry);
                    double inPrice = Double.valueOf(priceEntry.getText().toString());

                    boolean isPriceOffer = ((CheckBox) v.getRootView().
                            findViewById(R.id.isOfferCheckbox)).isChecked();

                    saveStoreState(selectedPlaceID, autoCompleteStoreText.getText().toString());

                    // TODO: Check the selected Google API place (if there is one)
                    // If not, add a new store using the provided name, then add the price
                    InsertPriceTask insertPriceTask = new InsertPriceTask(ParentActivity,
                            (EAN) ProductData.getParcelable(Constants.PRODUCT_EAN),
                            inPrice,
                            getUserCurrency(),
                            selectedPlaceID,
                            isPriceOffer);

                    insertPriceTask.execute();
                    dismiss();
                }
            }
        });
    }

    private boolean areRequiredFieldsFilledOut(View view)
    {
        boolean areFieldsFilledOut = true;
        EditText priceEntry = (EditText) view.findViewById(R.id.priceEntry);
        EditText storeEntry = (EditText) view.findViewById(R.id.storeEntry);

        if (priceEntry.getText().toString().isEmpty())
        {
            areFieldsFilledOut = false;
            priceEntry.setError(getResources().getString(R.string.prompt_fillOutField));
        }

        if (storeEntry.getText().toString().isEmpty())
        {
            areFieldsFilledOut = false;
            storeEntry.setError(getResources().getString(R.string.prompt_fillOutField));
        }

        return areFieldsFilledOut;
    }

    private LatLngBounds getLatLngBoundsFromLocation(double InLatitude, double InLongitude, double InRadius)
    {
        LatLng center = new LatLng(InLatitude, InLongitude);
        LatLng southwestPoint = SphericalUtil.computeOffset(center, InRadius * Math.sqrt(2.0), 225);
        LatLng northeastPoint = SphericalUtil.computeOffset(center, InRadius * Math.sqrt(2.0), 45);

        return new LatLngBounds(southwestPoint, northeastPoint);
    }

    private String getUserCurrency()
    {
        SharedPreferences preferences = ParentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(Constants.PREF_USERCURRENCY, Currency.getInstance(Locale.getDefault()).getCurrencyCode());
    }

    private double getStoreSearchDistance(boolean maxAllowedDistance)
    {
        SharedPreferences preferences = ParentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (maxAllowedDistance)
        {
            return preferences.getFloat(Constants.PREF_MAXSTOREDISTANCE, 10.0f);
        }
        else
        {
            return preferences.getFloat(Constants.PREF_PREFERREDSTOREDISTANCE, 2.0f);
        }
    }

    private void saveStoreState(String placeID, String primaryText)
    {
        SharedPreferences preferences = ParentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(Constants.PREF_STOREPLACEID, placeID);
        editor.putString(Constants.PREF_STOREPRIMARYTEXT, primaryText);

        editor.apply();
    }

    private String getSavedPlaceID()
    {
        SharedPreferences preferences = ParentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(Constants.PREF_STOREPLACEID, "");
    }

    private String getSavedPrimaryText()
    {
        SharedPreferences preferences = ParentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(Constants.PREF_STOREPRIMARYTEXT, "");
    }
}
