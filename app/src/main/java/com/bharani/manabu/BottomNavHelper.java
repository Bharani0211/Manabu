package com.bharani.manabu;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class BottomNavHelper extends androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior {
    public BottomNavHelper() {
    }

    public BottomNavHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static void switchActivities(final Context context, BottomNavigationView view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.bottom_nav_home:
                        context.startActivity(new Intent(context, MainActivity.class));
                        ((Activity) context).overridePendingTransition(0, 0);
                        break;
                    case R.id.bottom_nav_notification:
                        context.startActivity(new Intent(context, NotificationActivity.class));
                        ((Activity) context).overridePendingTransition(0, 0);
                        break;
                    case R.id.bottom_nav_settings:
                        context.startActivity(new Intent(context, SettingsActivity.class));
                        ((Activity) context).overridePendingTransition(0, 0);
                        break;
                    case R.id.bottom_nav_logout:
                        new AlertDialog.Builder(context)
                                .setTitle("Logout options")
                                .setMessage("Are you sure that you need to logout")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseAuth.getInstance().signOut();
                                        context.startActivity(new Intent(context, RegisterActivity.class));
                                        ((Activity) context).overridePendingTransition(0, 0);
                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                        break;
                }
                return false;
            }
        });
    }
}