package de.daikol.acclaim.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import de.daikol.acclaim.application.Challenger;
import de.daikol.acclaim.listener.OnLoginComplete;
import de.daikol.acclaim.model.user.LoginData;
import de.daikol.acclaim.model.user.User;
import de.daikol.acclaim.util.ChallengerBadRequestHandler;
import de.daikol.acclaim.util.Constants;
import de.daikol.acclaim.util.RunnableHolder;

/**
 * This class is used to download a picture asynchronous.
 */
public class LoginTask extends AsyncTask<RunnableHolder, Void, String> {

    private static final String LOG_TAG = "LoginTask";

    private final User user;

    private RunnableHolder[] runnables;

    private OnLoginComplete listener;

    /**
     * Constructor.
     */
    public LoginTask(User user, OnLoginComplete listener) {
        this.user = user;
        this.listener = listener;
    }

    protected String doInBackground(RunnableHolder... runnables) {

        this.runnables = runnables;

        if (user == null || user.getName() == null || user.getName().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty()) {
            Log.w(LOG_TAG, "username or password is empty.");
            return null;
        }
        // get the user
        try {

            LoginData loginData = new LoginData();
            loginData.setUsername(user.getName());
            loginData.setPassword(user.getPassword());

            HttpEntity<LoginData> request = new HttpEntity<>(loginData);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new ChallengerBadRequestHandler());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<Void> response = restTemplate.exchange(Constants.Activities.Auth.login, HttpMethod.POST, request, Void.class);
            List<String> authorizations = response.getHeaders().get(Challenger.AUTHORIZATION_HEADER);

            if (authorizations == null || authorizations.isEmpty() || authorizations.size() > 1) {
                Log.w(LOG_TAG, "authorizations is null, empty or greater than 1!");
                return null;
            }
            if(response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return null;
            }
            return authorizations.get(0);

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String authorization) {
        super.onPostExecute(authorization);
        if (runnables != null) {
            for (RunnableHolder holder : runnables) {
                if (authorization != null) {
                    holder.executeSuccess();
                } else {
                    holder.executeFailure();
                }
            }
        }
        listener.onLoginCompleted(authorization);
    }
}
