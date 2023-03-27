package com.reema.noteanalytics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity2 extends AppCompatActivity implements NoteAdapter.ItemClickListener{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAnalytics mFirebaseAnalytics;
    ArrayList<Note> item;
    NoteAdapter nAdapter;
    TextView textView;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    RecyclerView rycNote;
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR);
    int minute = calendar.get(Calendar.MINUTE);
    int second = calendar.get(Calendar.SECOND);
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        rycNote = findViewById(R.id.rycNote);
        item = new ArrayList<Note>();
        nAdapter = new NoteAdapter(this, item,this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        screenTrack("Notes Screen");

        Intent intent = getIntent();
        Category cat = (Category) intent.getSerializableExtra("Category");
         id = cat.getName();
//
     textView = findViewById(R.id.textView);
     textView.setVisibility(View.GONE);
//       textView.setText(id);


       GetAllNotes();

    }

    private void GetAllNotes() {
        DocumentReference docRef = db.collection("Categories").document(id);

// Create a reference to the collection inside the document
        CollectionReference postsRef = docRef.collection("Notes");
        postsRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d("tag", "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                if (documentSnapshot.exists()) {
                                    String id = documentSnapshot.getId();
                                    String name = documentSnapshot.getString("Note");
                                    String cat = documentSnapshot.getString("Note");
                                    Intent i = new Intent(MainActivity2.this, MainActivity3.class);

                                    Note note = new Note(id,name,cat);
                                    i.putExtra("Note",note);

                                    item.add(note);
                                    rycNote.setLayoutManager(layoutManager);
                                    rycNote.setHasFixedSize(true);
                                    rycNote.setAdapter(nAdapter);
                                    nAdapter.notifyDataSetChanged();
                                    Log.e("LogDATA", item.toString());
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LogDATA", "get failed with ");


                    }
                });
    }
    public void screenTrack(String screenName){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Main Activity2");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    @Override
    protected void onPause() {

        Calendar calendar = Calendar.getInstance();
        int hour2 = calendar.get(Calendar.HOUR);
        int minute2 = calendar.get(Calendar.MINUTE);
        int second2 = calendar.get(Calendar.SECOND);

        int h = hour2-hour;
        int m = minute2-minute;
        int s = second2-second;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String,Object> screens = new HashMap<>();
        screens.put("name","Notes Screen");
        screens.put("hours",h);
        screens.put("minute",m);
        screens.put("seconds",s);

        db.collection("Notes Screen").add(screens)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        Log.e("Hours",String.valueOf(h));
        Log.e("Minutes",String.valueOf(m));
        Log.e("Seconds",String.valueOf(s));
        super.onPause();
    }
    @Override
    public void onItemClick(int position, String id) {
        Intent i = new Intent(MainActivity2.this, MainActivity3.class);
        Note note = new Note(id, item.get(position).name.toString(),item.get(position).id.toString());
        i.putExtra("Note", note);
        //cardEvent("food@1","Food Button","Button");

        startActivity(i);
    }
}

