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
import de.daikol.motivator.listener.OnListUnreadComplete;
import de.daikol.motivator.model.Message;
import de.daikol.motivator.util.ChallengerBadRequestHandler;
import de.daikol.motivator.util.Constants;
import de.daikol.motivator.util.MessageList;

public class ListUnreadMessagesTask extends AsyncTask<Void, Void, List<Message>> {

    private static final String LOG_TAG = "ListUnreadMessagesTask";

    private final Motivator motivator;

    private OnListUnreadComplete listener;

    public ListUnreadMessagesTask(Motivator motivator, OnListUnreadComplete listener) {
        this.motivator = motivator;
        this.listener = listener;
    }

    @Override
    protected List<Message> doInBackground(Void... voids) {

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

            ResponseEntity<MessageList> response = restTemplate.exchange(Constants.Activities.Message.listUnread, HttpMethod.GET, request, MessageList.class);
            return response.getBody();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Message> messages) {
        super.onPostExecute(messages);
        listener.onListUnreadCompleted(messages);
    }
}
