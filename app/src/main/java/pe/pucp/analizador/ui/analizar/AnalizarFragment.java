package pe.pucp.analizador.ui.analizar;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import pe.pucp.analizador.Analizar;
import pe.pucp.analizador.CaraModel;
import pe.pucp.analizador.ObjetoModel;
import pe.pucp.analizador.R;
import pe.pucp.analizador.imagenModel;

public class AnalizarFragment extends Fragment {

    //variable que almacena resultados
    public imagenModel mImgMod;

    ProgressBar progressBar;
    TextView txtprogressBar;
    TextView txtresultado;
    ImageView img_foto;

    private AnalizarViewModel mViewModel;



    //almacenamiento
    private StorageReference mStorageRef;

    //database
    DatabaseReference databaseReference;

    public static AnalizarFragment newInstance() {
        return new AnalizarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.main_fragment, container, false);

        //Log.wtf("fragment analizar","ini");

        //crea objeto
        mImgMod=new imagenModel();


        //recibe parametros
        Analizar miactividad = (Analizar) getActivity();
        mImgMod.RutaLocal=miactividad.getRutaLocal();
        //Log.wtf("fragment analizar mRutaLocal",mRutaLocal);



        progressBar = root.findViewById(R.id.progress_bar);
        txtprogressBar = root.findViewById(R.id.progressBarinsideText);
        txtresultado = root.findViewById(R.id.txt_resultado);
        img_foto = root.findViewById(R.id.img_foto);

        //limpia resultado
        txtresultado.setText("");

        //crea referencia a almacenamiento
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //referencia a database
        databaseReference = FirebaseDatabase.getInstance().getReference("imagenes");

