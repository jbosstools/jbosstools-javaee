/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.internal.reveng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Vitali
 * 
 * This is specific efficient storage for names of catalogs, schemas, tables, columns and
 * it's relations. 
 */
public class TablesColumnsCollector {

	// filter for lower part of long
	public static final long LOWER_MASK = 0x00000000FFFFFFFFL;
	// filter for upper part of long
	public static final long UPPER_MASK = 0xFFFFFFFF00000000L;
	
	// if true  - collect full exact hierarchical structure - full structure info with redundancy  
	// ---------- (useful to get the tree structure easily)
	// ---------- all relations one-to-many
	// if false - result structure has no duplicate strings - structure without redundancy 
	// ---------- (useful for context help)
	// ---------- all relations many-to-many
	protected boolean fullStructure = false;
	// sorted in alphabetic order list of catalogs
	protected List catalogs;
	// sorted in alphabetic order list of schemas
	protected List schemas;
	// sorted in alphabetic order list of tables
	protected List tables;
	// sorted in alphabetic order list of columns
	protected List columns;

	/**
	 * Index relation array-maps.
	 * Maps which are used to save relations between items in both directions.
	 * Here is a short description:
	 * left <-> right;
	 * left - could be catalogs, schemas, tables;
	 * right - could be schemas, tables, columns;
	 * xMap[i] & UPPER_MASK -> this is index in left array;
	 * xMap[i] & LOWER_MASK -> this is index in right array
	**/
	// catalogs <-> schemas index map
	protected long[] csMap;
	// schemas <-> tables index map
	protected long[] stMap;
	// tables <-> columns index map
	protected long[] tcMap;

	// temporary structures
	protected List tempCATList;
	protected List tempCSTList;
	protected List tempTblList;
	protected List tempClnList;
	// simple protection of duplicates
	protected String strCAT;
	protected String strCST;
	protected String strTbl;
	protected String strCln;
	
	public TablesColumnsCollector() {
	}
	
	/**
	 * @param fullStructure
	 * if true  - collect full exact hierarchical structure - (useful to get the tree structure easily)  
	 * if false - result structure has no duplicate strings - (useful for context help) 
	 */
	public TablesColumnsCollector(boolean fullStructure) {
		this.fullStructure = fullStructure;
	}

	/**
	 * initialize collection  
	 */
	public void init() {
		catalogs = null;
		schemas = null;
		tables = null;
		columns = null;
		csMap = null;
		stMap = null;
		tcMap = null;
		tempCATList = new ArrayList();
		tempCSTList = new ArrayList();
		tempTblList = new ArrayList();
		tempClnList = new ArrayList();
		strCAT = "%%%%";
		strCST = "%%%%";
		strTbl = "%%%%";
		strCln = "%%%%";
	}

	public String updateNullValue(String str) {
		if (null == str) { 
			return "";
		}
		return str;
	}

	public void addCatalogName(String catalogName) {
		catalogName = updateNullValue(catalogName);
		String strCurr = "" + catalogName;
		if (!strCAT.equalsIgnoreCase(strCurr)) {
			tempCATList.add(strCurr);
			strCAT = strCurr;
		}
	}

	public void addSchemaName(String catalogName, String schemaName) {
		catalogName = updateNullValue(catalogName);
		schemaName = updateNullValue(schemaName);
		addCatalogName(catalogName);
		String strCurr = schemaName + "%" + catalogName;
		if (!strCST.equalsIgnoreCase(strCurr)) {
			tempCSTList.add(strCurr);
			strCST = strCurr;
		}
	}

	public void addTableName(String catalogName, String schemaName, String tableName) {
		catalogName = updateNullValue(catalogName);
		schemaName = updateNullValue(schemaName);
		tableName = updateNullValue(tableName);
		addSchemaName(catalogName, schemaName);
		String strCurr = fullStructure ?
				tableName + "%" + schemaName + "%" + catalogName :
				tableName + "%" + schemaName;
		if (!strTbl.equalsIgnoreCase(strCurr)) {
			tempTblList.add(strCurr);
			strTbl = strCurr;
		}
	}

	public void addColumnName(String catalogName, String schemaName, String tableName, String columnName) {
		catalogName = updateNullValue(catalogName);
		schemaName = updateNullValue(schemaName);
		tableName = updateNullValue(tableName);
		columnName = updateNullValue(columnName);
		addTableName(catalogName, schemaName, tableName);
		String strCurr = fullStructure ?
				columnName + "%" + tableName + "%" + schemaName + "%" + catalogName :
				columnName + "%" + tableName;
		if (!strCln.equalsIgnoreCase(strCurr)) {
			tempClnList.add(strCurr);
			strCln = strCurr;
		}
	}

