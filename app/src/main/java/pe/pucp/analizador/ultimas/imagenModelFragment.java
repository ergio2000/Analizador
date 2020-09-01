package pe.pucp.analizador.ultimas;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pe.pucp.analizador.R;
import pe.pucp.analizador.objetos.imagenModel;

/**
 * A fragment representing a list of Items.
 */
public class imagenModelFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public imagenModelFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static imagenModelFragment newInstance(int columnCount) {
        imagenModelFragment fragment = new imagenModelFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imagenmodel_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            //obtiene usuario logueado
            String mUsuarioId=getString(R.string.firebaseRTDB_publico); //usuario publico
            FirebaseAuth mAuth;
            FirebaseUser currentUser;
            try{
                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                if(currentUser!=null)
                {
                    mUsuarioId  = currentUser.getUid();
                }
            } catch (Exception e){e.printStackTrace();}

            //lista de base de datos
            final List<imagenModel> list = new ArrayList<>();

            //obtiene lista de imagenes
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            String filtroUsuario=getString(R.string.firebaseRTDB_name)+"//"+mUsuarioId;
            final DatabaseReference myRef = database.getReference( filtroUsuario);
            //implementa filtro de imagenes por usuario
            //optimizacion de indice por UsuarioId adicionado en reglas de consola de FireBase
            //ordenamiento por fecha de carga
            //myRef.limitToLast(20).addListenerForSingleValueEvent(
            //myRef.orderByChild("UsuarioId").equalTo(mUsuarioId).limitToLast(20).addListenerForSingleValueEvent(
            //myRef.orderByChild("FechaCarga").equalTo(mUsuarioId).orderByChild("FechaCarga").limitToLast(20).addListenerForSingleValueEvent(
            myRef.orderByChild("FechaCarga").limitToLast(20).addListenerForSingleValueEvent(
                    new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            //Log.wtf("listener :",snapshot.getValue().toString());
                            //System.out.println(snapshot.getValue());

                            try{
                                for (DataSnapshot child: snapshot.getChildren()) {
                                    list.add(child.getValue(imagenModel.class));
                                }
                                //Log.wtf("lista1: ",list.toString() );
                                Log.wtf("lista1: cantidad:", String.valueOf(list.size() ) );
                            }catch (Exception e){e.printStackTrace();}

                            //establece adaptador
                            //Log.wtf("lista2: ",list.toString() );
                            Log.wtf("lista2: cantidad:", String.valueOf(list.size() ) );
                            //recyclerView.setAdapter(new imagenModelRecyclerViewAdapter(DummyContent.ITEMS));
                            recyclerView.setAdapter(new imagenModelRecyclerViewAdapter(list));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    }
            );

        }
        return view;
    }
}