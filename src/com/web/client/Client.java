package com.web.client;


import com.web.staticType.Types;
import com.web.helper.Helper;

//import Logger.Logger;
import Model.MovieModel;
import com.web.webcontroller.ControllerInterface;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.Scanner;


public class Client {

    public static final int SERVER_ATWATER = 2964;
    public static final int SERVER_VERDUN = 2965;
    public static final int SERVER_OUTRAMONT = 2966;
    public static final String MOVIE_MANAGEMENT_REGISTERED_NAME = "MOVIE_MANAGEMENT";

    static Scanner input;
    public static Service atwaterService;
    public static Service verdunService;
    public static Service outramontService;
    private static ControllerInterface obj;

    public static void main(String[] args) throws Exception {
        URL atwaterURL = new URL("http://localhost:8080/atwater?wsdl");
        QName atwaterlQName = new QName("http://implementation.webcontroller.web.com/", "MovieManagementService");
        atwaterService = Service.create(atwaterURL, atwaterlQName);

        URL verdunURL = new URL("http://localhost:8080/verdun?wsdl");
        QName verdunQName = new QName("http://implementation.webcontroller.web.com/", "MovieManagementService");
        verdunService = Service.create(verdunURL, verdunQName);

        URL outramontURL = new URL("http://localhost:8080/outramont?wsdl");
        QName outramontQName = new QName("http://implementation.webcontroller.web.com/", "MovieManagementService");
        outramontService = Service.create(outramontURL, outramontQName);
        init();
    }

