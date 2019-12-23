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
import java.util.List;

import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.listener.OnListChallengesComplete;
import de.daikol.motivator.model.Competition;
import de.daikol.motivator.util.ChallengerBadRequestHandler;
import de.daikol.motivator.util.CompetitionList;
import de.daikol.motivator.util.Constants;

public class ListChallengesTask extends AsyncTask<Void, Void, List<Competition>> {

    private static final String LOG_TAG = "ListChallengesTask";

    private final Motivator motivator;

    private OnListChallengesComplete listener;

    public ListChallengesTask(Motivator motivator, OnListChallengesComplete listener) {
        this.motivator = motivator;
        this.listener = listener;
    }

    @Override
    protected List<Competition> doInBackground(Void... voids) {

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

            ResponseEntity<CompetitionList> response = restTemplate.exchange(Constants.Activities.Competition.list, HttpMethod.GET, request, CompetitionList.class);
            return response.getBody();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Competition> competitions) {
        super.onPostExecute(competitions);
        listener.onListChallengesCompleted(competitions);
    }
}
