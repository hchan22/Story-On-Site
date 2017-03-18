package nyc.c4q.helenchan.makinghistory;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.anwarshahriar.calligrapher.Calligrapher;
import nyc.c4q.helenchan.makinghistory.models.Content;
import nyc.c4q.helenchan.makinghistory.usercontentrecyclerview.UserContentAdapter;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Akasha on 3/8/17.
 */

public class UserProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private String TAG = "User Profile Activity: ";

    private ImageView userProfilePhoto;
    private TextView userNameTv;
    private TextView userPhotoCountTv;
    private int numUserPhotos = 0;
    private RelativeLayout userContentLayout;

    private RecyclerView userContentRV;
    private UserContentAdapter userContentAdapter;
    private DatabaseReference photoRef;


    private List<Content> userPhotoList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        photoRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("ContentList");

        setFontType();
        initViews();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Profile");

        userProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissions()) {
                    requestCameraPermissions();
                } else {
                    openCamera();
                }
            }
        });

        String userName = SignInActivity.mUsername;
        userNameTv.setText(userName);

        userContentRV = (RecyclerView) findViewById(R.id.user_profile_recycler_view);
        userContentRV.setLayoutManager((new GridLayoutManager(this, 2)));
        userContentAdapter = new UserContentAdapter();
        userContentRV.setAdapter(userContentAdapter);


        photoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Content userPhotoUrl = dataSnapshot.getValue(Content.class);
                userPhotoList.add(userPhotoUrl);
                userContentAdapter.setUserPhotoContent(userPhotoList);

                numUserPhotos = userPhotoList.size();
                userPhotoCountTv.setText(String.valueOf(numUserPhotos));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Content userPhotoUrl = dataSnapshot.getValue(Content.class);
                userPhotoList.add(userPhotoUrl);
                userContentAdapter.setUserPhotoContent(userPhotoList);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void initViews() {
        userProfilePhoto = (ImageView) findViewById(R.id.user_profile_photo);
        userNameTv = (TextView) findViewById(R.id.user_profile_name);
        userPhotoCountTv = (TextView) findViewById(R.id.user_num_photos);
        userContentLayout = (RelativeLayout) findViewById(R.id.profileContent);
    }

    private void setFontType() {
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "ArimaMadurai-Bold.ttf", true);
        calligrapher.setFont(findViewById(R.id.profileContent), "Raleway-Regular.ttf");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean checkPermissions() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermissions() {
        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA}, Constants.REQUEST_CODE_PROFILEPIC);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
            //put whatever logic you guys would like here!
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQUEST_CODE_PROFILEPIC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
        }
    }
}


