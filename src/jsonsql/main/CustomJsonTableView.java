package jsonsql.main;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CustomJsonTableView extends Application {

    @Override
    public void start(Stage primaryStage) {


        CustomTable customTable = new CustomTable();
        customTable.setFont(Font.font("Consolas", 20));
        customTable.setColumns(new ArrayList<>(List.of(
                "ID", "Name", "wildcard", "City", "Country",
                "Email", "Phone", "Occupation", "Department",
                "Role", "Status", "Salary", "Start Date", "Notes", "School", "Class", "Vehicle"
        )));

        String str = """
                    {
                    "ID": "1",
                    "Name": "Alice Smith",
                    "wildcard": "29",
                    "City": "New York",
                    "Country": "USA",
                    "Email": "alice@example.com",
                    "Phone": "555-1234",
                    "Occupation": "Software Engineer",
                    "Department": "Development",
                    "Role": "Full Stack Developer",
                    "Status": "Active",
                    "Salary": "$120,000",
                    "Start Date": "2020-05-12",
                    "Notes": "Remote worker",
                    "School": "Valera nt",
                    "Class": "Mage",
                    "Vehicle": "Mustang"
                    }
                """;
        JSONObject object = JSON.parseObject(str);

        for (int i = 0; i < 50; i++) { customTable.addRow(object); }


//
//        customTable.addRow(List.of("1", "Alice", "30", "New York", "USA", "alice@example.com", "555-1234", "Engineer", "R&D", "Developer", "Active", "95000", "2021-05-01", "Top performer"));
//        customTable.addRow(List.of("2", "Bob", "25", "San Francisco", "USA", "bob@example.com", "555-5678", "Designer", "UX", "Lead", "Active", "88000", "2022-01-15", ""));
//        customTable.addRow(List.of("3", "Carol", "29", "Chicago", "USA", "carol@example.com", "555-8765", "Manager", "Sales", "Senior", "Inactive", "102000", "2019-11-20", "On leave"));
//        customTable.addRow(List.of("4", "Dan", "35", "Boston", "USA", "dan@example.com", "555-3333", "HR", "People", "Coordinator", "Active", "73000", "2018-03-10", "", ""));
//        customTable.addRow(List.of("5", "Eve", "40", "Seattle", "USA", "eve@example.com", "555-4444", "CTO", "Tech", "Executive", "Active", "200000", "2015-06-30", "Company founder"));
//        customTable.addRow(List.of("6", "Frank", "31", "Miami", "USA", "frank@example.com", "555-2222", "Analyst", "Finance", "Associate", "Inactive", "69000", "2023-01-02", "New hire"));
//        customTable.addRow(List.of("1", "Alice", "30", "New York", "USA", "alice@example.com", "555-1234", "Engineer", "R&D", "Developer", "Active", "95000", "2021-05-01", "Top performer"));
//        customTable.addRow(List.of("2", "Bob", "25", "San Francisco", "USA", "bob@example.com", "555-5678", "Designer", "UX", "Lead", "Active", "88000", "2022-01-15", ""));
//        customTable.addRow(List.of("3", "Carol", "29", "Chicago", "USA", "carol@example.com", "555-8765", "Manager", "Sales", "Senior", "Inactive", "102000", "2019-11-20", "On leave"));
//        customTable.addRow(List.of("4", "Dan", "35", "Boston", "USA", "dan@example.com", "555-3333", "HR", "People", "Coordinator", "Active", "73000", "2018-03-10", ""));
//        customTable.addRow(List.of("5", "Eve", "40", "Seattle", "USA", "eve@example.com", "555-4444", "CTO", "Tech", "Executive", "Active", "200000", "2015-06-30", "Company founder"));
//        customTable.addRow(List.of("6", "Frank", "31", "Miami", "USA", "frank@example.com", "555-2222", "Analyst", "Finance", "Associate", "Inactive", "69000", "2023-01-02", "New hire"));


//        ScrollPane scrollPane = new ScrollPane(customTable);
//        scrollPane.setFitToHeight(true);
//        scrollPane.setFitToWidth(true);

//        VBox layout = new VBox(scrollPane);
//        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox layout = new VBox(customTable);
//        layout.setPrefSize(1200, 600);
//        layout.setMinSize(1200, 600);
//        layout.setMaxSize(1200, 600);
        VBox.setVgrow(customTable, Priority.ALWAYS);


        Scene scene = new Scene(layout, 1200, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Custom JSON Table - 15 Columns");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}