	protected void copyWithoutDuplicates(List arrTmp, List formList) {
		String strPrev = null, strCurr = null;
		for (int i = 0; i < arrTmp.size(); i++) {
			strCurr = (String)arrTmp.get(i);
			if (strPrev == strCurr) {
				continue;
			}
			if (null != strPrev && strPrev.equalsIgnoreCase(strCurr)) {
				continue;
			}
			strPrev = strCurr;
			formList.add(strCurr);
		}
	}
	
	protected void copyIncludeDuplicates(List arrTmp, List formList) {
		for (int i = 0; i < arrTmp.size(); i++) {
			String strCurr = (String)arrTmp.get(i);
			formList.add(strCurr);
		}
	}

	/**
	 * @param tempList - relation description list (with duplicates)
	 * @param formList
	 * @return
	 * size of tempList without duplicates
	 *  in case of fullStructure: tempList.size() == formList.size() & tempList <-> formList by elements
	 */
	protected int adjustList(List tempList, List formList) {
		ArrayList arrTmp = new ArrayList();
		Collections.sort(tempList, String.CASE_INSENSITIVE_ORDER);
		String strPrev = null, strCurr = null;
		int i, j;
		// remove duplicates
		for (i = 0, j = 0; i < tempList.size(); i++) {
			strCurr = (String)tempList.get(i);
			if (strPrev == strCurr) {
				continue;
			}
			if (null != strPrev && strPrev.equalsIgnoreCase(strCurr)) {
				continue;
			}
			strPrev = strCurr;
			String[] atc = strCurr.split("%", 2);
			tempList.set(j++, strCurr);
			arrTmp.add(fullStructure ? strCurr : atc[0]);
		}
		if (fullStructure) {
			copyIncludeDuplicates(arrTmp, formList);
		}
		else {
			Collections.sort(arrTmp, String.CASE_INSENSITIVE_ORDER);
			copyWithoutDuplicates(arrTmp, formList);
		}
		arrTmp = null;
		return j;
	}

	protected void adjustMap(long[] xMap, List tempList, List formList0, List formList1) {
		// in case of fullStructure: 
		// tempList.size() == formList0.size() & 
		// tempList <-> formList0 by elements
		int i, j = xMap.length;
		for (i = 0; i < j; i++) {
			String strCurr = (String)tempList.get(i);
			String[] atc = strCurr.split("%", 2);
			String tmp = atc[1];
			int keyT = Collections.binarySearch(formList1, tmp, String.CASE_INSENSITIVE_ORDER);
			if (keyT < 0 || keyT >= formList1.size() || !tmp.equalsIgnoreCase((String)formList1.get(keyT))) {
				keyT = Integer.MAX_VALUE;
			}
			tmp = atc[0];
			int keyC = fullStructure ?
					i : Collections.binarySearch(formList0, tmp, String.CASE_INSENSITIVE_ORDER);
			tmp = fullStructure ? strCurr : tmp;
			if (keyC < 0 || keyC >= formList0.size() || !tmp.equalsIgnoreCase((String)formList0.get(keyC))) {
				keyC = Integer.MAX_VALUE;
			}
			xMap[i] = ( ((long)keyT) << 32 ) | ((long)keyC);
		}
		Arrays.sort(xMap);
	}
	
	/**
	 * adjust collected results  
	 */
	public void adjust() {
		strCAT = null;
		strCST = null;
		strTbl = null;
		strCln = null;
		int size;
		catalogs = new ArrayList();
		if (null != tempCATList) {
			Collections.sort(tempCATList, String.CASE_INSENSITIVE_ORDER);
			copyWithoutDuplicates(tempCATList, catalogs);
			tempCATList = null;
		}
		schemas = new ArrayList();
		csMap = new long[0];
		if (null != tempCSTList) {
			size = adjustList(tempCSTList, schemas);
			csMap = new long[size];
			adjustMap(csMap, tempCSTList, schemas, catalogs);
			tempCSTList = null;
		}
		tables = new ArrayList();
		stMap = new long[0];
		if (null != tempTblList) {
			size = adjustList(tempTblList, tables);
			stMap = new long[size];
			adjustMap(stMap, tempTblList, tables, schemas);
			tempTblList = null;
		}
		columns = new ArrayList();
		tcMap = new long[0];
		if (null != tempClnList) {
			size = adjustList(tempClnList, columns);
			tcMap = new long[size];
			adjustMap(tcMap, tempClnList, columns, tables);
			tempClnList = null;
		}
	}
	