    public static void init() throws Exception {
        input = new Scanner(System.in);
        String userID;
        System.out.println("Enter UserID:(e.g. ATWA2345 or ATWC2345) ");
        userID = input.next().trim().toUpperCase();

            if ( Helper.checkUserType(userID) == Types.USER_TYPE_CUSTOMER) {
                try {
                    System.out.println("Successfully logged in as Customer (" + userID + ")");
                    customer(userID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Helper.checkUserType(userID) == Types.USER_TYPE_ADMIN) {
                try {
                    System.out.println("Successfully logged in as Admin (" + userID + ")");
                    admin(userID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Invalid userID format !! ( e.g. ATWA2345 or ATWC2345 )");
                init();
            }
    }

    private static String getServerID(String userID) {
        String branchAcronym = userID.substring(0, 3);
        if (branchAcronym.equalsIgnoreCase("ATW")) {
            obj = atwaterService.getPort(ControllerInterface.class);
            return branchAcronym;
        } else if (branchAcronym.equalsIgnoreCase("VER")) {
            obj = verdunService.getPort(ControllerInterface.class);
            return branchAcronym;
        } else if (branchAcronym.equalsIgnoreCase("OUT")) {
            obj = outramontService.getPort(ControllerInterface.class);
            return branchAcronym;
        }
        return "1";
    }

    private static void customer(String customerID) throws Exception {
        String serverID = getServerID(customerID);
        if (serverID.equals("1")) {
            init();
        }
        boolean repeat = true;
        printMenu(Types.USER_TYPE_CUSTOMER);
        int menuSelection = input.nextInt();
        String movieType;
        String movieID;
        String serverResponse;
        switch (menuSelection) {
            case Types.CUSTOMER_BOOK_MOVIE:
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                //Logger.clientLog(customerID, " attempting to bookEvent");
                serverResponse = obj.bookMovie(customerID, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(customerID, " bookEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case Types.CUSTOMER_GET_BOOKING_SCHEDULE:
               // Logger.clientLog(customerID, " attempting to getBookingSchedule");
                serverResponse = obj.getBookingSchedule(customerID);
                System.out.println(serverResponse);
                //Logger.clientLog(customerID, " bookEvent", " null ", serverResponse);
                break;
            case Types.CUSTOMER_CANCEL_MOVIE:
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                //Logger.clientLog(customerID, " attempting to cancelEvent");
                serverResponse = obj.cancelMovie(customerID, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(customerID, " bookEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case Types.CUSTOMER_SWAP_MOVIE:
                System.out.println("Please Enter the OLD movie to be replaced");
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                System.out.println("Please Enter the NEW movie to be replaced");
                String newMovieType = promptForMovieType();
                String newMovieID = promptForMovieID();
                //Logger.clientLog(customerID, " attempting to swapEvent");
                serverResponse = obj.swapMovie(customerID, newMovieID, newMovieType, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(customerID, " swapEvent", " oldEventID: " + eventID + " oldEventType: " + eventType + " newEventID: " + newEventID + " newEventType: " + newEventType + " ", serverResponse);
                break;
            case Types.CUSTOMER_LOGOUT:
                repeat = false;
                //Logger.clientLog(customerID, " attempting to Logout");
                init();
                break;
        }
        if (repeat) {
            customer(customerID);
        }
    }

    private static void admin(String movieAdminID) throws Exception {
        String serverID = getServerID(movieAdminID);
        if (serverID.equals("1")) {
            init();
        }
        boolean repeat = true;
        printMenu(Types.USER_TYPE_ADMIN);
        String customerID;
        String movieType;
        String movieID;
        String serverResponse;
        int capacity;
        int menuSelection = input.nextInt();
        switch (menuSelection) {
            case Types.ADMIN_ADD_MOVIE:
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                capacity = Helper.promptForCapacity(input);
               // Logger.clientLog(eventManagerID, " attempting to addEvent");
                serverResponse = obj.addMovie(movieID, movieType, capacity);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " addEvent", " eventID: " + eventID + " eventType: " + eventType + " eventCapacity: " + capacity + " ", serverResponse);
                break;
            case Types.ADMIN_REMOVE_MOVIE:
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                //Logger.clientLog(eventManagerID, " attempting to removeEvent");
                serverResponse = obj.removeMovie(movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " removeEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case Types.ADMIN_LIST_MOVIE_AVAILABILITY:
                movieType = promptForMovieType();
                //Logger.clientLog(eventManagerID, " attempting to listEventAvailability");
                serverResponse = obj.listMovieAvailability(movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " listEventAvailability", " eventType: " + eventType + " ", serverResponse);
                break;
            case Types.ADMIN_BOOK_MOVIE:
                customerID = askForCustomerIDFromManager(movieAdminID.substring(0, 3));
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                //Logger.clientLog(eventManagerID, " attempting to bookEvent");
                serverResponse = obj.bookMovie(customerID, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " bookEvent", " customerID: " + customerID + " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case Types.ADMIN_GET_BOOKING_SCHEDULE:
                customerID = askForCustomerIDFromManager(movieAdminID.substring(0, 3));
                //Logger.clientLog(eventManagerID, " attempting to getBookingSchedule");
                serverResponse = obj.getBookingSchedule(customerID);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " getBookingSchedule", " customerID: " + customerID + " ", serverResponse);
                break;
            case Types.ADMIN_CANCEL_EVENT:
                customerID = askForCustomerIDFromManager(movieAdminID.substring(0, 3));
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                //Logger.clientLog(eventManagerID, " attempting to cancelEvent");
                serverResponse = obj.cancelMovie(customerID, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " cancelEvent", " customerID: " + customerID + " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case Types.ADMIN_SWAP_EVENT:
                customerID = askForCustomerIDFromManager(movieAdminID.substring(0, 3));
                System.out.println("Please Enter the OLD movie to be swapped");
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                System.out.println("Please Enter the NEW movie to be swapped");
                String newMovieType = promptForMovieType();
                String newMovieID = promptForMovieID();
                //Logger.clientLog(eventManagerID, " attempting to swapEvent");
                serverResponse = obj.swapMovie(customerID, newMovieID, newMovieType, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " swapEvent", " customerID: " + customerID + " oldEventID: " + eventID + " oldEventType: " + eventType + " newEventID: " + newEventID + " newEventType: " + newEventType + " ", serverResponse);
                break;
            case Types.ADMIN_LOGOUT:
                repeat = false;
                //Logger.clientLog(eventManagerID, "attempting to Logout");
                init();
                break;
        }
        if (repeat) {
            admin(movieAdminID);
        }
    }

    private static String askForCustomerIDFromManager(String branchAcronym) {
        System.out.println("Please enter a customerID(Within " + branchAcronym + " Server):");
        String userID = input.next().trim().toUpperCase();
        if (Helper.checkUserType(userID) != Types.USER_TYPE_CUSTOMER || !userID.substring(0, 3).equals(branchAcronym)) {
            return askForCustomerIDFromManager(branchAcronym);
        } else {
            return userID;
        }
    }

    private static void printMenu(int userType) {
        System.out.println("---------------------------------");
        System.out.println("Please select one of the following options from the menu below:");
        if (userType == Types.USER_TYPE_CUSTOMER) {
            System.out.println("1.Book a Ticket");
            System.out.println("2.Get booking Schedule");
            System.out.println("3.Cancel ticket");
            System.out.println("4.Swap ticket");
            System.out.println("5.Logout");
        } else if (userType == Types.USER_TYPE_ADMIN) {
            System.out.println("1.Add show");
            System.out.println("2.Remove show");
            System.out.println("3.List show Availability");
//            System.out.println("4.Book a show");
//            System.out.println("5.Get Booking Schedule");
//            System.out.println("6.Cancel ticket");
           // System.out.println("7.Swap ticket");
            System.out.println("4.Logout");
        }
    }

    private static String promptForMovieType() {
        System.out.println("--------------------------------");
        System.out.println("Please choose an movieType below:");
        System.out.println("1.AVATAR");
        System.out.println("2.AVENGER");
        System.out.println("3.TITANIC");
        switch (input.nextInt()) {
            case 1:
                return MovieModel.AVATAR;
            case 2:
                return MovieModel.AVENGER;
            case 3:
                return MovieModel.TITANIC;
        }
        return promptForMovieType();
    }

    private static String promptForMovieID() {
        System.out.println("-----------------------");
        System.out.println("Please enter the movieID (e.g ATWM190120)");
        String showID = input.next().trim().toUpperCase();
        if (showID.length() == 10) {
            if (showID.substring(0, 3).equalsIgnoreCase("ATW") ||
                    showID.substring(0, 3).equalsIgnoreCase("VER") ||
                    showID.substring(0, 3).equalsIgnoreCase("OUT")) {
                if (showID.substring(3, 4).equalsIgnoreCase("M") ||
                        showID.substring(3, 4).equalsIgnoreCase("A") ||
                        showID.substring(3, 4).equalsIgnoreCase("E")) {
                    return showID;
                }
            }
        }
        return promptForMovieID();
    }


}