package de.daikol.acclaim.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Toast;

import de.daikol.acclaim.listener.OnGetUserComplete;
import de.daikol.acclaim.listener.OnLoginComplete;
import de.daikol.acclaim.model.user.User;
import de.daikol.acclaim.persistence.UserHelper;
import de.daikol.acclaim.tasks.GetUserTask;
import de.daikol.acclaim.tasks.LoginTask;
import de.daikol.acclaim.tasks.RegistrationTask;
import de.daikol.acclaim.util.BitmapUtility;
import de.daikol.acclaim.util.RunnableHolder;

/**
 * Created by daikol on 30.12.2016.
 */
public class Challenger extends Application implements OnLoginComplete, OnGetUserComplete {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String PREFERENCES_LOGIN = "loginData";
    public static final String PREFERENCES_LOGIN_AUTHORIZATION = "authorization";
    public static final String PREFERENCES_LOGIN_USERNAME = "username";
    public static final String PREFERENCES_LOGIN_PASSWORD = "password";
    public static final String PREFERENCES_LOGIN_EMAIL = "email";
    public static final String PREFERENCES_LOGIN_ID = "id";

    private UserHelper userHelper;

    private String authorization;

    @Override
    public void onCreate() {

        super.onCreate();

        // set authorization to null
        authorization = null;

        // Instantiates
        userHelper = new UserHelper(getBaseContext());

        User user = userHelper.fetchUser();
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_LOGIN, MODE_PRIVATE);

        if (user == null) {
            if (preferences != null && preferences.getLong(PREFERENCES_LOGIN_ID, 0) != 0) {
                user = new User();
                user.setId(preferences.getLong(PREFERENCES_LOGIN_ID, 0));
                user.setName(preferences.getString(PREFERENCES_LOGIN_USERNAME, "Fehler beim Laden!"));
                user.setEmail(preferences.getString(PREFERENCES_LOGIN_EMAIL, "Fehler beim Laden!"));
                user.setPassword(preferences.getString(PREFERENCES_LOGIN_PASSWORD, "Fehler beim Laden!"));
            }
        }

        if (authorization == null) {
            authorization = preferences.getString(PREFERENCES_LOGIN_AUTHORIZATION, null);
        }

        if (user != null) {
            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), "Herzlich willkommen!", Toast.LENGTH_SHORT);
                }
            };

            Runnable onFailure = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), "Beim Login ist etwas schief gelaufen.", Toast.LENGTH_LONG);
                }
            };

            login(user, onSuccess, onFailure);
        }


    }

    public void registrateUser(final User user, Runnable onSuccess, Runnable onFailure) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("user email must not be null or empty");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("user name must not be null or empty");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("user password must not be null or empty");
        }
        final RegistrationTask registrationTask = new RegistrationTask(user);
        final RunnableHolder holder = new RunnableHolder(onSuccess, onFailure);

        Runnable onSuccessChallenger = new Runnable() {
            @Override
            public void run() {
                userHelper.deleteUser();
                userHelper.persistUser(user);
                login(user, null, null);
            }
        };
        Runnable onFailureChallenger = new Runnable() {
            @Override
            public void run() {
                // nothing to do
            }
        };

        final RunnableHolder holderChallenger = new RunnableHolder(onSuccessChallenger, onFailureChallenger);

        registrationTask.execute(holder, holderChallenger);
    }

    public void login(final User user, Runnable onSuccess, Runnable onFailure) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("user name must not be null or empty");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("user password must not be null or empty");
        }
        final LoginTask loginTask = new LoginTask(user, this);

        Runnable onSuccessChallenger = new Runnable() {

            @Override
            public void run() {
                userHelper.deleteUser();
                userHelper.persistUser(user);
            }
        };
        Runnable onFailureChallenger = new Runnable() {
            @Override
            public void run() {
                userHelper.deleteUser();
            }
        };

        RunnableHolder holderChallenger = new RunnableHolder(onSuccessChallenger, onFailureChallenger);

        if (onSuccess != null && onFailure != null) {
            RunnableHolder holder = new RunnableHolder(onSuccess, onFailure);
            loginTask.execute(holder, holderChallenger);
        } else {
            loginTask.execute(holderChallenger);
        }
    }

    public boolean isLoggedIn() {
        return authorization != null;
    }

    public void logout() {
        authorization = null;
        userHelper.deleteUser();
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_LOGIN, MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public String getAuthorization() {
        return authorization;
    }

    public User getUser() {
        if (isLoggedIn()) {
            return userHelper.fetchUser();
        } else {
            return null;
        }
    }

    public boolean updateUser(User user, Runnable onSuccess, Runnable onFailure) {
        if (isLoggedIn()) {
            userHelper.deleteUser();
            userHelper.persistUser(user);
            if (onSuccess != null) {
                onSuccess.run();
            }
            return true;
        } else {
            if (onFailure != null) {
                onFailure.run();
            }
            return false;
        }
    }

    @Override
    public void onLoginCompleted(String authorization) {
        if (authorization == null) {
            return;
        }
        this.authorization = authorization;
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_LOGIN, MODE_PRIVATE).edit();
        editor.putString(PREFERENCES_LOGIN_AUTHORIZATION, authorization);
        editor.commit();

        new GetUserTask(this, this).execute();
    }

    @Override
    public void onGetUserCompleted(User user) {
        if (user == null) {
            return;
        }

        User saved = getUser();
        if (saved != null) {
            final String password = saved.getPassword();
            user.setPassword(password);
        }

        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_LOGIN, MODE_PRIVATE).edit();
        editor.putLong(PREFERENCES_LOGIN_ID, user.getId());
        editor.putString(PREFERENCES_LOGIN_USERNAME, user.getName());
        editor.putString(PREFERENCES_LOGIN_PASSWORD, user.getPassword());
        editor.putString(PREFERENCES_LOGIN_EMAIL, user.getEmail());
        editor.commit();


        if (user.getPicture() != null) {
            user.setBitmap(BitmapUtility.convertBitmap(user.getPicture()));
        }
        updateUser(user, null, null);
    }
}
