package pe.pucp.analizador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN=0;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MaterialButton btn;
        //adiciona cerrar sesion
        btn=  findViewById(R.id.btnlogcerses);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CerrarSesion();
                    }
                }
        );
        //adiciona borrar usuario
        btn=  findViewById(R.id.btnlogborusu);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BorrarUsuario();
                    }
                }
        );

        Log.wtf("login","1 inicio");
        // Initialize Firebase Auth
        try{
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e){e.printStackTrace();}

        //si usuario no esta logueado solicita seleccion
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null)
        {
            Log.wtf("login","2a configura lista de providers");
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()
            );

            Log.wtf("login","3 solicita login");
            // Create and launch sign-in intent
            //adicionar .setIsSmartLockEnabled(false) muestra siempre las opciones de loguin
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(true)
                            .build(),
                    RC_SIGN_IN);
        }
        else
            {
                Log.wtf("login","2b usuario logueado");
            }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.wtf("login","4 respuesta recibida");
        if (requestCode == RC_SIGN_IN) {
            Log.wtf("login","5 loguin");
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Log.wtf("login","6a respuesta correcta");
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                updateUI(user);
                /*
                String mail = user.getEmail();
                String nombre = user.getDisplayName();

                Bundle datos = new Bundle();
                datos.putString("mail", mail);
                datos.putString("nombre", nombre);

                //traslada informacion
                Log.wtf("login:",mail+"-"+nombre);
                */

                //Intent intent = new Intent(this, MainActivity.class);
                //intent.putExtra("datos", datos);
                //startActivity(intent);

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                //Log.e("Auth", response.getError().getMessage());
                Log.wtf("login","6b respuesta incorrecta");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser=null;

        try{
             currentUser = mAuth.getCurrentUser();

        } catch (Exception e){e.printStackTrace();}

        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser pUser)
    {
        String email = "";
        String nombre = "";
        String telefono = "";

        Log.wtf("login updateUI","actualizacion de formulario");

        if(pUser==null)
        {
            Log.wtf("login updateUI","usuario nulo solicitando usuario actual");
            pUser = mAuth.getCurrentUser();
        }

        if(pUser!=null)
        {
            Log.wtf("login updateUI","usuario existente obteniendo informacion");
            try
            {
                email = pUser.getEmail();
                nombre = pUser.getDisplayName();
                telefono=pUser.getPhoneNumber();

            }catch(Exception e){e.printStackTrace();}

        }

        TextView txtUsr = findViewById(R.id.txtLogUsr);
        txtUsr.setText(nombre + " - " + email + " - " + telefono);


    }


    //cerrar sesion
    private void CerrarSesion()
    {
        Log.wtf("login","cerrar sesion");

        try
        {
            //FirebaseAuth.getInstance().signOut();

            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            updateUI(null);
                        }
                    });
        }
        catch (Exception e){e.printStackTrace();}



    }

    private void BorrarUsuario() {
        Log.wtf("login", "ingreso a deleteAccount");

        try
        {
            AuthUI.getInstance()
                    .delete(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Deletion succeeded
                                updateUI(null);
                            } else {
                                // Deletion failed
                                Log.wtf("login", "borrado fallado");
                            }
                        }
                    });

        }catch (Exception e){e.printStackTrace();}

    }

}