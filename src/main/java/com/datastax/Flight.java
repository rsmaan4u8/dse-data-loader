package com.datastax;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Integer.parseInt;

@Table(keyspace = "flightsdb", name = "flights")
public class Flight {
    @Transient
    private String[] line;

    private int id;
    private int year;
    private int day;
    private Date date;
    private int airlineId;
    private String carrier;
    private int flightNumber;
    private int originAirportId;
    private String origin;
    private String originCityName;
    private String originStateAbr;
    private String destination;
    private String destinationCityName;
    private String destinationStateAbr;
    private Date departureTime;
    private Date arrivalTime;
    private int actualElapsedTime;
    private int airTime;
    private int distance;
    private int airTimeBucket;


    public Flight(String line) {
        this.line = line.split(",");

    }

    @PartitionKey
    public int getId() {
        return parseInt(get(0));
    }

    @Column(name = "year")
    public int getYear() {
        return  parseInt(get(1));
    }

    @Column(name = "day_of_month")
    public int getDay() {
        return parseInt(get(2));
    }

    @Column(name = "fl_date")
    public Date getDate() throws ParseException {
        return new SimpleDateFormat("yyyy/MM/dd").parse(get(3));
    }

    @Column(name = "airline_id")
    public int getAirlineId() {
        return parseInt(get(4));
    }

    @Column(name = "carrier")
    public String getCarrier() {
        return get(5);
    }


    @Column(name = "fl_num")
    public int getFlightNumber() {
        return parseInt(get(6));
    }


    @Column(name = "origin_airport_id")
    public int getOriginAirportId() {
        return parseInt(get(7));
    }


    @Column(name = "origin")
    public String getOrigin() {
        return get(8);
    }


    @Column(name = "origin_city_name")
    public String getOriginCityName() {
        return get(9);
    }


    @Column(name = "origin_state_abr")
    public String getOriginStateAbr() {
        return get(10).trim();
    }


    @Column(name = "dest")
    public String getDestination() {
        return get(11);
    }


    @Column(name = "dest_city_name")
    public String getDestinationCityName() {
        return get(12);
    }


    @Column(name = "dest_state_abr")
    public String getDestinationStateAbr() {
        return get(13).trim();
    }


    @Column(name = "dep_time")
    public Date getDepartureTime() throws ParseException {
        char[] chars = get(14).toCharArray();
        String hour = getHour(chars);
        String minutes = getMinutes(chars);
        return new SimpleDateFormat("yyyy/MM/dd hh:mm").parse(get(3) +" "+hour+":"+minutes);
    }


    @Column(name = "arr_time")
    public Date getArrivalTime() throws ParseException {
        char[] chars = get(15).toCharArray();
        String hour = getHour(chars);
        String minutes = getMinutes(chars);
        Date arrival = new SimpleDateFormat("yyyy/MM/dd hh:mm").parse(get(3) + " " + hour + ":" + minutes);

        return arrival.before(getDepartureTime()) ? new Date(arrival.getTime() + (1000 * 60 * 60 * 24)) : arrival;
    }


    @Column(name = "actual_elapsed_time")
    public int getActualElapsedTime() throws ParseException {
        return Integer.parseInt(get(16));
    }


    @Column(name = "air_time")
    public int getAirTime() throws ParseException {
        return Integer.parseInt(get(17));
    }

    @Column(name = "distance")
    public int getDistance() {
        return parseInt(get(18));
    }

    @Column(name = "air_time_bucket")
    public int getAirTimeBucket() throws ParseException {
        return getAirTime() / 10;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void setFlightNumber(int flightNumber) {
        this.flightNumber = flightNumber;
    }

    public void setOriginAirportId(int originAirportId) {
        this.originAirportId = originAirportId;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setOriginCityName(String originCityName) {
        this.originCityName = originCityName;
    }

    public void setOriginStateAbr(String originStateAbr) {
        this.originStateAbr = originStateAbr;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDestinationCityName(String destinationCityName) {
        this.destinationCityName = destinationCityName;
    }

    public void setDestinationStateAbr(String destinationStateAbr) {
        this.destinationStateAbr = destinationStateAbr;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setActualElapsedTime(int actualElapsedTime) {
        this.actualElapsedTime = actualElapsedTime;
    }

    public void setAirTime(int airTime) {
        this.airTime = airTime;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setAirTimeBucket(int airTimeBucket) {
        this.airTimeBucket = airTimeBucket;
    }

    private String getMinutes(char[] chars) {
        if (chars.length == 1)
            return "0"+chars[0];
        else if(chars.length == 2)
            return ""+chars[0]+chars[1];
        else if(chars.length == 3)
            return ""+chars[1]+chars[2];
        else
            return ""+chars[2]+chars[3];
    }

    private String getHour(char[] chars) {
        if(chars.length == 4)
            return ""+ chars[0] + chars[1];
        else if(chars.length == 3)
            return "0"+ chars[0];
        else
            return "00";
    }


    private String get(int index) {
        return this.line[index].trim();
    }
    
}
