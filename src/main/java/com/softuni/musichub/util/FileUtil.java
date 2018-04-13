package com.softuni.musichub.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.ServletContext;
import java.io.*;

@Component
public class FileUtil {

    private static final String PERSISTED_FILES_FOLDER = "temp";

    private static final String CLASSES_FOLDER = "/WEB-INF/classes";

    private final ServletContext servletContext;

    @Autowired
    public FileUtil(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public File createFile(byte[] fileContent, String fileName) throws IOException {
        // Use when upload file from Microsoft Edge or Internet Explorer,
        // because this browsers send path to file as a file name, not as an exact name
        if (fileName.contains(File.separator)) {
            fileName = new File(fileName).getName();
        }

        String persistedFilesFolderPath = this.servletContext.getRealPath(CLASSES_FOLDER)
                + File.separator + PERSISTED_FILES_FOLDER;
        File tempFolder = new File(persistedFilesFolderPath);
        tempFolder.mkdir();

        String fileFullPath = persistedFilesFolderPath + File.separator + fileName;
        File file = new File(fileFullPath);
        file.createNewFile();
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(fileContent);
        }

        return file;
    }
}
