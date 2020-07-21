package com.marsanpat.greta.Utils.Files;

import android.net.Uri;

import com.marsanpat.greta.Activities.MainActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    /**
     * Creates a file and writes a string on it. Returns the path to the created file.
     * @param inputString
     * @param directory
     * @param fileName
     * @return
     * @throws IOException
     */
    public String createAndWriteFile(String inputString, Uri directory, String fileName) throws IOException {
        File dir = new File(directory.getPath());
        if(!dir.exists()){
            dir.mkdir();
        }
        File exportFile = new File(dir,fileName);
        FileWriter writer = new FileWriter(exportFile);
        writer.write(inputString);
        writer.flush();
        writer.close();

        return exportFile.getAbsolutePath();
    }
}
