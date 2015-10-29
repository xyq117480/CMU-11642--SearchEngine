/** 
 *  Copyright (c) 2015, Carnegie Mellon University.  All Rights Reserved.
 */
import java.io.*;

/**
 *  The root class of all query operators that use a retrieval model
 *  to determine whether a query matches a document and to calculate a
 *  score for the document.  This class has two main purposes.  First, it
 *  allows query operators to easily recognize any nested query
 *  operator that returns a document scores (e.g., #AND (a #OR(b c)).
 *  Second, it is a place to store data structures and methods that are
 *  common to all query operators that calculate document scores.
 */
public abstract class QrySop extends Qry {

  /**
   *  Get a score for the document that docIteratorHasMatch matched.
   *  @param r The retrieval model that determines how scores are calculated.
   *  @return The document score.
   *  @throws IOException Error accessing the Lucene index
   */
  public abstract double getScore (RetrievalModel r)
    throws IOException;
  
  /**
   * Get a default score for a document specified by docid. It is used for 
   * Indri model when there is no match.
   * @param r The retrieval model, i.e. Indri.
   * @param docid The document id used to do calculation, e.g. document length.
   * @return
   * @throws IOException
   */
  public abstract double getDefaultScore (RetrievalModel r, long docid)
    throws IOException;

  /**
   *  Initialize the query operator (and its arguments), including any
   *  internal iterators.  If the query operator is of type QryIop, it
   *  is fully evaluated, and the results are stored in an internal
   *  inverted list that may be accessed via the internal iterator.
   *  @param r A retrieval model that guides initialization
   *  @throws IOException Error accessing the Lucene index.
   */
  public void initialize(RetrievalModel r) throws IOException {
    for (Qry q_i: this.args) {
      q_i.initialize (r);
    }
  }
  
  
}
