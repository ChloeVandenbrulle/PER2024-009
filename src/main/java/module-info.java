module com.example.test_librairies {
    requires atlantafx.base;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.web;

    opens com.example.test_librairies to javafx.fxml;
    exports com.example.test_librairies;
}