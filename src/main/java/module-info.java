module fr.inria.corese.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires atlantafx.base;
    requires org.fxmisc.richtext;
    requires static lombok;
    requires org.apache.jena.core;

    opens fr.inria.corese.demo to javafx.fxml;
    opens fr.inria.corese.demo.controller to javafx.fxml;

    exports fr.inria.corese.demo;
    exports fr.inria.corese.demo.controller;
}