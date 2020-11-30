package entertainment;

import fileio.SerialInputData;

import java.util.ArrayList;
import java.util.List;

public class Show extends Video {

    private List<String> cast;
    private int numberOfSeasons;
    private List<Season> seasons;
    private int totalDuration;

    public Show(final SerialInputData serialInputData) {
        super(serialInputData);
        this.cast = new ArrayList<>();
        this.numberOfSeasons = serialInputData.getNumberSeason();
        this.seasons = serialInputData.getSeasons();
        this.totalDuration = 0;
    }

    /**
     * @return
     */
    public List<String> getCast() {
        return cast;
    }

    /**
     *
     * @param cast
     */
    public void setCast(final List<String> cast) {
        this.cast = cast;
    }

    /**
     *
     * @return
     */
    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    /**
     *
     * @param numberOfSeasons
     */
    public void setNumberOfSeasons(final int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    /**
     *
     * @return
     */
    public List<Season> getSeasons() {
        return seasons;
    }

    /**
     *
     * @param seasons
     */
    public void setSeasons(final List<Season> seasons) {
        this.seasons = seasons;
    }

    /**
     *
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
     *
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
