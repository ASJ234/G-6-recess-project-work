package org.example.client;

public class User {
    // Attributes of the User class
    String username;
    String email;
    String registration_number;
    int id; // Assuming id is used elsewhere
    String schoolName;
    boolean isAuthenticated = false; // Initially not authenticated
    boolean isStudent = true; // Default to true assuming most users are students
    String output; // Output message for user actions

    // Method to log out the user by resetting relevant attributes
    public void logout() {
        this.username = ""; // Reset username
        this.email = ""; // Reset email
        this.registration_number = ""; // Reset registration number
        this.isAuthenticated = false; // Set authentication status to false
    }

    // Method to log in the user with provided details
    public void login(String username, String email, String registration_number, String schoolName, boolean isStudent) {
        this.username = username; // Set username
        this.email = email; // Set email
        this.registration_number = registration_number; // Set registration number
        this.schoolName = schoolName; // Set school name
        this.isStudent = isStudent; // Set user type (student or not)
        this.isAuthenticated = true; // Set authentication status to true
    }
}
