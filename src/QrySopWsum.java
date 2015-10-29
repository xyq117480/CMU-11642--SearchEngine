import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  The WSUM operator for Indri model.
 */

public class QrySopWsum extends QrySop {

	public List<Double> weights = new ArrayList<Double>();
	
	@Override
	public boolean docIteratorHasMatch(RetrievalModel r) {
		
		if(r instanceof RetrievalModelIndri){
			return this.docIteratorHasMatchMin(r);
		}else{
			throw new IllegalArgumentException
	        (r.getClass().getName() + " doesn't support the WSUM operator.");
		}
		
		
	}
	
	/**
	 * Implement abstract method getScore() from QrySop.
	 * Get a score for the current matched document, 
	 * also for a set of terms.
	 * @param The retrieval model that determines how scores are calculated.
	 * @return The document score.
	 */	
	@Override
	public double getScore(RetrievalModel r) throws IOException {
		if (r instanceof RetrievalModelIndri){
			return this.getScoreIndri(r);
		}else{
			throw new IllegalArgumentException
	        (r.getClass().getName() + " doesn't support the WSUM operator.");
		}
	}

	 /**
	  * Implement abstract method getDefaulScore() from QrySop.
	  * @param r The retrieval model that determines how scores are calculated.
	  * @param docid The internal document id in current inverted list.
	  * @return A default score.
	  * @throws IOException
	  */
	@Override
	public double getDefaultScore(RetrievalModel r, long docid)
			throws IOException {
		  double score = 0.0;
		  double weightSum = 0.0;
		  for (int i = 0; i < this.weights.size(); i++){
			  weightSum += this.weights.get(i);
		  }
		  if( r instanceof RetrievalModelIndri){
			  for (int i = 0; i < this.args.size(); i++){
				  
				  score += (this.weights.get(i) / weightSum) * ((QrySop)this.args.get(i)).getDefaultScore(r, docid);
				  
			  }
		  }
		  return score;
	}
	
	
	 /**
	  * Calculate score when retrieval model is Indri.
	  * If current term's docid matches, it calls regular getScore() method.
	  * Otherwise, it calls getDefaultScore() method to fetch score.
	  * @param r The retrieval model that determines how scores are calculated.
	  * @return The score for current operation, i.e. AND. 
	  * @throws IOException
	  */
	public double getScoreIndri (RetrievalModel r) throws IOException {
		  double score = 0.0;
		  double weightSum = 0.0;
		  
		  for (int i = 0; i < this.weights.size(); i++){
			  weightSum += this.weights.get(i);
		  }
		  
		  for (int i = 0; i < this.args.size(); i++){
			  if(this.args.get(i).docIteratorHasMatchCache()&&this.args.get(i).docIteratorGetMatch() == this.docIteratorGetMatch()){
				  score +=  (this.weights.get(i) / weightSum) * ((QrySop) this.args.get(i)).getScore(r);
				  //System.out.println(" in QrySopAnd getScoreIndri:  score = " + score + "  coming from getScore()");
			  }
			  else{
				  score += (this.weights.get(i) / weightSum) * ((QrySop)this.args.get(i)).getDefaultScore(r, this.docIteratorGetMatch());
				  //System.out.println(" in QrySopWand getScoreIndri:  score = " + score + "  coming from getDefaultScore()");
			  }
		  }
		  
		  
		  return score;
	  }

	@Override
	public double getRankedScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDfScore() {
		// TODO Auto-generated method stub
		return 0;
	}

}
