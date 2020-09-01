package pe.pucp.analizador.menu;

import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pe.pucp.analizador.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link menuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class menuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public menuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment menuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static menuFragment newInstance(String param1, String param2) {
        menuFragment fragment = new menuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);

    }


    @Override
    public void onStart() {
        super.onStart();
        ActualizaIconoLoggin();
    }

    //region actualizacion de icono segun usuario publico/loggeado
    private void ActualizaIconoLoggin()
    {
        Log.wtf("menuFragment","ActualizaIconoLoggin:"+getActivity().getTitle());
        boolean mUsu=false; //inicializa falso , no autenticado, publico
        //conecta a autenticacion
        FirebaseAuth mAuth;
        FirebaseUser currentUser;
        try{
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            if(currentUser!=null)
            {
                mUsu=true;
            }
        } catch (Exception e){e.printStackTrace();}

        try
        {
            //ActionBar actionBar = getSupportActionBar();
            ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
            //if (actionBar!=null)actionBar.setSubtitle("Total: ");
            if(mUsu==true)
            {
                actionBar.setIcon(R.drawable.ic_user_logged);
                //actionBar.setTitle("logeado");
                actionBar.setDisplayShowHomeEnabled(true);
            }
            else
            {
                actionBar.setIcon(R.drawable.ic_user_public);
                //actionBar.setTitle("publico");
                actionBar.setDisplayShowHomeEnabled(true);
            }

        }catch (Exception e){e.printStackTrace();}


    }
//endregion

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            //onBackPressed();
            Log.wtf("onOptionsItemSelected","home pres");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}