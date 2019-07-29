package de.daikol.acclaim.tasks;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import de.daikol.acclaim.activity.CompetitorSearchActivity;
import de.daikol.acclaim.application.Challenger;
import de.daikol.acclaim.listener.OnFindCompetitorComplete;
import de.daikol.acclaim.model.Competitor;
import de.daikol.acclaim.model.user.User;
import de.daikol.acclaim.util.ChallengerBadRequestHandler;
import de.daikol.acclaim.util.Constants;
import de.daikol.acclaim.util.UserList;

public class FindCompetitorTask extends AsyncTask<Void, Void, List<User>> {
    private static final String LOG_TAG = "FindCompetitorTask";

    private final String name;

    private final List<User> users;

    private final Challenger challenger;

    private final OnFindCompetitorComplete listener;

    public FindCompetitorTask(String name, List<User> users, Challenger challenger, OnFindCompetitorComplete listener) {
        this.name = name;
        this.users = users;
        this.challenger = challenger;
        this.listener = listener;
    }

    protected List<User> doInBackground(Void... nothing) {

        // get the user
        if (!challenger.isLoggedIn()) {
            return null;
        }

        try {

            users.clear();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(Challenger.AUTHORIZATION_HEADER, challenger.getAuthorization());
            HttpEntity<Void> request = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new ChallengerBadRequestHandler());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            return restTemplate.exchange(Constants.Activities.User.find + "/" + name, HttpMethod.GET, request, UserList.class).getBody();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<User> users) {
        super.onPostExecute(users);
        listener.onFindCompleted(users);
    }
}
