package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.genes.db.GenesDao;

public class Model {

	private GenesDao dao;
	private List<Genes> geni;
	private Map<String,Genes> idMapGeni;
	private List<Integer> cromosomi;
	private Graph<Integer,DefaultWeightedEdge> grafo;
	private List<Interactions> interazioni;
	Graph<Integer,DefaultWeightedEdge> grafoAppoggio ;
	private List<Integer> bestCammino;
	private double bestLunghezza;
	
	public Model() {
		
		this.dao = new GenesDao();
		
	}
	
	public void creaGrafo() {
		clearGraph();
		this.geni = new ArrayList<Genes>(this.dao.getAllGenes());
		loadMappaGeni();
		this.cromosomi = new ArrayList<Integer>(this.dao.getAllVertices());
		Graphs.addAllVertices(this.grafo, this.cromosomi);
		this.interazioni = new ArrayList<Interactions>(this.dao.getAllInteractions(idMapGeni));
		
		
		for (Interactions i : interazioni) {
			Integer chrom1 = idMapGeni.get(i.getGene1().getGeneId()).getChromosome();
			Integer chrom2 = idMapGeni.get(i.getGene2().getGeneId()).getChromosome();
			if(this.grafo.getEdge(chrom1, chrom2)==null) {
				Graphs.addEdge(this.grafo, chrom1, chrom2, i.getExpressionCorr());
			} else {
				DefaultWeightedEdge edge = this.grafo.getEdge(chrom1, chrom2);
				double peso = this.grafo.getEdgeWeight(edge);
				this.grafo.setEdgeWeight(edge, peso+i.getExpressionCorr());
				
			}
			
			
		}
		
		
	}

	private void clearGraph() {
		this.grafo = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
	}
	
	private void loadMappaGeni() {
		this.idMapGeni = new HashMap<String,Genes>();
		for (Genes g : this.geni) {
			idMapGeni.put(g.getGeneId(), g);
		}
	}

	public Graph<Integer, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}
	
	public double getPesoMinimo() {
		double pesoMinimo =0.0;
		
		for (DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)<pesoMinimo) {
				pesoMinimo = this.grafo.getEdgeWeight(e);
			}
		}
		return pesoMinimo;
	}
	
	public double getPesoMassimo() {
		double pesoMassimo =0.0;
		for (DefaultWeightedEdge e : this.grafo.edgeSet()) {
			
			if(this.grafo.getEdgeWeight(e)>pesoMassimo) {
				pesoMassimo = this.grafo.getEdgeWeight(e);
			}
		}
		return pesoMassimo;
	}

	public int getArchiSopraSoglia(double soglia) {
		int n = 0;
		for (DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if (this.grafo.getEdgeWeight(e)>soglia) {
				n++;
			}
		}
		return n;
	}
	
	public int getArchiSottoSoglia(double soglia) {
		int n = 0;
		for (DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if (this.grafo.getEdgeWeight(e)<soglia) {
				n++;
			}
		}
		return n;
	}

	public List<Integer> ricercaCammino(double soglia) {
		
		this.grafoAppoggio = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafoAppoggio, this.grafo.vertexSet())	;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(grafo.getEdgeWeight(e)>soglia) {
				this.grafoAppoggio.addEdge(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), e);
			}
		}
		
		List<Integer> visitabili = new ArrayList<Integer>(this.grafoAppoggio.vertexSet());
		List<Integer> visitati = new ArrayList<>();
		
//		for (Integer partenza : visitabili) {
//			visitati.add(partenza);
//			
//			visitati.remove(partenza);
//		}
		ricorsione(visitati,visitabili,soglia);
		return this.bestCammino;
	}

	private void ricorsione(List<Integer> visitati, List<Integer> visitabili, double soglia) {
		//condizione di uscita
		if(calcolaLunghezza(visitati)>bestLunghezza) {
			this.bestCammino = new ArrayList<>(visitati);
			this.bestLunghezza = calcolaLunghezza(visitati);
			return;
		}
		for (Integer next : visitabili) {
			if(!visitati.contains(next)) {
				visitati.add(next);
				List<Integer> successivi = new ArrayList<Integer>(Graphs.successorListOf(grafoAppoggio, next));
				ricorsione(visitati,successivi,soglia);
				visitati.remove(next);
			}
		}
		
	}

	private double calcolaLunghezza(List<Integer> visitati) {
		double lunghezza = 0.0;
		if(visitati.size()>1) {
			Integer partenza = visitati.get(0);
			for(int i=1; i<visitati.size();i++) {
				
				Integer successivo = visitati.get(i);
				DefaultWeightedEdge e = this.grafoAppoggio.getEdge(partenza, successivo);
				
				lunghezza += this.grafoAppoggio.getEdgeWeight(e);
				partenza = successivo;
			}
		}
		
		return lunghezza;
	}
}