package fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mechanic.appmechanic.MainActivity;
import com.example.mechanic.appmechanic.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import utilities.Constants;
import utilities.MyAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMechanic extends Fragment
{

    private ArrayList<String[]> mecanicos = new ArrayList<>();
    private ArrayList<String[]> servicios = new ArrayList<>();
    private ArrayList<String[]> califs = new ArrayList<>();
    private ArrayList<String> report = new ArrayList<>();
    private String id="";
    private MyAsyncTask myAsyncTask;
    private ImageAsyncTask imageAsyncTask;
    private FragmentLocation fragmentLocation;

    private TextView nombre;
    private TextView info;
    private Button btnEnviar;
    private RatingBar ratingBar;
    private ImageView profile;
    private RadioGroup radioGroup;
    private CircleImageView imagen;
    private Bitmap loadedImage;
    private String mURL = "http://system-innovation.hol.es/img/profiles/";
    LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public FragmentMechanic() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.fragment_mechanic,container,false);

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        //SE LE ASIGNA UN KEYLISTENER CUANDO SE PRESIONA
        // EL BOTÓN DE REGRESAR DEL DISPOSITIVO MÓVIL
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    cargarFragment(getFragmentLocation());
                    return true;
                } else {
                    return false;
                }
            }
        });

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("");
        //AÑADE UN COMPONENTE A LA BARRA DE ACCIONES
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)v.findViewById(R.id.collapse_layout);
        collapsingToolbarLayout.setTitle("Detalles Mecánicos");

        Context context = ((MainActivity)getActivity()).getBaseContext();
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));

        //INICIALIZA LAS VARIABLES
        nombre = (TextView)v.findViewById(R.id.nameMechanic);
        info = (TextView)v.findViewById(R.id.infoMechanic);
        btnEnviar = (Button)v.findViewById(R.id.btnevaluate);
        ratingBar = (RatingBar)v.findViewById(R.id.ratingBars);
        radioGroup = (RadioGroup)v.findViewById(R.id.radiogroups);
        imagen = (CircleImageView)v.findViewById(R.id.profile_image);
        profile = (ImageView)v.findViewById(R.id.ratmec);

        //DENTRO DE ESTE FOR, SE ASIGNA LA CALIFICACIÓN DEL MECÁNICO
        // Y SE MUESTRA EN LA INTERFAZ, AL IGUAL QUE SU INFORMACIÓN DEL MISMO
        for (int b = 0;b < mecanicos.size();b++) {
            if (mecanicos.get(b)[0] == id) {
                //EJECUTA UN ASYNCTASK PARA OBTENER LA IMAGEN DEL MECÁNICO DESDE EL WEBSERVICE
                imageAsyncTask = new ImageAsyncTask(mURL,mecanicos.get(b)[6]);
                imageAsyncTask.execute();
                imageAsyncTask = null;
                //ESTABLECE EL NOMBRE DEL MECÁNICO
                nombre.setText(mecanicos.get(b)[1]+" "+mecanicos.get(b)[2]);

                //ASIGNA CALIFICACIÓN DEL MECÁNICO
                if((califs.get(b)[1]).equals("1"))
                    profile.setImageResource(R.drawable.onegreenstar);
                else if((califs.get(b)[1]).equals("2"))
                    profile.setImageResource(R.drawable.twogreenstar);
                else if((califs.get(b)[1]).equals("3"))
                    profile.setImageResource(R.drawable.threegreenstar);
                else if((califs.get(b)[1]).equals("4"))
                    profile.setImageResource(R.drawable.fourgreenstar);
                else if((califs.get(b)[1]).equals("5"))
                    profile.setImageResource(R.drawable.fivegreenstar);
                else
                    profile.setImageResource(R.drawable.zerogreenstar);
                //ESTABLECE LA INFORMACIÓN DEL MECÁNICO
                String datos = "\nTeléfono: "+mecanicos.get(b)[3]+"\n";
                datos += "\nSERVICIOS\n\n";

                for (int i = 0; i < servicios.size(); i++) {
                    if (servicios.get(i)[2] == id) {
                        datos += "-> "+servicios.get(i)[1]+".\n";
                    }
                }
                info.setText(datos);
            }
        }



        //CREA RADIO BUTTONS CON LOS SERVICIOS CON LOS QUE CUENTA EL MECÁNICO
        ViewGroup servoption = (ViewGroup) v.findViewById(R.id.radiogroups);
        for (int i = 0; i < servicios.size(); i++) {
            if (servicios.get(i)[2]== id) {
                RadioButton button = new RadioButton(((MainActivity) getActivity()));
                button.setId(Integer.parseInt(servicios.get(i)[0]));
                button.setText(servicios.get(i)[1]);
                servoption.addView(button, linLayoutParam);
            }
        }

        //ASIGNA UN EVENTO CLICK AL BOTÓN DE ENVIAR CALIFICACIÓN
        btnEnviar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if(ratingBar.getRating() != 0.0 && selectedId != -1) {
                    report.clear();
                    report.add(id);
                    report.add(String.valueOf(selectedId));
                    report.add(String.valueOf(ratingBar.getRating()));
                    report.add("reports");
                    executeAsync();
                } else {
                    Toast.makeText(((MainActivity)getActivity()).getBaseContext(),
                            "Seleccione y dé un valor al servicio",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }


    private void cargarFragment(Fragment fragmento)
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_content,fragmento);
        ft.commit();
    }

    private FragmentLocation getFragmentLocation()
    {
        if(fragmentLocation == null)fragmentLocation = new FragmentLocation();
        return fragmentLocation;
    }


    public void mecanicos(ArrayList<String[]> mechanics)
    {
        this.mecanicos = mechanics;
    }

    public void servicios(ArrayList<String[]> services)
    {
        this.servicios = services;
    }

    public void calificaciones(ArrayList<String[]> calific)
    {
        this.califs = calific;
    }

    public void id(String id)
    {
        this.id = id;
    }

    private void executeAsync()
    {
        myAsyncTask = new MyAsyncTask(Constants.URL_WEB_SERVICE,report,((MainActivity)getActivity()));
        myAsyncTask.execute();
        myAsyncTask = null;
    }






    /////////////////////////////////////////////////////////////////////////////////////////////////7

    public class ImageAsyncTask extends AsyncTask<String,String,String> {
        private String mlink;
        private String mdata;
        //OBTIENE LA URL Y UN VALOR STRING
        public ImageAsyncTask(String link,String data) {this.mlink = link;this.mdata = data;}

        @Override
        protected String doInBackground(String... params) {
            try {
                //ABRE UNA CONEXIÓN A LA URL OBTENIDA EN EL CONSTRUCTOR
                URL url = new URL(mlink+mdata);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(false);
                conn.connect();

                //OBTIENE LA IMAGEN DE LA RESPUESTA DEL WEBSERVICE
                loadedImage = BitmapFactory.decodeStream(conn.getInputStream());

                //ASIGNA LA IMAGEN A LA INTERFAZ
                ((MainActivity)getActivity()).runOnUiThread(new Runnable() {
                    public void run(){
                        imagen.setImageBitmap(loadedImage);
                    }
                });
                return mlink;
            } catch(Exception e) {
                return new String(String.format("Exception: %s", e.getMessage()));
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //TERMINA EJECUCIÓN
        }
    }

}
