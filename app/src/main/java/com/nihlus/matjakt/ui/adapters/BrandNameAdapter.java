package com.nihlus.matjakt.ui.adapters;

/**
 * Adapter for the columnar ListView for prices
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.nihlus.matjakt.MainActivity;
import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.R;
import com.nihlus.matjakt.ui.ModifyProductActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@SuppressWarnings("UnusedAssignment")
public class BrandNameAdapter extends ArrayAdapter<String>
{

    private ArrayList<String> brands;

    public BrandNameAdapter(Context context, ArrayList<String> values)
    {
        super(context, android.R.layout.simple_list_item_1, values);
        brands = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = super.getView(position, convertView, parent);

        row.setOnLongClickListener(new View.OnLongClickListener()
        {
            @SuppressWarnings("UnusedAssignment")
            @Override
            public boolean onLongClick(final View v)
            {
                new AlertDialog.Builder(getContext())
                        .setTitle(MainActivity.getStaticContext().getString(R.string.dialog_confirm))
                        .setMessage(MainActivity.getStaticContext().getString(R.string.dialog_deleteEntry))
                        .setPositiveButton(MainActivity.getStaticContext().getString(R.string.dialog_Yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // Delete the entry
                                //deleteStoredBrand();
                                TextView text = (TextView) v;
                                deleteStoredBrand(text.getText().toString());
                            }
                        })
                        .setNegativeButton(MainActivity.getStaticContext().getString(R.string.dialog_No), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // Do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });

        row.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (getContext() instanceof ModifyProductActivity)
                {
                    TextView text = (TextView) v;
                    AutoCompleteTextView auto = (AutoCompleteTextView)((ModifyProductActivity) getContext()).findViewById(R.id.brandEdit);

                    auto.setText(text.getText());
                    auto.dismissDropDown();
                }
            }
        });
        return row;
    }

    private void deleteStoredBrand(String inBrand)
    {
        if (brands.contains(inBrand))
        {
            brands.remove(inBrand);
            setStoredBrands(brands);
        }
    }

    private void setStoredBrands(ArrayList<String> inBrands)
    {
        if (inBrands != null)
        {
            this.clear();
            this.addAll(inBrands);
            this.notifyDataSetChanged();

            brands = inBrands;
            JSONArray brandArray = new JSONArray(inBrands);

            SharedPreferences preferences = MainActivity.getStaticContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(Constants.PREF_BRANDARRAY, brandArray.toString());
            editor.apply();
        }
    }

    public static ArrayList<String> getStoredBrands()
    {
        ArrayList<String> brands = new ArrayList<>();
        try
        {
            SharedPreferences preferences = MainActivity.getStaticContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            JSONArray brandArray = new JSONArray(preferences.getString(Constants.PREF_BRANDARRAY, "[]"));

            for (int i = 0; i < brandArray.length(); i++)
            {
                brands.add(brandArray.getString(i));
            }
        }
        catch (JSONException jex)
        {
            // TODO: Create global exception handler
            jex.printStackTrace();
        }

        return brands;
    }
}