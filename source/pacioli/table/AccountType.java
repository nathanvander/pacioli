package pacioli.table;
import java.awt.Choice;

/**
*  Account Type is stored in the type column in the Account table as an int from 0.7.
*	0 means nothing, and shouldn't be used. I don't like to use it as an actual value
*	1 means Asset
*/

public enum AccountType {
		none,		//this is listed so it will be zero
		Asset,		//1
		Liability,
		Equity,
		Revenue,
		Expense,
		OtherRevenue,
		OtherExpense;

		public static AccountType lookup(int i) {
			return values()[i];
		}

		//convenience method
		public static String lookupName(int i) {
			return lookup(i).name();
		}

		public static AccountType lookupByString(String s) {
			return valueOf(AccountType.class,s);
		}

		public static Choice getAccountTypes() {
			Choice list = new Choice();
			for (AccountType v: AccountType.values()) {
				list.add(v.name());
			}
			return list;
		}

		public static int lookupOrdinal(String s) {
			AccountType t=valueOf(AccountType.class,s);
			return t.ordinal();
	}
}