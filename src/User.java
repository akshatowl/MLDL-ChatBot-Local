public class User {

    private String firstName;
    private String lastName;
    private String username;
    private String password;

    // Add other fields as needed

    // Getters and setters

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
