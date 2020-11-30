package entertainment;

import fileio.MovieInputData;

import java.util.ArrayList;
import java.util.List;

public class Movie extends Video {

    private int duration;
    private List<String> actors;
    private List<Double> ratings;

    public Movie(final MovieInputData movieInputData) {
        super(movieInputData);
        this.duration = movieInputData.getDuration();
        this.actors = new ArrayList<>();
        this.ratings = new ArrayList<>();
    }

    /**
     * @return
     */
    public int getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     */
    public void setDuration(final int duration) {
        this.duration = duration;
    }

    /**
     *
     * @return
     */
    public List<String> getActors() {
        return actors;
    }

    /**
     *
     * @param actors
     */
    public void setActors(final List<String> actors) {
        this.actors = actors;
    }

    /**
     *
     * @return
     */
    public List<Double> getRatings() {
        return ratings;
    }

    /**
     *
     * @param ratings
     */
    public void setRatings(final List<Double> ratings) {
        this.ratings = ratings;
    }

    /**
     *
     * @return
     */
    public double rating() {
        double sum = 0;
        int numOfRatings = 0;

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
