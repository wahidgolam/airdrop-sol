package com.zingit.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zingit.user.model.CampusChooser;

import java.util.ArrayList;
import java.util.Comparator;

import club.cred.neopop.PopFrameLayout;

public class ChooseCampus extends AppCompatActivity {

    FirebaseFirestore db;
    LoadingDialog loadingDialog;
    ArrayList<CampusChooser> campusList = new ArrayList<>();
    RecyclerView campusRV;
    CampusAdapter adapter;
    Campus chosenCampus;
    EditText search_campus;
    TextView chooseCampus;
    PopFrameLayout save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_campus);

        db = FirebaseFirestore.getInstance();

        campusRV = findViewById(R.id.campusRV);
        save = findViewById(R.id.login_btn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goAhead(view);
            }
        });

        loadingDialog = new LoadingDialog(ChooseCampus.this, "Fetching all campuses");
        loadingDialog.startLoadingDialog();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
            }
        }, 2000);
        search_campus = findViewById(R.id.search_id);
        adapter = new CampusAdapter(campusList);
        campusRV.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        campusRV.setLayoutManager(new LinearLayoutManager(this));
        campusRV.addItemDecoration(
                new DividerItemDecoration(this, layoutManager.getOrientation()) {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        int position = parent.getChildAdapterPosition(view);
                        // hide the divider for the last child
                        if (position == state.getItemCount() - 1) {
                            outRect.setEmpty();
                        } else {
                            super.getItemOffsets(outRect, view, parent, state);
                        }
                    }
                }
        );
        fetchCampuses();

        search_campus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());

            }

            private void filter(String toString) {
                ArrayList<CampusChooser> filteredItems = new ArrayList<CampusChooser>();
                for (CampusChooser campusChooser : campusList) {
                    if (campusChooser.getCampus().getName().toLowerCase().contains(toString.toLowerCase())) {
                        filteredItems.add(campusChooser);

                    }

                }
                adapter.filterlist(filteredItems);
            }


        });
    }
    public void fetchCampuses(){
        db.collection("campus")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Campus campus = document.toObject(Campus.class);
                                campusList.add(new CampusChooser(campus, false));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Campus Data", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void campusSelected(CampusChooser campusChooser){
        chosenCampus = campusChooser.getCampus();
        int i;
        for(i=0;i<campusList.size();i++)
        {
            if(campusChooser.equals(campusList.get(i)))
            {
                break;
            }
        }
        campusList.get(i).setSelected(true);
        unselectAll(i);
        adapter.notifyDataSetChanged();
    }
    public void unselectAll(int pos){
        for(int i = 0; i< campusList.size();i++){
            if(i!=pos){
                campusList.get(i).setSelected(false);
            }
        }
    }
    public void goAhead(View view){
        if(chosenCampus!=null){
            LoadingDialog loadingDialog2 = new LoadingDialog(ChooseCampus.this, "Updating your choice");
            loadingDialog2.startLoadingDialog();
            //loading
            //update
            StudentUser user = Dataholder.studentUser;
            db.collection("studentUser").document(user.getUserID()).update("campusID", chosenCampus.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Dataholder.studentUser.setCampusID(chosenCampus.getId());
                    loadingDialog2.dismissDialog();
                    Intent intent = new Intent(getApplicationContext(), Homescreen.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else{
            Toast.makeText(this, "Please choose a campus", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}