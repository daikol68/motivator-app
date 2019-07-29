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
import de.daikol.acclaim.util.ChallengerBadRequestHandler;
import de.daikol.acclaim.util.Constants;

public class ConfirmCompetitionTask extends AsyncTask<Void, Void, Boolean> {

    private static final String LOG_TAG = "ConfirmCompetitionTask";

    private final long competition;

    private final Challenger challenger;

    /**
     * Constructor.
     */
    public ConfirmCompetitionTask(long competition, Challenger challenger) {
        this.competition = competition;
        this.challenger = challenger;
    }

    protected Boolean doInBackground(Void... nothing) {

        // get the user
        if (!challenger.isLoggedIn()) {
            return null;
        }

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(Challenger.AUTHORIZATION_HEADER, challenger.getAuthorization());
            HttpEntity<Void> request = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new ChallengerBadRequestHandler());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<Boolean> response = restTemplate.exchange(Constants.Activities.Competition.apply + "/" + competition, HttpMethod.POST, request, Boolean.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                Log.w(LOG_TAG, response.getStatusCode().toString());
            }

            return response.getBody();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
