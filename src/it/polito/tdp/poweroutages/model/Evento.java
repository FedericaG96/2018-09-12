package it.polito.tdp.poweroutages.model;

import java.time.LocalDateTime;

public class Evento implements Comparable<Evento> {

	public enum Tipo{
		INIZIO_INTERRUZIONE,
		FINE_INTERRUZIONE,
		CANCELLA_PRESTITO,
	}
	
	private Tipo tipo;
	private Nerc nerc;
	private Nerc donatore;
	private LocalDateTime data;
	
	private LocalDateTime dataInizio;
	private LocalDateTime data_fine;
	public Evento(Tipo tipo, Nerc nerc, Nerc donatore, LocalDateTime data, LocalDateTime dataInizio,
			LocalDateTime data_fine) {
		super();
		this.tipo = tipo;
		this.nerc = nerc;
		this.donatore = donatore;
		this.data = data;
		this.dataInizio = dataInizio;
		this.data_fine = data_fine;
	}
	public Tipo getTipo() {
		return tipo;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
	public Nerc getNerc() {
		return nerc;
	}
	public void setNerc(Nerc nerc) {
		this.nerc = nerc;
	}
	public Nerc getDonatore() {
		return donatore;
	}
	public void setDonatore(Nerc donatore) {
		this.donatore = donatore;
	}
	public LocalDateTime getData() {
		return data;
	}
	public void setData(LocalDateTime data) {
		this.data = data;
	}
	public LocalDateTime getDataInizio() {
		return dataInizio;
	}
	public void setDataInizio(LocalDateTime dataInizio) {
		this.dataInizio = dataInizio;
	}
	public LocalDateTime getData_fine() {
		return data_fine;
	}
	public void setData_fine(LocalDateTime data_fine) {
		this.data_fine = data_fine;
	}
	@Override
	public int compareTo(Evento other) {
		return this.data.compareTo(other.data);
	}
	
}
