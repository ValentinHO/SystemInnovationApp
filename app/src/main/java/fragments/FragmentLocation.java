package fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mechanic.appmechanic.MainActivity;
import com.example.mechanic.appmechanic.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import utilities.Constants;

/**
 * A simple {@link Fragment} subclass.
 */

//SE IMPLEMENTA EL HILO OnMapReadyCallBack
public class FragmentLocation extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    private ConexionAsyncTask conexionAsyncTask;
    ArrayList<String[]> datas = new ArrayList<>();
    ArrayList<String[]> services = new ArrayList<>();
    ArrayList<String[]> califs = new ArrayList<>();
    private FragmentMechanic fragmentMechanic;
    private final static String TAG_FRAGMENT = "TAG_FRAGMENT";
    private FragmentHome fragmentHome;

    //CONSTRUCTOR DEL FRAGMENT LOCATION
    public FragmentLocation() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_location,container,false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.mecanicos_title);

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        //SE LE ASIGNA AL FRAGMENT, UN EVENTO CUANDO
        // SE PRESIONA EL BOTÓN BACK DEL DISPOSITIVO MÓVIL
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
        //RETORNA LA VISTA DEL FRAGMENT
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //SE INICIALIZA EL MAPA Y SE MUESTRA EN LA VISTA DEL FRAGMENT AL CARGARLA
        MapFragment mapFragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.mapita);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //SE ASIGNAN PERMISOS PARA ACCESAR AL GPS Y DETERMINAR LA POSICIÓN
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(((MainActivity)getActivity()).getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(((MainActivity)getActivity()).getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        //SE EJECUTA UN METODO QUE A SU VEZ,
        // EJECUTA UN ASYNCTASK PARA OBTENER LA INFORMACIÓN QUE SE
        // USARÁ PARA LOS MARCADORES O POSICIONES DE LOS MECANICOS EN EL MAPA
        ejecutarAsync();
    }
    //FUNCIÓN QUE EJECUTA EL ASYNC TASK
    private void ejecutarAsync()
    {
        conexionAsyncTask = new ConexionAsyncTask("index", Constants.URL_WEB_SERVICE);
        conexionAsyncTask.execute();
        conexionAsyncTask = null;
    }







    private void añadirMarkers()
    {
        //CREA MARCADORES Y LOS ASIGNA AL MAPA
        for (int i = 0; i < datas.size(); i++) {
            Marker x = map.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(datas.get(i)[4]), Double.parseDouble(datas.get(i)[5])))
                    .title("Mecánico: "+datas.get(i)[1] + " " + datas.get(i)[2])
            );
            x.showInfoWindow();
        }
        //EJECUTA FUNCIÓN asignarListener
            asignarListener();
    }

    private void asignarListener()
    {
        //Asigna evento onclick listener a cada marcador creado en añadirMakers
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String titulo = marker.getTitle();

                for (int i=0;i<datas.size();i++)
                {
                    if(("Mecánico: "+datas.get(i)[1]+" "+datas.get(i)[2]).equals(titulo))
                    {
                        //SI EL NOMBRE DEL MECÁNICO ES IGUAL AL TITULO DEL MARCADOR
                        //CARGA EL FRAGMENT MECHANIC
                        cargarFragment(getFragmentMechanic());
                        fragmentMechanic.mecanicos(datas);
                        fragmentMechanic.servicios(services);
                        fragmentMechanic.calificaciones(califs);
                        fragmentMechanic.id(datas.get(i)[0]);
                    }
                }
            }
        });
    }




    private FragmentMechanic getFragmentMechanic()
    {
        if(fragmentMechanic == null)fragmentMechanic = new FragmentMechanic();
        return fragmentMechanic;
    }

    private void cargarFragment(Fragment fragmento)
    {
        FragmentManager fm = ((MainActivity)getActivity()).getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_content,fragmento);
        ft.commit();
    }

    private FragmentHome getFragmentHome()
    {
        if(fragmentHome == null)fragmentHome = new FragmentHome();
        return fragmentHome;
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ASYNCTASK QUE ES EJECUTADO AL INICIO DE LA CARGA DE LA INTERFAZ DEL FRAGMENT LOCATION
    public class ConexionAsyncTask extends AsyncTask<String,String,String> {
        private String mlink;
        private String mdata;
        ArrayList<String> datos = new ArrayList<>();

        //CONSTRUCTOR QUE RECIBE LA URL Y UN VALOR STRING
        public ConexionAsyncTask(String data,String link){
            this.mlink = link;
            this.mdata = data;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                //GENERA LOS DATOS A ENVIAR AL WEBSERVICE EN FORMATO UTF-8 Y
                // ABRE LA CONEXIÓN A LA URL RECIBIDA EN EL CONSTRUCTOR
                String data = URLEncoder.encode("option", "UTF-8") + "=" + URLEncoder.encode(this.mdata, "UTF-8");
                URL url = new URL(mlink);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                //Envía datos a la url mediante POST
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                //Lee la informacion que envía el servidor
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                //ALMACENA LA RESPUESTA DEL WEBSERVICE EN UNA VARIABLE STRINGBUILDER
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                //CONVIERTE RESPUESTA A ARRAY JSON
                JSONArray results = new JSONArray(sb.toString());
                int c;
                JSONArray serv = null;
                JSONArray cals = null;
                //OBTIENE OBJETOS JSON DEL ARRAY ANTERIOR
                for (int i = 0; i < results.length(); i++) {
                    JSONObject obj = results.getJSONObject(i);
                    //SI ES EL PENULTIMO OBJETO ENTONCES OBTIENE LOS DEMAS ARRAY DE SERVICIOS
                    if ((results.length()-i)==2) {
                        for (c=0;c <datas.size();c++) {
                            serv = new JSONArray(obj.getString(datas.get(c)[0]));

                            for (int x = 0; x < serv.length(); x++) {
                                JSONObject ob = serv.getJSONObject(x);
                                String[] servi = {ob.getString("serviceid"), ob.getString("servicio"),datas.get(c)[0]};
                                services.add(servi);
                            }
                        }
                    } else if((results.length()-i)==1) {
                        for (c=0;c <datas.size();c++) {
                            cals = new JSONArray(obj.getString(datas.get(c)[0]));

                            for (int x = 0; x < cals.length(); x++) {
                                JSONObject ob = cals.getJSONObject(x);
                                String[] starsrat = {datas.get(c)[0],ob.getString("calif")};
                                califs.add(starsrat);
                            }
                        }
                    }
                    //SI NO ES EL PENULTIMO, ENTONCES SIGUE OBTENIENDO LOS MECANICOS
                    else {
                        String[] mechanic = {
                                obj.getString("idmecanico"), obj.getString("nombre"), obj.getString("apellidos"),
                                obj.getString("telefono"), obj.getString("latitud"), obj.getString("longitud"),
                                obj.getString("imagen")};
                        datas.add(mechanic);
                    }
                }//TERMINA EL CICLO DE RECORRIDO DEL ARRAY PRINCIPAL

                return sb.toString();

            } catch(Exception e) {
                return new String(String.format("Exception: %s", e.getMessage()));
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //AL TERMINAR LA EJECUCIÓN DEL ASYNCTASK SE EJECUTA LA FUNCIÓN añadirMakers
            añadirMarkers();
        }
    }
}
