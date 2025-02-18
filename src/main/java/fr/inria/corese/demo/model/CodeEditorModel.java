package fr.inria.corese.demo.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Stack;

public class CodeEditorModel {
    private final StringProperty content = new SimpleStringProperty("");
    private String currentSavedContent;
    private BooleanProperty modified = new SimpleBooleanProperty(false);
    private final Stack<CodeEditorChange> undoStack = new Stack<>();
    private final Stack<CodeEditorChange> redoStack = new Stack<>();
    private String currentFile;
    private static final int MAX_HISTORY = 500;

    public StringProperty contentProperty() {
        return content;
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String content) {
        this.content.set(content);
        setModified(true);
    }

    public String getCurrentSavedContent() {
        return currentSavedContent;
    }

    public void setCurrentSavedContent(String originalContent) {
        currentSavedContent = originalContent;
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
    }

    public BooleanProperty modifiedProperty() {
        return modified;
    }

    public boolean isModified() {
        return modified.get();
    }

    public void setModified(boolean modified) {
        this.modified.set(modified);
    }

    public void recordCurrentChange(String newContent) {
        String oldContent = getContent();

        if (!oldContent.equals(newContent)) {
            undoStack.push(new CodeEditorChange(oldContent, newContent));
            redoStack.clear();
            if (undoStack.size() > MAX_HISTORY) {
                undoStack.remove(0);
            }
            setContent(newContent);
            setModified(true);
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void undo() {
        if (canUndo()) {
            CodeEditorChange change = undoStack.pop();
            redoStack.push(change);
            setContent(change.getPreviousContent());
        }
    }

    public void redo() {
        if (canRedo()) {
            CodeEditorChange change = redoStack.pop();
            undoStack.push(change);
            setContent(change.getNewContent());
        }
    }

    public Stack<CodeEditorChange> getUndoStack() {
        return undoStack;
    }

    public Stack<CodeEditorChange> getRedoStack() {
        return redoStack;
    }
}