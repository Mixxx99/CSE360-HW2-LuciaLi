package databasePart1;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javafx.collections.*;

import application.User;
import application.Question;
import application.Answer;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!	
			
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		System.out.println("Creating tables...");
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
		        + "userName VARCHAR(255) UNIQUE, "
		        + "password VARCHAR(255), "
		        + "role VARCHAR(20), "  
		        //////////////////////////////
		        + "email VARCHAR(255), "  
		        + "name VARCHAR(255))";

		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
		    ///////////////////////////////////////
	    		+ "role VARCHAR(255), "
	    	    ///////////////////////////////////////
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
		////////////////////////////////////////////////////////
		///table for questions
	    String Questiontable = "CREATE TABLE IF NOT EXISTS questions ("
	    		+ "question_id INT AUTO_INCREMENT PRIMARY KEY,"
	    		+ "userName VARCHAR(255) DEFAULT NULL,"
	    		+ "questionTitle VARCHAR(255),"
	    		+ "questionContent TEXT,"
	    		+ "FOREIGN KEY (userName) REFERENCES cse360users(userName) ON DELETE SET NULL"
	    		+ ")";
	    statement.execute(Questiontable);
		
		////////////////////////////////////////////////////////
		///table for answer 
	    String AnswersTable = "CREATE TABLE IF NOT EXISTS answers ("
	            + "answer_id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "question_id INT, "
	            + "answerContent TEXT, "
	            + "userName VARCHAR(255) DEFAULT NULL, "
	            //+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " 
	            + "FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,"
	            + "FOREIGN KEY (userName) REFERENCES cse360users(userName) ON DELETE SET NULL"
	            + ")"; 
	    statement.execute(AnswersTable);
	    
	 //
	    String insertUnknownUser = "INSERT INTO cse360users (userName, password, role, email, name) "
	                             + "SELECT 'unknown', 'unknown', 'unknown', 'unknown', 'unknown' "
	                             + "WHERE NOT EXISTS (SELECT 1 FROM cse360users WHERE userName = 'unknown')";
	    statement.execute(insertUnknownUser);

	    
	}
	    //////////////////////////////////////////////////////////
	    ///
	    /////////////////////////
	    ///disconnected
	    ///
	    
	    public void disconnectAnswerFromUser(int answerId, String userName) throws SQLException {
	        String updateQuery = "UPDATE answers SET userName = 'unknown' WHERE answer_id = ? AND userName = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	            pstmt.setInt(1, answerId);
	            pstmt.setString(2, userName); 
	            int rowsUpdated = pstmt.executeUpdate();
	            if (rowsUpdated > 0) {
	                System.out.println("Answer ID " + answerId + " has been disconnected from user: " + userName);
	            } else {
	                System.out.println("Error: Could not disconnect answer. Maybe it's not your answer?");
	            }
	        }
	        
	        
	    }
	    
	    public void deleteAnswerPermanently(int answerId) throws SQLException {
	        String deleteQuery = "DELETE FROM answers WHERE answer_id = ?";

	        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
	            pstmt.setInt(1, answerId);
	            int rowsDeleted = pstmt.executeUpdate();
	            
	            if (rowsDeleted > 0) {
	                System.out.println("Answer ID " + answerId + " has been permanently deleted.");
	            } else {
	                System.out.println("Error: Answer ID " + answerId + " does not exist or has already been deleted.");
	            }
	        }
	    }
	    
	    public void disconnectQuestionFromUser(int questionID, String userName) throws SQLException {
	        String updateQuery = "UPDATE questions SET userName = 'unknown' WHERE question_id = ? AND userName = ?";
	        
	    	String checkUnknownUser = "SELECT COUNT(*) FROM cse360users WHERE userName = 'unknown'";
	    	try (Statement stmt = connection.createStatement();
	    	     ResultSet rs = stmt.executeQuery(checkUnknownUser)) {
	    	    if (rs.next() && rs.getInt(1) == 0) {
	    	        throw new SQLException("Error: 'unknown' user does not exist in cse360users.");
	    	    }
	    	}

	        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	            pstmt.setInt(1, questionID);
	            pstmt.setString(2, userName);

	            int updated = pstmt.executeUpdate();
	            if (updated > 0) {
	                System.out.println("Question ID " + questionID + " has been disconnected from user: " + userName);
	            } else {
	                System.out.println("Error: Could not disconnect question. Maybe it's not your question?");
	            }
	        }
	    }
	    

	    
	    public void deleteQuestionPermanently(int questionID) throws SQLException {
	    	

	        String deleteQuery = "DELETE FROM questions WHERE question_id = ?";

	        
	        
	        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
	            pstmt.setInt(1, questionID);
	            int rowsDeleted = pstmt.executeUpdate();

	            if (rowsDeleted > 0) {
	                System.out.println("Question ID " + questionID + " has been permanently deleted.");
	            } else {
	                System.out.println("Error: Question ID " + questionID + " does not exist or has already been deleted.");
	            }
	        }
	    }

	
	
	////////////////////////////////////////////////////////////////////////
	    public boolean updateAnswer(int answerId, String newContent, User user) throws SQLException {
	        String userRole = user.getRole();
	        String userName = user.getUserName();
	        String checkQuery = "SELECT COUNT(*) FROM answers WHERE answer_id = ? AND (userName = ? OR ? = 'admin')";
	        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
	            checkStmt.setInt(1, answerId);
	            checkStmt.setString(2, userName);
	            checkStmt.setString(3, userRole);  
	            
	            ResultSet rs = checkStmt.executeQuery();
	            if (rs.next() && rs.getInt(1) == 0) {
	                System.out.println("Error: You are neither the author nor an admin!");
	                return false;
	            }
	        }
	        String updateQuery = "UPDATE answers SET answerContent = ? WHERE answer_id = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	            pstmt.setString(1, newContent);
	            pstmt.setInt(2, answerId);

	            int rowsAffected = pstmt.executeUpdate();
	            return rowsAffected > 0; 
	        }
	    }

