package fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.mechanic.appmechanic.MainActivity;
import com.example.mechanic.appmechanic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment
{


    public FragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.fragment_home,container,false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Home");
        return v;
    }

}
