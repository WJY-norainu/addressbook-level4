package systemtests;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.TypicalTags.FRIENDS_TAG;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.RemoveTagCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.tag.Tag;

public class RemoveTagCommandSystemTest extends AddressBookSystemTest {

    @Test
    public void removeTag() {
        Model model = getModel();

        /* ----------------------------------- Perform valid removeTag operations ----------------------------------- */

        /* Case: remove tag friends from a non-empty address book, command with leading spaces and trailing spaces
         * -> tag removed and shows 0 person affected
         */
        Tag toRemove = FRIENDS_TAG;
        String command = "   " + RemoveTagCommand.COMMAND_WORD + "  " + VALID_TAG_FRIEND + " ";
        assertCommandSuccess(command, toRemove);

        /* Case: undo removing tag friends from the list -> tag friends restored */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo removing tag friends from the list -> tag friends added again */
        command = RedoCommand.COMMAND_WORD;
        model.removeTag(toRemove);
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: remove tag friend from an empty address book -> tag removed and shows 0 person affected */
        deleteAllPersons();
        assertCommandSuccess(toRemove);

        /* ----------------------------------- Perform invalid removeTag operations --------------------------------- */

        /* Case: missing tag -> rejected */
        command = RemoveTagCommand.COMMAND_WORD + "";
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveTagCommand.MESSAGE_USAGE));

        /* Case: invalid keyword -> rejected */
        command = "removesTag " + VALID_TAG_HUSBAND;
        assertCommandFailure(command, Messages.MESSAGE_UNKNOWN_COMMAND);

        /* Case: invalid tag -> rejected */
        command = RemoveTagCommand.COMMAND_WORD + INVALID_TAG_DESC;
        assertCommandFailure(command, Tag.MESSAGE_TAG_CONSTRAINTS);
    }

    /**
     * Executes the {@code RemoveTagCommand} that removes {@code toRemove} from the model and asserts that the,<br>
     * 1. Command box displays an empty string.<br>
     * 2. Command box has the default style class.<br>
     * 3. Result display box displays the success message of executing {@code RemoveTagCommand} with the details of
     * {@code toRemove}.<br>
     * 4. {@code Model}, {@code Storage} and {@code PersonListPanel} equal to the corresponding components in
     * the original empty model.<br>
     * 5. Browser url and selected card remain unchanged.<br>
     * 6. Status bar's sync status changes.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(Tag toRemove) {
        assertCommandSuccess(RemoveTagCommand.COMMAND_WORD + " " + toRemove.tagName, toRemove);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(Tag)}. Executes {@code command}
     * instead.
     * @see RemoveTagCommandSystemTest#assertCommandSuccess(Tag)
     */
    private void assertCommandSuccess(String command, Tag toRemove) {
        Model expectedModel = getModel();
        int numPersonsAffected = expectedModel.removeTag(toRemove);
        String expectedResultMessage = String.format(RemoveTagCommand.MESSAGE_DELETE_TAG_SUCCESS,
                                                    toRemove,
                                                    numPersonsAffected);

        assertCommandSuccess(command, expectedModel, expectedResultMessage);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Tag)} except asserts that
     * the,<br>
     * 1. Result display box displays {@code expectedResultMessage}.<br>
     * 2. {@code Model}, {@code Storage} and {@code PersonListPanel} equal to the corresponding components in
     * {@code expectedModel}.<br>
     * @see RemoveTagCommandSystemTest#assertCommandSuccess(String, Tag)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertResultDisplayAndCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and asserts that the,<br>
     * 1. Command box displays {@code command}.<br>
     * 2. Command box has the error style class.<br>
     * 3. Result display box displays {@code expectedResultMessage}.<br>
     * 4. {@code Model}, {@code Storage} and {@code PersonListPanel} remain unchanged.<br>
     * 5. Browser url, selected card and status bar remain unchanged.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
