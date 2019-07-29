package de.daikol.acclaim.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import de.daikol.acclaim.application.Challenger;
import de.daikol.acclaim.model.user.User;
import de.daikol.acclaim.util.ChallengerBadRequestHandler;
import de.daikol.acclaim.util.Constants;

public class UpdateUserTask extends AsyncTask<Void, Void, Void> {

    private static final String LOG_TAG = "UpdateUserTask";

    private final User user;

    private final Challenger challenger;

    public UpdateUserTask(User user, Challenger challenger) {
        this.user = user;
        this.challenger = challenger;
    }

    protected Void doInBackground(Void... nothing) {

        if (!challenger.isLoggedIn()) {
            return null;
        }

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(Challenger.AUTHORIZATION_HEADER, challenger.getAuthorization());
            HttpEntity<User> request = new HttpEntity<>(user, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new ChallengerBadRequestHandler());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<Boolean> response = restTemplate.exchange(Constants.Activities.User.update, HttpMethod.POST, request, Boolean.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                Log.w(LOG_TAG, response.getStatusCode().toString());
            }
            return null;

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
