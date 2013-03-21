package src;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UserSubmission {
	private String server;
	private int port, environmentID;
	private String resultsFilePath;

	public UserSubmission(String server, int port) {
		this.server = server;
		this.port = port;
		this.resultsFilePath = null;
	}

	public String runSimulation(String simulationFilePath) {
		int responseCode = sendSimulation(simulationFilePath);

		if (responseCode == 400) {
			System.err.println("Could not send simulation to server");
			return null;
		}
		resultsFilePath = null;

		int WOPResponse = 102;
		while (WOPResponse == 102) {
			WOPResponse = sendWOPPing();
		}

		if (WOPResponse == 401) {
			System.err.println("Server response 401: Authentication failure");
		} else if (WOPResponse == 404) {
			System.err.println("Server response 404: Environment not found");
		} else if (WOPResponse == 0) {
			System.err
					.println("Something went wrong. The server may not be contactable");
		}

		return resultsFilePath; // return filepath of results file
	}

	private int sendWOPPing() {
		try {
			URL serverURL = new URL("http://" + server + ":" + port
					+ "/resultassemblyhandler"); // throws MalformedURLException
			HttpURLConnection serverConnection = (HttpURLConnection) serverURL
					.openConnection();

			serverConnection.setRequestMethod("GET");
			serverConnection.setReadTimeout(10000);
			serverConnection.addRequestProperty("Environment-Id",
					Integer.toString(environmentID));

			serverConnection.connect();

			int response = serverConnection.getResponseCode();

			// If response is 200 then retrieve the results
			if (response == 200) {
				DataInputStream input = new DataInputStream(
						serverConnection.getInputStream());
				byte[] in = null, result = null;

				input.read(in);
				while (in != null) {
					byte[] copy = result;
					// Add in to the end of result
					result = new byte[copy.length + in.length];
					System.arraycopy(copy, 0, result, 0, copy.length);
					System.arraycopy(in, 0, result, copy.length, in.length);
				}
				BufferedOutputStream file = new BufferedOutputStream(
						new FileOutputStream("results.dsa"));
				file.write(result);
				file.flush();
				file.close();
			}
			return response;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private int sendSimulation(String filepath) {
		try {
			URL serverURL = new URL("http://" + server + ":" + port
					+ "/environmenthandler"); // throws MalformedURLException
			HttpURLConnection sendConnection = (HttpURLConnection) serverURL
					.openConnection(); // throws IOException

			sendConnection.setRequestMethod("POST");

			sendConnection.setDoOutput(true);

			DataOutputStream output = new DataOutputStream(
					sendConnection.getOutputStream());

			File source = new File(filepath);

			BufferedReader br = new BufferedReader(new FileReader(source));
			String line = br.readLine();
			// Send simulation to server
			while (line != null) {
				output.writeBytes(line);
				line = br.readLine();
			}
			output.flush();

			output.close();

			if (sendConnection.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(
						(InputStream) sendConnection.getInputStream()));

				StringBuilder sb = new StringBuilder();

				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();

				line = sb.toString();
				System.err.print(line);
				environmentID = Integer.parseInt(line);

			}

			return sendConnection.getResponseCode();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public static void main(String[] args) {

		UserSubmission user = new UserSubmission("127.0.0.1", 50080);
		user.runSimulation("simulation.py");
	}

}