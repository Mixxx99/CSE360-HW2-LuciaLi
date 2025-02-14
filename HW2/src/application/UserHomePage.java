package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePage {

	 private final DatabaseHelper databaseHelper;
	 private final User user;
	 
	 public UserHomePage(DatabaseHelper databaseHelper, User user) {
		 this.databaseHelper = databaseHelper;   
		 this.user = user;
	    }
	
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 10;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, Student!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    
		  //Implement the logout button on user home page and returns to the main setup/login page & select user role
		Button logoutButton = new Button("Logout");
        	logoutButton.setOnAction(event -> {
            		LogOut logOut = new LogOut(databaseHelper, primaryStage);
            		logOut.logout(); 
        	});
        	
        	
//////////////////////////////////////////////////////////////////////////////////////////
		
		
        Button addQuestion = new Button("add question");
	    
		addQuestion.setOnAction(event -> {
		   new insertQuestion(databaseHelper, user).show(primaryStage);
		   		System.out.println("Add Question");
		    });
	    
		Button viewallQuestions = new Button("View all Questions");
	    
		viewallQuestions.setOnAction(event -> {
		   new QuestionList(databaseHelper, user).show(primaryStage);
		        
		    });
		
		
//////////////////////////////////////////////////////////////////////////////////////////
       
		
	    //layout.getChildren().addAll(userLabel, logoutButton, roleselectionButton);
	    layout.getChildren().addAll(userLabel, logoutButton, addQuestion, viewallQuestions);
	    //layout.getChildren().addAll(addQuestion, viewallQuestions);
	    
	    Scene userScene = new Scene(layout, 800, 400);
	  
	    

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
    }
}
