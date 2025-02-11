package fr.inria.corese.demo.view;

import fr.inria.corese.demo.enums.IconButtonType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

public class FileExplorerView extends HBox {
    private TreeView<String> treeView;
    private IconButtonView newFileButton;
    private IconButtonView newFolderButton;
    private Button openFolderButton;
    private IconButtonView closeFileExplorerButton;
    private HBox buttonBar;
    private VBox mainContent;
    VBox buttonContainer;

    public FileExplorerView() {
        this.setSpacing(0);

        initializeComponents();
        initializeButtonBar();
        initializeMainContent();
        initializeButtonContainer();

        getChildren().addAll(mainContent, buttonContainer);
        StackPane.setAlignment(buttonContainer, Pos.CENTER);
    }

    private void initializeComponents() {
        treeView = new TreeView<>();

        treeView.setCellFactory(tv -> new TreeCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);

                    if (getTreeItem().getGraphic() != null) {
                        setGraphic(getTreeItem().getGraphic());
                    }

                    setOnMouseEntered(e ->
                            setStyle("-fx-background-color: #CCE8FF;")
                    );

                    setOnMouseExited(e -> {
                        if (!isSelected()) {
                            setStyle("");
                        }
                    });

                    selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        if (isNowSelected) {
                            setStyle("-fx-background-color: #CCE8FF;");
                        } else {
                            setStyle("");
                        }
                    });
                }
            }
        });

        treeView.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
        );

        treeView.setMinWidth(120);

        newFileButton = new IconButtonView(IconButtonType.NEW_FILE);
        newFolderButton = new IconButtonView(IconButtonType.NEW_FOLDER);
        closeFileExplorerButton = new IconButtonView(IconButtonType.CLOSE_FILE_EXPLORER);
        openFolderButton = new Button("Open Folder");
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
        openFolderButton.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(openFolderButton, buttonBar, treeView);
    }

    private void initializeButtonContainer() {
        buttonContainer = new VBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(closeFileExplorerButton);
    }

    public TreeView<String> getTreeView() {
        return treeView;
    }

    public void setTreeView(TreeView<String> treeView) {
        this.treeView = treeView;
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

    public Button getOpenFolderButton() {
        return openFolderButton;
    }

    public void openView() {
        mainContent.setVisible(true);
        mainContent.setManaged(true);
        closeFileExplorerButton.setGraphic(new FontIcon(MaterialDesignL.LESS_THAN));
        setMaxWidth(200);
    }

    public void closeView() {
        mainContent.setVisible(false);
        mainContent.setManaged(false);
        closeFileExplorerButton.setGraphic(new FontIcon(MaterialDesignG.GREATER_THAN));
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        setMaxWidth(30);
    }

}