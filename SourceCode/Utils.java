import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;

import java.util.Map;
import java.util.Objects;

import java.util.Map.Entry;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


//stoarge for connection variables and game variables, everything here is very self explanatory

public class Utils {
	
	private static Socket s;
	private static BufferedReader br;
	private static BufferedWriter bw;
	private static String player;
	private static String opponent;
	private static boolean turn;
	private static String gameOver;
	private static String response;
	private static int draw=0;
	
	//hashmap used to determine the state of the game
	private static HashMap<String,Integer> gameState = new HashMap<String,Integer>();static{

		gameState.put("r0",0);
		gameState.put("r1",0);
		gameState.put("r2",0);
		gameState.put("c0",0);
		gameState.put("c1",0);
		gameState.put("c2",0);
		gameState.put("d0",0);
		gameState.put("d1",0);
	}
	
	public static void increaseDrawCounter() {
		draw++;
	}
	public static void resetDrawCounter() {
		draw=0;
	}
	public static int getDraw() {
		return draw;
	}
	
	public static void setSocket(Socket socket) throws IOException {
		s = socket;
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
	}
	
    public static void changeScene(String fxml,ActionEvent e) throws IOException {
    	FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxml));
    	Parent root = loader.load();
    	Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    	Scene scene = new Scene(root);
    	stage.setScene(scene);
    	stage.show();	
    }
    public static void changeScene(String fxml,Node e) throws IOException {
    	FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxml));
    	Parent root = loader.load();
    	Stage stage = (Stage) e.getScene().getWindow();
    	Scene scene = new Scene(root);
    	stage.setScene(scene);
    	stage.show();	
    }
    public static void changeScene(String fxml,MouseEvent e) throws IOException {
    	FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxml));
    	Parent root = loader.load();
    	Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    	Scene scene = new Scene(root);
    	stage.setScene(scene);
    	stage.show();	
    }
    public static void showError(String header,String content) {
    	Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setHeaderText(header);
		errorAlert.setContentText(content);
		errorAlert.showAndWait();
    }
    public static void setPlayer(String string) {
    	player = string;
    	if (string == "X")
    		turn = true;
    	else
    		turn = false;
    }
    public static void setOpponent(String string) {
    	opponent = string;
    }
    public static String getPlayer() {
    	return player;
    }
    public static String getOpponent() {
    	return opponent;
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
    public static void changeTurn() {
    	if (turn)
    		turn=false;
    	else
    		turn=true;
    }
    public static boolean getTurn() {
    	return turn;
    }
	public static <T, E> String getKeyByValue(Map<T, E> map, E value) {
	    String key = null;
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            key = (String) entry.getKey();
	        }
	    }
	    return key;
	}
    public static String checkGameState(int col, int row) {
		
		gameState.replace("c"+col, gameState.get("c"+col)+1);
		gameState.replace("r"+row, gameState.get("r"+row)+1);
		if (col+row==2)
			if (col==1) {
				gameState.replace("d0", gameState.get("d0")+1);
				gameState.replace("d1", gameState.get("d1")+1);
			}
			else
				gameState.replace("d1", gameState.get("d1")+1);
		if (col+row==0||col+row==4)
			gameState.replace("d0", gameState.get("d0")+1);
		if (gameState.containsValue(3)) {
			gameOver = getKeyByValue(gameState, 3);
		}
		return gameOver;
    }
    public static String getGameOver() {
    	return gameOver;
    }
    public static void setGameOver(String s) {
    	gameOver = s;
    }
    public static Label newPlayerLabel() {
    	Label label = new Label(player);
    	label.setFont(Font.font("Arial", FontWeight.BOLD, 50));
    	label.setTextFill(Color.DARKBLUE);
    	return label;
    }
    public static Label newOpponentLabel() {
    	Label label = new Label(opponent);
    	label.setFont(Font.font("Arial", FontWeight.BOLD, 50));
    	label.setTextFill(Color.DARKRED);
    	return label; 	
    }
    public static boolean checkConnection() {
    	return s.isConnected();
    }
    public static void rematch () {
    	gameState.put("r0",0);
		gameState.put("r1",0);
		gameState.put("r2",0);
		gameState.put("c0",0);
		gameState.put("c1",0);
		gameState.put("c2",0);
		gameState.put("d0",0);
		gameState.put("d1",0);
		resetDrawCounter();
		gameOver=null;		
    }
    public static void delay(long millis, Runnable continuation) {
	      Task<Void> sleeper = new Task<Void>() {
	          @Override
	          protected Void call() throws Exception {
	              try { Thread.sleep(millis); }
	              catch (InterruptedException e) { }
	              return null;
	          }
	      };
	      sleeper.setOnSucceeded(event -> continuation.run());
	      new Thread(sleeper).start();
	    }
}
