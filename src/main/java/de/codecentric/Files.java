package de.codecentric;

import java.io.File;

final class Files {

    static String getExtension(File f) {
        int i = f.getName().lastIndexOf('.');
        return (i > 0 ? f.getName().substring(i + 1) : null);
    }

    static boolean isPropertiesFile(File f) {
        String extension = getExtension(f);
        return extension != null && extension.equals("properties");
    }
}
