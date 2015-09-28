import java.io.*;
import java.util.*;

/**
 * The NEAR/n operator for all retrieval models.
 */

public class QryIopNear extends QryIop{
	
	private int distance;
	
	/**
	 * Constructor. 
	 * @param n A max distance between two words. 
	 */
	public QryIopNear(int n){
		this.distance = n;
	}
	
	/**
	 *  Evaluate the query operator; the result is an internal inverted
	 *  list that may be accessed via the internal iterators.
	 *  @throws IOException Error accessing the Lucene index.
	 */
	
	public void evaluate() throws IOException {
		
		//  Create an empty inverted list.  
		this.invertedList = new InvList (this.getField());
		
		if (this.args.size () == 0) {
		     return;
		}
		
		//  Create an empty score list. 
		//  Score list here is only for debugging and printing. 
		ScoreList scorelist = new ScoreList();
		
		
		//  Each pass of the loop adds 1 document to scorelist
	    //  until  inverted lists are depleted.
		while(true){
			
			// Find the common part of inverted lists of all terms.
			if(!this.docIteratorHasMatchAll(null)){
				break;
			}
			
			int id = this.args.get(0).docIteratorGetMatch();
			
			if(id ==  Qry.INVALID_DOCID){
				break;
			}
			
			QryIop qi_last = ((QryIop)this.args.get(0));
			
			
			List<Integer> last = this.getLocationList(qi_last);
			
			// Each loop compares two locations on two inverted lists, storing the result to
			// a new list for next loop use.
			
			for(int i = 1; i < this.args.size(); i++){
				QryIop qi_curr = ((QryIop)this.args.get(i));
				List<Integer> curr = this.getLocationList(qi_curr);
				List<Integer> newCurr = new ArrayList<Integer>();
				int k1 = 0;
				int k2 = 0;
				while(k1<last.size()&&k2<curr.size()){
					if(curr.get(k2)<last.get(k1)){
						k2 ++;
						continue;
					}
					else if (curr.get(k2)-last.get(k1)<=this.distance){
						newCurr.add(curr.get(k2));
						k1 ++ ;
						k2 ++ ;
					}
					else{
						k1 ++ ;
					}
				}
				last = newCurr;
			
			}
			
			List<Integer> res = new ArrayList<Integer>(last);
			
			
			// Store the result to invertedList of this NEAR/n operator.
			if(res.size()>0){
				this.invertedList.appendPosting(id,res );
			}
			
			scorelist.add(id, last.size());
			
			// Loop to next doc.
			this.args.get(0).docIteratorAdvancePast(id);
			
			
			
		}
		// Just for debugging: 
		// scorelist.sortByExternal();
		// scorelist.print();
		// System.out.println("In near\n, field: " + this.getField());	
		
	}
	
	/**
	 * 
	 * @param q A QryIop instance provides the list of postings.
	 * @return A List instance for easy looping.
	 */
	
	private List<Integer> getLocationList(QryIop q){
		List<Integer> list = new ArrayList<Integer>();
		list = q.docIteratorGetMatchPosting().positions;
		return list;
	}
	
	
	
}
