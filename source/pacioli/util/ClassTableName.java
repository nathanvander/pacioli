package pacioli.util;

//maybe this should be called DbUtils

public class ClassTableName {

	//given the class of an object, with the dots, figure out what the tableName would be
	public static String flattenedTableName(String className) {
		if (className==null) throw new NullPointerException();
		String fixed=className.replace('.','_');
		return fixed;
	}

	//given the table name, with underscores, return what the classname is
	public static String unflatten(String s) {
		String unflattened=s.replace('_','.');
		return unflattened;
	}
}