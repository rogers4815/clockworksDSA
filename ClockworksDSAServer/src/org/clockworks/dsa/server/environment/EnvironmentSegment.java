package org.clockworks.dsa.server.environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EnvironmentSegment {

	private String[] parameters;
	private File pythonScript;
	private int parentId;
	private int id;
	private String results;

	/**
	 * Constructor
	 * 
	 * @param parentId
	 * @param id
	 * @param parameters
	 * @param pythonScript
	 */
	public EnvironmentSegment(int parentId, int id, String[] parameters,
			File pythonScript) {
		this.parentId = parentId;
		this.id = id;
		this.parameters = parameters;
		this.pythonScript = pythonScript;
	}

	/**
	 * Create a single file/object consisting of the python script together with
	 * the parameters relevant to this segment
	 * 
	 * @return
	 */
	public String getUnifiedFile() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(pythonScript));

			StringBuilder sb = new StringBuilder();

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line+'\n');
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Associate result object with this segment of the simulation
	 * 
	 * @param results
	 * @return
	 */
	public int insertResults(String results) {
		if (this.results != null) { // Where a duplicate has been received
			return 409;
		} else {
			this.results = results;
			return 200;
		}
	}

	public boolean isComplete() {
		return this.results != null;
	}

	public String getResults() {
		return this.results;
	}

	public int getId() {
		return this.id;
	}

	public int getParentId() {
		return this.parentId;
	}

	public String parametersToString() {
		String result = "";
		for (int i = 0; i < parameters.length; i++)
			result += parameters[i] + ";";
		return result;
	}

}
