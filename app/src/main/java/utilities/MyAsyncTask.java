package utilities;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mechanic.appmechanic.MainActivity;
import com.example.mechanic.appmechanic.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by valen on 12/02/2017.
 */

public class MyAsyncTask extends AsyncTask<String,String,String>
{
    private String mlink;
    private ArrayList<String> mdatos;
    private MainActivity mainActivity;

    public MyAsyncTask(String link, ArrayList<String> datos, MainActivity activity)
    {
        this.mlink = link;
        this.mdatos = datos;
        this.mainActivity = activity;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            String id = mdatos.get(0);
            String servicio = mdatos.get(1);
            String stars = mdatos.get(2);
            String option = mdatos.get(3);
            String data = URLEncoder.encode("option", "UTF-8") + "=" + URLEncoder.encode(option, "UTF-8");
            data += "&" + URLEncoder.encode("idmecanico", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            data += "&" + URLEncoder.encode("idservicio", "UTF-8") + "=" + URLEncoder.encode(servicio, "UTF-8");
            data += "&" + URLEncoder.encode("estrellas", "UTF-8") + "=" + URLEncoder.encode(stars, "UTF-8");

            URL url = new URL(mlink);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            return sb.toString();

        }
        catch (Exception e)
        {
            return new String("Exception: "+e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (s.equals("ok"))
            Constants.mostrarAlertDialog(mainActivity, "La información se envió correctamente", "Aviso", R.drawable.ic_announcement_black_24dp).show();
        else
            Constants.mostrarAlertDialog(mainActivity, s, "Atención", R.drawable.ic_report_problem_black_24dp).show();
    }
}
