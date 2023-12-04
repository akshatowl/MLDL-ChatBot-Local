import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.io.BufferedReader;
import com.sun.net.httpserver.*;
import java.util.concurrent.Executors;
import java.io.OutputStream;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Server {
	private MongoClient mongoClient;
    private MongoDatabase database;
    private final String BASE_URL = "https://mldl-chatbot-default-rtdb.firebaseio.com";
    
    public Server() {
    	mongoClient = MongoClients.create("mongodb+srv://akshatp24:password1234@chatbot.phhnt9t.mongodb.net/?retryWrites=true&w=majority");
        database = mongoClient.getDatabase("Chatbot");
    }
    public static boolean authenticateUser(String username, String password) {
        return makeFirebaseRequest(username, password);
    }

    private static boolean makeFirebaseRequest(String username, String password) {
        String firebaseUrl = "https://mldl-chatbot-default-rtdb.firebaseio.com/UserInfo.json";

        try {
            URL url = new URL(firebaseUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return checkCredentials(response.toString(), username, password);
            } else {
                System.out.println("Failed to authenticate. HTTP response code: " + responseCode);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean checkCredentials(String firebaseResponse, String username, String password) {
        if (firebaseResponse.contains("\"Username\":\"" + username + "\"")) {
            String storedPassword = extractPassword(firebaseResponse, username);
            return storedPassword.equals(password);
        }
        return false;
    }

    private static String extractPassword(String firebaseResponse, String username) {
        String[] entries = firebaseResponse.split("\\{");
        for (String entry : entries) {
            if (entry.contains("\"Username\":\"" + username + "\"")) {
                String[] parts = entry.split(",");
                for (String part : parts) {
                    if (part.contains("\"Password\":\"")) {
                        return part.split(":")[1].replace("\"", "").trim();
                    }
                }
            }
        }
        return "";
    }
    public void storeMessage(String userID, int sessionNum, long sessionID, String message) {
        MongoCollection<Document> messagesCollection = database.getCollection("messages");

        Document messageDocument = new Document()
                .append("userID", userID)
                .append("sessionNum", sessionNum)
                .append("sessionID", sessionID)
                .append("message", message);

        messagesCollection.insertOne(messageDocument);
    }
    public void storeSession(String userID, long sessionID) {
        MongoCollection<Document> sessionsCollection = database.getCollection("sessions");

        Document sessionDocument = new Document()
                .append("userID", userID)
                .append("sessionID", sessionID);

        sessionsCollection.insertOne(sessionDocument);
    }

    public void initializeServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/user", new UserHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
    }


    private class UserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGetRequest(exchange);
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            String username = getUsernameFromPath(exchange.getRequestURI());
            if (username != null) {
                getUser(username, exchange);
            } else {
                exchange.sendResponseHeaders(400, -1); // Bad Request
            }
        }

        private String getUsernameFromPath(URI uri) {
            String[] pathSegments = uri.getPath().split("/");
            if (pathSegments.length == 4 && "user".equals(pathSegments[2])) {
                return pathSegments[3];
            }
            return null;
        }

        private void getUser(String username, HttpExchange exchange) throws IOException {
            try {
                String userUrl = BASE_URL + "/" + username + ".json";
                URL url = new URL(userUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                            BufferedReader bufferedReader = new BufferedReader(reader)) {

                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }

                        sendResponse(exchange, 200, response.toString());
                    }
                } else {
                    sendResponse(exchange, 404, "User not found");
                }
            } catch (Exception e) {
                sendResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
//    private class SessionHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            if ("POST".equals(exchange.getRequestMethod())) {
//                handlePostRequest(exchange);
//            } else {
//                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
//            }
//        }
//
//        private void handlePostRequest(HttpExchange exchange) throws IOException {
//            // Extract data from the request
//            URI uri = exchange.getRequestURI();
//            String[] pathSegments = uri.getPath().split("/");
//            if (pathSegments.length == 4) {
//                String userID = pathSegments[2];
//                String sessionNum = pathSegments[3];
//                String sessionID = String.valueOf(System.currentTimeMillis()); // Generate a session ID
//
//                // Store sessionID in MongoDB
//                MongoCollection<Document> collection = database.getCollection("UserToMessages");
//                Document document = new Document("sessionID", sessionID);
//                collection.insertOne(document);
//
//                // Send response
//                sendResponse(exchange, 200, "Data stored!");
//            } else {
//                exchange.sendResponseHeaders(400, -1); // Bad Request
//            }
//        }
//    }

    private class ConversationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Similar logic for handling conversation storage
            // Extract data from the request, store data in MongoDB, and send response
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    // Existing code...

    public static void main(String[] args) throws IOException {
        new Server().initializeServer();
    }
}
