package entertainment;

import fileio.MovieInputData;
import fileio.SerialInputData;

import java.util.ArrayList;

public class Video {

    private String name;
    private int year;
    private ArrayList<String> genres;
    private int views;
    private int favourite;

    public Video(final MovieInputData movieInputData) {
        this.name = movieInputData.getTitle();
        this.year = movieInputData.getYear();
        this.genres = movieInputData.getGenres();
        this.views = 0;
        this.favourite = 0;
    }

    public Video(final SerialInputData serialInputData) {
        this.name = serialInputData.getTitle();
        this.year = serialInputData.getYear();
        this.genres = serialInputData.getGenres();
        this.views = 0;
        this.favourite = 0;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public int getYear() {
        return year;
    }

    /**
     *
     * @param year
     */
    public void setYear(final int year) {
        this.year = year;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getGenres() {
        return genres;
    }

    /**
     *
     * @param genres
     */
    public void setGenres(final ArrayList<String> genres) {
        this.genres = genres;
    }

    /**
     *
     * @return
     */
    public int getViews() {
        return views;
    }

    /**
     *
     * @param views
     */
    public void setViews(final int views) {
        this.views = views;
    }

    /**
     *
     * @return
     */
    public int getFavourite() {
        return favourite;
    }

    /**
     *
     * @param favourite
     */
    public void setFavourite(final int favourite) {
        this.favourite = favourite;
    }
}