///////////////////////////////////////////////////////////////////////////

	public boolean updateQuestion(int questionId, String newTitle, String newContent, String userName) throws SQLException {
	    String checkQuery = "SELECT COUNT(*) FROM questions WHERE question_id = ? AND (userName = ? OR ? IN (SELECT role FROM cse360users WHERE userName = 'admin'))";

	    // Check if the user is the author or an admin
	    try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
	        checkStmt.setInt(1, questionId);
	        checkStmt.setString(2, userName);
	        checkStmt.setString(3, userName);
	        ResultSet rs = checkStmt.executeQuery();

//	        if (rs.next() && rs.getInt(1) == 0) {
//	            System.out.println("Error:no");
//	            return false;
//	        }
	    }
	    // Perform UPDATE
	    String updateQuery = "UPDATE questions SET questionTitle = ?, questionContent = ? WHERE question_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setString(1, newTitle);
	        pstmt.setString(2, newContent);
	        pstmt.setInt(3, questionId);

	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
	    }
	}

	///////////////////////////////////////////////////////////////////////

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}
	
	//add new users into the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role, email, name) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			//////////////////////////////////////////
			pstmt.setString(4, user.getEmail());
	        pstmt.setString(5, user.getName());
	        	//////////////////////////////////////////
			pstmt.executeUpdate();
			System.out.println("✅ New user: " + user.getUserName() +" have been added.");
		}catch (SQLException e) {
		    System.err.println("Database error: " + e.getMessage());
		    e.printStackTrace();
		}
	}
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////
	// new questions in the database.
