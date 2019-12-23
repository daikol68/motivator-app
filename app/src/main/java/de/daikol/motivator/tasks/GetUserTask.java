package de.daikol.motivator.tasks;

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

import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.listener.OnGetUserComplete;
import de.daikol.motivator.model.user.User;
import de.daikol.motivator.util.ChallengerBadRequestHandler;
import de.daikol.motivator.util.Constants;
import de.daikol.motivator.util.RunnableHolder;

/**
 * This class is used to download a picture asynchronous.
 */
public class GetUserTask extends AsyncTask<RunnableHolder, Void, User> {

    private static final String LOG_TAG = "GetUserTask";

    private final Motivator motivator;

    private RunnableHolder[] runnables;

    private OnGetUserComplete listener;

    /**
     * Constructor.
     */
    public GetUserTask(Motivator motivator, OnGetUserComplete listener) {
        this.motivator = motivator;
        this.listener = listener;
    }

    protected User doInBackground(RunnableHolder... runnables) {

        this.runnables = runnables;

        // get the user
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(Motivator.AUTHORIZATION_HEADER, motivator.getAuthorization());
            HttpEntity<Void> request = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new ChallengerBadRequestHandler());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<User> response = restTemplate.exchange(Constants.Activities.User.get, HttpMethod.GET, request, User.class);
            return response.getBody();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(User user) {
        super.onPostExecute(user);
        if (runnables != null) {
            for (RunnableHolder holder : runnables) {
                if (user != null) {
                    holder.executeSuccess();
                } else {
                    holder.executeFailure();
                }
            }
        }
        listener.onGetUserCompleted(user);
    }
}
