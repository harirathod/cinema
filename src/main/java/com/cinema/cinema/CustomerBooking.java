package com.cinema.cinema;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class is to be used when the customer is making a booking.
 *
 * This class implements the functionality of commands and evaluates them.
 * @author Hari Rathod
 * @version 2023.02.05
 */

public class CustomerBooking {
    private Parser parser;
    private final TicketOffice office;
    private UserInputRecorder userInputRecorder;
    private View view;
    private final ObjectDataRecorder<Ticket> ticketDataRecorder = new ObjectDataRecorder<>(Filename.TICKET, Ticket.class);
    private final ObjectDataRecorder<Screen> screenDataRecorder = new ObjectDataRecorder<>(Filename.SCREEN, Screen.class);

    /**
     * Constructor to initialise fields.
     */
    public CustomerBooking()
    {
        parser = new Parser(System.in);
        office = new TicketOffice();
        view = new TextView();

        try {
            ticketDataRecorder.resetFile();
        } catch (IOException e) {
            view.displayError("There was an error resetting ticket history." + e.getMessage());
        }

        try {
            userInputRecorder = new UserInputRecorder();
        } catch (IOException e) {
            view.displayError("There was an error writing to " + e.getMessage());
        }

        populateScreens();
    }

    /**
     * Starts the booking, and continue to process user input until the user quits the application.
     */
    public void start()
    {
        view.display("Welcome to Glacier Cinema!");

        // While the user is not finished, get the next command and evaluate it.
        Command command;
        do {
            String input = parser.readInput();
            recordInputString(input);
            command = CommandConverter.convertToCommand(input);
            evaluateCommand(command);
        }
        while (command.getCommandWord() != CommandWord.QUIT);

        view.displayWithFormatting("Thanks for visiting, and have a great time!");
    }

    /**
     * Write this string to a text file.
     * @param inputString The string to be written.
     */
    private void recordInputString(String inputString)
    {
        try {
            userInputRecorder.writeStringToFile(inputString.toString());
        } catch (IOException e) {
            view.displayError("Error writing to file " + e.getMessage());
        }
    }

    /**
     * Process and evaluate the command entered by calling appropriate methods.
     * If null is entered, it evaluates the command as Command.UNKNOWN.
     *
     * @param command The command to evaluate.
     */
    public void evaluateCommand(Command command)
    {
        // Evaluate the command.
        switch (command.getCommandWord()) {
            case HELP -> help();
            case BOOK -> book();
            case LIST -> list(command);
            case BASKET -> showTickets();
            case QUIT -> {}
            default -> unknown();
        }
    }

    /**
     * Provide helpful information for the user.
     */
    private void help()
    {
        view.displayWithFormatting("""
                With our booking platform you can book tickets to movies.
                These are the available commands:""" + CommandWord.getAllCommands());
    }

    /**
     * Allows the user to book a seat for a movie, by prompting them to enter the name of the movie, then the
     * seat numbers.
     */
    private void book()
    {
        view.display("Which movie would you like to book a ticket for?");
        String movie = parser.readInput();

        Screen screen;
        try {
            // Get the screen that is screening the movie.
            screen = office.validateMovieTitle(movie);
        } catch (MovieDoesNotExistException e) {
            view.displayError(e.getMessage());
            return;
        }

        view.display("Current screening of the movie:\n" + screen.getDetails());
        view.display("Which seat would you like to book?");
        String[] seatPosition;

        // Use regex to check the row and column values entered are parsable integers.
        Pattern numberPattern = Pattern.compile("\\d+");
        do {
            view.display("Please provide the seat as '<column>, <row>'. Example: 3, 4");
            seatPosition = parser.readInputAsArray();
        } while (seatPosition.length < 2 || (!(numberPattern.matcher(seatPosition[0]).matches() && numberPattern.matcher(seatPosition[1]).matches())));

        int columnNumber = Integer.parseInt(seatPosition[0]);
        int rowNumber = Integer.parseInt(seatPosition[1]);
        try {
            // Check that the seat numbers are a valid position for that screen.
            screen.validateSeatNumbers(columnNumber, rowNumber);
        } catch (InvalidSeatException e) {
            view.displayError(e.getMessage());
            return;
        }

        try {
            Ticket ticket = office.bookTicket(movie, columnNumber, rowNumber);
            try {
                // Store the ticket in the tickets data file.
                ticketDataRecorder.writeToFile(ticket);
                view.displayWithFormatting("Ticket successfully added to basket."
                        + "\nYou have %d tickets in your basket.".formatted(ticketDataRecorder.getNumberOfObjects()));
            } catch (IOException | ClassNotFoundException e) {
                view.displayError("There was an error saving your ticket.");;
            }
        } catch (UnavailableSeatException | MovieDoesNotExistException e) {
            view.displayError(e.getMessage());
        }
    }

    /**
     * List all the movies being shown at the cinema.
     * @param command The command should have no second word. The purpose of the parameter being passed, is to
     *               ensure the user is not expecting something different to be listed than what is programmed, if they
     *                type 'list timings', for example.
     */
    private void list(Command command)
    {
        if(command.hasSecondWord()) {
            view.displayWithFormatting("Please do not enter any arguments after 'list'.");
            return;
        }
        String detailsOfMovies = office.getAllMoviesDetails();
        if (detailsOfMovies.isEmpty()) {
            view.displayWithFormatting("No movies currently showing.");
        } else {
            view.displayWithFormatting(detailsOfMovies);
        }
    }

    /**
     * Print details of all tickets that the user has booked.
     */
    private void showTickets()
    {
        try {
            List<Ticket> list = ticketDataRecorder.readListOfObjectsFromFile();
            if (list.isEmpty()) {
                view.displayWithFormatting("No tickets in your basket.");
            }
            else {
                String details = "";
                for (Ticket ticket : list) {
                    details += ticket.getDetails();
                }
                view.displayWithFormatting(details);
            }
        } catch (IOException | ClassNotFoundException e) {
            view.displayError("Error getting tickets from basket.");
        }
    }

    /**
     * Print an error message, if the command is unrecognised.
     */
    private void unknown()
    {
        view.displayWithFormatting("Sorry, we didn't understand what you meant.\nPlease enter 'help' for more advice.");
    }

    /**
     * Populate the cinema with screens.
     */
    private void populateScreens() {
        try {
            List<Screen> screens = screenDataRecorder.readListOfObjectsFromFile();
            for (Screen screen : screens) {
                try {
                    office.addScreen(screen);
                } catch (ScreenIdAlreadyExistsException e) {
                    view.displayError(e.getMessage());
                }
            }
        } catch (ClassNotFoundException e) {
            view.displayError(e.getMessage());
        } catch (IOException e) {
            view.displayError("Error handling file " + screenDataRecorder.getFILENAME() + " " + e.getMessage());
        }
    }
}
