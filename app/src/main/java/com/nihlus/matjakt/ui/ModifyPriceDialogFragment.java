/**
 *  ModifyPriceDialogFragment.java
 *
 *  Author:
 *       Jarl Gullberg <jarl.gullberg@gmail.com>
 *
 *  Copyright (c) 2016 Jarl Gullberg
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nihlus.matjakt.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.MatjaktPrice;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.database.inserters.InsertPriceTask;
import com.nihlus.matjakt.outpan.OutpanProduct;
import com.nihlus.matjakt.ui.adapters.PlaceAutocompleteAdapter;


import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;


public class ModifyPriceDialogFragment extends DialogFragment
{
    private final ViewProductActivity parentActivity;
    private final OutpanProduct productData;
    private final MatjaktPrice priceToEdit;
    private final Location location;

    private AutoCompleteTextView autoCompleteStoreText;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;

    private String selectedPlaceID;

    @SuppressWarnings("ValidFragment")
    public ModifyPriceDialogFragment(ViewProductActivity InActivity, OutpanProduct InProductData, Location InLocation)
    {
        this.parentActivity = InActivity;
        this.productData = InProductData;
        this.priceToEdit = null;
        this.location = InLocation;
    }

    @SuppressWarnings("ValidFragment")
    public ModifyPriceDialogFragment(ViewProductActivity InActivity, OutpanProduct InProductData, MatjaktPrice InNewPrice, Location InLocation)
    {
        this.parentActivity = InActivity;
        this.productData = InProductData;
        this.priceToEdit = InNewPrice;
        this.location = InLocation;
    }

    public ModifyPriceDialogFragment()
    {
        // Required empty public constructor
        this.parentActivity = null;
        this.productData = null;
        this.priceToEdit = null;
        this.location = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (priceToEdit != null)
        {
            builder.setTitle(getActivity().getResources().getString(R.string.ui_dialog_editprice));
        }
        else
        {
            builder.setTitle(getActivity().getResources().getString(R.string.ui_dialog_addprice));
        }

        final View view = View.inflate(parentActivity, R.layout.fragment_modify_price_dialog, null);
        final GoogleApiClient apiClient = parentActivity.getGoogleApiClient();
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

                // If we have an input price, load that as well
                if (priceToEdit != null)
                {
                    TextView priceEdit = (TextView)view.findViewById(R.id.priceEntry);
                    if (priceEdit != null)
                    {
                        priceEdit.setText(String.valueOf(priceToEdit.price));
                    }

                    autoCompleteStoreText.setText(priceToEdit.store.storePlace.getName());
                    selectedPlaceID = priceToEdit.store.placeID;


                    CheckBox offerCheck = (CheckBox)view.findViewById(R.id.isOfferCheckbox);
                    if (offerCheck != null)
                    {
                        offerCheck.setChecked(priceToEdit.isOffer);
                    }
                }

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
                filterTypes.add(Place.TYPE_GAS_STATION);

                AutocompleteFilter filter = AutocompleteFilter.create(filterTypes);
                LatLngBounds bounds = getLatLngBoundsFromLocation(location,
                        getStoreSearchDistance());

                placeAutocompleteAdapter = new PlaceAutocompleteAdapter(parentActivity,
                        parentActivity.getGoogleApiClient(),
                        bounds, filter);

                autoCompleteStoreText.setAdapter(placeAutocompleteAdapter);
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

                    if (priceToEdit != null)
                    {
                        InsertPriceTask insertPriceTask = new InsertPriceTask(parentActivity,
                                priceToEdit.ID,
                                productData.ean,
                                inPrice,
                                getUserCurrency(),
                                selectedPlaceID,
                                isPriceOffer);

                        insertPriceTask.execute();
                    }
                    else
                    {
                        InsertPriceTask insertPriceTask = new InsertPriceTask(parentActivity,
                                productData.ean,
                                inPrice,
                                getUserCurrency(),
                                selectedPlaceID,
                                isPriceOffer);

                        insertPriceTask.execute();
                    }

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

    private static LatLngBounds getLatLngBoundsFromLocation(Location InLocation, double InRadius)
    {
        LatLng center = new LatLng(InLocation.getLatitude(), InLocation.getLongitude());
        LatLng southwestPoint = SphericalUtil.computeOffset(center, InRadius * Math.sqrt(2.0), 225);
        LatLng northeastPoint = SphericalUtil.computeOffset(center, InRadius * Math.sqrt(2.0), 45);

        return new LatLngBounds(southwestPoint, northeastPoint);
    }

    private String getUserCurrency()
    {
        SharedPreferences preferences = parentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(Constants.PREF_USERCURRENCY, Currency.getInstance(Locale.getDefault()).getCurrencyCode());
    }

    private double getStoreSearchDistance()
    {
        SharedPreferences preferences = parentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getFloat(Constants.PREF_MAXSTOREDISTANCE, 10.0f);
    }

    private void saveStoreState(String placeID, String primaryText)
    {
        SharedPreferences preferences = parentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(Constants.PREF_STOREPLACEID, placeID);
        editor.putString(Constants.PREF_STOREPRIMARYTEXT, primaryText);

        editor.apply();
    }

    private String getSavedPlaceID()
    {
        SharedPreferences preferences = parentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(Constants.PREF_STOREPLACEID, "");
    }

    private String getSavedPrimaryText()
    {
        SharedPreferences preferences = parentActivity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(Constants.PREF_STOREPRIMARYTEXT, "");
    }
}
