package fragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.mechanic.appmechanic.MainActivity;
import com.example.mechanic.appmechanic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentContact extends Fragment
{

    private FragmentHome fragmentHome;

    public FragmentContact() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.fragment_contact,container,false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Contacto");

        /*final VideoView videoView =
                (VideoView) v.findViewById(R.id.videos);

        videoView.setVideoPath(
                "http://system-innovation.hol.es/videos/asociaciones.mp4");

        MediaController mediaController = new
                MediaController((MainActivity)getActivity());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();*/

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    cargarFragment(getFragmentHome());
                    return true;
                } else {
                    return false;
                }
            }
        });





        TextView body = (TextView)v.findViewById(R.id.body1);
        body.setText(Html.fromHtml(getString(R.string.infocon1)));

        TextView body2 = (TextView)v.findViewById(R.id.body2);
        body2.setText(Html.fromHtml(getString(R.string.infocon2)));

        TextView body3 = (TextView)v.findViewById(R.id.body3);
        body3.setText(Html.fromHtml(getString(R.string.infocon3)));

        TextView body4 = (TextView)v.findViewById(R.id.body4);
        body4.setText(Html.fromHtml(getString(R.string.infocon4)));

        return v;
    }


    private void cargarFragment(Fragment fragmento)
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_content,fragmento);
        ft.commit();
    }

    private FragmentHome getFragmentHome()
    {
        if(fragmentHome == null)fragmentHome = new FragmentHome();
        return fragmentHome;
    }

}
