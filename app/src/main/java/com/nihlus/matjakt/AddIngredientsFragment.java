package com.nihlus.matjakt;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nihlus.matjakt.UI.UIUpdateInterface;

/**
 * This fragment allows the user to edit the ingredients for a specified product, as well
 * as select a language for the ingredient list.
 */
public class AddIngredientsFragment extends Fragment implements UIUpdateInterface
{

    public AddIngredientsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_ingredients, container, false);
    }

    //TODO: Implement loading of UI data from local database and shared preferences
    @Override
    public void UpdateFragmentUI()
    {

    }
}