        //analisis de informacion
        //1 solicita almacenamiento de imagen -20%
        //2 solicita analisis de imagen       -40%
        //3 solicita deteccion de rostros     -60%
        //consolidacion de informacion        -80%
        //almacenamiento en base de datos     -100%
        //mostrar resultado
        almacenarImagen(mImgMod);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AnalizarViewModel.class);
        // TODO: Use the ViewModel
    }

    //1 almacena informacion
    private void almacenarImagen(final imagenModel pImgMod)
    {
        //accede al archivo en disco
        File filelocal= new File(pImgMod.RutaLocal);
        Uri file= Uri.fromFile( filelocal );

        Log.wtf("almacenarImagen rutalocal",pImgMod.RutaLocal);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        txtprogressBar.setText("subiendo imagen");

        //inicializa ruta remota
        pImgMod.RutaRemota="";

        try
        {
            //crea referencia del archivo local, en el almacenamiento remoto
            StorageReference riversRef = mStorageRef.child("images/"+filelocal.getName());
            //sube archivo local en el almacenamiento remoto
            riversRef.putFile(file)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double num=taskSnapshot.getBytesTransferred();
                            double den=taskSnapshot.getTotalByteCount();
                            double progress = taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()*100.0;
                            double div=(num/den)*100.0;
                            int porc=(int) div;
                            progressBar.setProgress(porc);
                            //Log.wtf("almacenarImagen etapa","progress "+ progress+" - " +num + " - " +den+ " - " + div  + " - " +porc );
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setProgress(100);
                            txtprogressBar.setText("imagen subida");

                            Task<Uri> downUrl=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            while(!downUrl.isComplete());
                            //almacena ruta remota
                            pImgMod.RutaRemota= downUrl.getResult().toString();
                            Log.i("mRutaRemota:",pImgMod.RutaRemota);
                            //2 solicita analisis de imagen
                            analizaImagen(pImgMod);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle unsuccessful uploads
                            // ...
                            e.printStackTrace();
                        }
                    });
        }
        catch (Exception e){e.printStackTrace();}

    }

    //2 solicita analisis de imagen
    private void analizaImagen(final imagenModel pImgMod)
    {
        progressBar.setProgress(20);
        txtprogressBar.setText("analizando imagen");

        //crea tarea asincrona para manejo de consulta a servicio web REST
        AsyncTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                // All your networking logic
                // should be here
                String texto="";
                int caras=0;

                try {
                    // Create URL
                    String mUrlAnaliza="https://southcentralus.api.cognitive.microsoft.com/vision/v3.0/analyze?visualFeatures=Faces,Description,Objects&language=es";
                    URL azureAnalizaEndpoint = new URL(mUrlAnaliza);
                    // Create connection
                    HttpsURLConnection myConnection = (HttpsURLConnection) azureAnalizaEndpoint.openConnection();
                    //establece metodo a post
                    myConnection.setRequestMethod("POST");

                    //añade request headers
                    myConnection.setRequestProperty("Host", "southcentralus.api.cognitive.microsoft.com");
                    myConnection.setRequestProperty("Content-Type","application/json; utf-8");
                    myConnection.setRequestProperty("Ocp-Apim-Subscription-Key","1663ca29d10548409234be26cc61940a");
                    //solicita que respuesta sea json
                    myConnection.setRequestProperty("Accept", "application/json");


                    //habilita escritura
                    myConnection.setDoOutput(true);

                    // Write the data
                    String jsonInputString = "{\"url\":\""+ pImgMod.RutaRemota+"\" }";
                    try(OutputStream os = myConnection.getOutputStream())
                    {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                    //lee respuestas
                    if (myConnection.getResponseCode() == 200) {
                        // Success
                        // Further processing here
                        progressBar.setProgress(25);

                        //lee respuesta en forma binaria
                        InputStream responseBody = myConnection.getInputStream();
                        //formatea respuesta en formato texto utf-8, usual de REST
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");
                        //formatea respuesta en formato json

                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        Log.wtf("nivel=","response");
                        //obtiene un elemento del json recibido
                        jsonReader.beginObject(); // Start processing the JSON object
                        while (jsonReader.hasNext()) { // Loop through all keys
                            String key = jsonReader.nextName(); // Fetch the next key
                            Log.wtf("nivel=","key " + key);
                            if(key.equals("description"))
                            {
                                jsonReader.beginObject();
                                while(jsonReader.hasNext())
                                {
                                    String key1 = jsonReader.nextName(); // Fetch the next key
                                    Log.wtf("nivel=","key1 " + key1);
                                    if(key1.equals("captions"))
                                    {
                                        jsonReader.beginArray();
                                        while (jsonReader.hasNext())
                                        {
                                            jsonReader.beginObject();
                                            while (jsonReader.hasNext())
                                            {
                                                String key2 = jsonReader.nextName(); // Fetch the next key
                                                Log.wtf("array key ",key2);
                                                if(key2.equals("text"))
                                                {
                                                    String texto1=jsonReader.nextString();
                                                    //Log.wtf("texto1=",texto1);
                                                    texto = texto + texto1;
                                                }
                                                else
                                                    {
                                                        jsonReader.skipValue();
                                                    }
                                            }
                                            jsonReader.endObject();
                                        }
                                        jsonReader.endArray();
                                    }
                                    else
                                        {
                                            jsonReader.skipValue();
                                        }
                                }
                                jsonReader.endObject();
                            }
                            else if(key.equals("faces"))
                            {
                                int f=0;
                                jsonReader.beginArray();
                                while (jsonReader.hasNext())
                                {
                                    f=f+1;
                                    jsonReader.skipValue();
                                }
                                jsonReader.endArray();
                                caras=f;
                            }
                            else if(key.equals("objects"))
                            {
                                int f=0;
                                jsonReader.beginArray();
                                while (jsonReader.hasNext())
                                {
                                    f=f+1;
                                    ObjetoModel mObjMod= new ObjetoModel("");

                                    jsonReader.beginObject();
                                    while (jsonReader.hasNext())
                                    {
                                        String key1=jsonReader.nextName();
                                        Log.wtf("objects key:",key1);

                                        switch (key1)
                                        {
                                            case"rectangle":
                                                jsonReader.beginObject();
                                                while (jsonReader.hasNext())
                                                {
                                                    String key2 = jsonReader.nextName();
                                                    Log.wtf("objecto rectangle ",key2);
                                                    //lee valor
                                                    int v = jsonReader.nextInt();
                                                    Log.wtf("objecto rectangle pos",String.valueOf(v));
                                                    //asigna valor
                                                    switch (key2)
                                                    {
                                                        case "y":
                                                            mObjMod.top=v;
                                                            break;
                                                        case "x":
                                                            mObjMod.left=v;
                                                            break;
                                                        case "w":
                                                            mObjMod.width=v;
                                                            break;
                                                        case "h":
                                                            mObjMod.height=v;
                                                            break;
                                                    }
                                                }
                                                jsonReader.endObject();
                                                break;
                                            case"object":
                                                mObjMod.name=jsonReader.nextString();
                                                break;
                                            default:
                                                jsonReader.skipValue();
                                        }

                                    }

                                    jsonReader.endObject();

                                    pImgMod.Objetos.add(mObjMod);
                                }
                                jsonReader.endArray();

                                pImgMod.numObjetos=f;
                            }
                            else
                                {
                                    jsonReader.skipValue();
                                }
                        }
                        jsonReader.endObject();
                        //cierra json reader
                        jsonReader.close();
                        //cierra conexion a servicio web
                        myConnection.disconnect();


                    } else {
                        // Error handling code goes here
                        Log.e("error response code:", String.valueOf( myConnection.getResponseCode() ) );
                    }

                    Log.wtf("texto=",texto);
                    Log.wtf("caras=",String.valueOf(caras) );
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                //devuelve valores
                pImgMod.numCaras=caras;
                pImgMod.Descripcion=texto;

                //siguiente etapa
                rostrosImagen(pImgMod);

            }
        });


    }

    //3 solicita deteccion de rostros
    private void rostrosImagen(final imagenModel pImgMod)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(40);
                txtprogressBar.setText("detectando rostros");
            }
        });

        Log.wtf("rostros imagen",String.valueOf(pImgMod.numCaras) );

        //procesa si se detectaron caras
        if(pImgMod.numCaras>0)
        {

            //crea tarea asincrona para manejo de consulta a servicio web REST
            AsyncTask.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    // All your networking logic
                    // should be here

                    try {
                        // Create URL
                        String mUrlAnaliza="https://southcentralus.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes=age,gender,smile,glasses,emotion&recognitionModel=recognition_03&returnRecognitionModel=false&detectionModel=detection_01";
                        URL azureAnalizaEndpoint = new URL(mUrlAnaliza);
                        // Create connection
                        HttpsURLConnection myConnection = (HttpsURLConnection) azureAnalizaEndpoint.openConnection();
                        //establece metodo a post
                        myConnection.setRequestMethod("POST");

                        //añade request headers
                        myConnection.setRequestProperty("Host", "southcentralus.api.cognitive.microsoft.com");
                        myConnection.setRequestProperty("Content-Type","application/json; utf-8");
                        myConnection.setRequestProperty("Ocp-Apim-Subscription-Key","ae0a78a5c59b44138e23ac5f8d29e7d6");
                        //solicita que respuesta sea json
                        myConnection.setRequestProperty("Accept", "application/json");


                        //habilita escritura
                        myConnection.setDoOutput(true);

                        // Write the data
                        String jsonInputString = "{\"url\":\""+ pImgMod.RutaRemota+"\" }";
                        try(OutputStream os = myConnection.getOutputStream())
                        {
                            byte[] input = jsonInputString.getBytes("utf-8");
                            os.write(input, 0, input.length);
                        }
                        //lee respuestas
                        if (myConnection.getResponseCode() == 200) {
                            // Success
                            // Further processing here
                            progressBar.setProgress(25);

                            //lee respuesta en forma binaria
                            InputStream responseBody = myConnection.getInputStream();
                            //formatea respuesta en formato texto utf-8, usual de REST
                            InputStreamReader responseBodyReader =
                                    new InputStreamReader(responseBody, "UTF-8");
                            //formatea respuesta en formato json

                            JsonReader jsonReader = new JsonReader(responseBodyReader);
                            Log.wtf("nivel=","response");
                            //obtiene un elemento del json recibido
                            jsonReader.beginArray(); // Start processing the JSON object
                            while (jsonReader.hasNext()) { // Loop through all objects
                                //crea objeto
                                CaraModel mCarMod = new CaraModel( 0,"");

                                jsonReader.beginObject();
                                while (jsonReader.hasNext())
                                {
                                    String key = jsonReader.nextName();
                                    Log.wtf("cara",key);
                                    if(key.equals("faceRectangle"))
                                    {
                                        jsonReader.beginObject();
                                        while (jsonReader.hasNext())
                                        {
                                            String key1 = jsonReader.nextName();
                                            Log.wtf("cara rectangle ",key1);
                                            //lee valor
                                            int v = jsonReader.nextInt();
                                            //asigna valor
                                            switch (key1)
                                            {
                                                case "top":
                                                    mCarMod.top=v;
                                                    break;
                                                case "left":
                                                    mCarMod.left=v;
                                                    break;
                                                case "width":
                                                    mCarMod.width=v;
                                                    break;
                                                case "height":
                                                    mCarMod.height=v;
                                                    break;
                                            }
                                        }
                                        jsonReader.endObject();
                                    }
                                    else if(key.equals("faceAttributes"))
                                    {
                                        double d;
                                        double d1;
                                        jsonReader.beginObject();
                                        while (jsonReader.hasNext())
                                        {
                                            String key1 = jsonReader.nextName();
                                            Log.wtf("cara atributes ",key1);

                                            //asigna valor
                                            switch (key1)
                                            {
                                                case "smile":
                                                    d=jsonReader.nextDouble();
                                                    mCarMod.smile="No sonrie";
                                                    if(d>=0.80){mCarMod.smile="Sonrie";}
                                                    break;
                                                case "glasses":
                                                    mCarMod.glasses=jsonReader.nextString();
                                                    break;
                                                case "emotion":
                                                    mCarMod.emotion="";
                                                    d=0;
                                                    jsonReader.beginObject();
                                                    while (jsonReader.hasNext())
                                                    {
                                                        String key2=jsonReader.nextName();
                                                        Log.wtf("cara atributes emotion",key2);

                                                        d1=jsonReader.nextDouble();
                                                        //evalua maximo
                                                        if(d1>=d)
                                                        {
                                                            d=d1;
                                                            mCarMod.emotion=key2;
                                                        }
                                                    }
                                                    jsonReader.endObject();
                                                    break;
                                                case "age":
                                                    mCarMod.age=jsonReader.nextDouble();
                                                    break;
                                                case "gender":
                                                    mCarMod.gender=jsonReader.nextString();
                                                    break;
                                                default:
                                                    jsonReader.skipValue();
                                                    break;
                                            }
                                        }
                                        jsonReader.endObject();
                                    }
                                    else
                                    {
                                        jsonReader.skipValue();
                                    }

                                }
                                jsonReader.endObject();

                                //adiciona a resultado
                                pImgMod.Caras.add(mCarMod);
                            }
                            jsonReader.endArray();
                            //cierra json reader
                            jsonReader.close();
                            //cierra conexion a servicio web
                            myConnection.disconnect();


                        } else {
                            // Error handling code goes here
                            Log.e("error response code:", String.valueOf( myConnection.getResponseCode() ) );
                        }


                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }


                    //siguiente etapa
                    consolidaInformacion(pImgMod);
                }
            });

        }
        else
        {
            //siguiente etapa
            consolidaInformacion(pImgMod);
        }


    }

    //consolidacion de informacion
    private void consolidaInformacion(final imagenModel pImgMod)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(60);
                txtprogressBar.setText("consolidando informacion");
            }
        });

        //con el nuevo modelo de datos la informacion ya esta consolidada
        //calcula nombre de archivo local
        String s= pImgMod.RutaLocal;
        File f = new File(s);
        s=f.getName();
        pImgMod.Archivo=s;

        Log.wtf("consolida informacion","1");

        //siguiente etapa
        almacenaInformación(pImgMod);
    }

    //almacenamiento en base de datos
    private void almacenaInformación(final imagenModel pImgMod)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(80);
                txtprogressBar.setText("almacenando informacion");
            }
        });


        Log.wtf("almacena informacion","1");

        //almacena
        databaseReference.child(pImgMod.Nombre).setValue(pImgMod);


        //siguiente etapa
        mostrarResultado(pImgMod);
    }

    //mostrar resultado
    private void mostrarResultado(final imagenModel pImgMod)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(100);
                txtprogressBar.setText("resultados");

                Bitmap mImg = BitmapFactory.decodeFile(pImgMod.RutaLocal);
                //And show the result in the image view.
                if(mImg!=null){

                    //adecua tamaño de imagen seleccionada a vista
                    //datos de imagen original
                    int currentBitmapWidth = mImg.getWidth();
                    int currentBitmapHeight = mImg.getHeight();
                    //datos de vista
                    int ivWidth = img_foto.getWidth();
                    int ivHeight = img_foto.getHeight();
                    //String msg=" ("+String.valueOf(currentBitmapWidth)+","+String.valueOf(currentBitmapHeight)+")   ,    ("+String.valueOf(ivWidth)+","+String.valueOf(ivHeight)+")";
                    //Log.wtf("imagenorigendestino",msg);
                    //escalamiento
                    int newWidth = ivWidth;
                    int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));
                    Bitmap newbitMap = Bitmap.createScaledBitmap(mImg, newWidth, newHeight, true);
                    //muestra en la vista
                    img_foto.setImageBitmap(newbitMap);
                }

                txtresultado.setText( pImgMod.toString()  );
            }
        });


        Log.wtf("mostrando resultado","1");
        Log.wtf("resultado = ",pImgMod.toString());
    }

}