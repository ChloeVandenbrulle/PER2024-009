package fr.inria.corese.demo.view;

import fr.inria.corese.demo.enums.IconButtonType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FileExplorerView extends HBox {
    private TreeView<String> treeView;
    private IconButtonView newFileButton;
    private IconButtonView newFolderButton;
    private IconButtonView openFolderButton;
    private HBox buttonBar;
    private VBox mainContent;

    public FileExplorerView() {
        this.setSpacing(0);

        initializeComponents();
        initializeButtonBar();
        initializeMainContent();

        VBox.setVgrow(this, Priority.ALWAYS);

        getChildren().addAll(mainContent);
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
                    setStyle("-fx-font-size: 12px;");

                    if (getTreeItem().getGraphic() != null) {
                        setGraphic(getTreeItem().getGraphic());
                    }

                    setOnMouseEntered(e ->
                            setStyle("-fx-background-color: #CCE8FF; -fx-font-size: 12px")
                    );

                    setOnMouseExited(e -> {
                        if (!isSelected()) {
                            setStyle("-fx-font-size: 12px");
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
        openFolderButton = new IconButtonView(IconButtonType.OPEN_FOLDER);
    }

    private void initializeButtonBar() {
        buttonBar = new HBox(5);
        buttonBar.setPadding(new Insets(5));
        buttonBar.getChildren().addAll(openFolderButton, newFileButton, newFolderButton);
    }

    private void initializeMainContent() {
        mainContent = new VBox(5);
        mainContent.setPadding(new Insets(0, 0, 5, 0));

        mainContent.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(mainContent, Priority.ALWAYS);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        VBox.setVgrow(treeView, Priority.ALWAYS);
        treeView.setMaxHeight(Double.MAX_VALUE);

        openFolderButton.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(buttonBar, treeView);
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


    public Button getOpenFolderButton() {
        return openFolderButton;
    }
}