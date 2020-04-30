package com.bharani.manabu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        NavBarSetup();

        notificationList=findViewById(R.id.notification_list);
        notificationList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void NavBarSetup() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonNav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_notification);
        BottomNavHelper.switchActivities(NotificationActivity.this,bottomNavigationView);
    }

    public static class NotificatonViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        Button acceptBtn, cancelBtn;
        CircleImageView profile_image;
        CardView cardView;

        public NotificatonViewHolder(@NonNull View itemview) {
            super(itemview);
            username = itemview.findViewById(R.id.findpeople_username);
            acceptBtn=itemview.findViewById(R.id.request_accept_btn);
            cancelBtn=itemview.findViewById(R.id.request_cancel_btn);
            profile_image=itemview.findViewById(R.id.find_people_profile);
            cardView=itemview.findViewById(R.id.single_people_cardView);
        }
    }

}
