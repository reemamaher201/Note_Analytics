package com.reema.noteanalytics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CatAdapter.ItemClickListener{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAnalytics mFirebaseAnalytics;
    ArrayList<Category> items;
    CatAdapter adapter;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    RecyclerView rycCat;
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR);
    int minute = calendar.get(Calendar.MINUTE);
    int second = calendar.get(Calendar.SECOND);
    CardView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        card = findViewById(R.id.card);
        screenTrack("Categories Screen");




        rycCat = findViewById(R.id.rycCat);
        items = new ArrayList<Category>();
        adapter = new CatAdapter(this, items,this);

//        card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
        GetAllCat();
    }


    private void GetAllCat() {

        db.collection("Categories").get()
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
                                    String name = documentSnapshot.getString("Category");
                                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                                    Category cat = new Category(id,name);
                                    intent.putExtra("Category", cat);
                                    items.add(cat);
                                    rycCat.setLayoutManager(layoutManager);
                                    rycCat.setHasFixedSize(true);
                                    rycCat.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    Log.e("LogDATA", items.toString());
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
    public void cardEvent(String id,String name,String content){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,content);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }
    public void screenTrack(String screenName){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Main Activity");
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
        screens.put("name","Categories Screen");
        screens.put("hours",h);
        screens.put("minute",m);
        screens.put("seconds",s);

        db.collection("Categories Screen").add(screens)
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
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        Category cat = new Category(id, items.get(position).id.toString());
        intent.putExtra("Category", cat);
        cardEvent(id,"Category Button",items.get(position).name);

        startActivity(intent);
    }


}