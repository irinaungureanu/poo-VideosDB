package entertainment;

import fileio.SerialInputData;

import java.util.ArrayList;
import java.util.List;

public class Show extends Video {
    /**
     * Actorii din serial
     */
    private List<String> cast;
    /**
     * Numarul de sezoane ale serialului
     */
    private int numberOfSeasons;
    /**
     * Lista cu informatii pentru fiecare sezon
     */
    private List<Season> seasons;
    /**
     * Durata totala a serialului
     */
    private int totalDuration;

    public Show(final SerialInputData serialInputData) {
        super(serialInputData);
        this.cast = new ArrayList<>();
        this.numberOfSeasons = serialInputData.getNumberSeason();
        this.seasons = serialInputData.getSeasons();
        this.totalDuration = 0;
    }

    /**
     * Returneaza actorii
     * @return
     */
    public List<String> getCast() {
        return cast;
    }

    /**
     * Seteaza actorii
     * @param cast
     */
    public void setCast(final List<String> cast) {
        this.cast = cast;
    }

    /**
     * Returneaza numarul de sezoane
     * @return
     */
    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    /**
     * Seteaza numarul de sezoane
     * @param numberOfSeasons
     */
    public void setNumberOfSeasons(final int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    /**
     * Returneaza lista de sezoane
     * @return
     */
    public List<Season> getSeasons() {
        return seasons;
    }

    /**
     * Seteaza lista de sezoane
     * @param seasons
     */
    public void setSeasons(final List<Season> seasons) {
        this.seasons = seasons;
    }

    /**
     *  Calculeaza durata totala a serialului prin adunarea duratelor tuturor
     *  sezoanelor
     * @return
     */
    public int getTotalDuration() {
        int duration = 0;

        for (int i = 0; i < this.numberOfSeasons; i++) {
            duration += this.getSeasons().get(i).getDuration();
        }
        return duration;
    }

    /**
     *  Calculeaza rating-ul serialului prin adunarea rating-urilor fiecarui
     *  sezon si impartirea la numarul de sezoane. Pentru a calcula rating-ul
     *  fiecarui sezon, se aduna toate rating-urile date sezonului respectiv
     *  si apoi se imparte la numarul de rating-uri dat.
     * @return
     */
    public double rating() {
        double sumTotal = 0;
        /*
         *  Vedem care este rating-ul pentru fiecare sezon, apoi facem o medie
         *  pentru a afla rating-ul pentru tot serialul
         */
        for (int i = 0; i < this.numberOfSeasons; i++) {
            double sumCurrentSeason = 0;
            int numRatings = 0;
            // Daca lista de rating-uri e goala, rating-ul acelui sezon e 0
            if (this.getSeasons().get(i).getRatings().isEmpty()) {
                continue;
            }

            for (int j = 0; j < this.getSeasons().get(i).getRatings().size(); j++) {
                sumCurrentSeason += this.getSeasons().get(i).getRatings().get(j);
                numRatings += 1;
            }
            sumTotal += sumCurrentSeason / numRatings;
        }
        return sumTotal / this.numberOfSeasons;
    }
}
