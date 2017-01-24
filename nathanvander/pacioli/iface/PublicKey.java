package nathanvander.pacioli.iface;

/**
* Public Key is just a wrapper around the encryption key, but it marks it
* so this isn't confused with a regular string
*
*/
public class PublicKey implements java.io.Serializable {
	/**
	* key is a BigInteger in base-16.  So this could be a really big number.
	*/
	public String key;

	public PublicKey(String k) {
		key=k;
	}
}