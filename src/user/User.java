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
    /**
     * Numele de utilizator
     */
    private String username;
    /**
     * Tipul utilizatorului: BASIC sau PREMIUM
     */
    private String subscription;
    /**
     * Istoricul de vizionare
     */
    private Map<String, Integer> history;
    /**
     * Lista de video-uri favorite
     */
    private ArrayList<String> favourite;
    /**
     * Rating-urile date de catre un utilizator
     */
    private Map<String, Double> ratings;

    public User(final UserInputData userInputData) {
        this.username = userInputData.getUsername();
        this.subscription = userInputData.getSubscriptionType();
        this.history = userInputData.getHistory();
        this.favourite = userInputData.getFavoriteMovies();
        this.ratings = new HashMap<>();
    }

    /**
     *  Returneaza numele de utilizator
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *  Seteaza numele de utilizator
     * @param username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     *  Returneaza tipul utilizatorului
     * @return
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     *  Seteaza tipul utilizatorului
     * @param subscription
     */
    public void setSubscription(final String subscription) {
        this.subscription = subscription;
    }

    /**
     *  Returneaza istoricul de vizionare
     * @return
     */
    public Map<String, Integer> getHistory() {
        return history;
    }

    /**
     *  Seteaza istoricul de vizionare
     * @param history
     */
    public void setHistory(final Map<String, Integer> history) {
        this.history = history;
    }

    /**
     *  Returneaza lista de favorite
     * @return
     */
    public ArrayList<String> getFavourite() {
        return favourite;
    }

    /**
     *  Seteaza lista de favorite
     * @param favourite
     */
    public void setFavourite(final ArrayList<String> favourite) {
        this.favourite = favourite;
    }

    /**
     *  Returneaza rating-ul
     * @return
     */
    public Map<String, Double> getRatings() {
        return ratings;
    }

    /**
     *  Seteaza rating-ul
     * @param ratings
     */
    public void setRatings(final Map<String, Double> ratings) {
        this.ratings = ratings;
    }

    /**
     *  La popularea bazei de data, adauga in lista de vizionate video-urile
     *  citite din fisierul de input ca fiind vizionate (in istoric)
     */
    public void addVideoToHistory() {
        for (String title : this.history.keySet()) {
            if (Database.getInstance().getMovies().containsKey(title)) {
                Database.getInstance().getMovies().get(title).setViews(
                        Database.getInstance().getMovies().get(title).getViews()
                                + history.get(title)
                );
            } else if (Database.getInstance().getShows().containsKey(title)) {
                Database.getInstance().getShows().get(title).setViews(
                        Database.getInstance().getShows().get(title).getViews()
                                + history.get(title)
                );
            }
        }
    }

    /**
     *  La popularea bazei de data, adauga in lista de favorite video-urile
     *  citite din fisierul de input ca fiind favorite
     */
    public void addVideoToFavorites() {
        for (String title : this.favourite) {
            if (Database.getInstance().getMovies().containsKey(title)) {
                Database.getInstance().getMovies().get(title).setFavourite(
                        Database.getInstance().getMovies().get(title).getFavourite()
                                + history.get(title)
                );
            } else if (Database.getInstance().getShows().containsKey(title)) {
                Database.getInstance().getShows().get(title).setFavourite(
                        Database.getInstance().getShows().get(title).getFavourite()
                                + history.get(title)
                );
            }
        }
    }

    /**
     *  Verific daca video-ul dat in actiune este vizionat. Daca da, verific
     *  daca nu cumva este deja in lista de favorite. Daca este deja, nu il
     *  mai adaug din nou. Daca nu este, il adaug.
     *  Daca nu este vizionat video-ul, atunci nu il adaug la favorite.
     * @param action
     * @param fileWriter
     * @return
     * @throws IOException
     */
    public JSONObject addToFavouriteVideos(final ActionInputData action, final Writer fileWriter)
            throws IOException {
        String title = action.getTitle();
        int actionId = action.getActionId();

        String outputToWrite;
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
     *  Adauga o vizionare unui video. Daca video-ul respectiv nu a mai fost
     *  vizionat pana acum, atunci se incrementeaza valoarea 0 (devine 1).
     *  Daca a mai fost vizionat, se incrementeaza numarul de vizionari deja
     *  existent.
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

        String outputToWrite;
        JSONObject jsonObjectToReturn;

        // Daca a mai fost vizionat, iau vechea valoare si abia apoi incrementez cu 1
        // Altfel, doar incrementez cu 1 (valoarea initiala a unui video nevizionat fiind 0)
        if (history.containsKey(title)) {
            views = history.get(title);
        }
        views += 1;
        history.put(title, views);

        // Chiar daca a mai fost sau nu vizionat, incrementez numarul de vizionari (initial = 0)
        // Daca vrem sa adaugam o vizionare unui film
        if (Database.getInstance().getMovies().containsKey(title)) {
            Database.getInstance().getMovies().get(title).setViews(views);
        } else if (Database.getInstance().getShows().containsKey(title)) {
            // Daca vrem sa adaugam o vizionare unui serial
            Database.getInstance().getShows().get(title).setViews(views);
        }
        outputToWrite = "success -> " + title + " was viewed with total views of " + (views);

        jsonObjectToReturn = fileWriter.writeFile(actionId, outputToWrite, outputToWrite);
        return jsonObjectToReturn;
    }

    /**
     *  Seteaza rating-ul pentru un video dat de actiune. Se verifica
     *  daca video-ul este un film sau un serial.
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
