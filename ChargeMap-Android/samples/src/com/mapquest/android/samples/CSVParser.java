package com.mapquest.android.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.SparseArray;

public class CSVParser {
	InputStream inputStream;
	HashMap<String, HashMap<String, ArrayList<String[]>>> stateToData;
	SparseArray<ArrayList<String[]>> resultMap;

	public CSVParser(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public SparseArray<ArrayList<String[]>> read() {
		resultMap = new SparseArray<ArrayList<String[]>>();
		stateToData = new HashMap<String, HashMap<String, ArrayList<String[]>>>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		try {
			String csvLine;
			ArrayList<String[]> temp;
			HashMap<String, ArrayList<String[]>> tempHashMap;
			while ((csvLine = reader.readLine()) != null) {
				String[] row = csvLine.split(",");
				if (resultMap.get(Integer.parseInt(row[0])) == null) {
					temp = new ArrayList<String[]>();
					temp.add(row);
					resultMap.put(Integer.parseInt(row[0]), temp);
				} else {
					resultMap.get(Integer.parseInt(row[0])).add(row);
				}
				if (stateToData.containsKey(row[6])){
					if (stateToData.get(row[6]).containsKey(row[5])){
						stateToData.get(row[6]).get(row[5]).add(row);
					}else{
						temp = new ArrayList<String[]>();
						temp.add(row);
						stateToData.get(row[6]).put(row[5], temp);
					}
				}else{
					temp = new ArrayList<String[]>();
					temp.add(row);
					tempHashMap = new HashMap<String, ArrayList<String[]>>();
					tempHashMap.put(row[5], temp);
					stateToData.put(row[6], tempHashMap);
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException("Error in reading CSV file: " + ex);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new RuntimeException("Error while closing input stream: "
						+ e);
			}
		}
		return resultMap;
	}

	public SparseArray<ArrayList<String[]>> getResultMap() {
		return resultMap;
	}

	public HashMap<String, HashMap<String, ArrayList<String[]>>> getStateToData() {
		return stateToData;
	}
}
