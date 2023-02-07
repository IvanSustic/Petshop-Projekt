module com.example.petshopprojekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires java.desktop;


    opens com.example.petshopprojekt to javafx.fxml;
    exports com.example.petshopprojekt;
}