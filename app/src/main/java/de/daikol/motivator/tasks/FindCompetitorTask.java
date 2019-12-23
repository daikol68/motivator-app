package de.daikol.motivator.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import de.daikol.motivator.application.Motivator;
import de.daikol.motivator.listener.OnFindCompetitorComplete;
import de.daikol.motivator.model.user.User;
import de.daikol.motivator.util.ChallengerBadRequestHandler;
import de.daikol.motivator.util.Constants;
import de.daikol.motivator.util.UserList;

public class FindCompetitorTask extends AsyncTask<Void, Void, List<User>> {
    private static final String LOG_TAG = "FindCompetitorTask";

    private final String name;

    private final List<User> users;

    private final Motivator motivator;

    private final OnFindCompetitorComplete listener;

    public FindCompetitorTask(String name, List<User> users, Motivator motivator, OnFindCompetitorComplete listener) {
        this.name = name;
        this.users = users;
        this.motivator = motivator;
        this.listener = listener;
    }

    protected List<User> doInBackground(Void... nothing) {

        // get the user
        if (!motivator.isLoggedIn()) {
            return null;
        }

        try {

            users.clear();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(Motivator.AUTHORIZATION_HEADER, motivator.getAuthorization());
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
