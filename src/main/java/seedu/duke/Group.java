//@@author avrilgk

package seedu.duke;

import seedu.duke.exceptions.GroupLoadException;
import seedu.duke.exceptions.GroupSaveException;
import seedu.duke.storage.GroupNameChecker;
import seedu.duke.storage.GroupStorage;
import seedu.duke.storage.FileIOImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Group {
    static Map<String, Group> groups = new HashMap<>();
    private static Optional<String> currentGroupName = Optional.empty();
    private static final GroupStorage groupStorage = new GroupStorage(new FileIOImpl());
    private static GroupNameChecker groupNameChecker = new GroupNameChecker();


    private final String groupName;
    private final List<User> members;
    private final List<Expense> expenseList;

    public Group(String groupName) {
        this.groupName = groupName;
        this.members = new ArrayList<>();
        this.expenseList = new ArrayList<>();
    }

    /**
     * Retrieves an existing group by its name or creates a new one if it does not exist.
     * It ensures that a user cannot create or join a new group without exiting their current group.
     *
     * @param groupName The name of the group to get or create.
     * @return The existing or newly created group.
     * @throws IllegalStateException If trying to create or join a new group while already in another group.
     */
    public static Optional<Group> getOrCreateGroup(String groupName) {

        // Check if group name is empty
        if (groupName == null || groupName.trim().isEmpty()) {
            System.out.println("Group name cannot be empty. Please try again.");
            return Optional.empty();
        }

        // Check if user is accessing a group they are already in
        getCurrentGroup().ifPresent(currentGroup -> {
            if (currentGroup.getGroupName().equals(groupName)) {
                System.out.println("You are already in " + groupName);
            }
        });

        // If the user is in a different group, prevent them from creating or joining a new group.
        if (isInGroup()) {
            getCurrentGroup().ifPresent(currentGroup -> {
                if (!currentGroup.getGroupName().equals(groupName)) {
                    throw new IllegalStateException("Please exit the current group '" + currentGroup.getGroupName()
                            + "' to create or join another group.");
                }
            });
        }

        Optional<Group> group = Optional.ofNullable(groups.get(groupName));

        // Create a new group if it doesn't exist
        if (group.isEmpty() && !groupNameChecker.doesGroupNameExist(groupName)) {
            Group newGroup = new Group(groupName);
            groups.put(groupName, newGroup);
            System.out.println(groupName + " created.");
            currentGroupName = Optional.of(groupName);
            group = Optional.of(newGroup);
        } else if (groupNameChecker.doesGroupNameExist(groupName)) {
            System.out.println("Group already exists. Use 'enter " + groupName + "' to enter the group.");
            return Optional.empty();
        }

        System.out.println("You are now in " + groupName);
        return group;
    }

    /**
     * Enter existing group.
     *
     * @param groupName The name of the group to enter.
     * @return The existing group.
     */
    public static Optional<Group> enterGroup(String groupName) {
        Optional<Group> group = Optional.ofNullable(groups.get(groupName));
        if (group.isEmpty()) {
            //@@author hafizuddin-a
            try {
                // If the group doesn't exist in memory, try loading it from file
                Group loadedGroup = groupStorage.loadGroupFromFile(groupName);
                if (loadedGroup != null) {
                    groups.put(groupName, loadedGroup);
                    group = Optional.of(loadedGroup);
                } else {
                    //@@ author avrilgk
                    System.out.println("Group does not exist.");
                    return Optional.empty();
                }
                // @@author hafizuddin-a
            } catch (GroupLoadException e) {
                System.out.println("Error loading group: " + e.getMessage());
                return Optional.empty();
            }
        }
        //@@author avrilgk
        currentGroupName = Optional.of(groupName);
        System.out.println("You are now in " + groupName);
        return group;
    }

    /**
     * Exits the current group.
     * If the user is not in any group, it displays a message asking the user to try again.
     */
    public static void exitGroup() {
        if (currentGroupName.isPresent()) {
            //@@author hafizuddin-a
            try {
                groupStorage.saveGroupToFile(groups.get(currentGroupName.get()));
                System.out.println("Group data saved successfully.");
            } catch (GroupSaveException e) {
                System.out.println("Error saving group: " + e.getMessage());
            }
            //@@author avrilgk
            System.out.println("You have exited " + currentGroupName.get() + ".");
            currentGroupName = Optional.empty();
        } else {
            System.out.println("You are not currently in any group. Please try again.");
        }
    }

    /**
     * Retrieves the current group.
     *
     * @return The current group, or null if the user is not in any group.
     */
    public static Optional<Group> getCurrentGroup() {
        return currentGroupName.map(groups::get);
    }

    /**
     * Checks if the user is currently in a group.
     *
     * @return true if the user is in a group, false otherwise.
     */
    public static boolean isInGroup() {
        return currentGroupName.isPresent();
    }

    /**
     * Checks if a user with the given name is a member of the group.
     *
     * @param memberName The name of the member to check.
     * @return true if the user is a member of the group, false otherwise.
     */
    public boolean isMember(String memberName) {
        for (User member : members) {
            if (member.getName().equals(memberName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a new member to the group.
     *
     * @param memberName The name of the member to add.
     * @return The newly added user, or null if the user is already a member of the group.
     */
    public User addMember(String memberName) {
        if (memberName == null || memberName.isEmpty()) {
            System.out.println("Please provide a valid member name.");
            return null;
        }

        if (isMember(memberName)) {
            System.out.println(memberName + " is already a member of " + groupName + ".");
            return null;
        }

        User newMember = new User(memberName);
        members.add(newMember);
        System.out.println(memberName + " has been added to " + groupName + ".");
        return newMember;
    }

    /**
     * Adds a new expense to the group.
     *
     * @param expense The Expense object to add.
     */
    public void addExpense(Expense expense) {
        expenseList.add(expense);
    }

    /**
     * Retrieves the name of the group.
     *
     * @return The name of the group.
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Retrieves the list of members in the group.
     *
     * @return The list of members in the group.
     */
    public List<User> getMembers() {
        return new ArrayList<>(members);
    }

    /**
     * Retrieves the list of expenses in the group.
     *
     * @return The list of expenses in the group.
     */
    public List<Expense> getExpenseList() {
        return new ArrayList<>(expenseList);
    }

    public void settle(String payerName, String payeeName) {
        User payer = findUser(payerName);
        User payee = findUser(payeeName);

        if (payer == null || payee == null) {
            System.out.println("User not found.");
            return;
        }

        double amount = calculateOutstandingAmount(payee, payer);

        if (amount > 0) {
            Settle settle = new Settle(payer, payee, amount);
            addExpense(settle);
            System.out.println(payerName + " should pay " + payeeName + " " + String.format("%.2f", amount));
        } else {
            System.out.println(payerName + " does not owe " + payeeName + " any money.");
        }
    }

    private User findUser(String userName) {
        for (User user : members) {
            if (user.getName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    private double calculateOutstandingAmount(User payer, User payee) {
        double totalAmount = 0;
        for (Expense expense : expenseList) {
            if (expense.getPayer().equals(payer.getName())) {
                for (Pair<String, Float> user : expense.getPayees()) {
                    if (user.getKey().equals(payee.getName())) {
                        totalAmount += user.getValue();
                    }
                }
            } else if (expense.getPayer().equals(payee.getName())) {
                for (Pair<String, Float> user : expense.getPayees()) {
                    if (user.getKey().equals(payer.getName())) {
                        totalAmount -= user.getValue();
                    }
                }
            }
        }
        return totalAmount;
    }
}

