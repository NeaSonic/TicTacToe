import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class MainMenuController implements Initializable{
	@FXML
	private TextField address;
	@FXML
	private TextField port;
	@FXML
	private Button join;
	@FXML
	private Button host;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		host.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int p;
				try {
					p = Integer.parseInt(port.getText());
					if (p>65535 || p<0)
						throw new NumberFormatException();
					ServerSocket ss = new ServerSocket(p);
					ss.setSoTimeout(30000);
					Utils.setSocket(ss.accept());
					Chat.setSocket(ss.accept());
					ss.close();
					Utils.setPlayer("X");
					Utils.setOpponent("O");
					Utils.changeScene("Game.fxml", event);
										
				}
				catch (NumberFormatException e) {
					Utils.showError("Input not valid", "Please input a port number between 0 and 65535");
					e.printStackTrace();
				}
			    catch (SocketTimeoutException e) {
				   Utils.showError("Unable to establish connection", "Connection Timeout");
				   e.printStackTrace();
			    } catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		});
		join.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					String ip = address.getText();
					int p;
					p = Integer.parseInt(port.getText());
					if (p>65535 || p<0)
						throw new NumberFormatException();
					Socket s = new Socket();
					InetAddress addr = InetAddress.getByName(ip);
					SocketAddress sockaddr = new InetSocketAddress(addr,p);
					s.connect(sockaddr);
					Utils.setSocket(s);
					s = new Socket();
					s.connect(sockaddr);
					Chat.setSocket(s);
					Utils.setPlayer("O");
					Utils.setOpponent("X");
					Utils.changeScene("Game.fxml", event);
				} catch (NumberFormatException e) {
					Utils.showError("Input not valid", "Please input a port number between 0 and 65535");
					e.printStackTrace();
				} catch (UnknownHostException e) {
					Utils.showError("Unknown host exception", "xDD");
					e.printStackTrace();
				} catch (IOException e) {
					Utils.showError("IO exception", "xDD");
					e.printStackTrace();
				}
				
			}
			
		});
	}
}

