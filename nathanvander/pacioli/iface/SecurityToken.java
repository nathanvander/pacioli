package nathanvander.pacioli.iface;

/**
* A SecurityToken is the username and encrypted password.  I expect this
* will be used all over the place
*
*/
public class SecurityToken implements java.io.Serializable {
	public String username;

	/**
	* password is a BigInteger in base-16.  So this could be a really big number.
	*/
	public String password;

	public SecurityToken(String uname, String token) {
		username=uname;
		password=token;
	}
}