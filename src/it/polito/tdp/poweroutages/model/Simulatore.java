package it.polito.tdp.poweroutages.model;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Simulatore {
	
	//Modello del mondo
	private Graph<Nerc,DefaultWeightedEdge> grafo;
	private List<PowerOutage> powerOutage;
	private Map<Nerc, Set<Nerc>> prestiti;
	
	//Parametri della simulazione
	private int k; 	//Numero di mesi
	
	//Valori in output
	private int CATASTROFI;
	private Map<Nerc, Long> bonus;
	
	//Coda
	private PriorityQueue<Evento> queue;
	
	public void init(int k, List<PowerOutage> powerOutages, NercIdMap nercMap, Graph<Nerc,DefaultWeightedEdge> grafo) {
		this.queue = new PriorityQueue<Evento>();
		this.bonus = new HashMap<Nerc, Long>();
		this.prestiti = new HashMap<Nerc, Set<Nerc>>();
	
		for(Nerc n : nercMap.values() ) {
			this.bonus.put(n, Long.valueOf(0));
			this.prestiti.put(n, new HashSet<Nerc>());
		}
		
		this.CATASTROFI = 0;
		this.k = k;
		this.powerOutage = powerOutages;
		this.grafo= grafo;
		
		//inserisco gli eventi iniziali
		for(PowerOutage po : this.powerOutage) {
			//imposto a null il donatore perchè all'inizio non si sa
			//  la data dell'evento INIZIO_INTERRUZIONE corrisponde alla data di inizio interruzione
			Evento e = new Evento(Evento.Tipo.INIZIO_INTERRUZIONE, po.getNerc(), null, po.getInizio(), po.getInizio(),po.getFine());
			queue.add(e);
		}
	}

	public void run() {
		
		Evento e;
		while((e = queue.poll() )!= null) {
			switch (e.getTipo()) {
			
			case INIZIO_INTERRUZIONE:
				
				Nerc nerc = e.getNerc();
				System.out.println("INIZIO INTERRUZIONE NERC: " + nerc);
				
				//cerco se c'è un donatore, altrimenti catastrofe
				Nerc donatore = null;
				
				//Algoritmo per selezionare il donatore
				
				//cerco tra i miei "debitori"
				//altrimenti prendo quello con peso arco minore
				if(this.prestiti.get(nerc).size()>0) {
					//scelgo tra i miei debitori
					double min = Long.MAX_VALUE;	//la classe Long racchiude un valore del tipo primitivo Long in un oggetto
													//MAX_VALUE :costante che mantiene il valore massimo che può avere un Long
					for(Nerc n : this.prestiti.get(nerc)) {
						DefaultWeightedEdge edge = this.grafo.getEdge(nerc, n);
						if(this.grafo.getEdgeWeight(edge) < min) { 
							if(!n.getStaPrestando()) {		//se il nerc debitore non sta prestando
								donatore = n;
								min = this.grafo.getEdgeWeight(edge);
						}
						}
					}
				}else {
					//prendo quello con peso arco minore, stavolta scorro tutti i nerc
					
					double min = Long.MAX_VALUE;
					List<Nerc> neighbours = Graphs.neighborListOf(this.grafo, nerc);
					for(Nerc n : neighbours) {
						DefaultWeightedEdge edge = this.grafo.getEdge(nerc, n);
						if(this.grafo.getEdgeWeight(edge) < min) { 
							if(!n.getStaPrestando()) {
								donatore = n;
								min = this.grafo.getEdgeWeight(edge);
						}
						}
					}					
				}
				
				if(donatore != null) {
					System.out.println("\tTROVATO DONATORE: " + donatore);
					donatore.setStaPrestando(true); 
					//  la data dell' evento FINE_INTERRUZIONE corrisponde alla data di fine interruzione
					Evento fine = new Evento(Evento.Tipo.FINE_INTERRUZIONE, e.getNerc(), donatore, e.getData_fine(),  e.getDataInizio(), e.getData_fine());
					queue.add(fine);
					this.prestiti.get(donatore).add(e.getNerc());	//aggiungo al set di prestiti del donatore, il nerc a cui ha dato energia
					Evento cancella = new Evento(Evento.Tipo.CANCELLA_PRESTITO,  e.getNerc(), donatore, e.getData().plusMonths(k),  e.getDataInizio(), e.getData_fine());
					this.queue.add(cancella);
				
				} else {
					System.out.println("\tCATASTROFE!!!!");
					this.CATASTROFI++;
				}
			
				break;
			
			case FINE_INTERRUZIONE:
				System.out.println("FINE INTERRUZIONE NERC "+ e.getNerc());
				
				//assegnare un bonus al donatore
				if(e.getDonatore()!=null) {
					this.bonus.put(e.getDonatore(), bonus.get(e.getDonatore()) + Duration.between(e.getDataInizio(), e.getData_fine()).toDays());
				}
				
				//dire che il donatore non sta più prestando
				e.getDonatore().setStaPrestando(false);
				
				break;
				
			case  CANCELLA_PRESTITO:
				System.out.println("CANCELLO PRESTITO "+ e.getDonatore()+ "-" + e.getNerc());
				
				
				//rimuovo dal set di prestiti del donatore il prestito
				this.prestiti.get(e.getDonatore()).remove(e.getNerc());
				break;
			}
		}
	}
	
	public int getCatastrofi() {
		return this.CATASTROFI;
	}
	
	public Map<Nerc,Long> getBonus(){
		return this.bonus;
	}
}
