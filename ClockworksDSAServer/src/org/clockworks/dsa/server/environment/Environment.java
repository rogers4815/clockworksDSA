package org.clockworks.dsa.server.environment;


import java.io.*;
import java.util.*;

import org.clockworks.dsa.server.singletons.PingQueue;

public class Environment {
	
	private int id;
	private EnvironmentSegment[] segments;
	private File pythonScript;
	
	/**
	 * Constructor that populates ALL member variables including segments.
	 * @param parameters
	 * @param pythonScript Must be passed after applying .setReadOnly() on the file object
	 */
	public Environment(int id,InputStream inputFromHttp){
		this.id = id;
		// NEED TO SPLIT inputFromHttp IN TO PYTHON SCRIPT AND PARAMETERS STRING!!!!
		this.setPythonScript(pythonScript);
		//create 2d array of strings of parameters 
		String byIdent[] = parameters.split(";;"); // Based on grammar used for
													// string
		String byArg[][] = new String[byIdent.length][];
		for (int i = 0; i < byIdent.length; i++) {
			String var[] = byIdent[i].split(";"); // Based on grammar used for
													// string
			byArg[i] = new String[var.length];
			for (int j = 0; j < byArg[i].length; j++) {
				byArg[i][j] = var[j];
			}
		}
		//create all permutations of parameters
		String[][] permuted = permute(byArg);
		segments = new EnvironmentSegment[permuted.length];
		File[] completedScripts = new File[permuted.length];

		// Convert original file in to array so new lines can be inserted.
		List<String> lines = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(
				new FileReader(pythonScript));) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				lines.add(sCurrentLine);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Find end of imports section of file.
		int insertAfterImports = 0;
		while (lines.get(insertAfterImports).contains("import"))
			insertAfterImports++;

		// Create new competed python scripts
		for (int i = 0; i < completedScripts.length; i++) {
			// create file name
			String[] tmpFileName = pythonScript.getName().split(".");
			String fileName = tmpFileName[0] + i + "." + tmpFileName[1];
			completedScripts[i] = new File(fileName);
			// fill in file data
			try (PrintStream out = new PrintStream(new FileOutputStream(
					completedScripts[i]))) {
				// imports section
				for (int j = 0; j < insertAfterImports; j++) {
					out.println(lines.get(j));
				}
				// new parameters
				for (int j = 0; j < permuted[i].length; j++) {
					out.println(permuted[i][j]);
				}
				// Remainder of original script
				for (int j = insertAfterImports; j < lines.size(); j++) {
					out.println(lines.get(j));
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Create all enviroment segments
		for (int i = 0; i < segments.length; i++) {
			segments[i] = new EnvironmentSegment(id, i, permuted[i],
					completedScripts[i]);
		}
	}
	
	/**
	 * Return the number of segments this simulation has been split up into
	 * @return
	 */
	public int getSegmentCount(){
		return segments.length;
	}
	
	/**
	 * Return a sub-array of the segments within a certain range
	 * @param first
	 * @param last
	 * @return
	 */
	public EnvironmentSegment[] getSegments(int first, int last){
		return Arrays.copyOfRange(segments, first, last);
	}
	
	/**
	 * Attempt to associate a particular segment of the simulation with its results
	 * @param results
	 * @param id
	 * @return
	 */
	public int completeSegmentWithResults(String results, int id){
		for(int i= 0; i<segments.length; i++){
			if(segments[i].getId()==id){
				return segments[i].insertResults(results);
			}
		}
		return 400; // Out of range, some sort of error
	}
	
	public int getId(){
		return this.id;
	}
	
	public File getPythonScript() {
		return pythonScript;
	}

	private void setPythonScript(File pythonScript) {
		this.pythonScript = pythonScript;
	}
	
	/**
	 * Return true if all the segments of this simulation have results associated
	 * @return
	 */
	private boolean isComplete(){
		for(EnvironmentSegment segment : segments){
			if(!segment.isComplete()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Return a single object consisting of the the results from all the simulation
	 * segments combined
	 * @return
	 */
	public String returnAssembledResult(){
		if(!isComplete()){
			return null;
		}
		//TODO: everything
		return null;
	}
	
	/**
	 * Given a 2D string array, creates all perumtaions of the strings and
	 * returns it as a new 2d String array.
	 * 
	 * @param toPermute
	 * @return
	 */
	private static String[][] permute(String[][] toPermute) {
		String[][] result = null;
		String[][] tmp;
		if (toPermute.length > 2) {
			tmp = permute(Arrays.copyOfRange(toPermute, 1, toPermute.length));
		} else {
			result = new String[toPermute[1].length * toPermute[0].length][2];
			int k = 0;
			for (int i = 0; i < toPermute[0].length; i++) {
				for (int j = 0; j < toPermute[1].length; j++) {
					result[k][0] = toPermute[0][i];
					result[k][1] = toPermute[1][j];
					k++;
				}
			}
			return result;
		}
		result = new String[toPermute[0].length * tmp.length][tmp[0].length + 1];
		int k = 0;
		for (int i = 0; i < toPermute[0].length; i++) {
			for (int j = 0; j < tmp.length; j++) {
				result[k][0] = toPermute[0][i];
				System.arraycopy(tmp[j], 0, result[k], 1, tmp[0].length);
				k++;
			}
		}
		return result;
	}
}
