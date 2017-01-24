package nathanvander.pacioli.iface;
import java.rmi.*;
import apollo.iface.DataStoreException;

/**
* This is the interface for security.
*
* This is not meant to be secure in a cryptographic sense, but just to be
* better than plaintext.  It uses a simple form of public key encryption.
*
* The rules are: the password is never transmitted across the network in plaintext
* and it is never stored in the database in plaintext.
*
* Password as used here is a 4-6 digit number, like a pin.
*
* The administrator also uses this interface, which is maybe not the best design
* but makes it simpler.  You do have to use the administrator password to use
* the functions.
*/
public interface Security extends Remote {
		//these are the ones meant to be publicly used
		/**
		* Get the public key for encryption.
		*/
		public PublicKey getPublicKey() throws RemoteException, DataStoreException;

		/**
		* Validate the token.
		* Returns true if valid, false if invalid.
		* We don't explain the reason for the failure.  It could be invalid username.
		*/
		public boolean validate(SecurityToken token) throws RemoteException, DataStoreException;

		/**
		* Change to the new password.  Returns true if success
		* or false for failure.  There could be several reasons for failure
		* but I am keeping it simple.
		*/
		public boolean changePassword(SecurityToken oldToken,SecurityToken newToken) throws RemoteException, DataStoreException;

		//========================================
		//this is for use by a systems administrator

		/**
		* Add a new user and return the user id or 0 if failure.
		* We don't worry about the actual name of the user here.  That would be set in an admin interface.
		*/
		public int addUser(SecurityToken admin,SecurityToken newUser) throws RemoteException, DataStoreException;

		/**
		* Used if the user forgets his password.  Although the arguments
		* look similar to addUser, this cannot be used to add a new user.
		* If the user is locked out, this will unlock it
		*/
		public boolean changePasswordForUser(SecurityToken admin, SecurityToken newPassword) throws RemoteException, DataStoreException;

		/**
		* lockOut a user.  Should return true unless the user doesn't exist.
		*/
		public boolean lockOutUser(SecurityToken admin,String username) throws RemoteException, DataStoreException;

		/**
		* Return a list of all users in the system.
		*/
		public String[] listUsers(SecurityToken admin) throws RemoteException, DataStoreException;

}