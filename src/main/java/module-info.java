module fr.inria.corese.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires static lombok;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;
    requires atlantafx.base;
    requires jdk.jsobject;
    requires org.kordamp.ikonli.materialdesign;
    requires org.kordamp.ikonli.materialdesign2;
    requires org.kordamp.ikonli.core;

    exports fr.inria.corese.demo;
    exports fr.inria.corese.demo.controller;
    exports fr.inria.corese.demo.view;

    opens fr.inria.corese.demo.controller to javafx.fxml;
    opens fr.inria.corese.demo.view to javafx.fxml;
}