package org.logic;

import java.util.Stack;

public class UndoRedoManager {
    private final Stack<Transaction> undoStack = new Stack<>();
    private final Stack<Transaction> redoStack = new Stack<>();

    public void saveState(Transaction transaction) {
        Transaction copy;
        if (transaction instanceof SplitTransaction) {
            copy = new SplitTransaction((SplitTransaction) transaction);
        } else {
            copy = new Transaction(transaction);
        }
        copy.setType(transaction.getType());
        undoStack.push(copy);
        redoStack.clear();
    }

    public Transaction undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return null;
        }

        Transaction lastTransaction = undoStack.pop();
        Transaction copy;
        if (lastTransaction instanceof SplitTransaction) {
            copy = new SplitTransaction((SplitTransaction) lastTransaction);
        } else {
            copy = new Transaction(lastTransaction);
        }
        copy.setType(lastTransaction.getType());
        redoStack.push(copy);

        System.out.println("Undo completed.");
        return lastTransaction;
    }

    public Transaction redo() {
        if (redoStack.isEmpty()) {
            System.out.println("Nothing to redo.");
            return null;
        }

        Transaction nextTransaction = redoStack.pop();
        Transaction copy;
        if (nextTransaction instanceof SplitTransaction) {
            copy = new SplitTransaction((SplitTransaction) nextTransaction);
        } else {
            copy = new Transaction(nextTransaction);
        }
        copy.setType(nextTransaction.getType());
        undoStack.push(copy);

        System.out.println("Redo completed.");
        return nextTransaction;
    }
}