package ru.terentyev.ideaplatformtesttask.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ru.terentyev.ideaplatformtesttask.util.CustomTimeDeserializer;


public class TicketRequest {
	
	private String origin;
	private String originName;
	private String destination;
	private String destinationName;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yy")
	private LocalDate departureDate;
	@JsonDeserialize(using = CustomTimeDeserializer.class)
	private LocalTime departureTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yy")
	private LocalDate arrivalDate;
	@JsonDeserialize(using = CustomTimeDeserializer.class)
	private LocalTime arrivalTime;
	private String carrier;
	private int stops;
	private int price;
	
	public TicketRequest(){}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getOriginName() {
		return originName;
	}

	public void setOriginName(String originName) {
		this.originName = originName;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public LocalDate getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(LocalDate departureDate) {
		this.departureDate = departureDate;
	}

	public LocalTime getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(LocalTime departureTime) {
		this.departureTime = departureTime;
	}

	public LocalDate getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(LocalDate arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public int getStops() {
		return stops;
	}

	public void setStops(int stops) {
		this.stops = stops;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	
	
	
}
