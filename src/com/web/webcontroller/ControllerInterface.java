package com.web.webcontroller;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ControllerInterface {
    public String addMovie(String movieID, String movieType, int bookingCapacity);

    public String removeMovie(String movieID, String movieType);

    public String listMovieAvailability(String movieType);



    public String bookMovie(String customerID, String movieID, String movieType);

    public String getBookingSchedule(String customerID);

    public String cancelMovie(String customerID, String movieID, String movieType);

    public String swapMovie(String customerID, String newMovieID, String newMovieType, String oldMovieID, String oldMovieType);

}