	public static final class Bounds {
		public int nL = 0;
		public int nH = 0;
	}

	/**
	 * binaryBoundsSearch is a method to find lower and upper bounds 
	 * in sorted array of strings for some string prefix.
	 * @param list - search array of strings (should be sorted in ascending case insensitive order);
	 * @param prefix - matching prefix;
	 * @param bounds - result interval in the list - all items in interval start from prefix.
	 * bounds.nL <= interval < bounds.nH
	 */
	public static final Bounds binaryBoundsSearch(List list, String prefix) {
		Bounds bounds = new Bounds();
		if (list.isEmpty()) {
			bounds.nL = bounds.nH = 0;
			return bounds;
		}
		prefix = prefix.toUpperCase();
		int low = 0;
		int high = list.size() - 1;
		int mid = (low + high) >> 1;
		// looking for mid - some element in the interval,
		// which is matched for criteria (has the prefix)
		while (low <= high) {
			mid = (low + high) >> 1;
			String midVal = ((String)list.get(mid)).toUpperCase();
			int cmp = midVal.compareToIgnoreCase(prefix);
			cmp = midVal.startsWith(prefix) ? 0 : cmp;
			if (cmp < 0) {
				low = mid + 1;
			}
			else if (cmp > 0) {
				high = mid - 1;
			}
			else {
				break;
			}
		}
		int low2 = mid;
		int high2 = mid;
		// looking for low bound - indicates minimal index of element in the interval,
		// which is matched for criteria (has the prefix)
		while (low <= high2) {
			mid = (low + high2) >> 1;
			String midVal = ((String)list.get(mid)).toUpperCase();
			int cmp = midVal.compareToIgnoreCase(prefix);
			cmp = midVal.startsWith(prefix) ? 0 : cmp;
			if (cmp < 0) {
				low = mid + 1;
			}
			else if (cmp >= 0) {
				high2 = mid - 1;
			}
		}
		// looking for high bound - indicates maximal index of element in the interval,
		// which is matched for criteria (has the prefix)
		while (low2 <= high) {
			mid = (low2 + high) >> 1;
			String midVal = ((String)list.get(mid)).toUpperCase();
			int cmp = midVal.compareToIgnoreCase(prefix);
			cmp = midVal.startsWith(prefix) ? 0 : cmp;
			if (cmp <= 0) {
				low2 = mid + 1;
			}
			else if (cmp > 0) {
				high = mid - 1;
			}
		}
		// adjust low bound 
		if (low < list.size()) {
			while (0 <= low && ((String)list.get(low)).toUpperCase().startsWith(prefix)) {
				low--;
			}
			low++;
		}
		if (high < low) {
			high = low;
		}
		// adjust high bound 
		while (high < list.size() && ((String)list.get(high)).toUpperCase().startsWith(prefix)) {
			high++;
		}
		bounds.nL = low;
		bounds.nH = high;
		return bounds;
	}


	/**
	 * Clear string from redundant information. 
	 * @param res
	 */
	protected String correctName(String input) {
		if (!fullStructure) {
			return input;
		}
		String[] atc = input.split("%", 2);
		return atc[0];
	}

	/**
	 * Clear list of string from redundant information. 
	 * @param res
	 */
	protected void correctNames(List res) {
		if (!fullStructure) {
			return;
		}
		for (int i = 0; i < res.size(); i++) {
			String strCurr = (String)res.get(i);
			String[] atc = strCurr.split("%", 2);
			res.set(i, atc[0]);
		}
	}

	protected List getXNames(List xArray, String prefix) {
		List res = new ArrayList();
		if (null == xArray) {
			return res;
		}
		if (null != prefix) {
			prefix = prefix.toUpperCase();
		}
		if (null == prefix) {
			res.addAll(xArray);
		}
		else {
			Bounds bounds = binaryBoundsSearch(xArray, prefix);
			for (; bounds.nL < bounds.nH; bounds.nL++) {
				res.add(xArray.get(bounds.nL));
			}
		}
		return res;
	}

	public List getMatchingCatalogNames(String prefix) {
		List res = getXNames(catalogs, prefix);
		correctNames(res);
		return res;
	}

	public List getMatchingSchemaNames(String prefix) {
		List res = getXNames(schemas, prefix);
		correctNames(res);
		return res;
	}

	public List getMatchingTableNames(String prefix) {
		List res = getXNames(tables, prefix);
		correctNames(res);
		return res;
	}

	public List getMatchingColumnNames(String prefix) {
		List res = getXNames(columns, prefix);
		correctNames(res);
		return res;
	}

