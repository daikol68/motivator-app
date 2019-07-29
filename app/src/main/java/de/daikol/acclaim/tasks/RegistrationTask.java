package de.daikol.acclaim.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import de.daikol.acclaim.model.user.User;
import de.daikol.acclaim.util.ChallengerBadRequestHandler;
import de.daikol.acclaim.util.Constants;
import de.daikol.acclaim.util.RunnableHolder;

/**
 * This class is used to download a picture asynchronous.
 */
public class RegistrationTask extends AsyncTask<RunnableHolder, Void, Void> {

    private static final String LOG_TAG = "RegistrationTask";

    private final User user;

    private RunnableHolder[] runnables;

    /**
     * Constructor.
     */
    public RegistrationTask(User user) {
        this.user = user;
    }

    protected Void doInBackground(RunnableHolder... runnables) {

        this.runnables = runnables;

        if (user == null) {
            Log.w(LOG_TAG, "user must not be null");
            return null;
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            Log.w(LOG_TAG, "user name must not be null");
            return null;
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            Log.w(LOG_TAG, "user email must not be null");
            return null;
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            Log.w(LOG_TAG, "user password must not be null");
            return null;
        }

        // get the user
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<User> request = new HttpEntity<>(user, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new ChallengerBadRequestHandler());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            restTemplate.exchange(Constants.Activities.Registration.start, HttpMethod.POST, request, Void.class);

            return null;

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        for (RunnableHolder holder : runnables) {
            holder.executeSuccess();
        }
    }
}
