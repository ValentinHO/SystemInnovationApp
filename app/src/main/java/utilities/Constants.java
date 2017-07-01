package utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.List;

import recyclers.RecyclerAdapter;

/**
 * Created by valen on 12/02/2017.
 */

public class Constants
{
    public static final String URL_WEB_SERVICE = "http://system-innovation.hol.es/conexion/obtenermarkers.php";

    public static AlertDialog mostrarAlertDialog(Activity activity, String mensaje, String titulo, int icono)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(mensaje);
        builder1.setIcon(icono);
        builder1.setTitle(titulo);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder1.create();
        return alertDialog;
    }
}
