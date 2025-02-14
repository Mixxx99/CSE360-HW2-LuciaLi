package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

public class AnswersList {

    private final DatabaseHelper databaseHelper;
    private final Question question;
    private final User user;
    

    public AnswersList(DatabaseHelper databaseHelper, Question question, User user) {
        this.databaseHelper = databaseHelper;
        this.question = question;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        Label questionTitle = new Label("Q: " + question.getTitle());
        questionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label questionContent = new Label(question.getContent());

        ListView<HBox> answerListView = new ListView<>();
        loadAnswers(answerListView, primaryStage);
        
        Label warningLabel = new Label();
        warningLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        TextArea answerInput = new TextArea();
        answerInput.setPromptText("Write your answer...");
        answerInput.setPrefRowCount(6); 
        answerInput.setWrapText(true);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> submitAnswer(answerInput, warningLabel, answerListView, primaryStage));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new QuestionList(databaseHelper, user).show(primaryStage));

        layout.getChildren().addAll(questionTitle, questionContent, answerListView, answerInput, submitButton, warningLabel, backButton);
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Answers");
    }

    public void loadAnswers(ListView<HBox> answerListView, Stage primaryStage) {
        try {
            List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getQuestionId());
            answerListView.getItems().clear();
            
            if (answers.isEmpty()) {
                Label noAnswersLabel = new Label("No answers yet.");
                HBox noAnswersBox = new HBox(10, noAnswersLabel);
                answerListView.getItems().add(noAnswersBox);
                return;
            }

            for (Answer a : answers) {
                Label answerLabel = new Label(a.getUserName() + ": " + a.getAnswerContent());
                HBox answerItem = new HBox(10, answerLabel);

                if (a.getUserName().equals(user.getUserName()) || user.getRole().equals("admin")) {
                    Button editButton = new Button("Edit");
                    Button deleteButton = new Button("Delete");
                    

                    editButton.setOnAction(e -> showEditPopup(a, answerListView, primaryStage));
                    deleteButton.setOnAction(e -> deleteAnswer(user,a, answerListView, primaryStage));

                    answerItem.getChildren().addAll(editButton, deleteButton);
                }
                answerListView.getItems().add(answerItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void submitAnswer(TextArea answerInput, Label warningLabel, ListView<HBox> answerListView, Stage primaryStage) {
        String answerText = answerInput.getText().trim();
        if (!answerText.isEmpty()) {
            try {
            	
                Answer newAnswer = new Answer(question.getQuestionId(), user.getUserName(), answerText);
                databaseHelper.addAnswer(newAnswer);
                answerInput.clear();
                loadAnswers(answerListView, primaryStage);
                warningLabel.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else { 
        	
        	warningLabel.setText("Answer cannot be empty!");
        }
    }

    
    
    public void showEditPopup(Answer answer, ListView<HBox> answerListView, Stage primaryStage) {
    	Stage popup = new Stage();
        popup.setTitle("Edit Answer");

        VBox popupLayout = new VBox(10);
        popupLayout.setStyle("-fx-padding: 10;");

        TextArea editInput = new TextArea(answer.getAnswerContent());
        editInput.setPrefSize(400, 200); //size
        
        //save
        Button saveButton = new Button("Save Changes");
        
        saveButton.setOnAction(e -> {
            String newContent = editInput.getText().trim();
            if (!newContent.isEmpty()) {
                try {
                    boolean success = databaseHelper.updateAnswer(answer.getAnswerId(), newContent, user);
                    if (success) {
                        loadAnswers(answerListView, primaryStage);
                        popup.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }else {
            	Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Content cannot be empty!");
                alert.showAndWait();
            }
        });
        popupLayout.getChildren().addAll(new Label("Edit Answer:"), editInput, saveButton);
        Scene popupScene = new Scene(popupLayout, 500, 300);
        popup.setScene(popupScene);
        popup.show();
    }
    
    private void deleteAnswer(User user, Answer answer, ListView<HBox> answerListView, Stage primaryStage) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove your name from this answer?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
        
        Label thisLabel = new Label(" ");
        thisLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        	if (response == ButtonType.YES) {
        		if(user.getRole().equals("admin")) {
        			try {
        				databaseHelper.deleteAnswerPermanently(answer.getAnswerId());
        				loadAnswers(answerListView, primaryStage);
        				//thisLabel.setText("Answer permanently deleted 😊");
        			}catch (SQLException ex) {
                        ex.printStackTrace();
                    }
        			
        		}else {
                	try {
                		databaseHelper.disconnectAnswerFromUser(answer.getAnswerId(), user.getUserName());
                		loadAnswers(answerListView, primaryStage); 
                		//thisLabel.setText("Answer disconnected successfully 😊");
                	} catch (SQLException ex) {
                    ex.printStackTrace();
                	}
        		}
            }
        });
    }
    
    

    
    
}

