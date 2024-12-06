package org.logic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileExporter {
    public FileExporter(String fileName, String generalExpenses, String personalExpenses, String insights) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(generalExpenses);
            writer.newLine();
            writer.write("---------------------------------------------------------------------------------------------------------------------");
            writer.newLine();
            writer.newLine();
            writer.write(personalExpenses);
            writer.newLine();
            writer.write("---------------------------------------------------------------------------------------------------------------------");
            writer.newLine();
            writer.newLine();
            writer.write(insights);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}