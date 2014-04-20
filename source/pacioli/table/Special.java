package pacioli.table;
import java.awt.Choice;

//These have a code, because we might be adding more specials and want to keep the data consistent
public enum Special {
		DEFAULT(0),
		CASH(10),			//cash and undeposited checks
		OPERATING_CHECKING(11),
		TRUST_CHECKING(12),
		RECEIVABLES(15),		//accounts receivable
		BILLABLE_TIME(18),	//asset
		BILLABLE_COST(19),
		TRUST_LIABILITY(20),
		PAYABLES(21),
		SALES(40),
		POTENTIAL_REVENUE(41),	//for time that hasn't been billed yet
		LOST_REVENUE(42),		//for written off time
		UNBILLABLE_TIME(50);

		private final int code;
		private Special(int c) {code=c;}

		public int code() {return code;}

		//I don't think this will ever be used
		public static Special lookupByOrdinal(int i) {
			return Special.values()[i];
		}
		//The string must match exactly the name
		public static Special lookupByString(String s) {
			return Special.valueOf(Special.class,s);
		}

		public static Special lookup(int code) {
			switch (code) {
				case 0: return DEFAULT;
				case 10: return CASH;			//cash and undeposited checks
				case 11: return	OPERATING_CHECKING;
				case 12: return TRUST_CHECKING;
				case 15: return RECEIVABLES;		//accounts receivable
				case 18: return BILLABLE_TIME;	//asset
				case 19: return BILLABLE_COST;
				case 20: return TRUST_LIABILITY;
				case 21: return PAYABLES;
				case 40: return SALES;
				case 41: return POTENTIAL_REVENUE;	//for time that hasn't been billed yet
				case 42: return LOST_REVENUE;		//for written off time
				case 50: return UNBILLABLE_TIME;
				default: return DEFAULT;
			}
		}

		//convenience method
		public static String lookupName(int code) {
			Special sp=lookup(code);
			return sp.name();
		}

		public static Choice getSpecials() {
			Choice list = new Choice();
			for (Special v: Special.values()) {
				list.add(v.name());
			}
			return list;
		}

}
