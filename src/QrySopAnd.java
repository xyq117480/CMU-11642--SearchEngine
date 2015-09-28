/**
 *  Copyright (c) 2015, Carnegie Mellon University.  All Rights Reserved.
 */

import java.io.*;

/**
 *  The OR operator for all retrieval models.
 */
public class QrySopAnd extends QrySop {

  /**
   *  Indicates whether the query has a match.
   *  @param r The retrieval model that determines what is a match
   *  @return True if the query matches, otherwise false.
   */
  public boolean docIteratorHasMatch (RetrievalModel r) {
    return this.docIteratorHasMatchAll (r);
  }

  /**
   *  Get a score for the document that docIteratorHasMatch matched.
   *  @param r The retrieval model that determines how scores are calculated.
   *  @return The document score.
   *  @throws IOException Error accessing the Lucene index
   */
  public double getScore (RetrievalModel r) throws IOException {

    if (r instanceof RetrievalModelUnrankedBoolean) {
      return this.getScoreUnrankedBoolean (r);
    }else if (r instanceof RetrievalModelRankedBoolean){
    	return this.getScoreRankedBoolean (r);
    } else {
      throw new IllegalArgumentException
        (r.getClass().getName() + " doesn't support the AND operator.");
    }
  }
  /**
   *  getScore for the RankedBoolean retrieval model.
   *  @param r The retrieval model that determines how scores are calculated.
   *  @return The document score.
   *  @throws IOException Error accessing the Lucene index
   */
  private double getScoreUnrankedBoolean (RetrievalModel r) throws IOException {
	  if (! this.docIteratorHasMatchCache()) {
	     return 0.0;
	  } else {
		  return 1.0;
	  }
  }
  /**
   *  getScore for the UnrankedBoolean retrieval model.
   *  @param r The retrieval model that determines how scores are calculated.
   *  @return The document score.
   *  @throws IOException Error accessing the Lucene index
   */
  private double getScoreRankedBoolean (RetrievalModel r) throws IOException {
	  if (! this.docIteratorHasMatchCache()) {
		  return 0.0;
      } else {
     	  return this.getRankedScore();
	  }
 
  }
  
  /**
   * Implements abstract getRankedScore() method.
   * Get score for AND operator. Use min to calculate score for each term.
   * @return The AND score.
   */
  
  public double getRankedScore () {
	  double min = Double.MAX_VALUE;
      for(int i = 0; i<this.args.size(); i++){
   
    	  if(this.args.get(i).docIteratorHasMatchCache()&&this.args.get(i).docIteratorGetMatch() == this.docIteratorGetMatch()){
    		 
    		if(this.args.get(i).getRankedScore()<min){
    			min = this.args.get(i).getRankedScore();
    		}
    		 
    	  }
    	  
      }
      return min;
  }
  
}
