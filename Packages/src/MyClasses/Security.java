package MyClasses;

import java.io.File;

public interface Security {
    public File encrypt(File file);
    public File decrypt(File file);
    public boolean isEncrypted(File file);
}
