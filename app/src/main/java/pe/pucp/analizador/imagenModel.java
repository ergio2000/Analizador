package pe.pucp.analizador;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class imagenModel {
    //propiedades
    public String Nombre; //uuid
    public String RutaLocal;
    public String RutaRemota;
    public String Archivo;

    public int numCaras;
    public List<CaraModel> Caras;

    public int numObjetos;
    public List<ObjetoModel> Objetos;

    public String Descripcion;

    //constructor
    public imagenModel()
    {
        Nombre= UUID.randomUUID().toString();
        RutaLocal="";
        RutaRemota="";
        Archivo="";
        numCaras=0;
        Caras= new ArrayList<CaraModel>();
        numObjetos=0;
        Objetos= new ArrayList<ObjetoModel>();
        Descripcion="";
    }

    //metodos
    public String toString()
    {
        //String mr="Nombre:" + Nombre + " - desc:"+ Descripcion + " - caras:"+String.valueOf(numCaras)+Caras.toString() + " - objetos:" + String.valueOf(numObjetos) +  Objetos.toString();
        String mr="Descripcion:"+ Descripcion + " - caras:"+String.valueOf(numCaras)+Caras.toString() + " - objetos:" + String.valueOf(numObjetos) +  Objetos.toString();
        return mr;
    }

    //public String getDescripcion(){return Descripcion;}

    public String Elementos()
    {
        String mCar ="";
        for (CaraModel obj : Caras) {
            mCar=mCar + "," + obj.Elementos();
        }

        String mObj ="";
        for (ObjetoModel obj : Objetos) {
            mObj=mObj + "," + obj.Elementos();
        }

        String mr="Caras:"+String.valueOf(numCaras)+ "(" + mCar + ") Objetos:" + String.valueOf(numObjetos) + "(" + mObj + ")";
        return mr;
    }

}
