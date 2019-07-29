package de.daikol.acclaim.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import de.daikol.acclaim.R;
import de.daikol.acclaim.application.Challenger;
import de.daikol.acclaim.model.user.User;
import de.daikol.acclaim.util.Constants;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";

    EditText username;

    EditText password;

    EditText email;

    Button registrationButton;

    TextView loginLink;

    Challenger application;

    ProgressDialog loader;

    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        application = (Challenger) getApplication();

        username = findViewById(R.id.registration_name);
        email = findViewById(R.id.registration_email);
        password = findViewById(R.id.registration_password);

        registrationButton = findViewById(R.id.btn_signup);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration();
            }
        });

        loginLink = findViewById(R.id.link_login);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void registration() {
        Log.d(TAG, "Registration");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        registrationButton.setEnabled(false);

        loader = new ProgressDialog(this);
        loader.setMessage("Registriere...");
        loader.setCancelable(false);
        loader.setInverseBackgroundForced(false);
        loader.show();

        String name = username.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        user = new User();
        user.setName(name);
        user.setPassword(userPassword);
        user.setEmail(userEmail);

        final Runnable onSuccess = new Runnable() {
            @Override
            public void run() {
                onSignupSuccess();
            }
        };
        final Runnable onFailure = new Runnable() {
            @Override
            public void run() {
                onSignupFailed();
            }
        };
        application.registrateUser(user, onSuccess, onFailure);
    }


    public void onSignupSuccess() {
        registrationButton.setEnabled(true);
        Intent result = new Intent();
        result.putExtra(Constants.SerializeableKeys.USER, user);
        setResult(RESULT_OK, null);
        if (loader != null) {
            loader.dismiss();
            loader = null;
        }
        finish();
    }

    public void onSignupFailed() {
        if (loader != null) {
            loader.dismiss();
            loader = null;
        }
        registrationButton.setEnabled(true);
        Toast.makeText(getBaseContext(), "Registrierung fehlgeschlagen!", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        String name = username.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (userPassword.isEmpty() || userPassword.length() < 2 || userPassword.length() > 20) {
            password.setError("Das Passwort sollte zwischen 2 und 20 Zeichen besitzen!");
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

}
