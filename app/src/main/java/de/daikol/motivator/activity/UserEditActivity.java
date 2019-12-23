package de.daikol.motivator.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.daikol.motivator.R;
import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.model.user.User;
import de.daikol.motivator.tasks.ConvertBitmapTask;
import de.daikol.motivator.tasks.UpdateUserTask;
import de.daikol.motivator.util.BitmapUtility;

public class UserEditActivity extends AppCompatActivity {

    private static final String TAG = "UserEditActivity";

    ImageView picture;

    EditText username;

    EditText password;

    EditText email;

    Button saveButton;

    Motivator application;

    ProgressDialog loader;

    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        application = (Motivator) getApplication();
        user = application.getUser();

        username = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        password = findViewById(R.id.user_password);
        picture = findViewById(R.id.user_picture);

        username.setText(user.getName());
        email.setText(user.getEmail());
        password.setText(user.getPassword());

        new ConvertBitmapTask(getApplicationContext(), picture, user).execute();

        saveButton = findViewById(R.id.btn_user_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 0);
            }
        });
    }

    public void save() {

        if (!validate()) {
            onSaveFailed();
            return;
        }

        saveButton.setEnabled(false);

        loader = new ProgressDialog(this);
        loader.setMessage("Speichere...");
        loader.setCancelable(false);
        loader.setInverseBackgroundForced(false);
        loader.show();

        User user = application.getUser();

        String name = username.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        user.setName(name);
        user.setEmail(userEmail);
        user.setPassword(userPassword);

        if (picture.getBackground() != null && picture.getBackground() instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) picture.getBackground()).getBitmap();
            user.setBitmap(bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] userPicture = baos.toByteArray();
            user.setPicture(userPicture);
        }

        final Runnable onSuccess = new Runnable() {
            @Override
            public void run() {
                onSaveSuccess();
            }
        };
        final Runnable onFailure = new Runnable() {
            @Override
            public void run() {
                onSaveFailed();
            }
        };
        application.updateUser(user, onSuccess, onFailure);
        new UpdateUserTask(user, application).execute();
    }

    private void onSaveFailed() {
        Toast.makeText(getApplicationContext(), "Beim speichern ist leider etwas schief gelaufen!", Toast.LENGTH_LONG);
    }

    private void onSaveSuccess() {
        Toast.makeText(getApplicationContext(), "Speichern erfolgreich!", Toast.LENGTH_SHORT);
        finish();
    }

    public boolean validate() {
        boolean valid = true;

        String name = username.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (userPassword.isEmpty() || userPassword.length() < 4 || userPassword.length() > 10) {
            password.setError("Das Passwort sollte zwischen 4 und 10 Zeichen besitzen!");
            valid = false;
        } else {
            password.setError(null);
        }

        if (name.isEmpty() || name.length() < 4) {
            username.setError("Ein Username sollte mindestens 4 Zeichen haben!");
            valid = false;
        } else {
            username.setError(null);
        }

        if (userEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Bitte geben Sie eine gültige E-Mail Adresse ein!");
            valid = false;
        } else {
            email.setError(null);
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            final Bitmap bitmap = BitmapUtility.scaleDownBitmap(selectedImage, BitmapUtility.HEIGHT, this);
            picture.setBackground(new BitmapDrawable(getResources(), bitmap));
        } catch (IOException e) {
            Log.w(TAG, "Catched IOException: " + e.getMessage());
        }
    }

}
