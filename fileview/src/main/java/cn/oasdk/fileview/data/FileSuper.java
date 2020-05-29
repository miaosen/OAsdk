package cn.oasdk.fileview.data;

import java.io.File;
import java.net.URI;

public class FileSuper extends File {


    public FileSuper(String pathname) {
        super(pathname);
    }

    public FileSuper(String parent, String child) {
        super(parent, child);
    }

    public FileSuper(File parent, String child) {
        super(parent, child);
    }

    public FileSuper(URI uri) {
        super(uri);
    }
}
