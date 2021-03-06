package io.kirikcoders.bitcse.events;

import android.animation.Animator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import io.kirikcoders.bitcse.R;
import io.kirikcoders.bitcse.utils.Constants;
import io.kirikcoders.bitcse.utils.InputCheckUtils;
import io.kirikcoders.bitcse.utils.UserDetails;

public class CreateEventActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 4;
    private EditText eventName;
    private EditText eventDate;
    private EditText eventTime;
    private EditText eventDescription;
    private EditText eventHeadline;
    private EditText eventVenue;
    private EditText eventContactOne;
    private EditText eventContactTwo;
    private EditText eventCost;
    private EditText eventParticipants;
    private ImageView eventBanner;
    private Uri imagePath;
    private Bitmap imageBitmap;
    private Dialog dialog;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference(Constants.EVENT_DATABASE);
    private FirebaseStorage imageStore = FirebaseStorage.getInstance();
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (InputCheckUtils.checkInputs(eventName,eventDate,eventTime,eventDescription,eventHeadline
        ,eventVenue,eventContactOne,eventContactTwo,eventCost,eventParticipants) &&
        eventBanner.getDrawable() != null){
            switch (item.getItemId()){
                case R.id.eventSave:
                    saveDataToFirebase(reference);
                    return true;
            }
        }
        else {
            if (eventBanner.getDrawable() == null)
                Toast.makeText(this, "Image has not been set", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "All input fields are mandatory.", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void saveDataToFirebase(final DatabaseReference reference) {
        final UserDetails user = new UserDetails(getApplicationContext(),Constants.USER_PREFERENCE_FILE);
        dialog = new Dialog(this);
        dialog.setTitle("Saving");
        View animationView = LayoutInflater.from(this).inflate(R.layout.loading_check_green,null);
        ProgressBar progressBar = animationView.findViewById(R.id.progressBar2);
        LottieAnimationView checked = animationView.findViewById(R.id.eventSavedAnimation);
        dialog.setContentView(animationView);
        dialog.show();
        final StorageReference storageReference = imageStore.getReference()
                .child("images/"+eventName.getText().toString());
        storageReference.putFile(imagePath)
        .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            reference.child(eventName.getText().toString()).child("date")
                    .setValue(eventDate.getText().toString());
            reference.child(eventName.getText().toString()).child("participants")
                    .setValue(eventParticipants.getText().toString());
            reference.child(eventName.getText().toString()).child("time")
                    .setValue(eventTime.getText().toString());
            reference.child(eventName.getText().toString()).child("description")
                    .setValue(eventDescription.getText().toString());
            reference.child(eventName.getText().toString()).child("headline")
                    .setValue(eventHeadline.getText().toString());
            reference.child(eventName.getText().toString()).child("venue")
                    .setValue(eventVenue.getText().toString());
            reference.child(eventName.getText().toString()).child("imageUrl")
                    .setValue(uri.toString());
            reference.child(eventName.getText().toString()).child("contactOne")
                    .setValue(eventContactOne.getText().toString());
            reference.child(eventName.getText().toString()).child("owner")
                    .setValue(user.getmUsn());
            reference.child(eventName.getText().toString()).child("contactTwo")
                    .setValue(eventContactTwo.getText().toString());
            reference.child(eventName.getText().toString()).child("cost")
                    .setValue(eventCost.getText().toString());
            progressBar.setVisibility(View.INVISIBLE);
            checked.setVisibility(View.VISIBLE);
            checked.playAnimation();

            checked.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }));
    }

    private String getUploadUrl(StorageReference storageReference) {
        final StringBuffer buffer = new StringBuffer();
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                buffer.append(uri.toString());
            }
        });
        return buffer.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_event,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        SharedPreferences mPrefs = getBaseContext().getSharedPreferences(Constants.USER_PREFERENCE_FILE, Context.MODE_PRIVATE);
        int mode = mPrefs.getInt("MODE",-1);
        getDelegate().setLocalNightMode(mode);
        eventBanner = findViewById(R.id.eventBanner);
        eventName = findViewById(R.id.eventName);
        eventDescription = findViewById(R.id.eventDescription);
        eventHeadline = findViewById(R.id.eventHeadline);
        eventVenue = findViewById(R.id.eventVenue);
        eventCost = findViewById(R.id.eventCost);
        eventParticipants = findViewById(R.id.eventParticipants);
        eventContactOne = findViewById(R.id.eventContactOne);
        eventContactTwo = findViewById(R.id.eventContactTwo);
        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
    }

    public void pickDateTime(View view) {
        switch (view.getId()){
            case R.id.eventDate:
                DatePickerFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getSupportFragmentManager(),"Pick a Date");
                break;
            case R.id.eventTime:
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(),"Pick a Time");
                break;
        }
    }

    public void setDate(int day, int month, int year) {
        eventDate.setText(day+"/"+(month+1)+"/"+year);
    }

    public void setTime(int hour, int minute) {
        StringBuffer s = new StringBuffer();
        String min;
        if (hour >= 12){
            s.append("PM");
            if (hour > 12)
                hour -= 12;
        }
        else
            s.append("AM");
        if (minute < 10)
            min = "0"+Integer.toString(minute);
        else
            min = Integer.toString(minute);
        eventTime.setText(hour+":"+min+" "+s);
    }

    public void loadImage(View view) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select your event banner"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null){
            imagePath = data.getData();
            try{
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
                eventBanner.setImageBitmap(imageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // make sure dialog exists before dismissing it.
        if(dialog != null)
            if (dialog.isShowing())
                dialog.dismiss();
    }
}
