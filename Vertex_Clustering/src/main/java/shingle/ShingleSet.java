package shingle;

import java.util.ArrayList;
import java.util.List;

import hashing.FactoryHashFunctions;
import hashing.HashFunctionInterface;
import tag.Tag;

public class ShingleSet {
	
	
	private List<Shingle> shingle_set;
	private ShingleVector shinglevector;
	
	
	public ShingleSet(List<Tag> page) {
		this.createShingles(page);
	} 
	

	public void createShingles(List<Tag> page) {
		
		this.shingle_set= new ArrayList<Shingle>(); 
		for(int i=0; i<page.size()-10; i++) {
			Shingle shingle = new Shingle(10);
			shingle.setTag_sequence(page.subList(i, i+10));
			this.shingle_set.add(shingle);
		}
	}

	public ShingleVector getShingleVector() {
		return this.shinglevector;
	}

	public List<Shingle> getShingle_set() {
		return this.shingle_set;
	}


	public void setShingle_set(List<Shingle> shingle_set) {
		this.shingle_set = shingle_set;
	}
	
	
	public void createShingleVector() {
		
		this.shinglevector = new ShingleVector();
		String[] vector=new String[8];

		FactoryHashFunctions factory=new FactoryHashFunctions();
		List<HashFunctionInterface> listHashFunctions=new ArrayList<>();
		listHashFunctions=factory.getFactory();

		for(int i=0;i<listHashFunctions.size();i++) {
			
			HashFunctionInterface hashFunction=listHashFunctions.get(i);

			int max = Integer.MAX_VALUE;
			String min = Integer.toString(max);

			for(Shingle s : getShingle_set()) {
				String input = s.concatTagSequence();
				String output = hashFunction.getHash(input);

				if(output.compareTo(min)<0)
					min=output;
			}

			int primo = Integer.parseInt(min.substring(0, min.length()/4), 16);
			int secondo = Integer.parseInt(min.substring(min.length()/4, min.length()/2), 16);
			int terzo = Integer.parseInt(min.substring(min.length()/2, min.length()*3/4), 16);
			int quarto = Integer.parseInt(min.substring(min.length()*3/4, min.length()), 16);

			vector[i] = Integer.toString(primo^secondo^terzo^quarto);
		}

		this.shinglevector.setVector(vector);
	}
	
}
