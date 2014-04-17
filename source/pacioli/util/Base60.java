package pacioli.util;
import pacioli.db.DSX;
import pacioli.db.ErrorCode;

/** This a 2 digit encoding, with a letter from a..e and a letter.  Leading zeros are omitted
*   a0 = 10
*   b0 = 20
*   e9 = 59
*/
public class Base60 {
	//convert an int to a base 60 string
	public static String base60String(int i) throws DSX {
		StringBuilder s60=new StringBuilder();

		while (i>59) {
			int r=i % 60;
			s60.insert(0,toC60(r,false));
			i=i-r;
			i=i/60;
		}
		//do remainder
		s60.insert(0,toC60(i,true));
		return s60.toString().trim();
	}

	//given a number from 0..59, return it in base60 format
	//returns a 2 digit char array.  If the number is 9 or less, the first digit will be
	//either 0 or blank.
	private static char[] toC60(int i,boolean first) throws DSX {
		if (i<0 || i>59) {throw new DSX(ErrorCode.PACIOLI_UTIL_BASE60,i+" is out of range 0..59");}
		char[] ca=new char[2];
		int r = i % 10;
		int d = (i - r)/10;
		ca[0]=alpha(d,first);
		ca[1]=(char)(r+48);
		return ca;
	}


	//input must be 0..5
	public static char alpha(int i,boolean first) throws DSX {
		switch(i) {
			case 0: if (first) {
						return (char)0;
					} else {
						return '0';
					}
			case 1: return 'a';
			case 2: return 'b';
			case 3: return 'c';
			case 4: return 'd';
			case 5: return 'e';
			default: throw new DSX(ErrorCode.PACIOLI_UTIL_BASE60,i+" is out of range 0..5");
		}
	}

	//----------------------------------
	/**
	* Input must be an 8-character array, which can handle numbers up to 12,959,999 (e9e9e9e9).
	* This increments it to the next base60 number.  Character array are shared objects so this
	* increments it in place.
	*/
	public static void increment(char[] ca) throws DSX {
		if (ca==null || ca.length!=8) {
			throw new DSX(ErrorCode.PACIOLI_UTIL_BASE60,"input must be a character array of length 8");
		}
		increment(ca,7);  //add 1 to the digit 7
	}

	//char 48='0'
	//char 49='1'
	//char 57='9'
	//char 58=':'  INVALID
	//char 96='`'  INVALID
	//char 97='a'
	//char 101='e'
	//char 102='f' INVALID
	private static void increment(char[] ca,int j) throws DSX {
		int odd=j%2;  //odd=1 if odd, 0 if even
		char c=ca[j];
		if (c==0 && odd==1) {
			c=(char)48;
		} else if (c==0 && odd==0) {
			c=(char)96;
		} else if (c==48 && odd==0) {
			c=(char)96;
		}
		c=(char)(c+1);

		//case 1: handle overflow on tens
		if (c==102) {
			c=(char)48;
			//set char
			ca[j]=c;
			//rollover
			if (j>0) {
				increment(ca,j-1);
			}
		//case 2:
		} else if (c>96) {
			//do nothing except set
			ca[j]=c;
		//case 3:
		} else if (c==96) {
			//should never happen
			throw new DSX(ErrorCode.PACIOLI_UTIL_BASE60,"invalid state (c==96)");
		//case 4:
		} else if (c==58) {
			c=(char)48;
			//set char
			ca[j]=c;
			//rollover
			if (j>0) {
				increment(ca,j-1);
			}
		//case 5:
		} else if (c>48) {
			//do nothing except set
			ca[j]=c;
		//case 6:
		} else {
			//should never happen
			throw new DSX(ErrorCode.PACIOLI_UTIL_BASE60,"invalid state (c=="+c+")");
		}
	}

	//==================================
	public static void main(String[] args) throws DSX {
		int i=Integer.parseInt(args[0]);
		//System.out.println("input="+i);
		//String s=base60String(i);
		//System.out.println("base 60="+s);

		//int i=5;
		String s60=base60String(i);
		char[] c60=s60.toCharArray();
		char[] ca=new char[8];
		System.arraycopy(c60,0,ca,8-c60.length,c60.length);
		System.out.println(ca);
		for (int k=0;k<10;k++) {
			increment(ca);
			System.out.println(ca);
		}

	}

}