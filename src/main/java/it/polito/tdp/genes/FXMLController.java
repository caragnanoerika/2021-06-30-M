/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.genes;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.genes.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model ;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnContaArchi"
    private Button btnContaArchi; // Value injected by FXMLLoader

    @FXML // fx:id="btnRicerca"
    private Button btnRicerca; // Value injected by FXMLLoader

    @FXML // fx:id="txtSoglia"
    private TextField txtSoglia; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doContaArchi(ActionEvent event) {
    	this.model.creaGrafo();
    	this.txtResult.setText("Grafo creato!\n");
    	this.txtResult.appendText("# archi: " + this.model.getGrafo().vertexSet().size() +"\n");
    	this.txtResult.appendText("# vertici: " + this.model.getGrafo().edgeSet().size()+"\n");
    	this.txtResult.appendText("\n\n ");
    	this.txtResult.appendText("Peso minimo: " + this.model.getPesoMinimo() + "\n");
    	this.txtResult.appendText("Peso massimo: " + this.model.getPesoMassimo());
    
    	this.txtResult.appendText("\n\n ");
    	try {
    		double soglia = Double.parseDouble(this.txtSoglia.getText());
    		
    		this.txtResult.appendText("# archi sopra la soglia: " + this.model.getArchiSopraSoglia(soglia)+"\n");
        	this.txtResult.appendText("# archi sotto la soglia: " + this.model.getArchiSottoSoglia(soglia)+"\n");
    	} catch(NumberFormatException e) {
    		this.txtResult.setText("ERRORE: la soglia deve essere un numero decimale. ");
    	}
    	
    
    
    }

    @FXML
    void doRicerca(ActionEvent event) {
    	try {
    		double soglia = Double.parseDouble(this.txtSoglia.getText());
    		List<Integer> cammino = new ArrayList<Integer>(this.model.ricercaCammino(soglia));
    		
    		for (Integer i : cammino) {
    			this.txtResult.appendText(""+ i + "\n");
    		}
    		
    	} catch(NumberFormatException e) {
    		this.txtResult.setText("ERRORE: la soglia deve essere un numero decimale. ");
    	}
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnContaArchi != null : "fx:id=\"btnContaArchi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnRicerca != null : "fx:id=\"btnRicerca\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtSoglia != null : "fx:id=\"txtSoglia\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model ;
		
	}
}
