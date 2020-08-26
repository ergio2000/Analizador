package pe.pucp.analizador;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    //soporte de menu superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// You should always call super.onCreateOptionsMenu()
// to ensure this call is also dispatched to Fragments
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnuprincipal, menu);
        return true;
    }
}