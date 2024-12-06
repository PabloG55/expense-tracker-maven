package org.logic;

import java.io.*;
import java.util.ArrayList;

public class FileOpener {
    private final ArrayList<String> transactions = new ArrayList<>();


    public FileOpener(String filePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                transactions.add(line);
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
        }
    }

    public ArrayList<String> getTransactions() {
        return new ArrayList<>(transactions);
    }
}
