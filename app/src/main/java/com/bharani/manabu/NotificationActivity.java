package com.bharani.manabu;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NotificationActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        NavBarSetup();
    }

    private void NavBarSetup() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonNav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_notification);
        BottomNavHelper.switchActivities(NotificationActivity.this,bottomNavigationView);
    }
}
