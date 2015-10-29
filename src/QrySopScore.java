/**
 *  Copyright (c) 2015, Carnegie Mellon University.  All Rights Reserved.
 */

import java.io.*;
import java.lang.IllegalArgumentException;
import java.lang.reflect.Field;

/**
 *  The SCORE operator for all retrieval models.
 */
public class QrySopScore extends QrySop {

  /**
   *  Document-independent values that should be determined just once.
   *  Some retrieval models have these, some don't.
   */
  
  /**
   *  Indicates whether the query has a match.
   *  @param r The retrieval model that determines what is a match
   *  @return True if the query matches, otherwise false.
   */
  public boolean docIteratorHasMatch (RetrievalModel r) {
    return this.docIteratorHasMatchFirst (r);
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
    }else if(r instanceof RetrievalModelRankedBoolean){
      return this.getScoreRankedBoolean (r);
    }else if(r instanceof RetrievalModelBM25){
      return this.getScoreBM25(r);
    }else if(r instanceof RetrievalModelIndri){
      return this.getScoreIndri(r);
    }else {
      throw new IllegalArgumentException
        (r.getClass().getName() + " doesn't support the SCORE operator.");
    }
  }
  
  /**
   *  getScore for the Unranked retrieval model.
   *  @param r The retrieval model that determines how scores are calculated.
   *  @return The document score.
   *  @throws IOException Error accessing the Lucene index
   */
  public double getScoreUnrankedBoolean (RetrievalModel r) throws IOException {
    if (! this.docIteratorHasMatchCache()) {
      return 0.0;
    } else {
      return 1.0;
    }
  }
  
  /**
   *  getScore for the Ranked retrieval model.
   *  @param r The retrieval model that determines how scores are calculated.
   *  @return The document score.
   *  @throws IOException Error accessing the Lucene index
   */
  public double getScoreRankedBoolean (RetrievalModel r) throws IOException {
    if (! this.docIteratorHasMatchCache()) {
      return 0.0;
    } else {
      
      return getRankedScore();
    }
  }
  
  
  /**
   * getScore for BM25 model.
   * @param r The retrieval model that determines how scores are calculated.
   * @return The score for current document.
   * @throws IOException
   */
  public double getScoreBM25 (RetrievalModel r) throws IOException {
	  if (!this.docIteratorHasMatchCache()){
		  return 0.0;
	  }
	  else{
		  double k1 = ((RetrievalModelBM25)r).k1;
		  double b = ((RetrievalModelBM25)r).b;
		  double k3 = ((RetrievalModelBM25)r).k3;
		  
		  double N = Idx.getNumDocs();
		  double df = this.args.get(0).getDfScore();
		  double tf = this.args.get(0).getRankedScore();
		  double doclen = Idx.getFieldLength(((QryIop)this.args.get(0)).getField(),this.args.get(0).docIteratorGetMatch() );
		  double qtf = 1;
		  double avg_doclen = Idx.getSumOfFieldLengths(((QryIop)this.args.get(0)).getField()) / (double) Idx.getDocCount(((QryIop)this.args.get(0)).getField());
		 
		  // idf could not be zero
		  double idf = Math.max(0, Math.log( (N-df+0.5)/(df+0.5) ));
		  //idf = Math.log( (N-df+0.5)/(df+0.5) );
		  double tf_weight = tf / (tf + k1*((1-b)+b*doclen/avg_doclen));
		  double user_weight = (k3+1)*qtf/(k3+qtf);
		  
		  return idf * tf_weight * user_weight;
	  }
  }
  
  /**
   * getScore for the Indri model.
   * This is used as regular getScore() method. 
   * @param r The retrieval model that determines how scores are calculated.
   * @return The score for current term + matched document.
   * @throws IOException
   */
  public double getScoreIndri (RetrievalModel r) throws IOException {
	  
	  double mu = ((RetrievalModelIndri)r).mu;
	  double lambda = ((RetrievalModelIndri)r).lambda;
	  try {
		  String field = ((QryIop)this.args.get(0)).getField();
		  double tf = this.args.get(0).getRankedScore();
		  float lengthC = Idx.getSumOfFieldLengths(field);
		  float lengthD = Idx.getFieldLength(field, this.args.get(0).docIteratorGetMatch());
		  double ctf =  ((QryIop)this.args.get(0)).getCtf();
		  double pMLE = ctf / lengthC;
		  double defaulScore = ( (1-lambda) * ( tf + mu*pMLE) / (lengthD + mu) ) + lambda * pMLE;
		  //System.out.println(defaulScore);
		  return defaulScore;
	  }
	  catch (IOException e){
		  System.out.println("***Error in getDefaultScore");
	  }
	  
	  return 0.0;
	  
  }
  
  /**
   * Implements abstract getRankedScore() method.
   * Get score for SCORE operator. 
   * @return The score for the document of current term.
   */
  public double getRankedScore(){
	  return this.args.get(0).getRankedScore();
  }
  
  /**
   * Implement abstract method getDfScore() from Qry.
   * Get the document frequency for current term.
   * @return Document frequency.
   */
  public double getDfScore(){
	  return this.args.get(0).getDfScore();
  }
  
  /**
   * Implement abstract method getDefaultScore() from QrySop.
   * Calculate a default score for Indri model.
   * @param r The retrieval model that determines how scores are calculated.
   * @param docid The document id in current term's inverted list.
   * @return The default score.
   */
  public double getDefaultScore (RetrievalModel r, long docid){
	  double mu = ((RetrievalModelIndri)r).mu;
	  double lambda = ((RetrievalModelIndri)r).lambda;
	  try {
		  String field = ((QryIop)this.args.get(0)).getField();
		  //double tf = this.args.get(0).getRankedScore();
		  float lengthC = Idx.getSumOfFieldLengths(field);
		  float lengthD = Idx.getFieldLength(field, (int)docid);
		  double ctf =  ((QryIop)this.args.get(0)).getCtf();
		  double pMLE = ctf / lengthC;
		  double defaulScore = ( (1-lambda) * ( 0 + mu*pMLE) / (lengthD + mu) ) + lambda * pMLE;
		  //System.out.println(" in QrySopScore getDefault:  score = " + defaulScore + "  coming from calculation");
		  return defaulScore;
	  }
	  catch (IOException e){
		  System.out.println("***Error in getDefaultScore");
	  }
	  
	  return 0.0;
	 
	  
	  
  }
  
  /**
   * Get the field parameter of current term.
   * @return Field string.
   */
  public String getField(){
	  return ((QryIop)this.args.get(0)).getField();
  }
  
  /**
   *  Initialize the query operator (and its arguments), including any
   *  internal iterators.  If the query operator is of type QryIop, it
   *  is fully evaluated, and the results are stored in an internal
   *  inverted list that may be accessed via the internal iterator.
   *  @param r A retrieval model that guides initialization
   *  @throws IOException Error accessing the Lucene index.
   */
  public void initialize (RetrievalModel r) throws IOException {

    Qry q = this.args.get (0);
    q.initialize (r);
  }

}
