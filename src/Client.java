import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
public class Client {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Enter 1 for TCP and 2 for UDP : ");
		int choose = scanner.nextInt();
		
		if(choose == 1) {
			try {
				Socket sock = new Socket("localhost", 3333);

				
				DataInputStream in = new DataInputStream(sock.getInputStream());
				DataOutputStream out = new DataOutputStream(sock.getOutputStream());
				
				System.out.println(in.readUTF());
				System.out.println();
				
				int flag = 1;
				while(flag == 1) {
					System.out.print("Enter a message: ");
					String message = "";
					while (message.equals("")) {
						message = scanner.nextLine();
					}
					
					if (message.equals("!e")) {
						out.writeUTF(message);
						System.out.println();

						System.out.print("Enter num1: ");
						Float num1 = scanner.nextFloat();
						out.writeFloat(num1);
						System.out.println();
						
						System.out.print("Enter operation + - / ^ * : ");
						char op = scanner.next().charAt(0);
						out.writeChar(op);
						System.out.println();
						
						System.out.print("Enter num2: ");
						Float num2 = scanner.nextFloat();
						out.writeFloat(num2);
						System.out.println();

						String result = in.readUTF();
						System.out.println("Server: " + result);
						System.out.println();
						scanner.nextLine();
					} else if (message.equals("!q")) {
						flag = 2;
						out.writeUTF("!q");
					} else {
						out.writeUTF(message);
						System.out.println();
						String result = in.readUTF();
						System.out.println("Server: " + result);
						System.out.println();
					}
					
					
					if(flag == 2) {
						System.out.println();
						System.out.println(in.readUTF());
					}
					System.out.println();
				}
				sock.close();
			}catch(IOException e) {
				System.out.println(e);
			}
		} 
		else if (choose == 2) {
			int flag = 1;
			try {
				System.out.print("Port: ");
				DatagramSocket ds = new DatagramSocket(scanner.nextInt());
				InetAddress ip = InetAddress.getByName("127.0.0.1");
				byte[] buffer = new byte[2056];
				DatagramPacket dpSender = new DatagramPacket(buffer, 0, ip, 3331);		
				DatagramPacket dpReciver = new DatagramPacket(buffer, 2056);
				
				
				while(flag == 1) {
					System.out.print("Enter a message: ");
					String message = "";
					while (message.equals("")) {
						message = scanner.nextLine();
					}
					
					if (message.equals("!e")) {
						dpSender.setData(message.getBytes());
						dpSender.setLength(message.length());
						ds.send(dpSender);
						System.out.println();

						System.out.print("Enter num1: ");
						String num1 = scanner.next();
						System.out.println();
						dpSender.setData(num1.getBytes());
						dpSender.setLength(num1.length());
						ds.send(dpSender);
						
						System.out.print("Enter operation + - / ^ * : ");
						String op = scanner.next();
						System.out.println();
						dpSender.setData(op.getBytes());
						dpSender.setLength(op.length());
						ds.send(dpSender);
						
						System.out.print("Enter num2: ");
						String num2 = scanner.next();
						System.out.println();
						dpSender.setData(num2.getBytes());
						dpSender.setLength(num2.length());
						ds.send(dpSender);

						ds.receive(dpReciver);
						System.out.println("Server: "+new String(dpReciver.getData(),0,dpReciver.getLength()));
						scanner.nextLine();
					} else if (message.equals("!q")) {
						flag = 2;
					} else {
						dpSender.setData(message.getBytes());
						dpSender.setLength(message.length());
						ds.send(dpSender);
						System.out.println();

						ds.receive(dpReciver);
						System.out.println("Server: "+new String(dpReciver.getData(),0,dpReciver.getLength()));
					}
					System.out.println();

				}
				ds.close();
			} catch(IOException e) {
				System.out.println(e);
			}
		}
		scanner.close();
	}
}
