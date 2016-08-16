/**
 *  SettingsActivity.java
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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.constants.Constants;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        LinearLayout linearLayout = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        View toolbarView = LayoutInflater.from(this).inflate(R.layout.settings_toolbar, linearLayout, false);
        Toolbar toolbar = (Toolbar)toolbarView.findViewById(R.id.toolbar);

        linearLayout.addView((View)toolbar.getParent(), 0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }

        return null;
    }

    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        final String locationPreferenceName = LocationPreferencesFragment.class.getName();
        final String generalPreferenceName = GeneralPreferencesFragment.class.getName();

        if (fragmentName.equals(locationPreferenceName))
        {
            return true;
        }
        else if (fragmentName.equals(generalPreferenceName))
        {
            return true;
        }

        return false;
    }

    public static class GeneralPreferencesFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences_general, false);
            addPreferencesFromResource(R.xml.preferences_general);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            final ListPreference currencyPreference = (ListPreference)findPreference("userCurrency");

            ArrayList<String> availableCurrencies = getCurrencyList();
            currencyPreference.setEntries(availableCurrencies.toArray(new CharSequence[availableCurrencies.size()]));
            currencyPreference.setEntryValues(availableCurrencies.toArray(new CharSequence[availableCurrencies.size()]));

            String storedUserCurrency = getUserCurrency();
            currencyPreference.setDefaultValue(storedUserCurrency);
            currencyPreference.setValue(storedUserCurrency);
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

        private String getUserCurrency()
        {
            SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            return preferences.getString(Constants.PREF_USERCURRENCY, "");
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            updatePreference(findPreference(key));
        }

        @Override
        public void onResume()
        {
            super.onResume();

            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i)
            {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceGroup)
                {
                    PreferenceGroup preferenceGroup = (PreferenceGroup)preference;
                    for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j)
                    {
                        updatePreference(preferenceGroup.getPreference(j));
                    }
                }
                else
                {
                    updatePreference(preference);
                }
            }
        }
    }

    public static class LocationPreferencesFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences_location, false);
            addPreferencesFromResource(R.xml.preferences_location);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            EditTextPreference preferredDistance = (EditTextPreference)findPreference("preferredStoreDistance");
            EditTextPreference maxDistance = (EditTextPreference)findPreference("maxStoreDistance");

            String storedPreferredDistanceValue = getDistanceValue(Constants.PREF_PREFERREDSTOREDISTANCE);
            String storedMaxDistanceValue = getDistanceValue(Constants.PREF_MAXSTOREDISTANCE);
            preferredDistance.setDefaultValue(storedPreferredDistanceValue);
            preferredDistance.setText(storedPreferredDistanceValue);

            maxDistance.setDefaultValue(storedMaxDistanceValue);
            maxDistance.setText(storedMaxDistanceValue);
        }

        private String getDistanceValue(String InKey)
        {
            SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            return String.valueOf(preferences.getFloat(InKey, 0));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            updatePreference(findPreference(key));
        }

        @Override
        public void onResume()
        {
            super.onResume();

            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i)
            {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceGroup)
                {
                    PreferenceGroup preferenceGroup = (PreferenceGroup)preference;
                    for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j)
                    {
                        updatePreference(preferenceGroup.getPreference(j));
                    }
                }
                else
                {
                    updatePreference(preference);
                }
            }
        }
    }

    private static void updatePreference(Preference preference)
    {
        if (preference instanceof ListPreference)
        {
            preference.setSummary(((ListPreference) preference).getEntry());
        }
        else if (preference instanceof EditTextPreference)
        {
            preference.setSummary(((EditTextPreference) preference).getText());
        }
    }

}
