package de.daikol.motivator.tasks;

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

import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.model.ProgressStepData;
import de.daikol.motivator.util.ChallengerBadRequestHandler;
import de.daikol.motivator.util.Constants;

public class ConfirmProgressTask extends AsyncTask<Void, Void, Void> {

    private static final String LOG_TAG = "ConfirmProgressTask";

    private final ProgressStepData progress;

    private final Motivator motivator;

    /**
     * Constructor.
     */
    public ConfirmProgressTask(ProgressStepData progress, Motivator motivator) {
        this.progress = progress;
        this.motivator = motivator;
    }

    protected Void doInBackground(Void... nothing) {

        if (!motivator.isLoggedIn()) {
            return null;
        }

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(Motivator.AUTHORIZATION_HEADER, motivator.getAuthorization());
            HttpEntity<ProgressStepData> request = new HttpEntity<>(progress, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new ChallengerBadRequestHandler());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<Void> response = restTemplate.exchange(Constants.Activities.Progress.apply, HttpMethod.POST, request, Void.class);

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
