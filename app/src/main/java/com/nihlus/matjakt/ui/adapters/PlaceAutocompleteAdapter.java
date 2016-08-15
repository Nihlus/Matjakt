/**
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.nihlus.matjakt.ui.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlaceAutocompleteAdapter extends ArrayAdapter<AutocompletePrediction>
    implements Filterable
{
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    // Current results in the adapter
    private ArrayList<AutocompletePrediction> resultList;

    // Api client for autocomplete requests
    private final GoogleApiClient googleApiClient;

    // Location bounds for the requests
    private final LatLngBounds bounds;

    // The filter used to restrict the queries to specific place types
    private final AutocompleteFilter placeFilter;

    public PlaceAutocompleteAdapter(Context context, GoogleApiClient InGoogleApiClient,
                                    LatLngBounds InBounds, AutocompleteFilter InPlaceFilter)
    {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        googleApiClient = InGoogleApiClient;
        bounds = InBounds;
        placeFilter = InPlaceFilter;
    }

    @Override
    public int getCount()
    {
        return resultList.size();
    }

    @Override
    public AutocompletePrediction getItem(int position)
    {
        return resultList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = super.getView(position, convertView, parent);

        // Sets the primary and secondary text for a row.
        // Note that getPrimaryText() and getSecondaryText() return a CharSequence that may contain
        // styling based on the given CharacterStyle.

        AutocompletePrediction item = getItem(position);

        TextView textView1 = (TextView) row.findViewById(android.R.id.text1);
        TextView textView2 = (TextView) row.findViewById(android.R.id.text2);
        textView1.setText(item.getPrimaryText(STYLE_BOLD));
        textView2.setText(item.getSecondaryText(STYLE_BOLD));

        return row;
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();

                // Skip the autocomplete query if no constraints are given
                if (constraint != null)
                {
                    resultList = getAutocomplete(constraint);
                    if (resultList != null)
                    {


                        results.values = resultList;
                        results.count = resultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                if (results != null && results.count > 0)
                {
                    // The API returned at least one result, update the data
                    notifyDataSetChanged();
                }
                else
                {
                    // The API did not return any results, invalidate the data set
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue)
            {
                if (resultValue instanceof AutocompletePrediction)
                {
                    return ((AutocompletePrediction) resultValue).getPrimaryText(null);
                }
                else
                {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }

    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint)
    {
        if (googleApiClient.isConnected())
        {
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi.getAutocompletePredictions(googleApiClient, constraint.toString(),
                    bounds, placeFilter);

            AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);

            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess())
            {
                Toast.makeText(getContext(), "Error contacting Places API: " + status.toString(),
                        Toast.LENGTH_SHORT).show();

                autocompletePredictions.release();
                return null;
            }

            // Freeze the results immutable representation that can be stored safely.
            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        }

        return null;
    }
}
