package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARTICIPATION_POINTS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TUTORIAL;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * Class to input participation points to an existing student in for a specified week
 */
public class InputParticipationCommand extends Command {
    public static final String COMMAND_WORD = "inputPP";
    public static final String SUCCESS_MSG = "Participation points input successfully!";
    public static final String ATTENDANCE_NOT_MARKED = "Before inputting participation points, "
            + "mark the attendance of the student first!";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Inserts participation points to the student identified\n"
            + "by the index number used in the displayed student list.\n"
            + "Parameters: INDEX (must be a positive integer), "
            + "[" + PREFIX_TUTORIAL + "TutorialToMark] \n"
            + "[" + PREFIX_PARTICIPATION_POINTS + "ParticipationPoints] \n"
            + "Example: " + COMMAND_WORD + " 1 t/1 pp/200";
    private final Index index;
    private final Index tut;
    private final int points;

    /**
     * Constructor for inputParticipationCommand
     * @param index index of student to update
     * @param tut tutorial to update
     */
    public InputParticipationCommand(Index index, Index tut, int points) {
        this.index = index;
        this.tut = tut;
        this.points = points;
    }

    /**
     * Executes the InputParticipationCommand to input participation for a student on a specific tutorial.
     *
     * @param model The model that the command operates on.
     * @return A CommandResult indicating the outcome of the execution.
     * @throws CommandException If there is an error in executing the command.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person studentToEdit = lastShownList.get(index.getZeroBased());
        Person editedStudent = new Person(
                studentToEdit.getName(), Optional.of(studentToEdit.getPhone()), Optional.of(studentToEdit.getEmail()),
                Optional.of(studentToEdit.getTelegramHandle()), Optional.of(studentToEdit.getAttendance()),
                studentToEdit.getTags(),
                studentToEdit.getComments(), studentToEdit.getAssignments(), Optional.of(studentToEdit.getGroup()));
        Attendance studentAtd = studentToEdit.getAttendance();
        if (!studentAtd.isMarkedWeek(this.tut.getZeroBased())) {
            return new CommandResult(ATTENDANCE_NOT_MARKED);
        }

        studentAtd.inputParticipationPoints(this.tut.getZeroBased(), this.points);
        model.setPerson(studentToEdit, editedStudent);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(SUCCESS_MSG);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof InputParticipationCommand)) {
            return false;
        }

        InputParticipationCommand otherInputParticipationCommand = (InputParticipationCommand) other;
        return index.equals(otherInputParticipationCommand.index) && tut.equals(otherInputParticipationCommand.tut)
                && points == otherInputParticipationCommand.points;
    }
}