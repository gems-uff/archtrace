package edu.uci.ics.archtrace.hooks.cm;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * Notifies ArchTrace about changes in CM repositories
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 17, 2004
 */
public class ArchTraceNotifier {

	/**
	 * Notifies ArchTrace
	 * @param args Host, Port, Repository, and Updated configuration
	 */
	public static void main(String[] args) {
		try {
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			String repository = args[2];
			String configuration = args[3];
			
			Socket client = new Socket(host, port);
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			out.println(repository);
			out.println(configuration);

			out.close();
			client.close();
		} catch (Exception e) {
			System.out.println("Syntax: java -cp ArchTrace.jar edu.uci.ics.archtrace.hooks.cm.ArchTraceNotifier <HOST> <PORT> <REPOSITORY> <CONFIGURATION>");
			e.printStackTrace();
		}
	}
}