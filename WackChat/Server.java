// Server.java
import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

	private static Set<User> users = new HashSet<>();

	public static void main(String argv[]) throws Exception
	{
		ServerSocket welcomeSocket = new ServerSocket(5000);
		System.out.println("Server started...");
		while(true) {
			Socket userSocket = welcomeSocket.accept();//get a connection from client
			Thread userThread = new Thread(new User(userSocket));//create a thread
			userThread.start(); //start the thread
//			System.out.println("New user has joined the chat on thread: " + userThread.getId());
			if(welcomeSocket.isClosed()) {
				welcomeSocket.close();
			}
		}

	}

	public static synchronized void writeToUsers(Message message){
		try {
			for (User user : users) {
					user.out.writeObject(message);
					user.out.reset();
			}
		}catch(Exception ex){
		System.out.println(ex.getMessage());
		}
  }

   private static class User implements Runnable {
		 	private Socket socket = null;
			ObjectInputStream in = null;
			ObjectOutputStream  out = null;
			String name = null;
			static Message message = null;

			public User (Socket socket){
				this.socket = socket;
				try {
					this.out= new ObjectOutputStream(this.socket.getOutputStream());
					this.in= new ObjectInputStream(this.socket.getInputStream());
				}catch(Exception ex){
					System.out.println(ex.getMessage());
				}
			}

		public void run(){

			try {
				message = new Message();
				message.setMsg("Enter a username: ");
				out.writeObject(message);
				out.reset();

				message = (Message)in.readObject();
				name = message.getMsg();
				users.add(this);
				message.setMsg(name +", welcome to the chat");

				writeToUsers(message);
				message = new Message();

         while (Thread.currentThread().isAlive()) {
            message = (Message)in.readObject();
						message.setMsg(this.name + " " + message.getMsg());
            writeToUsers(message);
        }

			} catch(Exception ex){
				System.err.println(ex.getMessage());
			}
		   finally {
				 	if (name != null) {
						System.out.println(name + " left");
						message.setMsg(name + " has left the chat");
						users.remove(this);
						writeToUsers(message);
					}
				}
			}
		}
}
