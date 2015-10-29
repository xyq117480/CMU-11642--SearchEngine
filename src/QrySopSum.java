import java.io.IOException;

/**
 *  The SUM operator for BM25 model.
 */
public class QrySopSum extends QrySop{

	/**
	 * Implement abstract method getScore() from QrySop.
	 * Get a score for the current matched document, 
	 * also for a set of terms.
	 * @param The retrieval model that determines how scores are calculated.
	 * @return The document score.
	 */
	@Override
	public double getScore(RetrievalModel r) throws IOException {
		if (r instanceof RetrievalModelBM25){
			
			
			double sum = 0;
			
			for( int i = 0; i < this.args.size(); i++){
				if (this.args.get(i).docIteratorHasMatchCache() && this.args.get(i).docIteratorGetMatch() == this.docIteratorGetMatch()){
					
					sum += ((QrySop)this.args.get(i)).getScore(r);
					
				}
				
			}
			return sum;
		}
		
		return 0;
	}
	
	
	/**
	 *  Indicates whether the query has a match.
     *  @param r The retrieval model that determines what is a match
	 *  @return True if the query matches, otherwise false.
	 */
	@Override
	public boolean docIteratorHasMatch(RetrievalModel r) {
		return this.docIteratorHasMatchMin(r);
	}
	
	
	/**
	 * Implement abstract method getRankedScore from Qry.
	 * Not useful in #SUM operator.
	 */
	@Override
	public double getRankedScore() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Implement abstract method getDfScore from Qry.
	 * Not useful in #SUM operator.
	 */
	@Override
	public double getDfScore() {
		// TODO Auto-generated method stub
		return -1000;
	}

	/**
	 * Implement abstract method getRankedScore from QrySop.
	 * Not useful in #SUM operator.
	 */
	@Override
	public double getDefaultScore(RetrievalModel r, long docid)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
