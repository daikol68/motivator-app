package de.daikol.motivator.util;

/**
 * Created by daikol on 28.12.2016.
 */

public interface Constants {

    String URL = "https://daikol.de/challenger-service/rest";

    interface SerializeableKeys {
        String COMPETITION = "competition";
        String COMPETITION_LISTENER = "competitionListener";
        String COMPETITOR = "competitor";
        String REWARD = "reward";
        String REWARD_BUYABLE = "rewardBuyable";
        String ACHIEVEMENT = "achievement";
        String ACHIEVEMENT_LOCKED = "achievementLocked";
        String PROGRESS = "progress";
        String PROGRESS_ACCEPTABLE = "progressAcceptable";
        String PROGRESS_LISTENER = "progressListener";
        String MESSAGE = "message";
        String POSITION = "position";
        String USER = "user";
    }

    interface Activities {
        interface Auth {
            String login = URL + "/auth" + "/login";
        }

        interface Competition {
            String buy = URL + "/competition" + "/buyReward";
            String create = URL + "/competition" + "/create";
            String apply = URL + "/competition" + "/confirm";
            String decline = URL + "/competition" + "/decline";
            String close = URL + "/competition" + "/close";
            String list = URL + "/competition" + "/list";
            String update = URL + "/competition" + "/update";
        }

        interface Message {
            String read = URL + "/message" + "/read";
            String send = URL + "/message" + "/send";
            String listUnread = URL + "/message" + "/listUnread";
        }

        interface Progress {
            String create = URL + "/progress" + "/create";
            String listOpenProgress = URL + "/progress" + "/listOpenProgress";
            String refuse = URL + "/progress" + "/refuse";
            String apply = URL + "/progress" + "/apply";
        }

        interface Registration {
            String start = URL + "/registration" + "/start";
            String complete = URL + "/registration" + "/complete";
            String resend = URL + "/registration" + "/resend";
        }

        interface User {
            String get = URL + "/user";
            String update = URL + "/user" + "/update";
            String find = URL + "/user" + "/find";
        }
    }

}
