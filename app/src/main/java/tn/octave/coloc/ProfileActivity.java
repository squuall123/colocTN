package tn.octave.coloc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private EditText fullnameField;
    private EditText birthField;
    private EditText genderField;
    private EditText statusField;
    private EditText mailField;
    private EditText phoneField;
    private EditText passwordField;

    private ImageView picture;
    private Image img;

    private SimpleDateFormat formatter;
    private Date date;

    private DatabaseReference mRef;
    //private Toolbar toolbar;
    private ProgressDialog progress;

    private Button validate;
    private Button chooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        fullnameField = (EditText) findViewById(R.id.name);
        birthField = (EditText) findViewById(R.id.birth);
        genderField = (EditText) findViewById(R.id.gender);
        statusField = (EditText) findViewById(R.id.status);
        mailField = (EditText) findViewById(R.id.mail);
        phoneField = (EditText) findViewById(R.id.phone);
        passwordField = (EditText) findViewById(R.id.password);
        validate = (Button) findViewById(R.id.validateProfile);
        chooser = (Button) findViewById(R.id.imageChooser);
        picture = (ImageView) findViewById(R.id.picture);

        mRef = FirebaseDatabase.getInstance().getReference();

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Profile");
        //toolbar.setTitleTextColor(Color.WHITE);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getColoredArrow());
        progress = ProgressDialog.show(ProfileActivity.this, "Loading",
                "Loading data ...", true);
        loadData();


        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });

        chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(ProfileActivity.this)
                        .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                        .folderMode(true) // folder mode (false by default)
                        .toolbarFolderTitle("Folder") // folder selection title
                        .toolbarImageTitle("Tap to select") // image selection title
                        .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
                        .single() // single mode
                        //.multi() // multi mode (default mode)
                        .limit(1) // max images can be selected (99 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                        //.origin(images) // original selected images, used in multi mode
                        //.exclude(images) // exclude anything that in image.getPath()
                        //.excludeFiles(files) // same as exclude but using ArrayList<File>
                        //.theme(R.style.CustomImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                        .enableLog(false) // disabling log
                        .imageLoader(new GrayscaleImageLoder()) // custom image loader, must be serializeable
                        .start();
            }
        });


    }


    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            //List<Image> images = ImagePicker.getImages(data);
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            Log.d("PICTURE",image.getPath());
            Drawable drawablePicture = Drawable.createFromPath(image.getPath());
            picture.setImageDrawable(drawablePicture);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Drawable getColoredArrow() {
        Drawable arrowDrawable = getResources().getDrawable(R.drawable.ic_action_back);
        Drawable wrapped = DrawableCompat.wrap(arrowDrawable);

        if (arrowDrawable != null && wrapped != null) {
            // This should avoid tinting all the arrows
            arrowDrawable.mutate();
            DrawableCompat.setTint(wrapped, Color.WHITE);
        }

        return wrapped;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void loadData(){
        Log.i("DEBUG","loading data");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Personne prs = (Personne) message.obj;
                updateUI(prs);
                Log.i("DEBUG","Received user data");
                progress.dismiss();
            }
        };

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("DEBUG","thread start");
                mRef.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                            if (userSnapshot.getKey().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                //already existing user profile
                                Log.i("DEBUG","found profile");
                                Personne personne = userSnapshot.getValue(Personne.class);
                                Message profileobj = new Message();
                                profileobj.obj = personne;
                                handler.sendMessage(profileobj);


                            }
                            else {
                                //user don't have profile forwarding to create one !
                                Log.i("DEBUG","creating profile");
                                createProfile();
                                progress.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        thread.start();
    }

    private void createProfile(){
        mRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(null);
    }

    private void updateUI(Personne personne){
        fullnameField.setText(personne.getFullName());
        birthField.setText(personne.getBirth());
        genderField.setText(personne.getGender());
        statusField.setText(personne.getStatus());
        mailField.setText(personne.getMail());
        phoneField.setText(personne.getPhone());
        passwordField.setText(personne.getPassword());

        //decoding image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes = baos.toByteArray();
        imageBytes = Base64.decode(personne.getPicture(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        picture.setImageBitmap(decodedImage);


    }

    private void updateUser(){
        Personne personne = new Personne();
        personne.setFullName(fullnameField.getText().toString());

        personne.setBirth(birthField.getText().toString());
        personne.setGender(genderField.getText().toString());
        personne.setStatus(statusField.getText().toString());
        personne.setMail(mailField.getText().toString());
        personne.setPhone(phoneField.getText().toString());
        personne.setPassword(passwordField.getText().toString());

        //ENCODING IMAGE
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap test = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        test.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodededPicture = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        Log.i("PICTURE",encodededPicture);

        personne.setPicture(encodededPicture);
        mRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(personne);
    }
}
