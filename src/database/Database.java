package database;

import actor.Actor;
import actor.ActorsAwards;
import common.Constants;
import entertainment.Movie;
import entertainment.Show;
import entertainment.Video;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONObject;
import user.User;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Database {
    /**
     * Instante bazei de date
     */
    private static Database instance = null;
    /**
     * Lista de actori
     */
    private Map<String, Actor> actors;
    /**
     * Lista de filme
     */
    private Map<String, Movie> movies;
    /**
     * Lista de seriale
     */
    private Map<String, Show> shows;
    /**
     * Lista de utilizatori
     */
    private Map<String, User> users;
    /**
     * Lista de video-uri
     */
    private List<Video> videos;
    /**
     * Lista de favorite
     */
    private List<Video> favourites;

    public Database() {
        this.actors = new HashMap<>();
        this.movies = new HashMap<>();
        this.shows = new HashMap<>();
        this.users = new HashMap<>();
        this.videos = new ArrayList<>();
        this.favourites = new ArrayList<>();
    }

    /**
     * Returneaza instanta Database
     * @return
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Goleste Baza de date
     */
    public void clearDatabase() {
        actors.clear();
        movies.clear();
        shows.clear();
        users.clear();
        videos.clear();
        favourites.clear();
    }

    /**
     * Returneaza lista de actori
     * @return
     */
    public Map<String, Actor> getActors() {
        return actors;
    }

    /**
     * Seteaza lista de actori
     * @param actors
     */
    public void setActors(final Map<String, Actor> actors) {
        this.actors = actors;
    }

    /**
     * Returneaza lista de filme
     * @return
     */
    public Map<String, Movie> getMovies() {
        return movies;
    }

    /**
     * Seteaza lista de filme
     * @param movies
     */
    public void setMovies(final Map<String, Movie> movies) {
        this.movies = movies;
    }

    /**
     * Returneaza lista de seriale
     * @return
     */
    public Map<String, Show> getShows() {
        return shows;
    }

    /**
     * Seteaza lista de seriale
     * @param shows
     */
    public void setShows(final Map<String, Show> shows) {
        this.shows = shows;
    }

    /**
     * Returneaza lista de utilizatori
     * @return
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * Seteaza lista de utilizatori
     * @param users
     */
    public void setUsers(final Map<String, User> users) {
        this.users = users;
    }

    /**
     * Returneaza lista de video-uri
     * @return
     */
    public List<Video> getVideos() {
        return videos;
    }

    /**
     * Seteaza lista de video-uri
     * @param videos
     */
    public void setVideos(final List<Video> videos) {
        this.videos = videos;
    }

    /**
     * Returneaza lista de favorite
     * @return
     */
    public List<Video> getFavourites() {
        return favourites;
    }

    /**
     * Seteaza lista de favorite
     * @param favourites
     */
    public void setFavourites(final List<Video> favourites) {
        this.favourites = favourites;
    }

    /**
     *  Cautari dupa actori. Se cauta dupa cateva criterii: average, awards si
     *  filter_description.
     *  Sortarile sunt facute asa cum se precizeaza in cerinta temei.
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
        if (criteria.equals(Constants.AVERAGE)) {
            // Ii adaug in lista de actori cu care voi lucra doar pe cei care au average != 0
            for (Actor actor : Database.getInstance().getActors().values()) {
                if (actor.average() != 0) {
                    listActors.add(actor);
                }
            }
            // Sortez lista in functie de average, iar daca au acelasi average, in functie de nume
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
            // Ii adaug in lista de actori doar pe cei care contin toate tipurile de premii date
            for (Actor actor : Database.getInstance().getActors().values()) {
                int numberOfDiffAwards = 0;
                for (String award : filters.get(Constants.FILTERS_AWARDS)) {
                    if (actor.getAwards().containsKey(ActorsAwards.valueOf(award))) {
                        numberOfDiffAwards += 1;
                    } else {
                        break;
                    }
                }
                if (numberOfDiffAwards == filters.get(Constants.FILTERS_AWARDS).size()) {
                    listActors.add(actor);
                }
            }
            // Sortez lista de actori in functie de numarul de premii primite, apoi dupa nume
            // (daca au acelasi numar de premii)
            Collections.sort(listActors, new Comparator<Actor>() {
                @Override
                public int compare(final Actor actor1, final Actor actor2) {
                    int awardsActor1 = 0, awardsActor2 = 0;
                    // Calculez numarul de awards pentru fiecare actor
                    for (int award : actor1.getAwards().values()) {
                        awardsActor1 += award;
                    }
                    for (int award : actor2.getAwards().values()) {
                        awardsActor2 += award;
                    }

                    if (awardsActor1 > awardsActor2) {
                        return 1;
                    } else if (awardsActor1 < awardsActor2) {
                        return -1;
                    }
                    return actor1.getName().compareTo(actor2.getName());
                }
            });
        } else if (criteria.equals(Constants.FILTER_DESCRIPTIONS)) {
            // Ii adaug in lista de actori doar pe cei care au in descriere toata cuvintele cerute
            for (Actor actor : Database.getInstance().getActors().values()) {
                int checkedNumbers = 0;
                for (String word : filters.get(Constants.FILTERS_WORDS)) {
                    // Verific daca cuvantul exista in descrierea actorului
                    if (actor.getCareerDescription().toLowerCase().
                            contains(word.toLowerCase() + " ")) {
                        checkedNumbers += 1;
                    }
                }
                if (checkedNumbers == filters.get(Constants.FILTERS_WORDS).size()) {
                    listActors.add(actor);
                }
            }
            // Sortez lista de actori dupa nume
            Collections.sort(listActors, new Comparator<Actor>() {
                @Override
                public int compare(final Actor actor1, final Actor actor2) {
                    return actor1.getName().compareTo(actor2.getName());
                }
            });
        }
        // Daca lista e mai scurta decat numarul cerut de actiune, se afiseaza
        // doar aceste elemente din lista, deci scadem numarul cerut
        if (listActors.size() < number) {
            number = listActors.size();
        }
        // Daca se cere o sortare descrescatoare
        if (sortType.equals(Constants.DESC)) {
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
     * Cautari dupa filme. Se cauta dupa cateva criterii: favorite, longest,
     * most_viewed si ratings.
     * Sortarile sunt facute asa cum se precizeaza in cerinta temei.
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

        StringBuilder outputToWrite;
        JSONObject jsonObjectToReturn;

        List<Movie> listMovies = new ArrayList<Movie>(Database.getInstance().movies.values());
        // Las in lista doar filmele care sunt din anul mentionat si au genurile mentionate
        // in actiune
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
        if (criteria.equals(Constants.FAVORITE)) {
            // Las in lista doar filmele care sunt favorite
            for (Video movie : favourites) {
                if (movie.getFavourite() != 0) {
                    listMovies.remove(movie);
                }
            }
            // Sortez lista dupa numarul de aparitii in favorite
            Collections.sort(listMovies, new Comparator<Movie>() {
                @Override
                public int compare(final Movie movie1, final Movie movie2) {
                    if (movie1.getFavourite() > movie2.getFavourite()) {
                        return 1;
                    } else if (movie1.getFavourite() < movie2.getFavourite()) {
                        return -1;
                    }
                    return 0;
                }
            });
        } else if (criteria.equals(Constants.LONGEST)) {
            // Sortez lista in functie de durata, si apoi in functie de nume
            Collections.sort(listMovies, new Comparator<Movie>() {
                @Override
                public int compare(final Movie movie1, final Movie movie2) {
                    int duration1 = Database.getInstance().getMovies().get(movie1.getName()).
                                getDuration();
                    int duration2 = Database.getInstance().getMovies().get(movie2.getName()).
                                getDuration();

                    if (duration1 > duration2) {
                        return 1;
                    } else if (duration1 < duration2) {
                        return -1;
                    }
                    return movie1.getName().compareTo(movie2.getName());
                }
            });
        } else if (criteria.equals(Constants.MOST_VIEWED)) {
            Map<String, Integer> moviesMap = new HashMap<>();
            // Pastrez doar filmele care au numarul de vizionari diferit de 0
            for (Movie movie : Database.getInstance().getMovies().values()) {
                int views = 0;
                for (User user : Database.getInstance().getUsers().values()) {
                    if (user.getHistory().containsKey(movie.getName())) {
                        views += Database.getInstance().getMovies().get(movie.getName()).
                                getViews();
                    }
                }
                if (views == 0) {
                    listMovies.remove(movie);
                } else {
                    moviesMap.put(movie.getName(), views);
                }
            }
            // Sortez lista in functie de numarul de vizionari
            Collections.sort(listMovies, new Comparator<Movie>() {
                @Override
                public int compare(final Movie movie1, final Movie movie2) {
                    int views1 = moviesMap.get(movie1.getName());
                    int views2 = moviesMap.get(movie2.getName());

                    if (views1 > views2) {
                        return 1;
                    } else if (views1 < views2) {
                        return -1;
                    }
                    return movie1.getName().compareTo(movie2.getName());
                }
            });
        } else if (criteria.equals(Constants.RATINGS)) {
            // Pastrez doar filmele care au cel putin un rating oferit
            for (Movie movie : Database.getInstance().getMovies().values()) {
                if (movie.rating() == 0) {
                    listMovies.remove(movie);
                }
            }
            // Sortez lista in functie de rating
             Collections.sort(listMovies, new Comparator<Movie>() {
                 @Override
                 public int compare(final Movie movie1, final Movie movie2) {
                     double rating1 = Database.getInstance().getMovies().get(movie1.getName()).
                             rating();
                     double rating2 = Database.getInstance().getMovies().get(movie2.getName()).
                             rating();

                     if (rating1 > rating2) {
                         return -1;
                     } else if (rating1 < rating2) {
                         return 1;
                     }
                     return 0;
                 }
             });
        }
        // Daca lista e mai scurta decat numarul cerut de actiune, se afiseaza
        // doar aceste elemente din lista, deci scadem numarul cerut
        if (listMovies.size() < number) {
            number = listMovies.size();
        }
        // Daca se cere o sortare descrescatoare
        if (sortType.equals(Constants.DESC)) {
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
     * Cautari dupa seriale. Se cauta dupa cateva criterii: favorite, longest,
     * most_viewed si ratings.
     * Sortarile sunt facute asa cum se precizeaza in cerinta temei.
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

        StringBuilder outputToWrite;
        JSONObject jsonObjectToReturn;

        List<Show> listShows = new ArrayList<Show>(Database.getInstance().shows.values());
        // Las in lista doar serialele care sunt din anul mentionat si au genurile mentionate
        // in actiune
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
        if (criteria.equals(Constants.FAVORITE)) {
            // Las in lista doar serialele care sunt favorite
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
            // Sortez lista dupa numarul de aparitii in favorite
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
        } else if (criteria.equals(Constants.LONGEST)) {
            // Sortez lista in functie de durata, si apoi in functie de nume
            Collections.sort(listShows, new Comparator<Show>() {
                @Override
                public int compare(final Show show1, final Show show2) {
                    int duration1 = Database.getInstance().getShows().get(show1.getName()).
                                getTotalDuration();
                    int duration2 = Database.getInstance().getShows().get(show2.getName()).
                                getTotalDuration();

                    if (duration1 > duration2) {
                        return -1;
                    } else if (duration1 < duration2) {
                        return 1;
                    }
                    return show1.getName().compareTo(show2.getName());
                }
            });
        } else if (criteria.equals(Constants.MOST_VIEWED)) {
            // Pastrez doar filmele care au numarul de vizionari diferit de 0
            for (Show show : Database.getInstance().getShows().values()) {
                int views = 0;
                for (User user : Database.getInstance().getUsers().values()) {
                    if (user.getHistory().containsKey(show.getName())) {
                        views += 1;
                    }
                }
                if (views == 0) {
                    listShows.remove(show);
                }
            }
            // Sortez lista in functie de numarul de vizionari
            Collections.sort(listShows, new Comparator<Show>() {
                @Override
                public int compare(final Show show1, final Show show2) {
                    int views1 = Database.getInstance().getShows().get(show1.getName()).getViews();
                    int views2 = Database.getInstance().getShows().get(show2.getName()).getViews();

                    if (views1 > views2) {
                        return -1;
                    } else if (views1 < views2) {
                        return 1;
                    }
                    return show1.getName().compareTo(show2.getName());
                }
            });
        } else if (criteria.equals(Constants.RATINGS)) {
            // Pastrez doar filmele care au cel putin un rating oferit
            for (Show show : Database.getInstance().getShows().values()) {
                if (show.rating() == 0) {
                    listShows.remove(show);
                }
            }
            // Sortez lista in functie de rating
            Collections.sort(listShows, new Comparator<Show>() {
                @Override
                public int compare(final Show show1, final Show show2) {
                    double rating1 = Database.getInstance().getShows().get(show1.getName()).
                            rating();
                    double rating2 = Database.getInstance().getShows().get(show2.getName()).
                            rating();

                    if (rating1 > rating2) {
                        return -1;
                    } else if (rating1 < rating2) {
                        return 1;
                    }
                    return 0;
                }
            });
        }
        // Daca lista e mai scurta decat numarul cerut de actiune, se afiseaza
        // doar aceste elemente din lista, deci scadem numarul cerut
        if (listShows.size() < number) {
            number = listShows.size();
        }
        if (sortType.equals(Constants.DESC)) {
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
     * Cautari dupa utilizatori. Se cauta dupa numarul de rating-uri.
     * Sortarile sunt facute asa cum se precizeaza in cerinta temei.
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
        // Adaug in lista doar utilizatorii care au dat rating-uri
        if (criteria.equals(Constants.NUM_RATINGS)) {
            for (User user : Database.getInstance().getUsers().values()) {
                if (user.getRatings().size() != 0) {
                    listUsers.add(user);
                }
            }
            // Sortez lista in functie de numarul de rating-uri date, apoi dupa nume
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
        // Daca lista e mai scurta decat numarul cerut de actiune, se afiseaza
        // doar aceste elemente din lista, deci scadem numarul cerut
        if (listUsers.size() < number) {
            number = listUsers.size();
        }
        // Daca se cere o sortare descrescatoare
        if (sortType.equals(Constants.DESC)) {
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

    /**
     * Recomandari pentru toti utilizatorii. Sunt de 2 tipuri: standard si best_unseen.
     * Sortarile sunt facute asa cum se precizeaza in cerinta temei.
     * @param action
     * @param fileWriter
     * @return
     */
    public JSONObject recommendationAllUsers(final ActionInputData action, final Writer fileWriter)
            throws IOException {
        int actionId = action.getActionId();
        String type = action.getType();
        String username = action.getUsername();

        StringBuilder outputToWrite = new StringBuilder();
        JSONObject jsonObjectToReturn;

        if (type.equals(Constants.STANDARD)) {
            // Intoarce primul video nevazut de utilizator
            for (Video video : videos) {
                if (!Database.getInstance().getUsers().get(username).getHistory().
                        containsKey(video.getName())) {
                    outputToWrite.append("StandardRecommendation result: " + video.getName());
                    break;
                }
            }

            if (outputToWrite.isEmpty()) {
                outputToWrite.append("StandardRecommendation cannot be applied!");
            }
        } else if (type.equals(Constants.BEST_UNSEEN)) {
            // Adaug in lista doar video-urile care nu au fost vazute de utilizator
            List<Video> unseenVideos = new ArrayList<>();
            for (Video video : videos) {
                if (!Database.getInstance().getUsers().get(username).getHistory().
                        containsKey(video.getName())) {
                    unseenVideos.add(video);
                }
            }
            // Sortez lista in functie de rating, iar apoi in functie de index
            Collections.sort(unseenVideos, new Comparator<Video>() {
                @Override
                public int compare(final Video video1, final Video video2) {
                    double rating1 = 0, rating2 = 0;
                    if (Database.getInstance().getMovies().containsKey(video1.getName())) {
                        rating1 = Database.getInstance().getMovies().get(video1.getName()).
                                rating();
                    } else if (Database.getInstance().getShows().containsKey(video1.getName())) {
                        rating1 = Database.getInstance().getShows().get(video1.getName()).
                                rating();
                    }

                    if (Database.getInstance().getMovies().containsKey(video2.getName())) {
                        rating2 = Database.getInstance().getMovies().get(video2.getName()).
                                rating();
                    } else if (Database.getInstance().getShows().containsKey(video2.getName())) {
                        rating2 = Database.getInstance().getShows().get(video2.getName()).
                                rating();
                    }

                    if (rating1 > rating2) {
                        return -1;
                    } else if (rating1 < rating2) {
                        return 1;
                    } else {
                        if (unseenVideos.indexOf(video1) > unseenVideos.indexOf(video2)) {
                            return 1;
                        } else if (unseenVideos.indexOf(video1) < unseenVideos.indexOf(video2)) {
                            return -1;
                        }
                    }
                    return 0;
                }
            });
            if (unseenVideos.size() == 0) {
                outputToWrite.append("BestRatedUnseenRecommendation cannot be applied!");
            } else {
                outputToWrite.append("BestRatedUnseenRecommendation result: ").
                        append(unseenVideos.get(0).getName());
            }
        }
        jsonObjectToReturn = fileWriter.writeFile(actionId, outputToWrite.toString(),
                outputToWrite.toString());
        return jsonObjectToReturn;
    }

    /**
     * Recomandari pentru utilizatorii premium. Sunt de 3 tipuri: popular, favorite si search.
     * Daca un utilizator basic cere una dintre aceste recomandari, cererea ii este respinsa.
     * Sortarile sunt facute asa cum se precizeaza in cerinta temei.
     * @param action
     * @param fileWriter
     * @return
     */
    public JSONObject recommendationPremiumUsers(final ActionInputData action,
                                                 final Writer fileWriter) throws IOException {
        int actionId = action.getActionId();
        String type = action.getType();
        String username = action.getUsername();
        String genreInput = action.getGenre();
        StringBuilder outputToWrite = new StringBuilder();
        JSONObject jsonObjectToReturn;
        User user = users.get(username);
        String subscription = user.getSubscription();
        if (type.equals(Constants.POPULAR)) {
            if (subscription.equals(Constants.BASIC)) {
                outputToWrite.append("PopularRecommendation cannot be applied!");
            } else {
                // Adaug in lista doar video-urile care au fost vizionate si au genul specificat
                Map<String, Integer> genres = new HashMap<>();
                for (Movie movie : Database.getInstance().getMovies().values()) { // Filme
                    for (String genre : movie.getGenres()) {
                        genres.put(genre, movie.getViews());
                        if (genres.containsKey(genre)) {
                            genres.put(genre, genres.get(genre) + movie.getViews());
                        }
                    }
                }
                for (Show show : Database.getInstance().getShows().values()) { // Seriale
                    for (String genre : show.getGenres()) {
                        if (genres.containsKey(genre)) {
                            genres.put(genre, genres.get(genre) + show.getViews());
                        } else {
                            genres.put(genre, show.getViews());
                        }
                    }
                }
                List<String> listOfGenres = new ArrayList<>(genres.keySet());
                // Sortez lista in functie de popularitatea genurilor
                Collections.sort(listOfGenres, new Comparator<String>() {
                    @Override
                    public int compare(final String genre1, final String genre2) {
                        if (genres.get(genre1) > genres.get(genre2)) {
                            return -1;
                        } else if (genres.get(genre1) < genres.get(genre2)) {
                            return 1;
                        }
                        return 0;
                    }
                });
                for (String genre : listOfGenres) {
                    for (Video video : videos) {
                        if (!user.getHistory().containsKey(video.getName())
                                && video.getGenres().contains(genre)) {
                            outputToWrite.append("PopularRecommendation result: ").
                                    append(video.getName());
                            break;
                        }
                    }
                    if (!outputToWrite.isEmpty()) {
                        break;
                    }
                }
                if (outputToWrite.isEmpty()) {
                    outputToWrite.append("PopularRecommendation cannot be applied!");
                }
            }
        } else if (type.equals(Constants.FAVORITE)) {
            if (subscription.equals(Constants.BASIC)) {
                outputToWrite.append("FavoriteRecommendation cannot be applied!");
            } else {
                // Adaug in lista doar video-urile care au fost marcate ca favorite de catre cel
                // putin un utilizator, dar care nu sunt favorite in lista utilizatorului pentru
                // care se face recomandarea
                Map<String, Integer> favouriteVideos = new HashMap<>();
                for (User currentUser : Database.getInstance().getUsers().values()) {
                    for (String video : currentUser.getFavourite()) {
                        if (!favouriteVideos.containsKey(video)) {
                            favouriteVideos.put(video, 1);
                        } else {
                            favouriteVideos.put(video, favouriteVideos.get(video) + 1);
                        }
                        if (user.getHistory().containsKey(video)) {
                            favouriteVideos.remove(video);
                        }
                    }
                }
                if (favouriteVideos.isEmpty()) {
                    outputToWrite.append("FavoriteRecommendation cannot be applied!");
                } else { // Extrag primul element din lista de favorite
                    outputToWrite.append("FavoriteRecommendation result: "
                            + favouriteVideos.entrySet().iterator().next().getKey());
                }
            }
        } else if (type.equals(Constants.SEARCH)) {
            if (subscription.equals(Constants.BASIC)) {
                outputToWrite.append("SearchRecommendation cannot be applied!");
            } else {
                List<Video> unseenVideos = new ArrayList<>();
                // Adaug in lista doar video-urile nevizionate de catre utilizatorul dat in actiune
                // si care are genul dat
                for (Video video : videos) {
                    if (!Database.getInstance().getUsers().get(action.getUsername()).getHistory().
                            containsKey(video.getName())
                            && video.getGenres().contains(genreInput)) {
                        unseenVideos.add(video);
                    }
                }
                // Sortez lista dupa rating-uri, iar apoi dupa nume
                Collections.sort(unseenVideos, new Comparator<Video>() {
                    @Override
                    public int compare(final Video video1, final Video video2) {
                        double rating1 = 0, rating2 = 0;
                        if (Database.getInstance().getMovies().containsKey(video1.getName())) {
                            rating1 = Database.getInstance().getMovies().get(video1.getName()).
                                    rating();
                        } else if (Database.getInstance().getShows().
                                containsKey(video1.getName())) {
                            rating1 = Database.getInstance().getShows().get(video1.getName()).
                                    rating();
                        }
                        if (Database.getInstance().getMovies().containsKey(video2.getName())) {
                            rating2 = Database.getInstance().getMovies().get(video2.getName()).
                                    rating();
                        } else if (Database.getInstance().getShows().
                                containsKey(video2.getName())) {
                            rating2 = Database.getInstance().getShows().get(video2.getName()).
                                    rating();
                        }
                        if (rating1 > rating2) {
                            return 1;
                        } else if (rating1 < rating2) {
                            return -1;
                        }
                        return video1.getName().compareTo(video2.getName());
                    }
                });
                if (unseenVideos.size() == 0) {
                    outputToWrite.append("SearchRecommendation cannot be applied!");
                } else {
                    outputToWrite.append("SearchRecommendation result: [");
                    for (int i = 0; i < unseenVideos.size(); i++) {
                        if (i != unseenVideos.size() - 1) {
                            outputToWrite.append(unseenVideos.get(i).getName() + ", ");
                        } else {
                            outputToWrite.append(unseenVideos.get(i).getName() + "]");
                        }
                    }
                }
            }
        }
        jsonObjectToReturn = fileWriter.writeFile(actionId, outputToWrite.toString(),
                outputToWrite.toString());
        return jsonObjectToReturn;
    }
}
