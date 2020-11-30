package actor;

import database.Database;
import fileio.ActorInputData;

import java.util.ArrayList;
import java.util.Map;

public class Actor {

    private String name;
    private String careerDescription;
    private ArrayList<String> filmography;
    private Map<ActorsAwards, Integer> awards;

    public Actor(final ActorInputData actorInputData) {
        this.name = actorInputData.getName();
        this.careerDescription = actorInputData.getCareerDescription();
        this.filmography = actorInputData.getFilmography();
        this.awards = actorInputData.getAwards();
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
    public String getCareerDescription() {
        return careerDescription;
    }

    /**
     *
     * @param careerDescription
     */
    public void setCareerDescription(final String careerDescription) {
        this.careerDescription = careerDescription;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getFilmography() {
        return filmography;
    }

    /**
     *
     * @param filmography
     */
    public void setFilmography(final ArrayList<String> filmography) {
        this.filmography = filmography;
    }

    /**
     *
     * @return awards lisa de premii
     */
    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    /**
     *  Set Awards
     *  @param awards lista de premii
     */
    public void setAwards(final Map<ActorsAwards, Integer> awards) {
        this.awards = awards;
    }

    /**
     *
     * @return
     */
    public double average() {
        double sum = 0;
        int numberOfVideos = 0;

        for (String video : this.getFilmography()) {
            // Iau pe rand cele 2 tipuri de video-uri: filme si seriale
            if (Database.getInstance().getMovies().containsKey(video)) {
                Double currentRating = Database.getInstance().getMovies().
                        get(video).rating();
                if (currentRating != 0) {
                    sum += currentRating;
                    numberOfVideos += 1;
                }
            }
            if (Database.getInstance().getShows().containsKey(video)) {
                Double currentRating = Database.getInstance().getShows().
                        get(video).rating();
                if (currentRating != 0) {
                    sum += currentRating;
                    numberOfVideos += 1;
                }
            }
        }

        if (numberOfVideos != 0) {
            return sum / numberOfVideos;
        }

        return 0;
    }
}
