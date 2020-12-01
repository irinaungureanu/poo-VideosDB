package entertainment;

import fileio.MovieInputData;
import fileio.SerialInputData;

import java.util.ArrayList;

public class Video {
    /**
     * Titlul video-ului
     */
    private String name;
    /**
     * Anul in care a fost lansat
     */
    private int year;
    /**
     * Genurile din care face parte
     */
    private ArrayList<String> genres;
    /**
     * Numarul de vizionari
     */
    private int views;
    /**
     * Numarul de utilizatori in lista carora a fost adaugat ca favorit
     */
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
     *  Retruneaza titlul video-ului
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *  Seteaza titlul video-ului
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *  Returneaza anul de lansare
     * @return
     */
    public int getYear() {
        return year;
    }

    /**
     *  Seteaza anul de lansare
     * @param year
     */
    public void setYear(final int year) {
        this.year = year;
    }

    /**
     *  Returneaza genurile
     * @return
     */
    public ArrayList<String> getGenres() {
        return genres;
    }

    /**
     *  Seteaza genurile
     * @param genres
     */
    public void setGenres(final ArrayList<String> genres) {
        this.genres = genres;
    }

    /**
     *  Returneaza numarul de vizionari
     * @return
     */
    public int getViews() {
        return views;
    }

    /**
     *  Seteaza numarul de vizionari
     * @param views
     */
    public void setViews(final int views) {
        this.views = views;
    }

    /**
     *  Returneaza numarul de adaugari la favorite
     * @return
     */
    public int getFavourite() {
        return favourite;
    }

    /**
     *  Seteaza numarul de adaugari la favorite
     * @param favourite
     */
    public void setFavourite(final int favourite) {
        this.favourite = favourite;
    }
}
