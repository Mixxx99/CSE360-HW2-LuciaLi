package application.test;

import databasePart1.DatabaseHelper;
import application.User;
import application.Question;
import application.Answer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseHelperTest {

    private DatabaseHelper databaseHelper;
    private User testUser;
    private User adminUser;
    private Question testQuestion;
    private Answer testAnswer;

    @BeforeAll
    void setup() throws SQLException {
        System.out.println(" ");
        System.out.println("Setting up test database...");

        databaseHelper = new DatabaseHelper();
        databaseHelper.connectToDatabase();
        databaseHelper.deleteAllUsers();

        testUser = new User("testUser", "password", "user", "test@example.com", "Test User");
        adminUser = new User("adminUser", "adminpass", "admin", "admin@example.com", "Admin");

        databaseHelper.register(testUser);
        databaseHelper.register(adminUser);
        System.out.println("Test users created.");

        testQuestion = new Question("testUser", "Test Question Title", "This is a test question.");
        int questionId = databaseHelper.addNewQuestion(testQuestion);
        testQuestion.setQuestionId(questionId);
        System.out.println("New Question ID: " + questionId);

        testAnswer = new Answer(questionId, "testUser", "This is a test answer.");
        int answerId = databaseHelper.addAnswer(testAnswer);
        testAnswer.setAnswerId(answerId);
        System.out.println("New Answer ID: " + answerId);
    }

    @Test
    @DisplayName("Add and Retrieve Questions")
    void testAddAndRetrieveQuestions() throws SQLException {
        System.out.println(" ");
        System.out.println("Test1");
        Question newQuestion = new Question("testUser", "Another Question", "Another test content.");
        int questionId = databaseHelper.addNewQuestion(newQuestion);
        System.out.println("New Question ID: " + questionId);

        List<Question> questions = databaseHelper.getAllQuestions();
        assertTrue(questions.stream().anyMatch(q -> q.getQuestionId() == questionId));
    }

    @Test
    @DisplayName("Edit Question user")
    void testEditQuestion() throws SQLException {
        System.out.println(" ");
        System.out.println("Test2.1");
        
        testQuestion = new Question("testUser", "Test Question Title", "This is a test question.");
        int questionId = databaseHelper.addNewQuestion(testQuestion);
        testQuestion.setQuestionId(questionId);
        System.out.println("New Question ID: " + questionId);

        testAnswer = new Answer(questionId, "testUser", "This is a test answer.");
        int answerId = databaseHelper.addAnswer(testAnswer);
        testAnswer.setAnswerId(answerId);
        System.out.println("New Answer ID: " + answerId);

        boolean success = databaseHelper.updateQuestion(testQuestion.getQuestionId(), "Updated Title", "Updated Content", testUser.getUserName());
        System.out.println("Update success: " + success);
        assertTrue(success);

        List<Question> questions = databaseHelper.getAllQuestions();
        assertTrue(questions.stream().anyMatch(q -> q.getTitle().equals("Updated Title")));
    }
    
    @Test
    @DisplayName("Edit Question admin")
    void testEditQuestion2() throws SQLException {
        System.out.println(" ");
        System.out.println("Test2.2");
        
        testQuestion = new Question("testUser", "Test Question Title", "This is a test question.");
        int questionId = databaseHelper.addNewQuestion(testQuestion);
        testQuestion.setQuestionId(questionId);
        System.out.println("New Question ID: " + questionId);

        testAnswer = new Answer(questionId, "testUser", "This is a test answer.");
        int answerId = databaseHelper.addAnswer(testAnswer);
        testAnswer.setAnswerId(answerId);
        System.out.println("New Answer ID: " + answerId);

        boolean success = databaseHelper.updateQuestion(testQuestion.getQuestionId(), "Updated Title", "Updated Content", adminUser.getUserName());
        System.out.println("Update success: " + success);
        assertTrue(success);

        List<Question> questions = databaseHelper.getAllQuestions();
        assertTrue(questions.stream().anyMatch(q -> q.getTitle().equals("Updated Title")));
    }

    @Test
    @DisplayName("Add and Retrieve Answers")
    void testAddAndRetrieveAnswers() throws SQLException {
        System.out.println(" ");
        System.out.println("Test3");
        Answer newAnswer = new Answer(testQuestion.getQuestionId(), "testUser", "Another test answer.");
        int answerId = databaseHelper.addAnswer(newAnswer);
        System.out.println("New Answer ID: " + answerId);
        assertTrue(answerId > 0);

        List<Answer> answers = databaseHelper.getAnswersForQuestion(testQuestion.getQuestionId());
        assertTrue(answers.stream().anyMatch(a -> a.getAnswerId() == answerId));
    }

    @Test
    @DisplayName("Edit Answer user")
    void testEditAnswer() throws SQLException {
        System.out.println(" ");
        System.out.println("Test4.1");
        
      
        testQuestion = new Question("testUser", "Test Question Title", "This is a test question.");
        int questionId = databaseHelper.addNewQuestion(testQuestion);
        testQuestion.setQuestionId(questionId);
        System.out.println("New Question ID: " + questionId);

        testAnswer = new Answer(questionId, "testUser", "This is a test answer.");
        int answerId = databaseHelper.addAnswer(testAnswer);
        testAnswer.setAnswerId(answerId);
        System.out.println("New Answer ID: " + answerId);
        

        boolean success = databaseHelper.updateAnswer(testAnswer.getAnswerId(), "Updated Answer", testUser);
        System.out.println("Answer Update success: " + success);
        assertTrue(success);

        List<Answer> answers = databaseHelper.getAnswersForQuestion(testAnswer.getQuestionId());
        assertTrue(answers.stream().anyMatch(a -> a.getAnswerContent().equals("Updated Answer")));
    }
    
    @Test
    @DisplayName("Edit Answer admin")
    void testEditAnswer2() throws SQLException {
        System.out.println(" ");
        System.out.println("Test4.2");
        
        
        testQuestion = new Question("testUser", "Test Question Title", "This is a test question.");
        int questionId = databaseHelper.addNewQuestion(testQuestion);
        testQuestion.setQuestionId(questionId);
        System.out.println("New Question ID: " + questionId);

        testAnswer = new Answer(questionId, "testUser", "This is a test answer.");
        int answerId = databaseHelper.addAnswer(testAnswer);
        testAnswer.setAnswerId(answerId);
        System.out.println("New Answer ID: " + answerId);
        
        boolean success = databaseHelper.updateAnswer(testAnswer.getAnswerId(), "Updated Answer", adminUser);
        System.out.println("Answer Update success: " + success);
        assertTrue(success);

        List<Answer> answers = databaseHelper.getAnswersForQuestion(testAnswer.getQuestionId());
        assertTrue(answers.stream().anyMatch(a -> a.getAnswerContent().equals("Updated Answer")));
    }

    @Test
    @DisplayName("User Disconnects Answer")
    void testUserDisconnectsAnswer() throws SQLException {
        System.out.println(" ");
        System.out.println("Test5");

        databaseHelper.disconnectAnswerFromUser(testAnswer.getAnswerId(), testUser.getUserName());
        System.out.println("Answer ID " + testAnswer.getAnswerId() + " disconnected from user.");

        List<Answer> answers = databaseHelper.getAnswersForQuestion(testAnswer.getQuestionId());
        assertTrue(answers.stream().anyMatch(a -> a.getUserName().equals("unknown")));
    }

    @Test
    @DisplayName("Admin Deletes Answer Permanently")
    void testAdminDeletesAnswer() throws SQLException {
        System.out.println(" ");
        System.out.println("Test6");

        Answer tempAnswer = new Answer(testQuestion.getQuestionId(), "testUser", "Temporary Answer");
        int tempAnswerId = databaseHelper.addAnswer(tempAnswer);
        System.out.println("Temporary Answer ID: " + tempAnswerId);

        databaseHelper.deleteAnswerPermanently(tempAnswerId);
        System.out.println("Temporary Answer ID " + tempAnswerId + " deleted.");

        List<Answer> answers = databaseHelper.getAnswersForQuestion(testQuestion.getQuestionId());
        assertFalse(answers.stream().anyMatch(a -> a.getAnswerId() == tempAnswerId));
    }
    
    @Test
    @DisplayName("User Disconnects Question")
    void testUserDisconnectsQuestion() throws SQLException {
        System.out.println(" ");
        System.out.println("Test7.1");


        testQuestion = new Question("testUser", "Test Question Title", "This is a test question.");
        int questionId = databaseHelper.addNewQuestion(testQuestion);
        testQuestion.setQuestionId(questionId);
        System.out.println("New Question ID: " + questionId);
        
        databaseHelper.disconnectQuestionFromUser(testQuestion.getQuestionId(), testUser.getUserName());
        System.out.println("Question ID " + testQuestion.getQuestionId() + " disconnected from user.");

        List<Question> questions = databaseHelper.getAllQuestions();
        assertTrue(questions.stream().anyMatch(q -> q.getQuestionId() == testQuestion.getQuestionId() && q.getUserName().equals("unknown")));
    }

    @Test
    @DisplayName("Admin Deletes Question Permanently")
    void testAdminDeletesQuestion() throws SQLException {
        System.out.println(" ");
        System.out.println("Test7.2");

        databaseHelper.deleteQuestionPermanently(testQuestion.getQuestionId());
        System.out.println("Question ID " + testQuestion.getQuestionId() + " deleted.");

        List<Question> questions = databaseHelper.getAllQuestions();
        assertFalse(questions.stream().anyMatch(q -> q.getQuestionId() == testQuestion.getQuestionId()));
    }

    
    

    @AfterAll
    void cleanup() throws SQLException {
        System.out.println(" ");
        System.out.println("Cleaning up database...");
        databaseHelper.closeConnection();
        System.out.println("Database connection closed.");
    }
}
