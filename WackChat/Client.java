// Client.java

import java.awt.event.*;
import java.awt.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

import javax.swing.*;

class Client extends Thread {
     static String name;
     static Message message = null;
     static Date date = null;

    public static void closeSocket(Socket socket) {
      try {
        socket.close();
      }
      catch(Exception ex) {
      }
    }

    public static void main(String argv[]) throws Exception
    {
      JFrame frame = new JFrame("WackChat");
      JPanel mainPanel = new JPanel(new BorderLayout());

      JTextArea chat = new JTextArea(30,30);
      JPanel chatArea = new JPanel(new FlowLayout(FlowLayout.CENTER));
      chatArea.add(chat);
      chat.setEditable(false);

      JTextField input = new JTextField(20);
      input.setEditable(true);

      JPanel topPnl = new JPanel();
      JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

      JButton button = new JButton("BOP IT");

      topPnl.add(new JScrollPane(chatArea));

      bottomPanel.add(input);
      bottomPanel.add(button);

      bottomPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
      topPnl.setBorder(BorderFactory.createLineBorder(Color.RED));
      topPnl.setBackground(Color.BLUE);
      mainPanel.add(topPnl, BorderLayout.NORTH);
      mainPanel.add(bottomPanel, BorderLayout.SOUTH);

      frame.add(mainPanel);
      frame.pack();
      frame.setBackground(Color.GREEN);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try (Socket serverSocket = new Socket("localhost",5000)) {
              ObjectOutputStream outToServer = new ObjectOutputStream(serverSocket.getOutputStream());
              ObjectInputStream inFromServer = new ObjectInputStream(serverSocket.getInputStream());

              DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");

              message = (Message)inFromServer.readObject();
              while (true) {
                name = JOptionPane.showInputDialog(null,message.getMsg(),"Wackchat",JOptionPane.PLAIN_MESSAGE);
                if (name == null) {
                  closeSocket(serverSocket);
                  break;
                }
                else if (name.isEmpty()) {
                  message.setMsg("Invalid input. Enter a username: ");
                }
                else {
                  break;
                }
              }
              message.setMsg(name);
              outToServer.writeObject(message);
              outToServer.reset();

              chat.append("Press ENTER to send a message. Press \".\" to end chat.\n");

              message = new Message();

              frame.setVisible(true);

              Thread writeThread = new Thread() {
              	public void run() {
                  try {
                	button.addActionListener(new ActionListener(){
                		public void actionPerformed(ActionEvent e)
                		  {
                                if(input.getText().startsWith(".") == true && input.getText().length() == 1) {
                                  closeSocket(serverSocket);
                                } else {
                                  date = new Date();
                                  message.setMsg(dateFormat.format(date) + ": " + input.getText());
                                   try {
                                     outToServer.writeObject(message);
                                     input.setText("");
                                    } catch(Exception ex) {
                                 	   System.err.println(ex.getMessage());
                                   }
                                }
                              }
                	});
                    input.addKeyListener(new KeyAdapter() {
                     @Override
                     public void keyPressed(KeyEvent e) {
                         if(e.getKeyCode() == KeyEvent.VK_ENTER){
                           if(input.getText().startsWith(".") == true && input.getText().length() == 1) {
                             closeSocket(serverSocket);
                           } else {
                             date = new Date();
                             message.setMsg(dateFormat.format(date) + ": " + input.getText());
                              try {
                                outToServer.writeObject(message);
                                input.setText("");
                               } catch(Exception ex) {
                            	   System.err.println(ex.getMessage());
                              }
                           }
                         }
                       }

                     });
              		    while (serverSocket.isClosed() == false) {
              			}
                  } catch(Exception ex){
              				System.err.println(ex.getMessage());
              			}
              	}
              };

              Thread readThread = new Thread() {
              	public void run() {
                  try {
              		while (serverSocket.isClosed() == false) {
                      message = (Message)inFromServer.readObject();
                      chat.append(message.getMsg() + "\n");
                      message = new Message();
              			}

                  }catch(Exception ex){
              				 System.err.println(ex.getMessage());
              			}
              	}
              };

                writeThread.start();
                readThread.start();

                while(serverSocket.isClosed() == false) {
                }
          } catch (Exception ex) {

              System.out.println(ex.getMessage());

          }
          System.out.println("You have left the chat.");
          frame.setVisible(false);
          frame.dispose();
    }
}
