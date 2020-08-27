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

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final int PICTURE_TAKEN_FROM_CAMERA = 1;
    private final int PICTURE_TAKEN_FROM_GALLERY = 2;
    private File outFile = null;

    private ImageView pictureZone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pictureZone = (ImageView)findViewById(R.id.ivimgsel);

        //verifica permisos de camara y acceso a disco
        //verificaPermisoCamara();
        verificaPermisos();
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



    public boolean onOptionsItemSelected(MenuItem item) {
        // Find which Menu Item has been selected
        switch (item.getItemId()) {
            // Check for each known Menu Item
            case (R.id.action_tomarfoto):
                Log.wtf("mainactivity","tomarfoto ini");
                startCamera();
                Log.wtf("mainactivity","tomarfoto fin");
                return true;
                // Pass on any unhandled Menu Items to super.onOptionsItemSelected
                // This is required to ensure that the up button and Fragment Menu Items
                // are dispatched properly.
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void startCamera()
    {

            Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //We prepare the intent to store the taken picture
            try{
                //File outputDir = ToolBox.storage_getExternalPublicFolder(Environment.DIRECTORY_PICTURES, "testApp", true);
                File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                //outFile = ToolBox.storage_createUniqueFileName("cameraPic", ".jpg", outputDir);
                outFile= new File(outputDir, "cameraPic.jpg");
                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));

            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                Log.wtf("startcamera2","inicio activity");
                startActivityForResult(takePicIntent, PICTURE_TAKEN_FROM_CAMERA);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.wtf("onActivityResult","inicio");
        Bitmap takenPictureData = null;

        switch(requestCode){

            case PICTURE_TAKEN_FROM_CAMERA:
                if(resultCode==Activity.RESULT_OK) {
                    takenPictureData = handleResultFromCamera(data);
                }
                break;
            case PICTURE_TAKEN_FROM_GALLERY:
                if(resultCode==Activity.RESULT_OK) {
                    //todo
                    // takenPictureData = handleResultFromChooser(data);
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
            String msg=" ("+String.valueOf(currentBitmapWidth)+","+String.valueOf(currentBitmapHeight)+")   ,    ("+String.valueOf(ivWidth)+","+String.valueOf(ivHeight)+")";
            Log.wtf("imagenorigendestino",msg);
            //escalamiento
            int newWidth = ivWidth;
            int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));
            Bitmap newbitMap = Bitmap.createScaledBitmap(takenPictureData, newWidth, newHeight, true);
            //muestra en la vista
            pictureZone.setImageBitmap(newbitMap);
        }
    }

    private Bitmap handleResultFromCamera(Intent data){
        Bitmap takenPictureData = null;
        Log.wtf("handleResultFromCamera","inicio");

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
                //String na= String.valueOf(Uri.fromFile(outFile));
                String na=outFile.getPath();
                Log.wtf("na",na);
                Log.wtf("ruta",outFile.getPath());
                Log.wtf("nombre",outFile.getName());
                takenPictureData = BitmapFactory.decodeFile(na);

            }catch(Exception e){
                e.printStackTrace();
            }
        }


        return takenPictureData;
    }



    private void verificaPermisos()
    {
        int permissionCode=1;
        /*
        String CAMERA_PERMISSION = Manifest.permission.CAMERA;
        String READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE; 
        String WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String[] PERMISSIONS = {CAMERA_PERMISSION, READ_EXTERNAL_STORAGE_PERMISSION, WRITE_EXTERNAL_STORAGE_PERMISSION};
*/
        String[] PERMISSIONS = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, permissionCode);
    }

    private void verificaPermisoCamara()
    {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
            final int MY_CAMERA_REQUEST_CODE = 100;
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.CAMERA },
                    MY_CAMERA_REQUEST_CODE );
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}