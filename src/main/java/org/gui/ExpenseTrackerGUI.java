package org.gui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.logic.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ExpenseTrackerGUI extends Application {

    private final ArrayList<Transaction> transactions;

    public ExpenseTrackerGUI() {
        transactions = new ArrayList<>();
    }

    @Override
    public void start(Stage primaryStage) {
        // Main layout
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Header section
        Label headerLabel = new Label("Expense Tracker");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Load file button
        Button loadFileButton = new Button("Load Transactions File");
        loadFileButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 15px;");
        loadFileButton.setOnAction(e -> loadTransactionsFile(primaryStage));

        // Placeholder for transaction details
        Label transactionLabel = new Label("No transactions file loaded.");
        transactionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        // Add elements to the layout
        root.getChildren().addAll(headerLabel, loadFileButton, transactionLabel);

        // Scene setup
        // Class-level variable to store the main scene
        Scene mainScene = new Scene(root, 600, 350); // Store the main scene
        primaryStage.setTitle("Expense Tracker");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void loadTransactionsFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Transactions File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            FileOpener fileOpener = new FileOpener(filePath);
            ArrayList<String> transactionLines = fileOpener.getTransactions();
            TransactionParser transactionParser = new TransactionParser(transactionLines);
            if (!transactionParser.getTransactions().isEmpty()) {
                for (Transaction transaction : transactionParser.getTransactions()) {
                    if (transaction.getAmount() >= 0) {  // Check if the amount is positive
                        transactions.add(transaction);  // Only add if the amount is positive
                    }
                }
                txtInfo(stage, selectedFile);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "No transactions available. Try again with a different file", ButtonType.OK);
                alert.setHeaderText("No transactions");

                // Show the alert and handle result
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Try again with a different file", ButtonType.OK);
            alert.setHeaderText("File Error");

            // Show the alert and handle result
            alert.showAndWait();
        }
    }

    private void txtInfo(Stage primaryStage, File file) {
        // Create new layout for the next stage
        VBox nextRoot = new VBox(15);
        nextRoot.setPadding(new Insets(20));
        nextRoot.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome to the Expense Tracker");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label fileLabel = new Label("You loaded: " + file.getName() + " with " + transactions.size() + " Transactions");
        fileLabel.setStyle("-fx-font-size: 14px;");

        Button startButton = new Button("Start");
        startButton.setOnAction(e -> displayTransactions(primaryStage)); // Navigate to display transactions

        nextRoot.getChildren().addAll(welcomeLabel, fileLabel, startButton);

        // Set new scene on the primary stage
        Scene nextScene = new Scene(nextRoot, 600, 350);
        primaryStage.setScene(nextScene);
    }
    private int currentTransactionIndex = 0;

    private void displayTransactions(Stage primaryStage) {
        TransactionCategorizer categorizer = new TransactionCategorizer();

        // Layout for displaying transactions
        BorderPane transactionRoot = new BorderPane();
        transactionRoot.setPadding(new Insets(15));

        // Transaction display area (centered rectangle)
        Label transactionDisplay = new Label();
        transactionDisplay.setStyle("-fx-font-size: 16px; -fx-border-color: black; "
                + "-fx-border-width: 1px; -fx-padding: 10px;");
        transactionDisplay.setAlignment(Pos.CENTER);
        transactionDisplay.setPrefSize(1500, 200);
        transactionRoot.setCenter(transactionDisplay);

        // Buttons for classifying transactions
        HBox classificationButtons = new HBox(20);
        classificationButtons.setAlignment(Pos.CENTER);
        classificationButtons.setPadding(new Insets(10));

        Button finishButton = new Button("Finish?");
        finishButton.setPrefSize(100, 25); // Set preferred width and height
        finishButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px;"); // Larger font and padding
        finishButton.setVisible(false); // Initially hidden

        finishButton.setTranslateX(12);

        finishButton.setOnAction(e -> {
            exportExpensesToFile();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Open file", ButtonType.OK);
            alert.setHeaderText("All transactions are categorized!");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    openExpensesFile();
                    Platform.exit();
                }
            });
        });

        Button personalButton = new Button("Personal");
        personalButton.setOnAction(e -> {
            if (!(currentTransactionIndex == transactions.size())) {
                Transaction updatedTransaction = categorizer.categorize(transactions.get(currentTransactionIndex), "Personal");
                updateTransaction(updatedTransaction, transactionDisplay);
                moveToNextTransaction(primaryStage, transactionDisplay, finishButton);
            } else {
                showError("Last Transaction", "No more transactions to categorize. Undo, Redo or Finish.");
            }
        });

        Button generalButton = new Button("General");
        generalButton.setOnAction(e -> {
            if (!(currentTransactionIndex == transactions.size())) {
                Transaction updatedTransaction = categorizer.categorize(transactions.get(currentTransactionIndex), "General");
                updateTransaction(updatedTransaction, transactionDisplay);
                moveToNextTransaction(primaryStage, transactionDisplay, finishButton);
            } else {
                showError("Last Transaction", "No more transactions to categorize. Undo, Redo or Finish.");
            }
        });

        Button splitButton = new Button("Split");
        splitButton.setOnAction(e -> {
            if (!(currentTransactionIndex == transactions.size())) {
                handleSplitTransaction(categorizer, transactionDisplay, primaryStage, finishButton);
            } else {
                showError("Last Transaction", "No more transactions to categorize. Undo, Redo or Finish.");
            }
        });

        classificationButtons.getChildren().addAll(personalButton, generalButton, splitButton);

        // Navigation buttons
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(e -> {
            Transaction undoneTransaction = categorizer.undo();
            if (undoneTransaction != null) {
                if (currentTransactionIndex > 0) {
                    currentTransactionIndex--; // Move to the previous transaction
                    finishButton.setVisible(false);
                }
                transactions.set(currentTransactionIndex, undoneTransaction);
                updateTransactionDisplay(transactionDisplay, currentTransactionIndex, finishButton);
            } else {
                showError("Undo Error", "No more actions to undo.");
            }
        });

        Button redoButton = new Button("Redo");
        redoButton.setOnAction(e -> {
            Transaction redoneTransaction = categorizer.redo();
            if (redoneTransaction != null) {
                transactions.set(currentTransactionIndex, redoneTransaction);
                currentTransactionIndex++; // Move to the next transaction
                updateTransactionDisplay(transactionDisplay, currentTransactionIndex, finishButton);
            } else {
                showError("Redo Error", "No more actions to redo.");
            }
        });

        HBox navigationButtons = new HBox(20);
        navigationButtons.setAlignment(Pos.CENTER);
        navigationButtons.setPadding(new Insets(10));
        navigationButtons.getChildren().addAll(undoButton, classificationButtons, redoButton);

        VBox bottomLayout = new VBox(10);
        bottomLayout.setAlignment(Pos.CENTER);
        bottomLayout.getChildren().addAll(finishButton, navigationButtons);

        transactionRoot.setBottom(bottomLayout);

        // Display the first transaction (if available)
        updateTransactionDisplay(transactionDisplay, currentTransactionIndex, finishButton);

        // Create and set the new scene
        Scene transactionScene = new Scene(transactionRoot, 1500, 400);
        primaryStage.setScene(transactionScene);
        primaryStage.centerOnScreen();
    }

    // Updates the transaction display to show the current transaction
    private void updateTransactionDisplay(Label transactionDisplay, int index, Button finishButton) {
        if (transactions.isEmpty()) {
            transactionDisplay.setText("No Transactions Available");
        } else if (currentTransactionIndex < transactions.size()) {
            Transaction currentTransaction = transactions.get(currentTransactionIndex);
            transactionDisplay.setText(currentTransaction.toString());
        } else {
            transactionDisplay.setText("All transactions are categorized!");
            finishButton.setVisible(true);
        }
    }

    // Updates the transaction in the list and refreshes the display
    private void updateTransaction(Transaction updatedTransaction, Label transactionDisplay) {
        transactions.set(currentTransactionIndex, updatedTransaction);
    }

    // Handles a split transaction with input validation
    private void handleSplitTransaction(TransactionCategorizer categorizer, Label transactionDisplay, Stage primaryStage, Button finishButton) {
        if (transactions.isEmpty()) return;

        Transaction currentTransaction = transactions.get(currentTransactionIndex);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Split Transaction");
        dialog.setHeaderText("Enter the amount to split as personal expense:");
        dialog.setContentText("Split Amount:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                double splitAmount = Double.parseDouble(input);
                Transaction updatedTransaction = categorizer.categorize(currentTransaction, "Split", splitAmount);
                updateTransaction(updatedTransaction, transactionDisplay);
                moveToNextTransaction(primaryStage, transactionDisplay, finishButton);
            } catch (NumberFormatException ex) {
                showError("Invalid Input", "Please enter a valid number.");
            } catch (IllegalArgumentException ex) {
                showError("Invalid Split Amount", ex.getMessage());
            }
        });
    }

    // Moves to the next transaction after categorizing the current one
    private void moveToNextTransaction(Stage primaryStage, Label transactionDisplay, Button finishButton) {
        if (currentTransactionIndex < transactions.size() - 1) {
            currentTransactionIndex++;
            updateTransactionDisplay(transactionDisplay, currentTransactionIndex, finishButton);
        } else {
            transactionDisplay.setText("All transactions are categorized!");
            currentTransactionIndex++;
            finishButton.setVisible(true);
        }
    }


    private void exportExpensesToFile() {
        // Create ExpenseCalculator and calculate totals
        ExpenseCalculator expenseCalculator = new ExpenseCalculator();
        expenseCalculator.calculateTotals(transactions);

        // Format the expenses using ExpenseFormatter
        ExpenseFormatter expenseFormatter = new ExpenseFormatter();
        expenseFormatter.formatExpenses(transactions, expenseCalculator);

        // Generate insights
        InsightProvider insightProvider = new InsightProvider(transactions);
        String insights = insightProvider.getFormattedInsights();

        // Export the formatted expenses and insights using FileExporter
        new FileExporter("Expenses.txt",
                expenseFormatter.getGeneralExpenses(),
                expenseFormatter.getPersonalExpenses(),
                insights);

        System.out.println("Expenses and insights exported to Expenses.txt");
    }

    private void openExpensesFile() {
        // The file path should be the one where the file was saved.
        File file = new File("Expenses.txt");

        // Check if the file exists and open it
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file); // Open the file with the default program
            } catch (IOException e) {
                showError("Error Opening File", "Could not open the file.");
            }
        } else {
            showError("File Not Found", "The file was not found. Please check the file path.");
        }
    }

    // Displays an error dialog
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