	/**
	 * return list of matching strings.
	 * Here is a short description what is happen here.
	 * the same schema with left <-> right which was above:
	 * xArray1 - right;
	 * xArray2 - left;
	 * xxMap - <-> index relation array-map;
	 * name - exactly string in xArray2;
	 * prefix - prefix string in xArray1.
	 * Matching criteria:
	 * list of strings from xArray1 which start from prefix and which is in relation with
	 * item from xArray2 which is exactly match the name.
	**/
	protected List getXNames(List xArray1, long[] xxMap, List xArray2, String name, String prefix) {
		List res = new ArrayList();
		if (null == xArray1) {
			return res;
		}
		if (null != name) {
			name = name.toUpperCase();
		}
		if (null != prefix) {
			prefix = prefix.toUpperCase();
		}
		if (null == name && null == prefix) {
			res.addAll(xArray1);
		}
		else if (null == name) {
			Bounds bounds = binaryBoundsSearch(xArray1, prefix);
			for (; bounds.nL < bounds.nH; bounds.nL++) {
				res.add(xArray1.get(bounds.nL));
			}
		}
		else {
			if (null == xArray2 || null == xxMap) {
				return res;
			}
			Bounds bounds = binaryBoundsSearch(xArray2, name);
			for (; bounds.nL < bounds.nH; bounds.nL++) {
				int keyT = bounds.nL;
				String itemT = (String)xArray2.get(keyT);
				if (fullStructure) {
					if (!itemT.toUpperCase().startsWith(name)) {
						continue;
					}
					name = correctName(name);
					itemT = correctName(itemT);
				}
				if (!name.equalsIgnoreCase(itemT)) {
					continue;
				}
				long keyTlong = ((long)keyT) << 32; 
				int i = Arrays.binarySearch(xxMap, keyTlong);
				if (i < 0) {
					i = - i - 1;
				}
				if (null == prefix) {
					for ( ; i < xxMap.length; i++) {
						if (keyTlong != (xxMap[i] & UPPER_MASK)) {
							break;
						}
						int keyC = (int)(xxMap[i] & LOWER_MASK);
						if (0 <= keyC && keyC < xArray1.size()) {
							res.add(xArray1.get(keyC));
						}
					}
				}
				else {
					for ( ; i < xxMap.length; i++) {
						if (keyTlong != (xxMap[i] & UPPER_MASK)) {
							break;
						}
						int keyC = (int)(xxMap[i] & LOWER_MASK);
						if (0 <= keyC && keyC < xArray1.size() && ((String)xArray1.get(keyC)).toUpperCase().startsWith(prefix)) {
							res.add(xArray1.get(keyC));
						}
					}
				}
			}
		}
		return res;
	}

	public List getCatalogNames() {
		List res = new ArrayList();
		if (null != catalogs) {
			res.addAll(catalogs);
		}
		return res;
	}

	public List getMatchingSchemaNames(String catalogName, String prefix) {
		List res = getXNames(schemas, csMap, catalogs, catalogName, prefix);
		correctNames(res);
		return res;
	}

	public List getMatchingTablesNames(String schemaName, String prefix) {
		List res = getXNames(tables, stMap, schemas, schemaName, prefix);
		correctNames(res);
		return res;
	}

	public List getMatchingColumnNames(String tableName, String prefix) {
		List res = getXNames(columns, tcMap, tables, tableName, prefix);
		correctNames(res);
		return res;
	}

	public List getMatchingTablesNames(String catalogName, String schemaName, String prefix) {
		catalogName = updateNullValue(catalogName);
		schemaName = updateNullValue(schemaName);
		schemaName = "" + schemaName + "%" + catalogName;
		List res = getXNames(tables, stMap, schemas, schemaName, prefix);
		correctNames(res);
		return res;
	}

	public List getMatchingColumnNames(String schemaName, String tableName, String prefix) {
		schemaName = updateNullValue(schemaName);
		tableName = updateNullValue(tableName);
		tableName = "" + tableName + "%" + schemaName;
		List res = getXNames(columns, tcMap, tables, tableName, prefix);
		correctNames(res);
		return res;
	}

	public List getMatchingColumnNames(String catalogName, String schemaName, String tableName, String prefix) {
		catalogName = updateNullValue(catalogName);
		schemaName = updateNullValue(schemaName);
		tableName = updateNullValue(tableName);
		tableName = "" + tableName + "%" + schemaName + "%" + catalogName;
		List res = getXNames(columns, tcMap, tables, tableName, prefix);
		correctNames(res);
		return res;
	}
}
