package fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mechanic.appmechanic.MainActivity;
import com.example.mechanic.appmechanic.R;
import com.google.android.gms.maps.MapFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import recyclers.RecyclerAdapter;
import recyclers.RecyclerAdapterCards;
import recyclers.RecyclerViewOnClickListener;
import utilities.Constants;

import static android.support.v7.recyclerview.R.attr.layoutManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTips extends Fragment
{

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager lManager;
    private List<RecyclerAdapter> items = new ArrayList<>();
    private View v;
    private FragmentHome fragmentHome;


    public FragmentTips() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
         View v = inflater.inflate(R.layout.fragment_tips,container,false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.tips_title);

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        //SE ASIGNA UN KEYEVENT CUANDO SE PRESIONE EL
        // BOTÓN BACK DEL DISPOSITIVO MÓVIL
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
        //INICIALIZA LA VARIABLE RECYCLER PARA MOSTRAR LA LISTA DE LOS TIPS
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
        initRecyclerView();
        return v;
    }

    private void initRecyclerView() {
        //ESTABLECE PROPIEDADES AL RECYCLER
        recyclerView.setHasFixedSize(true);
        lManager = new LinearLayoutManager((MainActivity)getActivity());
        recyclerView.setLayoutManager(lManager);

        recyclerView.setAdapter(new RecyclerAdapterCards(tipsList(), new RecyclerViewOnClickListener()
        {
            @Override
            public void onClick(View v, int position)
            {
                tipSelection(position);
            }
        }));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    //RETORNA LA LISTA CON LA INFORMACIÓN DE LOS TIPS
    private List<RecyclerAdapter> tipsList() {
        return this.items;
    }
    //ASIGNA LA INFORMACION DE LOS TIPS A UNA LISTA
    public void setItems(List<RecyclerAdapter> lista)
    {
        this.items =lista;
    }

    //FUNCIÓN QUE MUESTRA UNA PEQUEÑA VENTANA EMERGENTE CON LA
    // INFORMACIÓN DEL TIP, AL DAR SELECCIONAR ALGUNO DE LOS TIPS DE LA LISTA
    private void tipSelection(int position) {
        LayoutInflater inflater= LayoutInflater.from(((MainActivity)getActivity()));
        View view=inflater.inflate(R.layout.alert_builder, null);

        TextView textview=(TextView)view.findViewById(R.id.textmsg);
        textview.setText(items.get(position).getPasos());
        Button btnc = (Button)view.findViewById(R.id.closeButtonTip);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(((MainActivity)getActivity()));
        builder1.setView(view);
        builder1.setIcon(R.drawable.herramientas);
        builder1.setTitle("TIP: "+items.get(position).getTipName());
        builder1.setCancelable(false);

        final AlertDialog alertDialog = builder1.create();
        btnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
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
