package user;

import database.Database;
import fileio.ActionInputData;
import fileio.UserInputData;
import fileio.Writer;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private String username;
    private String subscription;
    private Map<String, Integer> history;
    private ArrayList<String> favourite;
    private Map<String, Double> ratings;

    public User(final UserInputData userInputData) {
        this.username = userInputData.getUsername();
        this.subscription = userInputData.getSubscriptionType();
        this.history = userInputData.getHistory();
        this.favourite = userInputData.getFavoriteMovies();
        this.ratings = new HashMap<>();
    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     *
     * @return
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     *
     * @param subscription
     */
    public void setSubscription(final String subscription) {
        this.subscription = subscription;
    }

    /**
     *
     * @return
     */
    public Map<String, Integer> getHistory() {
        return history;
    }

    /**
     *
     * @param history
     */
    public void setHistory(final Map<String, Integer> history) {
        this.history = history;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getFavourite() {
        return favourite;
    }

    /**
     *
     * @param favourite
     */
    public void setFavourite(final ArrayList<String> favourite) {
        this.favourite = favourite;
    }

    /**
     *
     * @return
     */
    public Map<String, Double> getRatings() {
        return ratings;
    }

    /**
     *
     * @param ratings
     */
    public void setRatings(final Map<String, Double> ratings) {
        this.ratings = ratings;
    }

    /**
     *
     * @param action
     * @param fileWriter
     * @return
     * @throws IOException
     */
    public JSONObject addToFavouriteVideos(final ActionInputData action, final Writer fileWriter)
            throws IOException {
        String title = action.getTitle();
        int actionId = action.getActionId();

        String outputToWrite = "";
        JSONObject jsonObjectToReturn;

        // Verific daca a fost vizionat deja de utilizator
        if (history.containsKey(title)) {
            // Verific daca nu cumva e deja in lista de favorite
            if (favourite.contains(title)) {
                outputToWrite = "error -> " + title + " is already in favourite list";
            } else {
                // Daca nu e in lista de favorite, il adaug
                favourite.add(title);
                outputToWrite = "success -> " + title + " was added as favourite";
            }
        } else {
            // Daca nu a fost vizionat, nu il pot adauga in lista de favorite
            outputToWrite = "error -> " + title + " is not seen";
        }

        jsonObjectToReturn = fileWriter.writeFile(actionId, outputToWrite, outputToWrite);
        return jsonObjectToReturn;
    }

    /**
     *
     * @param action
     * @param fileWriter
     * @return
     * @throws IOException
     */
    public JSONObject addView(final ActionInputData action, final Writer fileWriter)
            throws IOException {
        String title = action.getTitle();
        int actionId = action.getActionId();
        int views = 0;

        String outputToWrite = "";
        JSONObject jsonObjectToReturn;

        // Chiar daca a mai fost sau nu vizionat, incrementez numarul de vizionari (initial = 0)
        // Daca vrem sa adaugam o vizionare unui film
        if (Database.getInstance().getMovies().containsKey(title)) {
            views = Database.getInstance().getMovies().get(title).getViews();
            Database.getInstance().getMovies().get(title).setViews(views + 1);
        } else if (Database.getInstance().getShows().containsKey(title)) {
            // Daca vrem sa adaugam o vizionare unui serial
            views = Database.getInstance().getShows().get(title).getViews();
            Database.getInstance().getShows().get(title).setViews(views + 1);
        }

        outputToWrite = "success -> " + title + " was viewed with total views of " + (views + 1);

        jsonObjectToReturn = fileWriter.writeFile(actionId, outputToWrite, outputToWrite);
        return jsonObjectToReturn;
    }

    /**
     *
     * @param action
     * @param fileWriter
     * @return
     * @throws IOException
     */
    public JSONObject setRating(final ActionInputData action, final Writer fileWriter)
            throws IOException {
        String title = action.getTitle();
        int actionId = action.getActionId();

        String outputToWrite = "";
        JSONObject jsonObjectToReturn;
        // Id-ul video-ului, fie ca e el serial sau film
        int videoId = 0;
        // Verific daca e vorba de un film
        if (Database.getInstance().getMovies().containsKey(title)) {
            // Verific daca a fost vizionat ca sa ii pot da rating
            if (history.containsKey(title)) {
                // Verific daca nu cumva i-am oferit deja rating
                if (ratings.containsKey(title)) {
                    outputToWrite = "error -> " + title + " has been already rated";
                } else {
                    ratings.put(title, action.getGrade());
                    // Extrag vechea lista de ratings si adaug rating-ul nou
                    List<Double> currentRatings = Database.getInstance().getMovies().get(title).
                            getRatings();
                    currentRatings.add(action.getGrade());
                    Database.getInstance().getMovies().get(title).setRatings(currentRatings);
                    outputToWrite = "success -> " + title + " was rated with " + action.getGrade()
                            + " by " + action.getUsername();
                }
            } else {
                outputToWrite = "error -> " + title + " is not seen";
            }
        } else if (Database.getInstance().getShows().containsKey(title)) {
            // Verific daca e vorba de un serial
            int seasons = action.getSeasonNumber();
            // Verific daca a fost vizionat ca sa ii pot da rating
            if (history.containsKey(title)) {
                // Verific daca nu cumva i-am oferit deja rating
                if (ratings.containsKey(title + action.getSeasonNumber())) {
                    outputToWrite = "error -> " + title + " has been already rated";
                } else {
                    String newTitle = title + action.getSeasonNumber();
                    ratings.put(newTitle, action.getGrade());
                    // Extrag vechea lista de ratings si adaug rating-ul nou
                    List<Double> currentRatings = Database.getInstance().getShows().get(title).
                            getSeasons().get(seasons - 1).getRatings();
                    currentRatings.add(action.getGrade());
                    Database.getInstance().getShows().get(title).getSeasons().get(seasons - 1).
                            setRatings(currentRatings);
                    outputToWrite = "success -> " + title + " was rated with " + action.getGrade()
                            + " by " + action.getUsername();
                }
            } else {
                outputToWrite = "error -> " + title + " is not seen";
            }
        }

        jsonObjectToReturn = fileWriter.writeFile(actionId, outputToWrite, outputToWrite);
        return jsonObjectToReturn;
    }
}
