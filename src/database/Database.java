package database;

import actor.Actor;
import actor.ActorsAwards;
import common.Constants;
import entertainment.Movie;
import entertainment.Show;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONObject;
import user.User;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class Database {

    private static Database instance = null;
    private Map<String, Actor> actors;
    private Map<String, Movie> movies;
    private Map<String, Show> shows;
    private Map<String, User> users;

    public Database() {
        this.actors = new HashMap<>();
        this.movies = new HashMap<>();
        this.shows = new HashMap<>();
        this.users = new HashMap<>();
    }

    /**
     * @return
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * nothing
     */
    public void clearRepository() {
        actors.clear();
        movies.clear();
        shows.clear();
        users.clear();
    }

    /**
     *
     * @return
     */
    public Map<String, Actor> getActors() {
        return actors;
    }

    /**
     *
     * @param actors
     */
    public void setActors(final Map<String, Actor> actors) {
        this.actors = actors;
    }

    /**
     *
     * @return
     */
    public Map<String, Movie> getMovies() {
        return movies;
    }

    /**
     *
     * @param movies
     */
    public void setMovies(final Map<String, Movie> movies) {
        this.movies = movies;
    }

    /**
     *
     * @return
     */
    public Map<String, Show> getShows() {
        return shows;
    }

    /**
     *
     * @param shows
     */
    public void setShows(final Map<String, Show> shows) {
        this.shows = shows;
    }

    /**
     *
     * @return
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     *
     * @param users
     */
    public void setUsers(final Map<String, User> users) {
        this.users = users;
    }

    /**
     *
     * @param action
     * @param fileWriter
     * @return
     * @throws IOException
     */
    public JSONObject searchQueryActors(final ActionInputData action, final Writer fileWriter)
            throws IOException {
        int actionId = action.getActionId();
        int number = action.getNumber();
        List<List<String>> filters = action.getFilters();
        String sortType = action.getSortType();
        String criteria = action.getCriteria();

        StringBuilder outputToWrite;
        JSONObject jsonObjectToReturn;

        List<Actor> listActors = new ArrayList<>();

        if (criteria.equals("average")) {
            for (Actor actor : Database.getInstance().getActors().values()) {
                if (actor.average() != 0) {
                    listActors.add(actor);
                }
            }
            Collections.sort(listActors, new Comparator<Actor>() {
                @Override
                public int compare(final Actor actor1, final Actor actor2) {
                    if (actor1.average() > actor2.average()) {
                        return 1;
                    } else if (actor1.average() < actor2.average()) {
                        return -1;
                    }
                    return actor1.getName().compareTo(actor2.getName());
                }
            });
        } else if (criteria.equals(Constants.AWARDS)) {
            for (Actor actor : Database.getInstance().getActors().values()) {
                int numberOfDiffAwards = 0;
                for (String award : filters.get(Constants.FILTERS_AWARDS)) {
                    if (actor.getAwards().containsKey(ActorsAwards.valueOf(award))) {
                        numberOfDiffAwards += 1;
                    } else {
                        break;
                    }
                }

                if (numberOfDiffAwards == filters.get(Constants.FILTERS_AWARDS).
                        size()) {
                    listActors.add(actor);
                }
            }

            Collections.sort(listActors, new Comparator<Actor>() {
                @Override
                public int compare(final Actor actor1, final Actor actor2) {
                    int awardsActor1 = 0;
                    int awardsActor2 = 0;
                    // Calculez numarul de awards pentru fiecare actor
                    for (int award : actor1.getAwards().values()) {
                        awardsActor1 += award;
                    }
                    for (int award : actor2.getAwards().values()) {
                        awardsActor2 += award;
                    }

                    if (awardsActor1 > awardsActor2) {
                        return -1;
                    } else if (awardsActor1 < awardsActor2) {
                        return 1;
                    }

                    return actor1.getName().compareTo(actor2.getName());
                }
            });
        } else if (criteria.equals(Constants.FILTER_DESCRIPTIONS)) {
            for (Actor actor : listActors) {
                int checkedNumbers = 0;
                for (String word : filters.get(Constants.FILTERS_WORDS)) {
                    if (actor.getCareerDescription().contains(word)) {
                        checkedNumbers += 1;
                    }
                }
                if (checkedNumbers == filters.get(Constants.FILTERS_WORDS).size()) {
                    listActors.add(actor);
                }
            }

            Collections.sort(listActors, new Comparator<Actor>() {
                @Override
                public int compare(final Actor actor1, final Actor actor2) {
                    return actor1.getName().compareTo(actor2.getName());
                }
            });
        }

        if (listActors.size() < number) {
            number = listActors.size();
        }

        if (sortType.equals("desc")) {
            Collections.reverse(listActors);
        }

        outputToWrite = new StringBuilder("Query result: [");
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                outputToWrite.append(listActors.get(i).getName());
            } else {
                outputToWrite.append(listActors.get(i).getName()).append(", ");
            }
        }
        outputToWrite.append("]");


        jsonObjectToReturn = fileWriter.writeFile(actionId, outputToWrite.toString(),
                outputToWrite.toString());
        return jsonObjectToReturn;
    }

    /**
     *
     * @param action
     * @param fileWriter
     * @return
     * @throws IOException
     */
    public JSONObject searchQueryMovies(final ActionInputData action, final Writer fileWriter)
            throws IOException {
        int actionId = action.getActionId();
        int number = action.getNumber();
        List<List<String>> filters = action.getFilters();
        String sortType = action.getSortType();
        String criteria = action.getCriteria();

        StringBuilder outputToWrite = new StringBuilder();
        JSONObject jsonObjectToReturn;

        List<Movie> listMovies = new ArrayList<>();

        listMovies = new ArrayList<Movie>(Database.getInstance().movies.values());

        for (Movie movie : Database.getInstance().getMovies().values()) {
            String year = Integer.toString(movie.getYear());
            if (filters.get(Constants.FILTERS_YEAR).get(0) != null
                    && !filters.get(Constants.FILTERS_YEAR).contains(year)) {
                listMovies.remove(movie);
            }
            if (filters.get(Constants.FILTERS_GENRE).get(0) != null) {
                for (String genre : filters.get(Constants.FILTERS_GENRE)) {
                    if (!movie.getGenres().contains(genre)) {
                        listMovies.remove(movie);
                    }
                }
            }
        }

        if (criteria.equals("favorite")) {
            for (Movie movie : Database.getInstance().getMovies().values()) {
                if (movie.rating() != 0) {
                    listMovies.remove(movie);
                }
            }

            Collections.sort(listMovies, new Comparator<Movie>() {
                @Override
                public int compare(final Movie movie1, final Movie movie2) {
                    if (movie1.getFavourite() > movie2.getFavourite()) {
                        return -1;
                    } else if (movie1.getFavourite() < movie2.getFavourite()) {
                        return 1;
                    }
                    return 0;
                }
            });
        } else if (criteria.equals("longest")) {
            Collections.sort(listMovies, new Comparator<Movie>() {
                @Override
                public int compare(final Movie movie1, final Movie movie2) {
                    int duration1 = 0;
                    int duration2 = 0;
                    if (Database.getInstance().getMovies().containsKey(movie1.getName())) {
                        duration1 = Database.getInstance().getMovies().get(movie1.getName()).
                                getDuration();
                    }

                    if (Database.getInstance().getMovies().containsKey(movie2.getName())) {
                        duration2 = Database.getInstance().getMovies().get(movie2.getName()).
                                getDuration();
                    }

                    if (duration1 > duration2) {
                        return -1;
                    } else if (duration1 < duration2) {
                        return 1;
                    }
                    return movie1.getName().compareTo(movie2.getName());
                }
            });
            Collections.reverse(listMovies);
        } else if (criteria.equals("most_viewed")) {
            for (Movie movie : Database.getInstance().getMovies().values()) {
                if (movie.getViews() != 0) {
                    listMovies.remove(movie);
                }
            }
        }

        if (listMovies.size() < number) {
            number = listMovies.size();
        }

        if (sortType.equals("desc")) {
            Collections.reverse(listMovies);
        }

        outputToWrite = new StringBuilder("Query result: [");
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                outputToWrite.append(listMovies.get(i).getName());
            } else {
                outputToWrite.append(listMovies.get(i).getName()).append(", ");
            }
        }
        outputToWrite.append("]");

        jsonObjectToReturn = fileWriter.writeFile(actionId, outputToWrite.toString(),
                outputToWrite.toString());
        return jsonObjectToReturn;
    }

    /**
     *
     * @param action
     * @param fileWriter
     * @return
     * @throws IOException
     */
    public JSONObject searchQueryShows(final ActionInputData action, final Writer fileWriter)
            throws IOException {

        int actionId = action.getActionId();
        int number = action.getNumber();
        List<List<String>> filters = action.getFilters();
        String sortType = action.getSortType();
        String criteria = action.getCriteria();

        StringBuilder outputToWrite = new StringBuilder();
        JSONObject jsonObjectToReturn;

        List<Show> listShows = new ArrayList<>();
        listShows = new ArrayList<Show>(Database.getInstance().shows.values());

        for (Show show : Database.getInstance().getShows().values()) {
            String year = Integer.toString(show.getYear());
            if (filters.get(Constants.FILTERS_YEAR).get(0) != null
                    && !filters.get(Constants.FILTERS_YEAR).contains(year)) {
                listShows.remove(show);
            }
            if (filters.get(Constants.FILTERS_GENRE).get(0) != null) {
                for (String genre : filters.get(Constants.FILTERS_GENRE)) {
                    if (!show.getGenres().contains(genre)) {
                        listShows.remove(show);
                    }
                }
            }
        }

        if (criteria.equals("favorite")) {
            for (Show show : Database.getInstance().getShows().values()) {
                int isFavourite = 0;
                for (User user : Database.getInstance().getUsers().values()) {
                    if (user.getFavourite().contains(show.getName())) {
                        isFavourite += 1;
                    }
                }
                if (isFavourite == 0) {
                    listShows.remove(show);
                }
            }

            Collections.sort(listShows, new Comparator<Show>() {
                @Override
                public int compare(final Show show1, final Show show2) {
                    if (show1.getFavourite() > show2.getFavourite()) {
                        return -1;
                    } else if (show1.getFavourite() < show2.getFavourite()) {
                        return 1;
                    }
                    return 0;
                }
            });
        } else if (criteria.equals("longest")) {
            Collections.sort(listShows, new Comparator<Show>() {
                @Override
                public int compare(final Show show1, final Show show2) {
                    int duration1 = 0;
                    int duration2 = 0;
                    if (Database.getInstance().getShows().containsKey(show1.getName())) {
                        duration1 = Database.getInstance().getShows().get(show1.getName()).
                                getTotalDuration();
                    }

                    if (Database.getInstance().getShows().containsKey(show2.getName())) {
                        duration2 = Database.getInstance().getShows().get(show2.getName()).
                                getTotalDuration();
                    }

                    if (duration1 > duration2) {
                        return -1;
                    } else if (duration1 < duration2) {
                        return 1;
                    }
                    return show1.getName().compareTo(show2.getName());
                }
            });
        }

        if (listShows.size() < number) {
            number = listShows.size();
        }

        if (sortType.equals("desc")) {
            Collections.reverse(listShows);
        }

        outputToWrite = new StringBuilder("Query result: [");
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                outputToWrite.append(listShows.get(i).getName());
            } else {
                outputToWrite.append(listShows.get(i).getName()).append(", ");
            }
        }
        outputToWrite.append("]");

        jsonObjectToReturn = fileWriter.writeFile(actionId, outputToWrite.toString(),
                outputToWrite.toString());
        return jsonObjectToReturn;
    }

    /**
     *
     * @param action
     * @param fileWriter
     * @return
     * @throws IOException
     */
    public JSONObject searchQueryUsers(final ActionInputData action, final Writer fileWriter)
            throws IOException {
        int actionId = action.getActionId();
        int number = action.getNumber();
        String sortType = action.getSortType();
        String criteria = action.getCriteria();

        StringBuilder outputToWrite;
        JSONObject jsonObjectToReturn;

        List<User> listUsers = new ArrayList<>();
        if (criteria.equals(Constants.NUM_RATINGS)) {
            for (User user : Database.getInstance().getUsers().values()) {
                if (user.getRatings().size() != 0) {
                    listUsers.add(user);
                }
            }

            Collections.sort(listUsers, new Comparator<User>() {
                @Override
                public int compare(final User user1, final User user2) {
                    if (user1.getRatings().size() > user2.getRatings().size()) {
                        return 1;
                    } else if (user1.getRatings().size() < user2.getRatings().size()) {
                        return -1;
                    }

                    return user1.getUsername().compareTo(user2.getUsername());
                }
            });
        }

        if (listUsers.size() < number) {
            number = listUsers.size();
        }

        if (sortType.equals("desc")) {
            Collections.reverse(listUsers);
        }

        outputToWrite = new StringBuilder("Query result: [");
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                outputToWrite.append(listUsers.get(i).getUsername());
            } else {
                outputToWrite.append(listUsers.get(i).getUsername()).append(", ");
            }
        }
        outputToWrite.append("]");

        jsonObjectToReturn = fileWriter.writeFile(actionId, outputToWrite.toString(),
                outputToWrite.toString());
        return jsonObjectToReturn;
    }
}
