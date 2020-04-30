package com.bharani.manabu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private FloatingActionButton findPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.contact_list);
        findPeople=findViewById(R.id.find_people);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if(!isConnected())
        {
            new AlertDialog.Builder(this)
                    .setTitle("Internet connection Alert!")
                    .setMessage("Please check your internet connection")
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }

        NavBarSetup();

        findPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findPeople = new Intent(MainActivity.this,FindPeopleActivity.class);
                startActivity(findPeople);
            }
        });
    }

    private void NavBarSetup() {
        bottomNavigationView = findViewById(R.id.bottonNav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavHelper());
        BottomNavHelper.switchActivities(MainActivity.this,bottomNavigationView);
    }

    private boolean isConnected(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }

}
