import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * The NEAR/n operator for all retrieval models.
 */

public class QryIopWindow extends QryIop{
	
	//Window size
	private int N;
	
	public QryIopWindow(int n) {
		this.N = n;
	}
	
	/*
	 * An alternative method. Iterating through iterator.
	 * 
	public void evaluate1() throws IOException {
		//  Create an empty inverted list.  
		this.invertedList = new InvList (this.getField());
		
		if (this.args.size () == 0) {
		     return;
		}
		
		
		
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
			boolean allEnd = false;
			while(true){
				int _max = -1;
				int _min = Integer.MAX_VALUE;
				int _min_flag = -1;
				List<Integer> res = new ArrayList<Integer>();
				for (int i = 0; i < this.args.size(); i++){
					QryIop cur = ((QryIop)this.args.get(i));
					if(cur.locIteratorHasMatch()){
						System.out.println("doc: "+ id + " term: "+ i + " loc: "+cur.locIteratorGetMatch());
						
						res.add(cur.locIteratorGetMatch());
						
						if(_max < cur.locIteratorGetMatch()){
							_max = cur.locIteratorGetMatch();
							
						}
						if(_min > cur.locIteratorGetMatch()){
							_min = cur.locIteratorGetMatch();
							_min_flag = i;
						}
						
					}
					else{ 
						allEnd = true;
						break;
					}
				}// end of for loop
				
				if(allEnd){
					break;
				}
				int _size = 1 + _max - _min;
				if (_size > this.N){
					try{
						int locid = ((QryIop)this.args.get(_min_flag)).locIteratorGetMatch();
						((QryIop)this.args.get(_min_flag)).locIteratorAdvancePast(locid);
						
					}
					catch(IndexOutOfBoundsException e){
						System.out.println("_min_flag == -1 or >3");
					}
				}
				else{
					this.invertedList.appendPosting(id, res);
					System.out.println("ever??");
					for(int i = 0; i < this.args.size(); i++){
						int locid = ((QryIop)this.args.get(i)).locIteratorGetMatch();
						((QryIop)this.args.get(i)).locIteratorAdvancePast(locid);
					}
				}
			
			
			}// end of inner while true
			
			
			
			this.args.get(0).docIteratorAdvancePast(id);
			
			
			
		}
		
	}
	*/
	
	
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
		
		
		
	    //  Until  inverted lists are depleted.
		while(true){
			
			// Find the common part of inverted lists of all terms.
			if(!this.docIteratorHasMatchAll(null)){
				break;
			}
			
			int id = this.args.get(0).docIteratorGetMatch();
			
			if(id ==  Qry.INVALID_DOCID){
				break;
			}
			
			List<Integer> res = new ArrayList<Integer>();
			int[] _indexes = new int[this.args.size()];
			boolean someListReachesEnd = false;
			
			while(true){
				int _max = Integer.MIN_VALUE;
				int _min = Integer.MAX_VALUE;
				int _min_flag = -1;		
				
				
				for (int i = 0; i < this.args.size(); i++){
					
					QryIop cur = ((QryIop)this.args.get(i));
					List<Integer> curr = this.getLocationList(cur);
					if( _indexes[i] >= curr.size()){
						someListReachesEnd = true;
						break;
					}
					
					if(_max < curr.get(_indexes[i])){
						_max = curr.get(_indexes[i]);
						
					}
					if(_min > curr.get(_indexes[i])){
						_min = curr.get(_indexes[i]);
						_min_flag = i;
					}
					
				}
				if (someListReachesEnd){
					break;
				}
				int _size = 1 + _max - _min;
				
				if(_size > this.N){
					_indexes[_min_flag] ++;
				}else{
					
		
					res.add(_max);
					
					for (int j = 0; j < this.args.size(); j++){
						_indexes[j] ++;
					}
				}
				
			}
			
			if(res.size()>0){
				this.invertedList.appendPosting(id,res );
			}
			
			
			// Loop to next doc.
			this.args.get(0).docIteratorAdvancePast(id);
			
			
			
		}
		
		
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
