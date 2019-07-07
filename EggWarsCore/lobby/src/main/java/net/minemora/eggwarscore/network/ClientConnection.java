package net.minemora.eggwarscore.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.utils.CmdColor;

public abstract class ClientConnection extends Thread {
	
	private Socket socket;
	private boolean shutdown = false;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	public ClientConnection(Socket clientSocket) {
		this.socket = clientSocket;
	}
	
	public void run() {

		try {
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		String inputLine = null;
		
		while (!shutdown) {
			
			try {
				inputLine = in.readLine();
			} catch (IOException e) {
				EggWarsCoreLobby.getPlugin().getLogger().severe(CmdColor.RED + "Trying to use a socket closed? or just stoping the server (02)" 
						+ CmdColor.RESET);
				break;
			}
			
			processInput(inputLine);
		}
	}
	
	public abstract void processInput(String inputLine);
	
	public abstract void onClose();
	
	public void shutdown() {
		this.shutdown = true;
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		onClose();
	}

	public PrintWriter getWriter() {
		return out;
	}

	public Socket getSocket() {
		return socket;
	}
}