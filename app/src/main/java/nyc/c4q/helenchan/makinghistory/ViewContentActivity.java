package nyc.c4q.helenchan.makinghistory;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.anwarshahriar.calligrapher.Calligrapher;
import nyc.c4q.helenchan.makinghistory.contentrecyclerview.ViewContentAdapter;
import nyc.c4q.helenchan.makinghistory.models.Content;
import nyc.c4q.helenchan.makinghistory.models.MapPoint;
import nyc.c4q.helenchan.makinghistory.usercontentrecyclerview.RvCenterStart;

/**
 * Created by leighdouglas on 3/6/17.
 */

public class ViewContentActivity extends AppCompatActivity {

    private RecyclerView contentRV;
    private DatabaseReference databaseRefToMappoint;
    private DatabaseReference refToLocationOfPicTaken;
    private double lat;
    private double lng;
    private ViewContentAdapter viewContentAdapter;
    private ProgressDialog mProgressDialog;
    private Animator animator;
    private int shortAnimationLength;
    private String currentKey;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewcontent);
        if(Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#5e454b"));
        }

        contentRV = (RecyclerView) findViewById(R.id.content_recycler_view);
        contentRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        viewContentAdapter = new ViewContentAdapter();
        contentRV.setAdapter(viewContentAdapter);
        contentRV.addItemDecoration(new RvCenterStart(40));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(contentRV);
        lat = getIntent().getDoubleExtra("Latitude", 0);
        lng = getIntent().getDoubleExtra("Longitude", 0);
        Log.d("lat", String.valueOf(lat));
        mProgressDialog = new ProgressDialog(this);

        setFontType();
    }

    private void setFontType() {
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "ArimaMadurai-Bold.ttf", true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(EditContentActivity.PICLATLONG)) {
                String location = extras.getString(EditContentActivity.PICLATLONG);
                refToLocationOfPicTaken = FirebaseDatabase.getInstance().getReference().child("MapPoint").child(location);
                refToLocationOfPicTaken.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        MapPoint mapPoint = dataSnapshot.getValue(MapPoint.class);
                        lat = mapPoint.getLatitude();
                        lng = mapPoint.getLongitude();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        }

        DatabaseReference databaseRefGetFaves = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UserFavorites");
        databaseRefGetFaves.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Content> favesList = new ArrayList<>();
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        System.out.println(ds.getKey());
                        Content currentValue = ds.getValue(Content.class);
                        favesList.add(currentValue);
                    }
                }
                viewContentAdapter.setUserFavorites(favesList);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseRefToMappoint = FirebaseDatabase.getInstance().getReference().child("MapPoint");
        databaseRefToMappoint.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Content> tempList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    MapPoint mp = ds.getValue(MapPoint.class);
                    if (mp.getLatitude() == lat && mp.getLongitude() == lng) {
                        currentKey = ds.getKey();
                        DatabaseReference databaseRefToMappoint2 = FirebaseDatabase.getInstance().getReference().child("MapPoint").child(currentKey).child("ContentList");
                        databaseRefToMappoint2.orderByKey();
                        databaseRefToMappoint2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot s : dataSnapshot.getChildren()){
                                    Content current = s.getValue(Content.class);
                                    System.out.println(current.getUrl());
                                    tempList.add(current);
                                }
                                viewContentAdapter.setMapContent(tempList);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        List<Content> tempList2 = new ArrayList<>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                Intent intent = new Intent(ViewContentActivity.this, SignInActivity.class);
                startActivity(intent);
                return true;
            case R.id.user_profile:
                Intent userProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(userProfileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
