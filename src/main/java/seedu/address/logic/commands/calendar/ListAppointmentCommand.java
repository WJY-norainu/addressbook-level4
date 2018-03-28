package seedu.address.logic.commands.calendar;

import static java.util.Objects.requireNonNull;
import static seedu.address.ui.CalendarPanel.AGENDA_VIEW_PAGE;

import java.time.LocalDate;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.ShowAppointmentListEvent;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;

/**
 * Lists all appointments within two dates in a calendar.
 */
public class ListAppointmentCommand extends Command {

    public static final String COMMAND_WORD = "listAppointment";
    public static final String COMMAND_ALIAS = "la";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all appointments in a celebrity calendar within a certain date range. ";

    public static final String MESSAGE_SUCCESS = "Listed appointments successfully.";

    public static final String MESSAGE_NO_APPTS_ERROR = "No appointments to list!";

    private LocalDate startDate;
    private LocalDate endDate;

    public ListAppointmentCommand() {

    }

    public ListAppointmentCommand(LocalDate startDate, LocalDate endDate) {
        requireNonNull(startDate);
        requireNonNull(endDate);

        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public CommandResult execute() throws CommandException {
        if (!model.getStorageCalendar().hasAtLeastOneAppointment()) {
            throw new CommandException(MESSAGE_NO_APPTS_ERROR);
        }
        startDate = model.getStorageCalendar().getEarliestDate();
        endDate = model.getStorageCalendar().getLatestDate();

        model.setCelebCalendarViewPage(AGENDA_VIEW_PAGE);
        EventsCenter.getInstance().post(new ShowAppointmentListEvent(model.getStorageCalendar()
                .getAppointmentsWithinDate(startDate, endDate)));

        return new CommandResult(MESSAGE_SUCCESS);
    }
}
