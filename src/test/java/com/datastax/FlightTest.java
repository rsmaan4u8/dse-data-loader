package com.datastax;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.Assert.*;

public class FlightTest {

    @Test
    public void testFlight_shouldParseLine() throws ParseException {
        String line = "3,2012,1,2012/11/11,19805,AA,1,12478,JFK,New York, NY,LAX,Los Angeles, CA,855,1142,347,330,2475";

        Flight flight = new Flight(line);

        assertEquals(3, flight.getId());
        assertEquals(2012, flight.getYear());
        assertEquals(1, flight.getDay());
        assertEquals("2012/11/11 00:00:00", getDateString(flight.getDate()));
        assertEquals(19805, flight.getAirlineId());
        assertEquals("AA", flight.getCarrier());
        assertEquals(1, flight.getFlightNumber());
        assertEquals(12478, flight.getOriginAirportId());
        assertEquals("JFK", flight.getOrigin());
        assertEquals("New York", flight.getOriginCityName());
        assertEquals("NY", flight.getOriginStateAbr());
        assertEquals("LAX", flight.getDestination());
        assertEquals("Los Angeles", flight.getDestinationCityName());
        assertEquals("CA", flight.getDestinationStateAbr());
        assertEquals("2012/11/11 08:55:00" , getDateString(flight.getDepartureTime()));
        assertEquals("2012/11/11 11:42:00", getDateString(flight.getArrivalTime()));
        assertEquals(347, (flight.getActualElapsedTime()));
        assertEquals(330, (flight.getAirTime()));
        assertEquals(2475, flight.getDistance());
    }

    @Test
    public void testFlight_shouldAdjustDate() throws ParseException {
        String line = "250,2012,31,2012/01/31,19805,AA,8,12173,HNL,Honolulu, HI,DFW,Dallas/Fort Worth, TX,1817,546,449,425,3784";

        Flight flight = new Flight(line);

        assertEquals(250, flight.getId());
        assertEquals(2012, flight.getYear());
        assertEquals(31, flight.getDay());
        assertEquals("2012/01/31 00:00:00", getDateString(flight.getDate()));
        assertEquals(19805, flight.getAirlineId());
        assertEquals("AA", flight.getCarrier());
        assertEquals(8, flight.getFlightNumber());
        assertEquals(12173, flight.getOriginAirportId());
        assertEquals("HNL", flight.getOrigin());
        assertEquals("Honolulu", flight.getOriginCityName());
        assertEquals("HI", flight.getOriginStateAbr());
        assertEquals("DFW", flight.getDestination());
        assertEquals("Dallas/Fort Worth", flight.getDestinationCityName());
        assertEquals("TX", flight.getDestinationStateAbr());
        assertEquals("2012/01/31 18:17:00" , getDateString(flight.getDepartureTime()));
        assertEquals("2012/02/01 05:46:00", getDateString(flight.getArrivalTime()));
        assertEquals(449, (flight.getActualElapsedTime()));
        assertEquals(425, (flight.getAirTime()));
        assertEquals(3784, flight.getDistance());
    }

    @Test
    public void testFlight_shouldAdjustHour() throws ParseException {
        String line = "250,2012,31,2012/01/28,19805,AA,8,12173,HNL,Honolulu, HI,DFW,Dallas/Fort Worth, TX,117,546,449,425,3784";

        Flight flight = new Flight(line);

        assertEquals("2012/01/28 05:46:00", getDateString(flight.getArrivalTime()));
    }

    @Test
    public void testFlight_shouldAdjustMinute() throws ParseException {
        String line = "250,2012,31,2012/01/28,19805,AA,8,12173,HNL,Honolulu, HI,DFW,Dallas/Fort Worth, TX,17,46,449,425,3784";

        Flight flight = new Flight(line);

        assertEquals("2012/01/28 00:17:00", getDateString(flight.getDepartureTime()));
        assertEquals("2012/01/28 00:46:00", getDateString(flight.getArrivalTime()));
    }

    @Test
    public void testFlight_shouldAdjustSingleMinute() throws ParseException {
        String line = "250,2012,31,2012/01/28,19805,AA,8,12173,HNL,Honolulu, HI,DFW,Dallas/Fort Worth, TX,1,9,449,425,3784";

        Flight flight = new Flight(line);

        assertEquals("2012/01/28 00:01:00", getDateString(flight.getDepartureTime()));
        assertEquals("2012/01/28 00:09:00", getDateString(flight.getArrivalTime()));
    }

    private String getDateString(Date date) {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
    }
}