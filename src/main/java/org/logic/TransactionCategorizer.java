package org.logic;

public class TransactionCategorizer {
    private final UndoRedoManager undoRedoManager = new UndoRedoManager();

    /**
     * Categorizes a transaction with the given category.
     *
     * @param transaction The transaction to be categorized.
     * @param category The category to assign (e.g., "Personal", "General", "Split").
     * @return The updated transaction.
     */
    public Transaction categorize(Transaction transaction, String category, double... splitAmount) {
        Transaction updatedTransaction;
        if (category.equalsIgnoreCase("Personal")) {
            updatedTransaction = new Transaction(transaction);
            updatedTransaction.setType("Personal");
        } else if (category.equalsIgnoreCase("General")) {
            updatedTransaction = new Transaction(transaction);
            updatedTransaction.setType("General");
        } else if (category.equalsIgnoreCase("Split") && splitAmount.length > 0) {
            updatedTransaction = splitTransaction(transaction, splitAmount[0]);
        } else {
            updatedTransaction = transaction;
        }

        // Save the updated state
        undoRedoManager.saveState(updatedTransaction);

        return updatedTransaction;
    }

    /**
     * Splits a transaction into personal and general parts.
     *
     * @param transaction The transaction to be split.
     * @param splitAmount The amount to allocate as personal expense.
     * @return A new SplitTransaction with the updated values.
     */
    private SplitTransaction splitTransaction(Transaction transaction, double splitAmount) {
        if (splitAmount <= 0 || splitAmount >= transaction.getAmount()) {
            throw new IllegalArgumentException("Split amount must be between 0 and " + transaction.getAmount());
        }

        return new SplitTransaction(transaction.date, transaction.postedDate, transaction.description,
                transaction.getAmount(), transaction.category, splitAmount);
    }

    /**
     * Undoes the last categorization.
     *
     * @return The previous state of the transaction, or null if there's nothing to undo.
     */
    public Transaction undo() {
        return undoRedoManager.undo();
    }

    /**
     * Redoes the last undone categorization.
     *
     * @return The redone transaction, or null if there's nothing to redo.
     */
    public Transaction redo() {
        return undoRedoManager.redo();
    }
}
