package entertainment;

import fileio.MovieInputData;

import java.util.ArrayList;
import java.util.List;

public class Movie extends Video {
    /**
     * Durata filmului
     */
    private int duration;
    /**
     * Actorii care joaca in film
     */
    private List<String> actors;
    /**
     * Lista de rating-uri date filmului
     */
    private List<Double> ratings;

    public Movie(final MovieInputData movieInputData) {
        super(movieInputData);
        this.duration = movieInputData.getDuration();
        this.actors = new ArrayList<>();
        this.ratings = new ArrayList<>();
    }

    /**
     * Returneaza durata filmului
     * @return
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Seteaza durata filmului
     * @param duration
     */
    public void setDuration(final int duration) {
        this.duration = duration;
    }

    /**
     * Returneaza lista de actori
     * @return
     */
    public List<String> getActors() {
        return actors;
    }

    /**
     * Seteaza lista de actori
     * @param actors
     */
    public void setActors(final List<String> actors) {
        this.actors = actors;
    }

    /**
     * Returneaza rating-urile
     * @return
     */
    public List<Double> getRatings() {
        return ratings;
    }

    /**
     * Seteaza rating-urile
     * @param ratings
     */
    public void setRatings(final List<Double> ratings) {
        this.ratings = ratings;
    }

    /**
     * Calculeaza rating-ul filmului prin adunarea tuturor rating-urilor
     * si apoi impartirea sumei la cate rating-uri sunt
     * @return
     */
    public double rating() {
        double sum = 0;
        int numOfRatings = 0;

        // Daca lista de rating-uri e goala, nu a fost oferit niciun rating
        if (this.getRatings().isEmpty()) {
            return 0;
        }

        for (Double rating : this.getRatings()) {
            sum += rating;
            numOfRatings += 1;
        }
        return sum / numOfRatings;
    }
}
