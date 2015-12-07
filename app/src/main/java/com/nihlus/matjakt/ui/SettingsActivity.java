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

import java.util.List;

/**
 * Created by jarl on 12/5/15.
 * Allows the user to tune the app settings.
 */
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
        final String appearancePreferenceName = AppearancePreferencesFragment.class.getName();

        if (fragmentName.equals(locationPreferenceName))
        {
            return true;
        }
        else if (fragmentName.equals(generalPreferenceName))
        {
            return true;
        }
        else if (fragmentName.equals(appearancePreferenceName))
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

    public static class AppearancePreferencesFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences_appearance, false);
            addPreferencesFromResource(R.xml.preferences_appearance);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

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
