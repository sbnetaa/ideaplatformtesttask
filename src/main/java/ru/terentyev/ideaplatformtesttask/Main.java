package ru.terentyev.ideaplatformtesttask;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ru.terentyev.ideaplatformtesttask.entities.TicketRequest;

public class Main {

	private static ObjectMapper objectMapper = new ObjectMapper();

	public static void main(String[] args) {
		setup();
		List<TicketRequest> tickets = readJsonToTicketsList("src/main/resources/tickets.json");	
		Map<String, List<TicketRequest>> ticketsPerCarrier = separateTicketsPerCarrier(tickets);		
		Map<String, List<TicketRequest>> ticketsFiltered = filterTicketsByCities(ticketsPerCarrier);
		Map<String, Map<TicketRequest, Duration>> ticketsPerCarrierWithDuration = calculateDuration(ticketsFiltered);
		Map<String, Duration> minimumDurationPerCarrier = findMinimumDurationPerCarrier(ticketsPerCarrierWithDuration);
		
		System.out.println("Минимальное время перелета для каждой компании:\n КОМПАНИЯ - ВРЕМЯ\n");
		for (Map.Entry<String, Duration> entry : minimumDurationPerCarrier.entrySet())
			System.out.println(entry.getKey() + " - " + entry.getValue().toHours() + " часов, " + entry.getValue().toMinutesPart() + " минут");
		System.out.println("\n\n\n");
		
		List<TicketRequest> ticketsWithRequiredPath = findTicketsWithRequiredPath(ticketsFiltered);
		double median = findMedian(ticketsWithRequiredPath);
		double middle = findMiddle(ticketsWithRequiredPath);
		printDifference(median, middle);
	}

	
	public static void setup() {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
	}
	
	
	public static List<TicketRequest> readJsonToTicketsList(String path){
		List<TicketRequest> tickets = null;
		
		try {
			Map <String, List<TicketRequest>> requestMap = objectMapper.readValue(new File(path), new TypeReference<Map<String, List<TicketRequest>>>() {});
			tickets = requestMap.get("tickets");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return tickets;
	}
	
	
	public static Map<String, List<TicketRequest>> separateTicketsPerCarrier(List<TicketRequest> tickets){
		Map <String, List<TicketRequest>> ticketsPerCarrier = new HashMap<>();
		for (TicketRequest ticket : tickets) {
			String carrier = ticket.getCarrier();
			if (!ticketsPerCarrier.containsKey(carrier)) ticketsPerCarrier.put(carrier, new ArrayList<>());
			ticketsPerCarrier.get(carrier).add(ticket);
		}
		return ticketsPerCarrier;
	}
	
	
	// Здесь мы просто обновляем мапу, отсеивая перелеты с ненужными нам городами
	public static Map<String, List<TicketRequest>> filterTicketsByCities(Map<String, List<TicketRequest>> inputMap){	
		for (Map.Entry<String, List<TicketRequest>> entry : inputMap.entrySet()) 
			inputMap.put(entry.getKey(), inputMap.get(entry.getKey()).stream().filter(tr -> (tr.getOrigin().equals("VVO") && tr.getDestination().equals("TLV"))).collect(Collectors.toList()));
		return inputMap;
	}
	
	
	public static Map<String, Map<TicketRequest, Duration>> calculateDuration(Map<String, List<TicketRequest>> inputMap) {
		Map<String, Map<TicketRequest, Duration>> outputMap = new HashMap<>();
		for (Map.Entry<String, List<TicketRequest>> entry : inputMap.entrySet()) {
			outputMap.put(entry.getKey(), new HashMap<>());
			for (TicketRequest ticket : entry.getValue()) {
				LocalDateTime departure = ticket.getDepartureDate().atTime(ticket.getDepartureTime());
				LocalDateTime arrival = ticket.getArrivalDate().atTime(ticket.getArrivalTime());			
				Duration duration = Duration.between(departure, arrival);
				outputMap.get(entry.getKey()).put(ticket, duration);
			}
		}
		
		return outputMap;
	}
	
	
	public static Map<String, Duration> findMinimumDurationPerCarrier(Map<String, Map<TicketRequest, Duration>> inputMap){
		Map<String, Duration> outputMap = new HashMap<>();
		for (Map.Entry<String, Map<TicketRequest, Duration>> outerEntry : inputMap.entrySet()) {
			Duration minimum = null;
			for (Map.Entry<TicketRequest, Duration> innerEntry : outerEntry.getValue().entrySet()) {
				if (minimum == null || innerEntry.getValue().minus(minimum).toMinutes() < 0) minimum = innerEntry.getValue();
			}
			outputMap.put(outerEntry.getKey(), minimum);
		}
		return outputMap;
	}
	
	public static List<TicketRequest> findTicketsWithRequiredPath(Map<String, List<TicketRequest>> inputMap){
		List<TicketRequest> ticketsWithRequiredPath = new ArrayList<>();
		for (Map.Entry<String, List<TicketRequest>> entry : inputMap.entrySet()) {
			for (TicketRequest ticket : entry.getValue()) {
				ticketsWithRequiredPath.add(ticket);
			}
		}
		return ticketsWithRequiredPath;
	}
	
	public static double findMedian(List<TicketRequest> inputList) {
		return (inputList.get((int)Math.ceil(inputList.size() / 2.0)).getPrice() + inputList.get((int)Math.floor(inputList.size() / 2)).getPrice()) / 2;
	}
	
	
	public static double findMiddle(List<TicketRequest> inputList) {
		double sum = 0;
		for (TicketRequest ticket : inputList)
			sum += (double) ticket.getPrice();
		return sum / inputList.size();
	}
	
	public static void printDifference(double median, double middle) {
		double result = median - middle;
		if (result > 0) 
			System.out.println("Медиана(" + median + ") больше средней цены(" + middle + ") на " + result + " единиц");
		else if (result < 0)
			System.out.println("Средняя цена(" + middle + ") больше медианы(" + median + ") на " + Math.abs(result) + " единиц");
		else
			System.out.println("Средняя цена(" + middle + ") равна медиане(" + median + ")");
	}
}
