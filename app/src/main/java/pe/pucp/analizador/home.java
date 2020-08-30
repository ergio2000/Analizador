package pe.pucp.analizador;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.io.File;

public class home extends Fragment {
//region MANEJO DE CAMARA
    //codigo de respuesta
    final int PICTURE_TAKEN=1;
//endregion

    //auxiliar para visualizacion de imagen
    private ImageView pictureZone = null;
    //variable que almacena la ruta de la imagen local a visualizar
    private String mRutaLocal="";

    public home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //verifica permisos de camara, y acceso a disco (camara/galeria)
        verificaPermisos();

        //establece listener de boton seleccionar galeria
        Button btn_sel_img = root.findViewById(R.id.btnselimg);
        btn_sel_img.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v) {
                startGaleria();
            }
        });

        //establece referencia a control de imagen
        pictureZone = root.findViewById(R.id.ivimgsel);

        //establece listener de boton analizar
        Button btn_ana_img = root.findViewById(R.id.btnanaimg);
        btn_ana_img.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v) {
                analizarImagen();
            }
        });

        return  root;
    }


//region MANEJO DE GALERIA

    //inicializacion de la galeria y peticion de seleccion de imagen
    private void startGaleria()
    {
        //This takes images directly from gallery
        try
        {
            Intent gallerypickerIntent = new Intent(Intent.ACTION_PICK);
            gallerypickerIntent.setType("image/*");
            startActivityForResult(gallerypickerIntent, PICTURE_TAKEN);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap takenPictureData = null;
        String mRutaLocal="";

        switch(requestCode){

            case PICTURE_TAKEN:
                if(resultCode==Activity.RESULT_OK) {
                    mRutaLocal = handleResultFromChooser(data);
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

    private Bitmap handleResultFromChooserini(Intent data){
        Bitmap takenPictureData = null;

        Uri photoUri = data.getData();
        if (photoUri != null){
            try {

                //We get the file path from the media info returned by the content resolver
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(photoUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                String na=filePath;
                //Log.wtf("na",na);
                takenPictureData = BitmapFactory.decodeFile(na);

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return takenPictureData;
    }

    private String handleResultFromChooser(Intent data){
        //resultado
        String mRutaLocal="";

        Uri photoUri = data.getData();
        if (photoUri != null){
            try {

                //We get the file path from the media info returned by the content resolver
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(photoUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                mRutaLocal=filePath;
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
        ActivityCompat.requestPermissions((Activity) getActivity(), PERMISSIONS, permissionCode);
    }

//endregion


//region ANALIZAR IMAGEN

    public void analizarImagen()
    {
        String mRutaLocal="";
        //obtiene ruta local
        mRutaLocal=(String)pictureZone.getTag();
        //Log.wtf("ruta local",mRutaLocal);
        //abre activity analizar
        Intent myIntent = new Intent(getActivity(), Analizar.class);
        myIntent.putExtra("rutalocal", mRutaLocal); //ruta del archivo local
        startActivity(myIntent);
    }


//endregion


}