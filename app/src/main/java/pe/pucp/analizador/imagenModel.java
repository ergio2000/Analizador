package pe.pucp.analizador;

import java.util.ArrayList;
import java.util.List;

public class imagenModel {
    //propiedades
    public String RutaLocal;
    public String RutaRemota;

    public int numCaras;
    public List<CaraModel> Caras;

    public int numObjetos;
    public List<ObjetoModel> Objetos;

    public String Descripcion;

    //constructor
    public imagenModel()
    {
        RutaLocal="";
        RutaRemota="";
        numCaras=0;
        Caras= new ArrayList<CaraModel>();
        numObjetos=0;
        Objetos= new ArrayList<ObjetoModel>();
        Descripcion="";
    }

    //metodos
    public String toString()
    {
        String mr="desc:"+ Descripcion + " - caras:"+String.valueOf(numCaras)+Caras.toString() + " - objetos:" + String.valueOf(numObjetos) +  Objetos.toString();
        return mr;
    }
}
