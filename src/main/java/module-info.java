module fr.inria.corese.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.web;

    opens fr.inria.corese.demo to javafx.fxml;
    exports fr.inria.corese.demo;
}