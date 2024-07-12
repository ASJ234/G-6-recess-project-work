package org.example.server;

import org.json.*;

import java.io.*;

public class FileStorage {
    String filePath;

    // Constructor to initialize FileStorage with a specified file path
    public FileStorage(String filePath) throws IOException {
        this.filePath = filePath;
        File file = new File(this.filePath);

        // If the file does not exist, create it and initialize with an empty JSON array
        if (!file.exists()) {
            FileWriter fhandle = new FileWriter(this.filePath);
            fhandle.write(new JSONArray().toString());
            fhandle.flush();
        }
    }

    // Method to add a new JSON object entry to the file
    public void add(JSONObject newEntry) {
        JSONArray jsonArray = this.read(); // Read current contents of the file into a JSONArray
        jsonArray.put(newEntry); // Add the new JSON object to the array

        // Write the updated JSONArray back to the file
        try (FileWriter file = new FileWriter(this.filePath)) {
            file.write(jsonArray.toString(4)); // Write formatted JSON with indentation of 4 spaces
            file.flush(); // Flush to ensure data is written immediately
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to read all JSON objects from the file and return them as a JSONArray
    public JSONArray read() {
        JSONArray jsonArray = new JSONArray();

        try (FileReader reader = new FileReader(this.filePath)) {
            StringBuilder jsonData = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1) {
                jsonData.append((char) i); // Read file character by character into a StringBuilder
            }
            if (jsonData.length() > 0) {
                jsonArray = new JSONArray(jsonData.toString()); // Parse JSON data into JSONArray
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    // Method to retrieve a specific JSON object entry by username
    public JSONObject readEntryByUserName(String username) {
        JSONArray jsonArray = this.read(); // Read all entries from the file into a JSONArray
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("username").equals(username)) {
                return jsonObject; // Return the JSON object if username matches
            }
        }
        return null; // Return null if no matching username is found
    }

    // Method to filter JSON object entries by registration number and return as a JSON string
    public String filterParticipantsByRegNo(String regNo) {
        JSONArray jsonArray = this.read(); // Read all entries from the file into a JSONArray
        JSONArray output = new JSONArray(); // Initialize an empty JSONArray to store filtered entries
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("regNo").equals(regNo)) {
                output.put(jsonObject); // Add matching entries to the output JSONArray
            }
        }
        return output.toString(); // Return the filtered entries as a JSON string
    }

    // Method to delete a specific JSON object entry by username
    public void deleteEntryByUserName(String username) {
        JSONArray jsonArray = this.read(); // Read all entries from the file into a JSONArray
        JSONArray updatedArray = new JSONArray(); // Initialize an empty JSONArray for updated entries

        // Copy entries except the one with the specified username to the updated JSONArray
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (!jsonObject.getString("username").equals(username)) {
                updatedArray.put(jsonObject);
            }
        }

        // Write the updated JSONArray back to the file
        try (FileWriter file = new FileWriter(this.filePath)) {
            file.write(updatedArray.toString(4)); // Write formatted JSON with indentation of 4 spaces
            file.flush(); // Flush to ensure data is written immediately
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Private method to delete the file entirely (not used in public API)
    private void delete() {
        File file = new File(this.filePath);
        file.delete(); // Delete the file from the filesystem
    }
}
