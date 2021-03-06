package clustering;

import java.util.ArrayList;
import java.util.List;
import shingle.MaskedShingleVector;
import shingle.ShingleVector;
import tag.Tag;
import tag.Pagina;

public class PageClustering {

	List<Cluster> clusters;

	public PageClustering() {
		this.clusters = new ArrayList<>();
	}

	public void algorithm(List<Pagina> sample_pages){

		/**************************FIRST PASS************************/
		HashTable table = new HashTable();

		for (Pagina p : sample_pages){

			//creazione vettore v
			List<Tag> p_taglist = p.getListaTag();
			p.createShingles(p_taglist);
			ShingleVector v;
			p.createShingleVector();
			v = p.getShingleVector();
			v.createMasks();

			for (MaskedShingleVector mv : v.getAllMV()){
				if (table.contains(mv))
					mv.incrementCount();
				else{
					mv.setCount(1);
					table.insert(mv);
				}
			}

			System.out.println(v.getAllMV().toString());
		}
		/*************************END FIRST PASS********************/
		
		
		/*************************SECOND PASS***********************/
		
		table.sort_by_count_increasing();
		//Lista di tutti i vettori 8/8 in ordine crescente del count
		List<MaskedShingleVector> all88MV = table.getAll88MaskedVector();
		table.sort_by_count_decreasing();
		int count_for_decrement=0;
		boolean mv_with_max_count = false;
		
		//per ogni 8/8 troviamo il masked che lo copre con il maggiore count
		//e diminuaiamo di un valore pari al suo count il count degli altri
		//Masked che coprono l'8/8
		for (MaskedShingleVector mv : all88MV){
			ShingleVector v1 = new ShingleVector();
			v1.copy(mv);
			if(mv.cover(v1) ){
				if(!mv_with_max_count){
					count_for_decrement = mv.getCount();
					mv_with_max_count = true;
				}
				else{
					mv.decrementCount(count_for_decrement);
				}
			}
		}
		
		//Eliminiamo i Masked con un count al di sotto di una soglia predefinita
		table.deleteAllUnderTreshold(10);
		
		/*************************END SECOND PASS*************************/
		
		
		/*************************THIRD PASS******************************/
		
		//Creiamo un cluster per ogni Masked rimanente in H
		for(MaskedShingleVector mv: table.getTable_mvectors()){
			Cluster c = new Cluster();
			c.setSignature(mv);
			clusters.add(c);
		}
		
		//Per ogni pagina controlliamo quale masked copre il suo shingle vector
		//per poi aggiungere la pagina al cluster associato al masked
		for (Pagina p : sample_pages){
			table.sort_by_count_decreasing();
			for(MaskedShingleVector mv : table.getTable_mvectors()){
				if(mv.cover(p.getShingleVector())){
					Cluster c = getClusterFromSignature(mv);
					c.addPage(p);
				}
			}
		}
		/*************************END THIRD PASS**************************/
		
	}

	public List<Cluster> getClusters() {
		System.out.println("Stampa cluster");
		return clusters;
	}

	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}

	private Cluster getClusterFromSignature(MaskedShingleVector mv){
		for(Cluster c:this.clusters)
			if(c.getSignature().equals(mv))
				return c;
		return null;
	}


}

