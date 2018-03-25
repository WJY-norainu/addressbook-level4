package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.model.calendar.CelebCalendar;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.exceptions.TagNotFoundException;

/**
 * Represents the in-memory model of the address book data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private static final String CALENDAR_SOURCE_NAME  = "Celeb Calendar Source";

    private final AddressBook addressBook;
    private final FilteredList<Person> filteredPersons;
    private final CalendarSource celebCalendarSource;

    private String currentCelebCalendarViewPage;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        celebCalendarSource = new CalendarSource(CALENDAR_SOURCE_NAME);
        initializeCalendarSource(celebCalendarSource);

        currentCelebCalendarViewPage = "day";
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        addressBook.resetData(newData);
        indicateAddressBookChanged();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateAddressBookChanged() {
        raise(new AddressBookChangedEvent(addressBook));
    }

    @Override
    public synchronized void deletePerson(Person target) throws PersonNotFoundException {
        addressBook.removePerson(target);
        indicateAddressBookChanged();
    }

    @Override
    public synchronized void addPerson(Person person) throws DuplicatePersonException {
        addressBook.addPerson(person);
        if (person.isCelebrity()) {
            addCelebrity(person);
        }
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    @Override
    public void addCelebrity(Person person) throws DuplicatePersonException {
        addressBook.addCelebrity(person);
        celebCalendarSource.getCalendars().add(new CelebCalendar(person));
    }

    @Override
    public void updatePerson(Person target, Person editedPerson)
            throws DuplicatePersonException, PersonNotFoundException {
        requireAllNonNull(target, editedPerson);

        addressBook.updatePerson(target, editedPerson);
        indicateAddressBookChanged();
    }

    @Override
    public int removeTag(Tag tag) throws DuplicatePersonException, PersonNotFoundException, TagNotFoundException {
        int numPersonsAffected = addressBook.removeTag(tag);
        indicateAddressBookChanged();
        return numPersonsAffected;
    }

    @Override
    public void setCelebCalendarViewPage(String newCurrentCelebCalendarViewPage) {
        currentCelebCalendarViewPage = newCurrentCelebCalendarViewPage;
    }

    //=========== Celeb Calendar Accessors ===================================================================

    @Override
    public ObservableList<Calendar> getCelebCalendars() {
        return celebCalendarSource.getCalendars();
    }

    @Override
    public CalendarSource getCelebCalendarSource() {
        return celebCalendarSource;
    }

    @Override
    public String getCurrentCelebCalendarViewPage() {
        return currentCelebCalendarViewPage;
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(filteredPersons);
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return addressBook.equals(other.addressBook)
                && filteredPersons.equals(other.filteredPersons);
    }

    /**
     * Populates our CalendarSource by creating a calendar for every celebrity in our addressbook
     */
    private void initializeCalendarSource(CalendarSource calSource) {
        requireNonNull(addressBook);
        ObservableList<Person> celebrities = addressBook.getCelebritiesList();
        for (Person celebrity : celebrities) {
            calSource.getCalendars().add(new CelebCalendar(celebrity));
        }
    }

}
