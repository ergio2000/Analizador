package pe.pucp.analizador;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

import pe.pucp.analizador.ultimas.ultimasActivity;

public class MainActivity extends AppCompatActivity {
//region MANEJO DE CAMARA
    //codigo de respuesta
    final int PICTURE_TAKEN=1;
    //archivo de salida para tomar foto
    private File outFile = null;
//endregion

    //auxiliar para visualizacion de imagen
    private ImageView pictureZone = null;
    //ruta de imagen local
    String mRutaLocal="";

    private String mLoginIdentificador="";
    private String mLoginUsuario="";
    private String mLoginEmail="";
    private String mLoginTelefono="";

    //on create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pictureZone = findViewById(R.id.ivimgsel);

        //verifica permisos de camara, y acceso a disco (camara/galeria)
        verificaPermisos();

    }

    //on start
    @Override
    protected void onStart()
    {
        super.onStart();
        //obtiene informacion del usuario
        ObtieneUsuario();
    }

    //soporte de menu superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// You should always call super.onCreateOptionsMenu()
// to ensure this call is also dispatched to Fragments
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnuprincipal, menu );
        return true;
    }


    //manejo de elementos de menu
    public boolean onOptionsItemSelected(MenuItem item) {
        // Find which Menu Item has been selected
        switch (item.getItemId()) {
            // Check for each known Menu Item
            case (R.id.action_tomarfoto):
                //solicita tomar foto
                startCamera();
                return true;
            case(R.id.action_about):
                //acerca de
                acercaDe();
                return true;
            case(R.id.action_login):
                //acerca de
                login();
                return true;
            case(R.id.action_ultimasfotos):
                //acerca de
                ultimas();
                return true;
                // Pass on any unhandled Menu Items to super.onOptionsItemSelected
                // This is required to ensure that the up button and Fragment Menu Items
                // are dispatched properly.
            default: return super.onOptionsItemSelected(item);
        }
    }

    //acerca de
    private void acercaDe()
    {
        try{
            Intent myIntent = new Intent(this, AboutActivity.class);
            startActivity(myIntent);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //login
    private void login()
    {
        try{
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //ultimas fotos
    private void ultimas()
    {
        try{
            Intent myIntent = new Intent(this, ultimasActivity.class);
            startActivity(myIntent);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//region MANEJO DE CAMARA

    //inicializacion de la camara y peticion de captura de foto
    private void startCamera()
    {
        try{
            Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outFile= new File(outputDir, "cameraPic.jpg");
            takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));
            startActivityForResult(takePicIntent, PICTURE_TAKEN);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap takenPictureData = null;

        switch(requestCode) {

            case PICTURE_TAKEN:
                if (resultCode == Activity.RESULT_OK) {
                    mRutaLocal = handleResultFromCamera(data);
                    takenPictureData = BitmapFactory.decodeFile(mRutaLocal);
                }
                break;
        }
        //And show the result in the image view.
        if(takenPictureData!=null){

            //adecua tamaño de imagen seleccionada a vista
            //datos de imagen original
            int currentBitmapWidth = takenPictureData.getWidth();
            int currentBitmapHeight = takenPictureData.getHeight();
            //datos de vista
            int ivWidth = pictureZone.getWidth();
            int ivHeight = pictureZone.getHeight();
            //String msg=" ("+String.valueOf(currentBitmapWidth)+","+String.valueOf(currentBitmapHeight)+")   ,    ("+String.valueOf(ivWidth)+","+String.valueOf(ivHeight)+")";
            //Log.wtf("imagenorigendestino",msg);
            //escalamiento
            int newWidth = ivWidth;
            int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));
            Bitmap newbitMap = Bitmap.createScaledBitmap(takenPictureData, newWidth, newHeight, true);
            //muestra en la vista
            pictureZone.setImageBitmap(newbitMap);
            pictureZone.setTag(mRutaLocal);
        }
    }

    private Bitmap handleResultFromCameraini(Intent data){
        Bitmap takenPictureData = null;
        //Log.wtf("handleResultFromCamera","inicio");

        if(data!=null){
            //Android sets the picture in extra data.
            Bundle extras = data.getExtras();
            if(extras!=null && extras.get("data")!=null){
                takenPictureData = (Bitmap) extras.get("data");
            }
        }else{
            //If we used EXTRA_OUTPUT we do not have the data so we get the image
            //from the output.
            try{
                //takenPictureData = ToolBox.media_getBitmapFromFile(outFile);
                String na=outFile.getPath();
                //Log.wtf("na",na);
                takenPictureData = BitmapFactory.decodeFile(na);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return takenPictureData;
    }

    private String handleResultFromCamera(Intent data){
        String mRutaLocal="";
        //Log.wtf("handleResultFromCamera","inicio");

        if(data==null)
        {
            //If we used EXTRA_OUTPUT we do not have the data so we get the image
            //from the output.
            try{
                //takenPictureData = ToolBox.media_getBitmapFromFile(outFile);
                String na=outFile.getPath();
                //Log.wtf("na",na);
                mRutaLocal=na;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return mRutaLocal;
    }

    //utilitario de verificacion de permisos
    private void verificaPermisos()
    {
        int permissionCode=1;
        String[] PERMISSIONS = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, PERMISSIONS, permissionCode);
    }

//endregion

    private void ObtieneUsuario()
    {

        //borra variables
        mLoginIdentificador="";
        mLoginEmail="";
        mLoginUsuario="";
        mLoginTelefono="";

        //conecta a autenticacion
        FirebaseAuth mAuth;
        FirebaseUser currentUser;
        try{
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            if(currentUser!=null)
            {
                mLoginIdentificador  = currentUser.getUid();
                mLoginEmail  = currentUser.getEmail();
                mLoginUsuario = currentUser.getDisplayName();
                mLoginTelefono = currentUser.getPhoneNumber();
            }
        } catch (Exception e){e.printStackTrace();}
        ActualizarDatosUsuario();
    }

    private void ActualizarDatosUsuario()
    {
        Log.wtf("main activity","(identificador:"+mLoginIdentificador+") , (usuario:"+mLoginUsuario+") , (email:"+mLoginEmail+") , (telefono:"+mLoginTelefono+")\"");
    }


    public void setLoginUsuario(String pU){mLoginUsuario=pU;  }
    public void setLoginEmail(String pE){mLoginEmail=pE;  }
}