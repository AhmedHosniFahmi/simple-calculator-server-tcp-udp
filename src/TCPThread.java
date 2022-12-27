import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class TCPThread extends Thread{
    private Socket socket;
    Calculator calc;
    HashMap<String, String> responses;
    
    TCPThread(Socket sock) {
        this.socket = sock;
        calc = new Calculator();
		responses = new HashMap<>();
		responses.put("Hi", "Hi");
		responses.put("Hello", "Hi");
		responses.put("How are you?", "I'm fine, what about you?");
		responses.put("Are you ok?", "Yes, I am. Thank you for asking.");
		responses.put("Are you still running?", "Yes, sir!");
		responses.put("Can you calculate something for me?", "Sure! Send it anytime!");
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(this.socket.getInputStream());
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
            
            out.writeUTF("Connection with the server has been established.");
            System.out.println("Connection with " + this.socket.getInetAddress().getHostName() + " has been established");
            
            while(true) {
                String message = in.readUTF();
                if (message.equals("!e")) {
                    float num1 = in.readFloat();
                    char op = in.readChar();
                    float num2 = in.readFloat();
                    
                    String result = num1+" "+Character.toString(op)+" "+num2+" = "+ calc.calculate(num1,num2,op);
                    out.writeUTF(result);
                } else if(message.equals("!q")) {
                    System.out.println("Connection has been terminated");
                    out.writeUTF("Connection has been terminated");
                    break;
                } else {
                    String response = responses.get(message);
                    if (response != null) out.writeUTF(response);
                    else out.writeUTF("Oh, I do not find a response to your message in my responses list. Sorry for that :)");
                }
            }

        } catch(IOException e) {
            System.out.println(e);
        }
        
    }
}