//	public void addNewQuestion(Question question) throws SQLException {
//		String insertQuestion = "INSERT INTO questions (userName, questionTitle, questionContent) VALUES (?, ?, ?)";
//		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
//			pstmt.setString(1, question.getUserName());
//			pstmt.setString(2, question.getTitle());
//			pstmt.setString(3, question.getContent());
//			
//			pstmt.executeUpdate();
//			System.out.println("added~~ question id ： " + question.getQuestionId());
//		}catch (SQLException e) {
//		    System.err.println("Database error: " + e.getMessage());
//		    e.printStackTrace();
//		    
//		}
//	}
	
	
	public int addNewQuestion(Question question) throws SQLException {
	    String insertQuery = "INSERT INTO questions (userName, questionTitle, questionContent) VALUES (?, ?, ?)";
	    int generatedId = -1;

	    try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, question.getUserName());
	        pstmt.setString(2, question.getTitle());
	        pstmt.setString(3, question.getContent());

	        pstmt.executeUpdate();
	        
	 

	        //Retrieve the generated question_id
	        ResultSet rs = pstmt.getGeneratedKeys();
	        if (rs.next()) {
	            generatedId = rs.getInt(1);
	            System.out.println("✅ New Question: " + question.getTitle() +" have been created. Added by: " + question.getUserName() + ".");
	            System.out.println("✅ New Question ID: " + generatedId);
	           
	        }
	    }
	    return generatedId; //Return the correct question_id
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////
///new answers in the database
	public int addAnswer(Answer answer) throws SQLException {
	    String insertQuery = "INSERT INTO answers (question_id, userName, answerContent) VALUES (?, ?, ?)";
	    int generatedId = -1; 

	    try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setInt(1, answer.getQuestionId());
	        pstmt.setString(2, answer.getUserName());
	        pstmt.setString(3, answer.getAnswerContent());

	        pstmt.executeUpdate();

	        ResultSet rs = pstmt.getGeneratedKeys();
	        if (rs.next()) {
	            generatedId = rs.getInt(1);
	            answer.setAnswerId(generatedId);
	            System.out.println("✅ New Answer added. ");
	            System.out.println("✅ New Answer ID: " + generatedId);
	        }
	    }
	    return generatedId;
	}
		
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	//////////////////////////////////////////////////////////////////////////////
	///get all Questions 
	public List<Question> getAllQuestions() throws SQLException {
	    List<Question> questions = new ArrayList<>();
	    String query = "SELECT question_id, userName, questionTitle, questionContent FROM questions";

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {
	        
	        while (rs.next()) {
	            questions.add(new Question(
	                rs.getInt("question_id"),
	                rs.getString("userName"),
	                rs.getString("questionTitle"),
	                rs.getString("questionContent")
	            ));
	        }
	    }
	    return questions;
	}
	//////////////////////////////////////////////////////////////////////////////
	///get all answers of one question!!
	public List<Answer> getAnswersForQuestion(int questionId) throws SQLException {
	    List<Answer> answers = new ArrayList<>();
	    String query = "SELECT answer_id, question_id, userName, answerContent FROM answers WHERE question_id = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, questionId);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            answers.add(new Answer(
	                rs.getInt("answer_id"),
	                rs.getInt("question_id"),
	                rs.getString("userName"),
	                rs.getString("answerContent")
	            ));
	        }
	    }
	    return answers;
	}

	
	
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String role) {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
		String query = "INSERT INTO InvitationCodes (code, role) VALUES (?,?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
		///////////////////////////////////
		pstmt.setString(2, role);
		///////////////////////////////////
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	//////////////////////////////////////////////////////////////////////////
	///GetRole
	public String getRoleByInvitationCode(String code) throws SQLException {
	    String query = "SELECT role FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getString("role");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	/////////////////////////////////////////////////////////////////////////////

	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

	// Returns observable array list of users in database.
	public ObservableList<User> getUsers() {
    	ObservableList<User> users = FXCollections.observableArrayList();
    	String query = "SELECT * FROM cse360users";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	        	users.add(new User(rs.getString("userName"), "", rs.getString("role"),
	        			/*rs.getString("email"), rs.getString("name")*/ "", ""));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	return users;
    }
	
	//For test
	public void deleteAllUsers() throws SQLException {
	    String deleteQuery = "DELETE FROM cse360users WHERE userName <> 'unknown'";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
	        int rowsDeleted = pstmt.executeUpdate();
	        System.out.println("✅ All users except 'unknown' have been deleted.");
	    }
	}
}
