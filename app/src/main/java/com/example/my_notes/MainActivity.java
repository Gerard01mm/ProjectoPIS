package com.example.my_notes;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_notes.RecyclerView_adapters.FoldersAdapter;
import com.example.my_notes.ui.foldershome.FolderViewModel;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Notes.NoteFolder;

public class MainActivity extends AppCompatActivity{

    private String name, email;
    private AppBarConfiguration mAppBarConfiguration;


    private RecyclerView fRecyclerView;
    private Context parentContext;
    private AppCompatActivity fActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_notes, R.id.nav_calendar, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Define RecyclerView elements: 1) Layout Manager and 2) Adapter

        getFromSettingsActivity();
        changeNavHeaderData(navigationView);
    }
    /*Metode que canvia el nom actual i el correu de la 'nav_header_main.xml', és a dir, del menú
    * amb les dades obtingudes a l'hora de fer login*/
    private void changeNavHeaderData(NavigationView navigationView) {
        View header = navigationView.getHeaderView(0);
        TextView nameUser = (TextView) header.findViewById(R.id.nameMenu);
        nameUser.setText(name);
        TextView emailUser = (TextView) header.findViewById(R.id.emailMenu);
        emailUser.setText(email);
    }

    /*Metode que agafa les dades introduides a l'activity LoginUserActivity que es pasen juntament amb
     * l'intent que fem servir per passar a la MainActivity i que administrem amb el Bundle*/
    private void getFromSettingsActivity() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("usuario");
            email = bundle.getString("email");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}