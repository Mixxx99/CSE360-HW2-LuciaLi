package application;


public class Question {
	
		private int questionId;
		private String userName;
	    private String title;
	    private String content;
	    
	    

	    // Constructor to initialize a new User object with userName, password, and role.
	    public Question(String userName, String title, String content) {
	    	this.questionId = -1;
	    	this.userName = userName;
	    	this.title = title;
	        this.content = content;
	    }
	    
	    
	    public Question(int questionId, String userName, String title, String content) {
	    	this.questionId = questionId;
	    	this.userName = userName;
	    	this.title = title;
	        this.content = content;
	    }
	    
	    

	    public int getQuestionId() { return questionId; }
	    public void setQuestionId(int questionId) { this.questionId = questionId; }
	    public String getUserName() { return userName; }
	    public String getTitle() { return title; }
	    public String getContent() { return content; }
	    
	}
