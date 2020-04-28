package guiz;

import java.io.File;

public class Category {

    private String name;
    private File file;

    public Category(String name, File file) {
        this.name = name; //name è già spogliato dell'estensione .txt
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
