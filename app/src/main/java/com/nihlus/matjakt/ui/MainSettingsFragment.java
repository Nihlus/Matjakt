package com.nihlus.matjakt.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.nihlus.matjakt.R;
import com.nihlus.matjakt.constants.Constants;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Set;


public class MainSettingsFragment extends Fragment
{
    public MainSettingsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View createdView =  inflater.inflate(R.layout.fragment_main_settings, container, false);


        final EditText prefDistance = (EditText)createdView.findViewById(R.id.preferredStoreDistanceText);
        prefDistance.setText(getDistanceValue(Constants.PREF_PREFERREDSTOREDISTANCE));
        prefDistance.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (!s.toString().isEmpty())
                {
                    MainSettingsFragment.this.updatePreferenceDistanceValue(Constants.PREF_PREFERREDSTOREDISTANCE,
                            Float.valueOf(s.toString()));
                }
            }
        });


        final EditText maxDistance = (EditText)createdView.findViewById(R.id.maximumStoreDistanceText);
        maxDistance.setText(getDistanceValue(Constants.PREF_MAXSTOREDISTANCE));
        maxDistance.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (!s.toString().isEmpty())
                {
                    MainSettingsFragment.this.updatePreferenceDistanceValue(Constants.PREF_MAXSTOREDISTANCE,
                            Float.valueOf(s.toString()));
                }
            }
        });

        final Spinner currencySpinner = (Spinner)createdView.findViewById(R.id.currencySpinner);
        final ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, getCurrencyList());
        currencySpinner.setAdapter(currencyAdapter);

        currencySpinner.setSelection(currencyAdapter.getPosition(getUserCurrency()));

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                updatePreferenceCurrencyValue(currencySpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        final CheckBox darkThemeCheckBox = (CheckBox)createdView.findViewById(R.id.useDarkThemeCheckbox);
        darkThemeCheckBox.setChecked(getShouldUseDarkTheme());
        darkThemeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                updateShouldUseDarkThemeValue(isChecked);
            }
        });


        return createdView;
    }

    private void updatePreferenceDistanceValue(String InKey, float InData)
    {
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        preferenceEditor.putFloat(InKey, InData);

        preferenceEditor.apply();
    }

    private void updatePreferenceCurrencyValue(String InCurrency)
    {
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        preferenceEditor.putString(Constants.PREF_USERCURRENCY, InCurrency);

        preferenceEditor.apply();
    }

    private void updateShouldUseDarkThemeValue(boolean shouldUseDarkTheme)
    {
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        preferenceEditor.putBoolean(Constants.PREF_USEDARKTHEME, shouldUseDarkTheme);

        preferenceEditor.apply();
    }

    private String getDistanceValue(String InKey)
    {
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return String.valueOf(preferences.getFloat(InKey, 0));
    }

    private String getUserCurrency()
    {
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(Constants.PREF_USERCURRENCY, "");
    }

    private boolean getShouldUseDarkTheme()
    {
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(Constants.PREF_USEDARKTHEME, false);
    }

    private ArrayList<String> getCurrencyList()
    {
        ArrayList<String> outList = new ArrayList<>();

        Set<Currency> Currencies = Currency.getAvailableCurrencies();
        for (Currency c : Currencies)
        {
            outList.add(c.getCurrencyCode());
        }

        return outList;
    }
}
