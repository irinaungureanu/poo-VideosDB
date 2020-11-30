package main;

import actor.Actor;
import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import entertainment.Movie;
import entertainment.Show;
import fileio.Input;
import fileio.InputLoader;
import fileio.UserInputData;
import fileio.SerialInputData;
import fileio.ActorInputData;
import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.Writer;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import database.Database;
import user.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();

        //TODO add here the entry point to your implementation

        /*
         *  Incarcare in obiecte a datelor citite din fisier
         */
        List<ActionInputData> actionInputData = input.getCommands();
        List<ActorInputData> actorInputDataList = input.getActors();
        List<MovieInputData> movieInputData = input.getMovies();
        List<SerialInputData> showInputData = input.getSerials();
        List<UserInputData> userInputData = input.getUsers();

        Database.getInstance().clearDatabase();
        JSONObject jsonObject = new JSONObject();
//        System.out.println("TEST: " + inputLoader.getInputPath());

        // Introducerea datelor despre actori din fisierul de input in baza de date
        for (ActorInputData actor : actorInputDataList) {
            Database.getInstance().getActors().put(actor.getName(), new Actor(actor));
        }
        // Introducerea datelor despre filme din fisierul de input in baza de date
        for (MovieInputData movie : movieInputData) {
            Database.getInstance().getMovies().put(movie.getTitle(), new Movie(movie));
            Database.getInstance().getVideos().add(new Movie(movie));
        }
        // Introducerea datelor despre seriale din fisierul de input in baza de date
        for (SerialInputData show : showInputData) {
            Database.getInstance().getShows().put(show.getTitle(), new Show(show));
            Database.getInstance().getVideos().add(new Show(show));
        }
        // Introducerea datelor despre utilizatori din fisierul de input in baza de date
        for (UserInputData user : userInputData) {
            Database.getInstance().getUsers().put(user.getUsername(), new User(user));
            Database.getInstance().getUsers().get(user.getUsername()).addVideoToFavorites();
            Database.getInstance().getUsers().get(user.getUsername()).addVideoToHistory();
        }
        /*
         *  Iau pe rand toate actiunile primite si in functie de tipul fiecarei actiuni
         *  se apeleaza metoda potrivita.
         */
        for (ActionInputData action : actionInputData) {
            String actionType = action.getActionType();
//            System.out.println("ACTION " + actionType);
            // Daca actiunea e o comanda
            if (action.getActionType().equals(Constants.COMMAND)) {
                User currentUser = Database.getInstance().getUsers().get(action.getUsername());
                // Adaugare la favorite a unui film/serial
                if (action.getType().equals(Constants.FAVORITE)) {
                    jsonObject = currentUser.addToFavouriteVideos(action, fileWriter);
                } else if (action.getType().equals(Constants.VIEW)) {
                    // Adaugare vizionare a unui film/serial
                    jsonObject = currentUser.addView(action, fileWriter);
                } else if (action.getType().equals(Constants.RATING)) {
                    // Adaugare a unui rating pentru un film/serial
                    jsonObject = currentUser.setRating(action, fileWriter);
                }
            } else if (action.getActionType().equals(Constants.QUERY)) {
                // Daca actiunea e un query
                // Cautare informatii despre actori
                if (action.getObjectType().equals(Constants.ACTORS)) {
                    jsonObject = Database.getInstance().searchQueryActors(action, fileWriter);
                } else if (action.getObjectType().equals(Constants.MOVIES)) {
                    // Cautare informatii despre filme
                    jsonObject = Database.getInstance().searchQueryMovies(action, fileWriter);
                } else if (action.getObjectType().equals(Constants.SHOWS)) {
                    // Cautarea informatii despre seriale
                    jsonObject = Database.getInstance().searchQueryShows(action, fileWriter);
                } else if (action.getObjectType().equals(Constants.USERS)) {
                    // Cautare informatii despre utilizatori
                    jsonObject = Database.getInstance().searchQueryUsers(action, fileWriter);
                }
            } else if (action.getActionType().equals("recommendation")) {
                // Daca actiunea e o recomandare
                // Recomandari pentru toti utilizatorii
                if (action.getType().equals(Constants.STANDARD)
                        || action.getType().equals(Constants.BEST_UNSEEN)) {
                    jsonObject = Database.getInstance().recommendationAllUsers(action, fileWriter);
                } else if (action.getType().equals(Constants.POPULAR)
                        || action.getType().equals(Constants.FAVORITE)
                        || action.getType().equals(Constants.SEARCH)) {
                    // Recomandari doar pentru utilizatorii premium
                    jsonObject = Database.getInstance().
                            recommendationPremiumUsers(action, fileWriter);
                }
            }
            arrayResult.add(jsonObject);
        }

        fileWriter.closeJSON(arrayResult);
    }
}
