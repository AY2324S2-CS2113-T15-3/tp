//@@author hafizuddin-a

package seedu.duke;

import seedu.duke.exceptions.GroupDeleteException;
import seedu.duke.storage.FileIO;
import seedu.duke.storage.FileIOImpl;
import seedu.duke.storage.GroupStorage;

import java.util.Optional;

/**
 * Represents a command handler for group-related operations.
 * Provides static methods to create groups, add members to groups, and exit groups.
 */
public class GroupCommand {
    /**
     * Creates a new group or retrieves an existing group with the specified name.
     *
     * @param groupName the name of the group to create or retrieve
     */
    public static void createGroup(String groupName) {
        try {
            Group.getOrCreateGroup(groupName);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    //@@author avrilgk

    /**
     * Deletes the current group.
     * If the user is not currently in a group, prints a message indicating so.
     *
     * @param groupName the name of the group to delete
     */
    public static void deleteGroup(String groupName) {
        if (Group.groups.containsKey(groupName)) {
            Group.groups.remove(groupName);
            deleteGroupFile(groupName);
            System.out.println("The group " + groupName + " has been deleted.");
        } else {
            System.out.println("The group " + groupName + " does not exist.");
        }
    }

    //@@author hafizuddin-a
    /**
     * Deletes the group file for the specified group name.
     *
     * @param groupName the name of the group whose file needs to be deleted
     */
    private static void deleteGroupFile(String groupName) {
        try {
            FileIO fileIO = new FileIOImpl();
            GroupStorage groupStorage = new GroupStorage(fileIO);
            groupStorage.deleteGroupFile(groupName);
        } catch (GroupDeleteException e) {
            System.out.println("Failed to delete the group file: " + e.getMessage());
        }
    }

    /**
     * Adds a member with the specified name to the current group.
     * If the user is not currently in a group, prints a message asking them to create or join a group first.
     *
     * @param memberName the name of the member to add
     */
    public static void addMember(String memberName) {
        Optional<Group> currentGroup = Group.getCurrentGroup();
        if (currentGroup.isEmpty()) {
            System.out.println("Please create or join a group first.");
            return;
        }

        currentGroup.get().addMember(memberName);
    }

    /**
     * Enters an existing group with the specified name.
     *
     * @param groupName the name of the group to enter
     */

    public static void enterGroup(String groupName) {
        Group.enterGroup(groupName);
    }

    /**
     * Exits the current group.
     * If the user is not currently in a group, prints a message indicating so.
     */
    public static void exitGroup(String groupName) {
        Group.exitGroup(groupName);
    }
}

