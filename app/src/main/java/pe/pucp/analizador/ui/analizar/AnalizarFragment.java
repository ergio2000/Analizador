package pe.pucp.analizador.ui.analizar;

import androidx.lifecycle.ViewModelProviders;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import pe.pucp.analizador.Analizar;
import pe.pucp.analizador.R;

public class AnalizarFragment extends Fragment {

    private boolean mAlmacenar=true;
    //ruta local de imagen
    String mRutaLocal="";
    //ruta rmota de imagen
    String mRutaRemota="";

    ProgressBar progressBar;
    TextView txtprogressBar;

    private AnalizarViewModel mViewModel;

    //almacenamiento
    private StorageReference mStorageRef;

    public static AnalizarFragment newInstance() {
        return new AnalizarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.main_fragment, container, false);

        //Log.wtf("fragment analizar","ini");

        //recibe parametros
        Analizar miactividad = (Analizar) getActivity();
        mRutaLocal=miactividad.getRutaLocal();
        mAlmacenar=miactividad.getAlmacenar();
        //Log.wtf("fragment analizar mRutaLocal",mRutaLocal);

        progressBar = root.findViewById(R.id.progress_bar);
        txtprogressBar = root.findViewById(R.id.progressBarinsideText);


        //crea referencia a almacenamiento
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //solicita almacenamiento de imagen
        if(mAlmacenar==true)
        {
            almacenarImagen(mRutaLocal);
        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AnalizarViewModel.class);
        // TODO: Use the ViewModel
    }

private void almacenarImagen(String pRutaLocal)
    {
        //accede al archivo en disco
        File filelocal= new File(pRutaLocal);
        Uri file= Uri.fromFile( filelocal );

        Log.wtf("almacenarImagen rutalocal",pRutaLocal);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        txtprogressBar.setText("subiendo imagen");



        try
        {
            //Log.wtf("almacenarImagen etapa","1");
            //crea referencia del archivo local, en el almacenamiento remoto
            StorageReference riversRef = mStorageRef.child("images/"+filelocal.getName());
            //Log.wtf("almacenarImagen etapa","2");
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
                            Log.wtf("almacenarImagen etapa","3");
                            progressBar.setProgress(100);
                            txtprogressBar.setText("imagen subida");

                            Task<Uri> downUrl=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            while(!downUrl.isComplete());
                            Log.i("url:",downUrl.getResult().toString());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle unsuccessful uploads
                            // ...
                            Log.wtf("almacenarImagen etapa","4");
                            e.printStackTrace();
                        }
                    });
        }
        catch (Exception e){e.printStackTrace();}

        Log.wtf("almacenarImagen etapa","5");
    }

}