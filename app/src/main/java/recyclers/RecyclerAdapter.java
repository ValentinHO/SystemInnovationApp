package recyclers;

/**
 * Created by valen on 12/12/2016.
 */

public class RecyclerAdapter {

    private String tipName;
    private String pasos;
    private String folio;

    public RecyclerAdapter(String folio,String tipName,String pasos) {
        this.tipName = tipName;
        this.pasos = pasos;
        this.folio = folio;
    }

    public String getTipName() {
        return tipName;
    }

    public void setTipName(String tipName) {
        this.tipName = tipName;
    }

    public String getPasos()
    {
        return pasos;
    }

    public void setPasos(String pasos) {
        this.pasos = pasos;
    }

    public String getFolio(){
        return folio;
    }

    public void setFolio(String folio){
        this.folio = folio;
    }
}
