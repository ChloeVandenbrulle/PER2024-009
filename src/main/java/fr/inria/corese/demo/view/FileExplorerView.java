package fr.inria.corese.demo.view;

import fr.inria.corese.demo.enums.IconButtonType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

public class FileExplorerView extends HBox {
    private TreeView<String> treeView;
    private IconButtonView newFileButton;
    private IconButtonView newFolderButton;
    private IconButtonView closeFileExplorerButton;
    private HBox buttonBar;
    private VBox mainContent;

    public FileExplorerView() {
        this.setSpacing(0);

        initializeComponents();
        initializeButtonBar();
        initializeMainContent();

        getChildren().addAll(mainContent, closeFileExplorerButton);
        closeFileExplorerButton.alignmentProperty().setValue(Pos.CENTER_RIGHT);
    }

    private void initializeComponents() {
        treeView = new TreeView<>();
        treeView.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
        );
        treeView.setMinWidth(120);

        newFileButton = new IconButtonView(IconButtonType.NEW_FILE);
        newFolderButton = new IconButtonView(IconButtonType.NEW_FOLDER);
        closeFileExplorerButton = new IconButtonView(IconButtonType.CLOSE_FILE_EXPLORER);
    }

    private void initializeButtonBar() {
        buttonBar = new HBox(5);
        buttonBar.setPadding(new Insets(5));
        buttonBar.getChildren().addAll(newFileButton, newFolderButton);
    }

    private void initializeMainContent() {
        mainContent = new VBox(5);
        mainContent.setPadding(new Insets(0, 0, 5, 0));
        VBox.setVgrow(treeView, Priority.ALWAYS);
        mainContent.getChildren().addAll(buttonBar, treeView);
    }

    public TreeView<String> getTreeView() {
        return treeView;
    }

    public Button getNewFileButton() {
        return newFileButton;
    }

    public Button getNewFolderButton() {
        return newFolderButton;
    }

    public Button getCloseFileExplorerButton() {
        return closeFileExplorerButton;
    }
}