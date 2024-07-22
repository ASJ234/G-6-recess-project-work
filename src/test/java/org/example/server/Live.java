package org.example.server;

import com.itextpdf.text.DocumentException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Live {
    static class Challenge {
        int id;
        String name;
        List<Participant> participants;

        Challenge(int id, String name) {
            this.id = id;
            this.name = name;
            this.participants = new ArrayList<>();
        }
    }

    static class Participant {
        int id;
        String username;
        List<Question> questions;
        String email;

        Participant(int id, String username, String email) {
            this.id = id;
            this.username = username;
            this.questions = new ArrayList<>();
            this.email = email;
        }
    }

    static class Question {
        String question;
        String answer;
        int score;

        Question(String question, String answer, int score) {
            this.question = question;
            this.answer = answer;
            this.score = score;
        }
    }

    public void check() throws SQLException, ClassNotFoundException, DocumentException, IOException, MessagingException {
        DatabaseConnection dbConnection = new DatabaseConnection();

        List<Challenge> challenges = fetchChallenges(dbConnection);
        if (!challenges.isEmpty()) {
            for (Challenge challenge : challenges) {
                fetchParticipants(dbConnection, challenge);

                for (Participant participant : challenge.participants) {
                    fetchQuestions(dbConnection, challenge.id, participant);

                    JSONArray attemptsJson = convertQuestionsToJson(participant.questions);

                    EmailSending email = new EmailSending();
                    email.sendChallengeReportPDF(challenge.name, participant.username, participant.email, attemptsJson);
                }

                dbConnection.create("INSERT INTO `email_challenge_results` (`challenge`, `status`) VALUES (" + challenge.id + ", " + 1 + ")");
            }
        }

    }

    private List<Challenge> fetchChallenges(DatabaseConnection dbConnection) throws SQLException {
        List<Challenge> challenges = new ArrayList<>();
        try (ResultSet rs = dbConnection.read("SELECT * FROM `challenge` WHERE `closing_date` < CURRENT_DATE AND `challenge_id` NOT IN (SELECT `challenge` FROM `email_challenge_results`);")) {
            while (rs.next()) {
                challenges.add(new Challenge(rs.getInt("challenge_id"), rs.getString("challenge_name")));
            }
        }
        return challenges;
    }

    private void fetchParticipants(DatabaseConnection dbConnection, Challenge challenge) throws SQLException {
        String query = "SELECT DISTINCT " +
                "p.id AS participant_id, " +
                "FIRST_VALUE(a.id) OVER (PARTITION BY p.id ORDER BY a.id) AS attempt_id, " +
                "p.username, " +
                "p.email_address " +
                "FROM `attempts` a " +
                "JOIN `participants` p ON a.participant = p.id " +
                "WHERE a.challenge = " + challenge.id + " " +
                "GROUP BY p.id;";

        ResultSet participants = dbConnection.read(query);
        while (participants.next()) {
            challenge.participants.add(new Participant(participants.getInt("participant_id"), participants.getString("username"), participants.getString("email_address")));
        }

    }

    private void fetchQuestions(DatabaseConnection dbConnection, int challengeId, Participant participant) throws SQLException {
        String query = "SELECT question, answer, score FROM question_answers WHERE id IN " +
                "(SELECT question FROM `attempts` WHERE challenge = " + challengeId +
                " and participant = " + participant.id + ");";

        ResultSet questions = dbConnection.read(query);

        while (questions.next()) {
            participant.questions.add(new Question(
                    questions.getString("question"),
                    questions.getString("answer"),
                    questions.getInt("score")
            ));

        }

    }

    private JSONArray convertQuestionsToJson(List<Question> questions) {
        JSONArray attemptsJson = new JSONArray();
        for (Question q : questions) {
            JSONObject obj = new JSONObject();
            obj.put("question", q.question);
            obj.put("answer", q.answer);
            obj.put("score", q.score);
            attemptsJson.put(obj);
        }
        return attemptsJson;
    }
}
