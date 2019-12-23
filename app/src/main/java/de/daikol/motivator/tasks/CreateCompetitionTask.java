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
import de.daikol.motivator.model.Competition;
import de.daikol.motivator.util.ChallengerBadRequestHandler;
import de.daikol.motivator.util.Constants;

/**
 * This class is used to download a picture asynchronous.
 */
public class CreateCompetitionTask extends AsyncTask<Void, Void, Long> {

    private static final String LOG_TAG = "CreateCompetitionTask";

    private final Competition competition;

    private final Motivator motivator;

    /**
     * Constructor.
     */
    public CreateCompetitionTask(Competition competition, Motivator motivator) {
        this.competition = competition;
        this.motivator = motivator;
    }

    protected Long doInBackground(Void... voids) {

        if (!motivator.isLoggedIn()) {
            return null;
        }

        // get the user
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(Motivator.AUTHORIZATION_HEADER, motivator.getAuthorization());
            HttpEntity<Competition> request = new HttpEntity<>(competition, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new ChallengerBadRequestHandler());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<Long> response = restTemplate.exchange(Constants.Activities.Competition.create, HttpMethod.POST, request, Long.class);
            return response.getBody();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Long competition) {
        super.onPostExecute(competition);
        if (competition != null) {
            this.competition.setId(competition);
        }
    }
}
