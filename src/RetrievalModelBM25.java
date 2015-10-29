/**
 *  An object that stores parameters for the BM 25
 *  retrieval model (there are none) and indicates to the query
 *  operators how the query should be evaluated.
 */
public class RetrievalModelBM25 extends RetrievalModel {

	
	public static final double k1 = 1.2;
	public static final double b = 0.75;
	public static final double k3 = 0;
	
	public String defaultQrySopName() {
		
		return new String("#sum");
	}
	
}
