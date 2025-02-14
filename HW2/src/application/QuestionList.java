package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;

import java.sql.SQLException;
import java.util.List;

public class QuestionList {
	
	private final DatabaseHelper databaseHelper;
	private final User user;

	public QuestionList(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user; 
    }
    
	public void show(Stage primaryStage) {
	    VBox layout = new VBox(10);
	    layout.setStyle("-fx-padding: 10;");

	    Label titleLabel = new Label("All Questions:");
	    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
	    

	    ListView<HBox> questionListView = new ListView<>();

	    try {
	        List<Question> questions = databaseHelper.getAllQuestions();

	        for (Question q : questions) {
	            Label questionLabel = new Label(q.getTitle() + " (by " + q.getUserName() + ")");   
	            Button viewButton = new Button("View");
	            
	            
	            HBox questionItem = new HBox(10, questionLabel, viewButton);
	            
	            // Show button only if user is the author
	            if (q.getUserName().equals(user.getUserName()) || user.getRole().equals("admin")) {
	            	Button editButton = new Button("Edit");
		            Button deleteButton = new Button("Delete");
		            
		            editButton.setOnAction(e -> showEditQuestionPopup(q, questionListView, primaryStage));
		            deleteButton.setOnAction(e -> deleteQuestion(q, questionListView, primaryStage));   
		            
		            questionItem.getChildren().addAll(editButton, deleteButton);
	            }
	          

	            viewButton.setOnAction(e -> new AnswersList(databaseHelper, q, user).show(primaryStage));

	            questionListView.getItems().add(questionItem);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    Button backButton = new Button("Back");
	    backButton.setOnAction(a -> new UserHomePage(databaseHelper, user).show(primaryStage));

	    layout.getChildren().addAll(titleLabel, questionListView, backButton);
	    primaryStage.setScene(new Scene(layout, 800, 400));
	    primaryStage.setTitle("Question List");
	}


	
	private void deleteQuestion(Question question, ListView<HBox> questionListView, Stage primaryStage) {
	    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this question?", ButtonType.YES, ButtonType.NO);
	    confirm.showAndWait().ifPresent(response -> {
	        
	        if (response == ButtonType.YES) {
	            try {
	                if (user.getRole().equals("admin")) {
	                    databaseHelper.deleteQuestionPermanently(question.getQuestionId());
	                    System.out.println("Question deleted by Admin.");
	                    //thisLabel.setText("Question permanently deleted 😊");
	                } else {
	                    databaseHelper.disconnectQuestionFromUser(question.getQuestionId(), user.getUserName());
	                    System.out.println("Question disconnected from user.");
	                    //thisLabel.setText("Question disconnected successfully 😊");
	                }

	                new QuestionList(databaseHelper, user).show(primaryStage);

	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        }
	    });
	}

    
    
    private void showEditQuestionPopup(Question question, ListView<HBox> questionListView, Stage primaryStage) {
        Stage popup = new Stage();
        popup.setTitle("Edit Question");

        VBox popupLayout = new VBox(10);
        popupLayout.setStyle("-fx-padding: 10;");

        TextField titleInput = new TextField(question.getTitle());
        titleInput.setPromptText("Edit Question Title");

        TextArea contentInput = new TextArea(question.getContent());
        contentInput.setPrefSize(400, 200);

        Button saveButton = new Button("Save Changes");

        saveButton.setOnAction(e -> {
            String newTitle = titleInput.getText().trim();
            String newContent = contentInput.getText().trim();
            if (!newTitle.isEmpty() && !newContent.isEmpty()) {
                try {
                    boolean success = databaseHelper.updateQuestion(question.getQuestionId(), newTitle, newContent, user.getUserName());
                    if (success) {
                        System.out.println("Question updated successfully!");
                        popup.close();
                        new QuestionList(databaseHelper, user).show(primaryStage);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
            	Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Title or content cannot be empty!");
                alert.showAndWait();
            }
        });

        popupLayout.getChildren().addAll(new Label("Edit Title:"), titleInput, new Label("Edit Content:"), contentInput, saveButton);
        Scene popupScene = new Scene(popupLayout, 500, 300);
        popup.setScene(popupScene);
        popup.show();
    }

    
    
}
