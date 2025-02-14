package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.*;



/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class insertQuestion {
	
	private final DatabaseHelper databaseHelper;
	private final User user;
	

    public insertQuestion(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user; 
    }
    
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 10;");
	    
	    // Label to display Hello user
	    Label header = new Label("Insert your Question header here: ");
	    header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    Label content = new Label("Insert your Question content here: ");
	    content.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	
	    //get userName
	    String userName =user.getUserName();
    	System.out.println(userName);
    	

    	//space to add header 
	    TextField headerInput = new TextField();
        headerInput.setPromptText("Enter question title...");
        
        
      //space to add question
        TextArea contentInput = new TextArea();
        contentInput.setPromptText("Enter question content...");
        contentInput.setPrefRowCount(5); 
        
        

	   
    	//button save 
        Button saveButton = new Button("Save");
        
        //button back
        Button backButton = new Button("Back");
        
        //message
        Label ErrorMessage = new Label(""); ;
        ErrorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        
        //if button save create a question in database, link to Questions. 
        saveButton.setOnAction(a -> {
        	String title = headerInput.getText();
            String contentText = contentInput.getText();

            if (title.isEmpty() || contentText.isEmpty()) {
            	ErrorMessage.setText("Title or content cannot be empty!");
                return;
            }

            Question newQuestion = new Question(userName, title, contentText);

            try {
                databaseHelper.addNewQuestion(newQuestion);
                System.out.println("Question saved ~❤");
                
            } catch (SQLException e) {
                System.err.println("Error saving question: " + e.getMessage());
            }
            
            //go to questionList page
            new QuestionList(databaseHelper, user).show(primaryStage);
        });
        
        
        //back
        backButton.setOnAction(a -> {
        	new UserHomePage(databaseHelper, user).show(primaryStage);
        });
       
	    	
    	
    	
	    //words & button
	    layout.getChildren().addAll(header,headerInput, content, contentInput, ErrorMessage, saveButton, backButton);
	    
	    //scene set up
	    Scene userScene = new Scene(layout, 800, 400);
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Add Question");
	    
	    
	    
	    }
    
    }
