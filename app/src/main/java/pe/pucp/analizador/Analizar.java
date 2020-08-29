package pe.pucp.analizador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import pe.pucp.analizador.ui.analizar.AnalizarFragment;

public class Analizar extends AppCompatActivity {

    //propiedades
    private String mRutaLocal="";
    private boolean mAlmacenar=true;


    //constructor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analizar_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, AnalizarFragment.newInstance())
                    .commitNow();
        }

        Intent intent = getIntent ();
        Bundle extras = intent.getExtras();
        mRutaLocal=extras.getString("rutalocal");
        mAlmacenar=extras.getBoolean("almacenar");
        //Log.wtf("Analizar activity mRutaLocal",mRutaLocal);
    }


    //metodos

    //AUXILIAR DE ACCESO A VARIABLE POR FRAGMENTOS HIJOS
    public String getRutaLocal(){return mRutaLocal;}
    public Boolean getAlmacenar(){return mAlmacenar;}

}