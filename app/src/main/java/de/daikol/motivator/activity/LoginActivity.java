package de.daikol.motivator.activity;

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

import de.daikol.motivator.R;
import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.model.user.User;

public class LoginActivity extends AppCompatActivity {

    /**
     * The tag used for logging.
     */
    private static final String LOG_TAG = "LoginActivity";

    private static final int REQUEST_SIGNUP = 0;

    EditText username;

    EditText password;

    Button loginButton;

    TextView registrationLink;

    Motivator application;

    ProgressDialog loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        application = (Motivator) getApplication();

        username = findViewById(R.id.login_username);
        password = findViewById(R.id.registration_password);
        loginButton = findViewById(R.id.btn_login);
        registrationLink = findViewById(R.id.link_signup);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        registrationLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        final User user = application.getUser();
        if (user != null) {
            this.username.setText(user.getName());
            this.password.setText(user.getPassword());
            login();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final User user = application.getUser();
        if (user != null) {
            this.username.setText(user.getName());
            this.password.setText(user.getPassword());
            login();
        }
    }

    public void login() {
        Log.d(LOG_TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        loader = new ProgressDialog(this);
        loader.setMessage("Authentifiziere...");
        loader.setCancelable(false);
        loader.setInverseBackgroundForced(false);
        loader.show();

        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        Runnable onSuccess = new Runnable() {
            @Override
            public void run() {
                onLoginSuccess();
            }
        };
        Runnable onFailure = new Runnable() {
            @Override
            public void run() {
                onLoginFailed();
            }
        };
        User data = new User();
        data.setName(username);
        data.setPassword(password);

        application.login(data, onSuccess, onFailure);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Intent news = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(news);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        if (loader != null) {
            loader.dismiss();
            loader = null;
        }
        Intent news = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(news);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login fehlgeschlagen", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
        if (loader != null) {
            loader.dismiss();
            loader = null;
        }
    }

    public boolean validate() {
        boolean valid = true;

        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        if (username.isEmpty() || username.length() < 4) {
            this.username.setError("Bitte einen Usernamen mit mindestens 4 Zeichen eingeben!");
            valid = false;
        } else {
            this.username.setError(null);
        }

        if (password.isEmpty() || password.length() < 2 || password.length() > 20) {
            this.password.setError("Das Passwort sollte zwischen 2 und 20 Zeichen besitzen!");
            valid = false;
        } else {
            this.password.setError(null);
        }

        return valid;
    }
}
