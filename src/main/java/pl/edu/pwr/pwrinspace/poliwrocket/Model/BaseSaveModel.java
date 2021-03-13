package pl.edu.pwr.pwrinspace.poliwrocket.Model;

public abstract class BaseSaveModel {

    private final String path;

    private final String fileName;

    private final String persistPrefix;

    public BaseSaveModel(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
        this.persistPrefix = "BAD_";
    }

    public BaseSaveModel(String path, String fileName, String persistPrefix) {
        this.path = path;
        this.fileName = fileName;
        this.persistPrefix = persistPrefix;
    }


    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public String getPersistPrefix() {
        return persistPrefix;
    }

}
