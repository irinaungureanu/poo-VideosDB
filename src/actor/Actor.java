package actor;

import database.Database;
import fileio.ActorInputData;

import java.util.ArrayList;
import java.util.Map;

public class Actor {
    /**
     * Numele actorului
     */
    private String name;
    /**
     * O descriere a carierei actorului
     */
    private String careerDescription;
    /**
     * Filmele/Serialele in care actorul a avut un rol de jucat
     */
    private ArrayList<String> filmography;
    /**
     * Premiile castigate
     */
    private Map<ActorsAwards, Integer> awards;

    public Actor(final ActorInputData actorInputData) {
        this.name = actorInputData.getName();
        this.careerDescription = actorInputData.getCareerDescription();
        this.filmography = actorInputData.getFilmography();
        this.awards = actorInputData.getAwards();
    }

    /**
     * Returneaza numele actorului
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Seteaza numele actorului
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returneaza descrierea carierei actorului
     * @return
     */
    public String getCareerDescription() {
        return careerDescription;
    }

    /**
     * Seteaza descrierea carierei actorului
     * @param careerDescription
     */
    public void setCareerDescription(final String careerDescription) {
        this.careerDescription = careerDescription;
    }

    /**
     * Returneaza lista de video-uri (filme & seriale) in care a jucat actorul
     * @return
     */
    public ArrayList<String> getFilmography() {
        return filmography;
    }

    /**
     * Seteaza lista de video-uri in care a jucat actorul
     * @param filmography
     */
    public void setFilmography(final ArrayList<String> filmography) {
        this.filmography = filmography;
    }

    /**
     * Returneaza lista de premii obtinute
     * @return
     */
    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    /**
     *  Seteaza lista de premii obtinuta
     *  @param awards lista de premii
     */
    public void setAwards(final Map<ActorsAwards, Integer> awards) {
        this.awards = awards;
    }

    /**
     * Calculeaza media dintre rating-ul filmelor si rating-ul serialelor
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
