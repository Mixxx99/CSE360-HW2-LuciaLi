package application;

import databasePart1.DatabaseHelper;
import javafx.stage.Stage;

public class LogOut {
    private final DatabaseHelper databaseHelper;
    private final Stage primaryStage;

    public LogOut(DatabaseHelper databaseHelper, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
    }

    public void logout() {  
        primaryStage.setScene(null); // 清空当前界面，防止内存泄漏
        SetupLoginSelectionPage loginPage = new SetupLoginSelectionPage(databaseHelper);
        loginPage.show(primaryStage);
    }
}
