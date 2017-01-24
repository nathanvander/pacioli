package nathanvander.pacioli.security;
import nathanvander.pacioli.iface.*;
import java.math.BigInteger;

/**
* This sits on the client side.  Encrypt a password using the public key
*/
public class ClientSecurity {

	/**
	* This is a relatively simple algorithm.
	* Z=(M^E) mod N
	*
	*/
	public static SecurityToken encrypt(PublicKey pk,String username,int password) {
		if (password<1000) {
			throw new IllegalArgumentException("Password must be at least 1000");
		}
		BigInteger bigM = BigInteger.valueOf(password);
		BigInteger bigN = new BigInteger(pk.key,16);
		//the 65537 is a constant
		BigInteger bigE = BigInteger.valueOf(65537L);

		BigInteger encrypted = bigM.modPow(bigE,bigN);

		return new SecurityToken(username,encrypted.toString(16));
	}
}