package fr.inria.corese.demo.model;

public class FileItem {
    private final String name;
    private boolean loading;

    public FileItem(String name) {
        this.name = name;
        this.loading = false;
    }

    public String getName() {
        return name;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}