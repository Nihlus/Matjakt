package com.nihlus.matjakt;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 * Should be deprecated as soon as possible.
 */
public class MainActivityFragment extends Fragment
{

    public ArrayAdapter<String> listAdapter;
    ArrayList<String> listItems = new ArrayList<>();

    public MainActivityFragment()
    {
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
