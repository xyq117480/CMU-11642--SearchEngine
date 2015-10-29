/**
 *  An object that stores parameters for the Indri
 *  retrieval model (there are none) and indicates to the query
 *  operators how the query should be evaluated.
 */
public class RetrievalModelIndri extends RetrievalModel{

	
	public static final double mu = 2500;
	public static final double lambda = 0.4;
	
	@Override
	public String defaultQrySopName() {
		return new String("#and");
	}

}
