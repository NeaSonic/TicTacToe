import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


//same as utils but for chat, other class was getting too big and i cba to sort through it again xDD

public class Chat {
	
	private static Socket s;
	private static BufferedReader br;
	private static BufferedWriter bw;
	private static String response;
	
	public static void setSocket(Socket socket) throws IOException {
		s = socket;
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
	}
	public static void sendResponse(String response){
    	try {
			bw.write(response);
			bw.newLine();
			bw.flush();
			System.out.println("Sent"+response);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	public static String getResponse(){
    	try {
    		response = br.readLine();
			System.out.println("Received"+response);
			return response;
		} catch (IOException e) {
			return null;
		}
    }

}
