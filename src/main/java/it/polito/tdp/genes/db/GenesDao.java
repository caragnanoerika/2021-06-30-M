package it.polito.tdp.genes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.genes.model.Genes;
import it.polito.tdp.genes.model.Interactions;


public class GenesDao {
	
	public List<Genes> getAllGenes(){
		String sql = "SELECT DISTINCT GeneID, Essential, Chromosome FROM Genes WHERE Chromosome > 0 ";
		List<Genes> result = new ArrayList<Genes>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes = new Genes(res.getString("GeneID"), 
						res.getString("Essential"), 
						res.getInt("Chromosome"));
				result.add(genes);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	

	public List<Integer> getAllVertices(){
		String sql = "SELECT DISTINCT Chromosome FROM Genes WHERE Chromosome > 0 ";
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Integer chromosome = res.getInt("Chromosome");
				result.add(chromosome);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	
	
	}
	
	public List<Interactions> getAllInteractions(Map<String,Genes> idMap){
		String sql = "SELECT DISTINCT g1.Chromosome,  i.* ,  g2.Chromosome "
				+ "FROM interactions i, genes g1, genes g2 "
				+ "WHERE g1.GeneID = i.GeneID1 AND g2.GeneID = i.GeneID2 AND g1.Chromosome != g2.Chromosome AND g1.Chromosome>0 AND g2.Chromosome>0 "
				+ "ORDER BY g1.Chromosome, g2.Chromosome ";
		
		List<Interactions> interazioni = new ArrayList<Interactions>();
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Genes g1 = idMap.get(res.getString("i.GeneID1"));
				Genes g2 = idMap.get(res.getString("i.GeneID2"));
				
				interazioni.add(new Interactions(g1,g2, res.getString("i.Type"),res.getDouble("i.Expression_Corr")));
			}
			res.close();
			st.close();
			conn.close();
			return interazioni;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}

	
}
