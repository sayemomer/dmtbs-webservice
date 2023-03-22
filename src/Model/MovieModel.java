package Model;

import java.util.List;
import java.util.ArrayList;

import static com.web.webcontroller.implementation.MovieManagement.*;


public class MovieModel {

    public static final String MOVIE_TIME_MORNING = "Morning";
    public static final String MOVIE_TIME_AFTERNOON = "Afternoon";
    public static final String MOVIE_TIME_EVENING = "Evening";

    public static final String AVATAR = "Avatar";
    public static final String AVENGER = "Avenger";
    public static final String TITANIC = "Titanic";

    public static final int EVENT_FULL = -1;
    public static final int ALREADY_REGISTERED = 0;
    public static final int ADD_SUCCESS = 1;

    private String movieType;
    private String movieID;
    private int movieCapacity;

    private String movieTimeSlot;
    private String movieServer;

    private String movieDate;

    private List<String> registeredClients;


    public MovieModel(String movieType , String movieID , int movieCapacity){
        this.movieID = movieID;
        this.movieType = movieType;
        this.movieCapacity = movieCapacity;
        this.movieTimeSlot = detectMovieTimeSlot(movieID);
        this.movieServer = detectMovieServer(movieID);
        this.movieDate = detectMovieDate(movieID);
        registeredClients = new ArrayList<>();

    }

    public static String detectMovieServer(String movieID){
        if (movieID.substring(0, 3).equalsIgnoreCase("ATW")) {
            return MOVIE_SERVER_ATWATER;
        } else if (movieID.substring(0, 3).equalsIgnoreCase("VER")) {
            return MOVIE_SERVER_VERDUN;
        } else {
            return MOVIE_SERVER_OUTRAMONT;
        }
    }

    public static String detectMovieTimeSlot(String movieID){
        if (movieID.substring(3, 4).equalsIgnoreCase("M")) {
            return MOVIE_TIME_MORNING;
        } else if (movieID.substring(3, 4).equalsIgnoreCase("A")) {
            return MOVIE_TIME_AFTERNOON;
        } else {
            return MOVIE_TIME_EVENING;
        }
    }

    public static String detectMovieDate(String movieID) {

        return movieID.substring(4, 6) + "/" + movieID.substring(6, 8) + "/20" + movieID.substring(8, 10);
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getMovieServer() {
        return movieServer;
    }

    public void setMovieServer(String eventServer) {
        this.movieServer = movieServer;
    }

    public int getMovieCapacity() {
        return movieCapacity;
    }

    public void setMovieCapacity(int movieCapacity) {
        this.movieCapacity = movieCapacity;
    }

    public int getMovieRemainCapacity() {
        return movieCapacity - registeredClients.size();
    }

    public String getMovieDate() {
        return movieDate;
    }

    public void setMovieDate(String movieDate) {
        this.movieDate = movieDate;
    }

    public String getMovieTimeSlot() {
        return movieTimeSlot;
    }

    public void setMovieTimeSlot(String movieTimeSlot) {
        this.movieTimeSlot = movieTimeSlot;
    }

    public boolean isFull() {
        return getMovieCapacity() == registeredClients.size();
    }

    public List<String> getRegisteredClientIDs() {
        return registeredClients;
    }

    public void setRegisteredClientsIDs(List<String> registeredClientsIDs) {
        this.registeredClients = registeredClientsIDs;
    }

    public int addRegisteredClientID(String registeredClientID) {
        if (!isFull()) {
            if (registeredClients.contains(registeredClientID)) {
                return ALREADY_REGISTERED;
            } else {
                registeredClients.add(registeredClientID);
                return ADD_SUCCESS;
            }
        } else {
            return EVENT_FULL;
        }
    }

    public boolean removeRegisteredClientID(String registeredClientID) {
        return registeredClients.remove(registeredClientID);
    }

    @Override
    public String toString() {
        return " (" + getMovieID() + ") in the " + getMovieTimeSlot() + " of " + getMovieDate() + " Total[Remaining] Capacity: " + getMovieCapacity() + "[" + getMovieRemainCapacity() + "]";
    }

}