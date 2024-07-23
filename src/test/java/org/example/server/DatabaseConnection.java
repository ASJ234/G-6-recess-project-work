package org.example.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class DatabaseConnection {
    // Database connection parameters
    String url = "jdbc:mysql://localhost:3306/mtc_challenge_comp20";
    String username = "root";
    String password = "";
    Connection connection;
    Statement statement;

    // Constructor to establish database connection
    public DatabaseConnection() throws SQLException, ClassNotFoundException {
        // Load MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Connect to the database
        this.connection = DriverManager.getConnection(this.url, this.username, this.password);
        this.statement = connection.createStatement();
    }

    // Method to execute a create SQL command
    public void create(String sqlCommand) throws SQLException {
        this.statement.execute(sqlCommand);
    }

    // Method to execute a read SQL command and return ResultSet
    public ResultSet read(String sqlCommand) throws SQLException {
        return this.statement.executeQuery(sqlCommand);
    }

    // Method to execute an update SQL command
    public void update(String sqlCommand) throws SQLException {
        this.statement.execute(sqlCommand);
    }

    // Method to execute a delete SQL command
    public void delete(String sqlCommand) throws SQLException {
        this.statement.execute(sqlCommand);
    }

    // Method to close database connection and statement
    public void close() throws SQLException {
        if (this.statement != null) this.statement.close();
        if (this.connection != null) this.connection.close();
    }

    // Method to create a participant entry in the database
    public void createParticipant(String username, String firstname, String lastname, String emailAddress, String dob, String registration_number, String imagePath) throws SQLException {
        String sql = "INSERT INTO `participants` (`username`, `firstname`, `lastname`, `emailAddress`, `dob`, `registration_number`, `imagePath`) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, firstname);
            stmt.setString(3, lastname);
            stmt.setString(4, emailAddress);
            stmt.setString(5, dob);
            stmt.setString(6, registration_number);
            stmt.setString(7, imagePath);
            stmt.executeUpdate();
        }
    }

    // Method to create a rejected participant entry in the database
    public void createParticipantRejected(String username, String firstname, String lastname, String emailAddress, String dob, String registration_number, String imagePath) throws SQLException {
        String sql = "INSERT INTO `rejectedparticipant` (`username`, `firstname`, `lastname`, `emailAddress`, `dob`, `registration_number`, `imagePath`) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, firstname);
            stmt.setString(3, lastname);
            stmt.setString(4, emailAddress);
            stmt.setString(5, dob);
            stmt.setString(6, registration_number);
            stmt.setString(7, imagePath);
            stmt.executeUpdate();
        }
    }

    // Method to retrieve challenges from the database
    public ResultSet getChallenges() throws SQLException {
        String sql = "SELECT * FROM `mtc_challenge_comp20`.`challenges` WHERE `starting_date` <= CURRENT_DATE AND `closing_date` >= CURRENT_DATE;";
        return this.statement.executeQuery(sql);
    }

    // Method to retrieve challenge questions from the database by challenge_id
    public ResultSet getChallengeQuestions(int challenge_id) throws SQLException {
        String sql = "SELECT qar.* FROM `mtc_challenge_comp20`.`question_answer_records` qar "+
                "ORDER BY RAND() " +
                "LIMIT 10";
        PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
       // preparedStatement.setInt(1, challenge_id);
        return preparedStatement.executeQuery();
    }


    // Method to create a challenge attempt entry in the database
    public void createChallengeAttempt(int participantId, int challengeId, int score, int totalScore) throws SQLException {
        String sql = "INSERT INTO participant_challenge_attempts (participant_id, challenge_id, score, total_score) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, participantId);
        pstmt.setInt(2, challengeId);
        pstmt.setInt(3, score);
        pstmt.setInt(4, totalScore);
        pstmt.executeUpdate();
    }



//Method to create Challeng_Attempt table for population
    public void ChallengeAttempt(int participantId, int challengeId, int questionId) throws SQLException {
        String sql = "INSERT INTO attempts (participant_id, challenge_id, question_id) VALUES (?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, participantId);
        pstmt.setInt(2, challengeId);
        pstmt.setInt(3, questionId);
        pstmt.executeUpdate();
    }


    public ResultSet getChallengeDetails(int challengeId) throws SQLException {
        String sql = "SELECT * FROM challenges WHERE challenge_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, challengeId);
        return pstmt.executeQuery();
    }

    // Method to retrieve representative details from the database by regNo
    public ResultSet getRepresentative(String registration_number) throws SQLException {
        String sqlCommand = "SELECT * FROM `schools` WHERE registration_number = " + registration_number + ";";
        return this.statement.executeQuery(sqlCommand);
    }

    public ResultSet getRejectedParticipant(String username, String email, String registrationNumber) throws SQLException {
        String query = "SELECT * FROM rejectedparticipant WHERE username = ? AND emailAddress = ? AND registration_number = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, email);
        pstmt.setString(3, registrationNumber);
        return pstmt.executeQuery();
    }


    public ResultSet getCorrectAnswer(int questionId) throws SQLException {
        String sql = "SELECT score, answer FROM question_answer_records WHERE question_id = ? ";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, questionId);
        return pstmt.executeQuery();
    }


}