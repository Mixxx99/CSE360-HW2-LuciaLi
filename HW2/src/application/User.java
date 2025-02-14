package application;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    private String userName;
    private String password;
    private String role;
    private String email;
    private String name;

    // Constructor to initialize a new User object with userName, password, and role.
    public User( String userName, String password, String role, String email, String name) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.email = email;
        this.name = name;
    }
    
    public User( String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    
    // Sets the role of the user.
    public void setRole(String role) {
    	this.role=role;
    }

    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getName() { return name; }
}
