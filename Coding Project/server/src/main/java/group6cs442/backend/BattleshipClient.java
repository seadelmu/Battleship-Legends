package group6cs442.backend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class BattleshipClient extends Thread{
	
	private Socket socketClient;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	
	
	public BattleshipClient(String address, int port)
    {
        // establish a connection
        try {
            socketClient = new Socket(address, port);
            System.out.println("Connected");
 
            // takes input from terminal
            in = new ObjectInputStream(socketClient.getInputStream());
 
            // sends output to the socket
            
            out = new ObjectOutputStream(
                socketClient.getOutputStream());
        }
        catch (UnknownHostException u) {
            System.out.println(u);
            return;
        }
        catch (IOException i) {
            System.out.println(i);
            return;
        }
    }

}
