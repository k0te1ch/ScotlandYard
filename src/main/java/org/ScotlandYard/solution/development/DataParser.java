package org.ScotlandYard.solution.development;

import com.google.gson.Gson;
import org.ScotlandYard.solution.development.models.DataSave;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * This class handles loading and saving data for Highway Control
 */
public class DataParser {

    private final Gson gson;

    public DataParser () {
        gson = new Gson();
    }


    public DataSave loadV3Data(File file) throws IOException {

        String input = StringUtils.join(Files.readAllLines(file.toPath()),"");

        return gson.fromJson(input, DataSave.class);
    }

    public void saveV3Data(DataSave data, File file) throws IOException {

        PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);
        writer.write(gson.toJson(data));
        writer.close();

    }
}
