import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class DataAdapter {

    private final String BASE_URL = "http://localhost:8080/api/user";
    private String baseUrl = "http://localhost:8080/api";

    public String getUser(String username) {
        try {
            URL url = new URL(BASE_URL + "/" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            } else {
                return "Failed to get user. HTTP status: " + responseCode;
            }
        } catch (Exception e) {
            return "Failed to get user. Exception: " + e.getMessage();
        }
    }
    
    public static boolean authenticateUser(String username, String password) {
        return Server.authenticateUser(username, password);
    }
    public void storeSession(String userID, String sessionNum, long sessionID) {
        try {
            URL url = new URL(baseUrl + "/sessions/" + userID + "/" + sessionNum);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Session created successfully
                System.out.println("Session created!");

                // Now, you might want to trigger a message creation in MongoDB.
                // Call the modified storeMessage function in the Server class.
                Server server = new Server(); // Assuming you have a constructor that initializes the database
                server.storeMessage(userID, Integer.parseInt(sessionNum), sessionID, "Session created!");
            } else {
                System.out.println("Error creating session. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 // Method to store a message using an HTTP request
    public void storeMessage(String userID, long sessionID, String message, int messageNum) {
        try {
            URL url = new URL(BASE_URL + "/" + sessionID + "/" + messageNum);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // Include the message and userID in the request body
            String requestBody = String.format("{\"message\": \"%s\", \"userID\": \"%s\"}", message, userID);

            // Set content type
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable input/output streams
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Write request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Message stored successfully
                System.out.println("Message stored!");
            } else {
                System.out.println("Error storing message. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Add other methods as needed
}
