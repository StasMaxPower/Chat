module org.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires comands;


    opens org.example.client to javafx.fxml;
    exports org.example.client;
}