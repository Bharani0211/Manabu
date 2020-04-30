package com.bharani.manabu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindPeopleActivity extends AppCompatActivity {

    private RecyclerView findFriendsList;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        Toolbar findToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(findToolbar);
        ActionBar ab=getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        search=findViewById(R.id.contact_search);
        findFriendsList=findViewById(R.id.search_contact_list);
        findFriendsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        Button videoCallBtn;
        CircleImageView profile_image;
        CardView cardView;

        public FriendsViewHolder(@NonNull View itemview) {
            super(itemview);
            username = itemview.findViewById(R.id.contact_username);
            videoCallBtn=itemview.findViewById(R.id.request_accept_btn);
            profile_image=itemview.findViewById(R.id.contact_profile);
            cardView=itemview.findViewById(R.id.single_contact_cardView);
        }
    }

}
