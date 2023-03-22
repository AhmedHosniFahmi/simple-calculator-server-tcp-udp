import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Server {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		HashMap<String, String> responses = new HashMap<>();
		responses.put("Hi", "Hi");
		responses.put("Hello", "Hi");
		responses.put("How are you?", "I'm fine, what about you?");
		responses.put("Are you ok?", "Yes, I am. Thank you for asking.");
		responses.put("Are you still running?", "Yes, sir!");
		responses.put("Can you calculate something for me?", "Sure! Send it anytime!");
		
		System.out.print("enter 1 for tcp and 2 for udp : ");
		int choose = scanner.nextInt();
		if(choose == 1) {
			System.out.println("Server is using TCP");
			try {
				ServerSocket soc = new ServerSocket(3333);
				while (true) {
					Socket sock = soc.accept();
					TCPThread thread = new TCPThread(sock);
					thread.start();
				}

			} catch(IOException e) {
				System.out.println(e);
			}
		} else if (choose == 2) {
			System.out.println("Server is using UDP");
			try {
				DatagramSocket ds = new DatagramSocket(3331);
				InetAddress ip = InetAddress.getByName("127.0.0.1");
				byte[] buffer = new byte[2056];
				DatagramPacket dpReciver = new DatagramPacket(buffer, 2056);

				HashMap<Integer, List<String>> port_data = new HashMap<>();
				Calculator calc = new Calculator();

				while (true) {
					ds.receive(dpReciver);
					int port = dpReciver.getPort();
					String message = new String(dpReciver.getData(),0,dpReciver.getLength()); System.out.println(message);
					if (message.equals("!e")) { 
						port_data.put(port, new ArrayList<>());
					}
					else if (port_data.containsKey(port)) {
						port_data.get(port).add(message);
						
						List<String> list = port_data.get(port);
						// System.out.println(list);
						if (list.size() == 3) {
							Float num1 = Float.parseFloat(list.get(0));
							char op = list.get(1).charAt(0);
							Float num2 = Float.parseFloat(list.get(2));
							try {
								DatagramPacket dpSender = new DatagramPacket(buffer, 0, ip, port);
								dpSender.setData((num1+" "+Character.toString(op)+" "+num2+" = "+ calc.calculate(num1,num2,op)).getBytes());
								dpSender.setLength((num1+" "+Character.toString(op)+" "+num2+" = "+ calc.calculate(num1,num2,op)).length());
								ds.send(dpSender);
							} catch (SocketException e) {
								System.out.println(e);
							}
							port_data.remove(port);
						}
					} else {
						try {
							String response = responses.get(message);
							if (response == null) {
								response = "Oh, I do not find a response to your message in my responses list. Sorry for that :)";
							}
							DatagramPacket dpSender = new DatagramPacket(buffer, 0, ip, port);
							dpSender.setData(response.getBytes());
							dpSender.setLength(response.length());
							ds.send(dpSender);
						} catch (SocketException e) {
							System.out.println(e);
						}
					}

				}
					
			} catch(IOException e) {
				System.out.println(e);
			}
		}
		scanner.close();
	}
}
