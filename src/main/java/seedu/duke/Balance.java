package seedu.duke;

import seedu.duke.exceptions.ExpensesException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Balance {
    protected String userName;
    protected Map<String, Float> balanceList;

    public Balance(String userName, Map<String, Float> userList) {
        this.userName = userName;
        this.balanceList = userList;
    }

    public Balance(String userName, Group group){
        this(userName, group.getExpenseList(), group.getMembers());
    }

    /**
     * Populates balanceList with list of Users and list of Expenses in Group.
     *
     * @param userName The name of User to print the balance of.
     * @param expenses The list of Expenses in Group.
     * @param users The list of Users in Group
     */
    public Balance(String userName, List<Expense> expenses, List<User> users) {
        this.userName = userName;
        this.balanceList = new HashMap<>();

        // Populate balanceList with other Users from Group
        for (User user : users) {
            if(!user.getName().equals(userName)) {
                balanceList.put(user.getName(), 0f);
            }
        }

        // Add Expenses to balanceList
        for (Expense expense : expenses) {
            addExpense(expense);
        }
    }

    public String getUserName() {
        return userName;
    }

    public Map<String, Float> getBalanceList() {
        return balanceList;
    }

    /**
     * Adds an Expense to balanceList.
     *
     * @param expense The Expense to be added.
     */
    private void addExpense(Expense expense) {
        String payerName = expense.getPayerName();
        List<Pair<String, Float>> payees = expense.getPayees();

        if(payerName.equals(userName)) {
            for(Pair<String, Float> payee : payees) {
                String payeeName = payee.getKey();
                Float payeeAmount = payee.getValue();

                if(payeeName.equals(userName)){
                    continue;
                }

                Float currentOwed = balanceList.get(payeeName);
                Float newOwed = currentOwed + payeeAmount;

                balanceList.put(payeeName, newOwed);
            }
        } else {
            for(Pair<String, Float> payee : payees) {
                String payeeName = payee.getKey();
                Float payeeAmount = payee.getValue();

                if (!payeeName.equals(userName)) {
                    continue;
                }

                Float currentOwed = balanceList.get(payerName);
                Float newOwed = currentOwed - payeeAmount;

                balanceList.put(payerName, newOwed);
                break;
            }
        }
    }

    public void printBalance() {
        StringBuilder printOutput = new StringBuilder();
        printOutput.append(String.format("User %s's Balance List:", userName));

        for (Map.Entry<String, Float> entry : balanceList.entrySet()) {
            printOutput.append(String.format("\n  %s : %.2f", entry.getKey(), entry.getValue()));
        }

        printOutput.append("\nEnd of Balance List");
        UserInterface.printMessage(printOutput.toString(), MessageType.SUCCESS);
    }
}
