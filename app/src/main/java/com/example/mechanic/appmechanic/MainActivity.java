package com.example.mechanic.appmechanic;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

import fragments.FragmentContact;
import fragments.FragmentHome;
import fragments.FragmentLocation;
import fragments.FragmentTips;
import recyclers.RecyclerAdapter;
import utilities.Constants;

public class MainActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentHome fragmentHome;
    private FragmentLocation fragmentLocation;
    private FragmentTips fragmentTips;
    private FragmentContact fragmentContact;
    private TipsAsyncTask tipsAsyncTask;
    private List<RecyclerAdapter> items = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tipsAsyncTask = new TipsAsyncTask("tips", Constants.URL_WEB_SERVICE);
        tipsAsyncTask.execute();
        tipsAsyncTask = null;

        inicializarToolbar();
        inicializarComponentes();
    }

    private void inicializarToolbar()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private void inicializarComponentes()
    {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        if(navigationView != null)
        {
            setupDrawerContent(navigationView);
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        cargarFragment(getFragmentHome());
    }





    private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item)
                    {
                        drawerLayout.closeDrawers();

                        switch (item.getItemId())
                        {
                            case R.id.nav_home:
                                item.setChecked(true);
                                if(getSupportActionBar().getTitle() != "Home")
                                    cargarFragment(getFragmentHome());
                                break;

                            case R.id.nav_location:
                                item.setChecked(true);
                                if(getSupportActionBar().getTitle() != "Mecánicos")
                                    cargarFragment(getFragmentLocation());
                                break;

                            case R.id.nav_tips:
                                item.setChecked(true);
                                if(getSupportActionBar().getTitle() != "Tips")
                                    cargarFragment(getFragmentTips());
                                    Constants c = new Constants();
                                    fragmentTips.setItems(items);
                                break;
                            case R.id.nav_contact:
                                item.setChecked(true);
                                if(getSupportActionBar().getTitle() != "Contacto")
                                    cargarFragment(getFragmentContact());
                                break;
                        }
                        return true;
                    }
                }
        );
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

    private FragmentLocation getFragmentLocation()
    {
        if(fragmentLocation == null)fragmentLocation = new FragmentLocation();
        return fragmentLocation;
    }

    private FragmentTips getFragmentTips()
    {
        if(fragmentTips == null)fragmentTips = new FragmentTips();
        return fragmentTips;
    }

    private FragmentContact getFragmentContact()
    {
        if(fragmentContact == null)fragmentContact = new FragmentContact();
        return fragmentContact;
    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.Salir:
                exitApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public boolean onKeyDown(int keyCode, android.view.KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                exitApp();
            } else {
                getFragmentManager().popBackStack();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void exitApp()
    {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Confirmación");
        alerta.setMessage("¿Deseas salir de la aplicación?");
        alerta.setNegativeButton("No", null);
        alerta.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                MainActivity.this.finish();
            }
        });
        alerta.show();
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////


    public class TipsAsyncTask extends AsyncTask<String,String,String>
    {
        private String mlink;
        private String mdata;

        public TipsAsyncTask(String data,String link)
        {
            this.mlink = link;
            this.mdata = data;
        }


        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                String data = URLEncoder.encode("option", "UTF-8") + "=" + URLEncoder.encode(this.mdata, "UTF-8");

                URL url = new URL(mlink);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                //Envía datos a la url mediante POST
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                //Lee la informacion que envía el servidor
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()
                        )
                );

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null)
                {
                    sb.append(line);
                    break;
                }
                //CONVIERTE RESPUESTA A ARRAY JSON
                JSONArray results = new JSONArray(sb.toString());

                String id = "";
                String nombre = "";
                String pasos = "";
                //OBTIENE OBJETOS JSON DEL ARRAY ANTERIOR
                for (int i = 0; i < results.length(); i++)
                {
                    JSONObject obj = results.getJSONObject(i);

                    id = obj.getString("folio");
                    nombre = obj.getString("nombre");
                    pasos = obj.getString("pasos");

                    items.add(new RecyclerAdapter(id,nombre,pasos));
                }

                return sb.toString();

            }
            catch(Exception e)
            {
                return new String(String.format("Exception: %s", e.getMessage()));
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            //Toast.makeText(getBaseContext(),String.valueOf(items.size()),Toast.LENGTH_SHORT).show();
            //Log.d("Resultado",String.valueOf(items.size()));
        }
    }
}
