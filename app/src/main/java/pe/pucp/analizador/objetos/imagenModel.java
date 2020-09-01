package pe.pucp.analizador.objetos;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pe.pucp.analizador.objetos.CaraModel;
import pe.pucp.analizador.objetos.ObjetoModel;

public class imagenModel {
    //propiedades
    public String UsuarioId;
    public String Nombre; //uuid
    public String RutaLocal;
    public String RutaRemota;
    public String Archivo;

    public int numCaras;
    public List<CaraModel> Caras;

    public int numObjetos;
    public List<ObjetoModel> Objetos;

    public String Descripcion;

    public String FechaCarga;//auxiliar que almacena la fecha de carga para ordenar imagenes

    //constructor
    public imagenModel()
    {
        UsuarioId="";//identificador de usuario
        Nombre= UUID.randomUUID().toString();//identificadr de imagen
        RutaLocal="";
        RutaRemota="";
        Archivo="";
        numCaras=0;
        Caras= new ArrayList<>();
        numObjetos=0;
        Objetos= new ArrayList<>();
        Descripcion="";
        FechaCarga="";
    }

    //metodos
    @NotNull
    public String toString()
    {
        //String mr="Nombre:" + Nombre + " - desc:"+ Descripcion + " - caras:"+String.valueOf(numCaras)+Caras.toString() + " - objetos:" + String.valueOf(numObjetos) +  Objetos.toString();
        return "Descripcion:"+ Descripcion + " - caras:"+numCaras+Caras.toString() + " - objetos:" + numObjetos +  Objetos.toString();
    }

    //public String getDescripcion(){return Descripcion;}

    public String Elementos()
    {
        StringBuilder mCar = new StringBuilder();
        for (CaraModel obj : Caras) {
            mCar.append(",").append(obj.Elementos());
        }

        StringBuilder mObj = new StringBuilder();
        for (ObjetoModel obj : Objetos) {
            mObj.append(",").append(obj.Elementos());
        }

        return "Caras:"+numCaras +"(" + mCar.toString() + ") Objetos:" + numObjetos + "(" + mObj + ")";
    }

    //auxiliar de actualizacion de fecha
    public void ActualizaFechaCarga()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date hoy=new Date(); //fecha de hoy
        FechaCarga = formatter.format(hoy);
    }
}
