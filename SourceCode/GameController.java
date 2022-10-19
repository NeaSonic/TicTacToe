import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class GameController implements Initializable{
	@FXML
	private GridPane gp;
	@FXML
	private Label turn;
	@FXML
	private Label wins;
	@FXML
	private Label losses;
	@FXML
	private Label label1;
	@FXML
	private VBox vbox;
	@FXML
	private ScrollPane sp;
	@FXML
	private TextArea tf;
	@FXML
	private Label label2;
	@FXML
	private Label label3;
	private Line line;
	private String response;
	private Thread thread1, thread2;
	private Stack<Node> labels = new Stack<>();
	
	//variable used to stop threads on exit
	private boolean exit = true;

	//GAME CONTROLLER
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		//thread handling turns
		thread1 = new Thread(new Runnable() {
			@Override
			public void run() {
				while (exit) {
					if (Utils.getTurn() == true)
						gp.setDisable(false);
					else {
						gp.setDisable(true);
						update();
					}		
				}	
			}		
		});
		updateTurnLabel();
		
		
		//scroll down chat after each message
		vbox.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
				sp.setVvalue((Double) newValue);
			}
		});
		
		
		
		//send and display sent chat message
		tf.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				if (event.getCode()==KeyCode.ENTER) {
					String message = tf.getText();
					if (!message.isEmpty()) {
						HBox hbox = new HBox();
						hbox.setAlignment(Pos.CENTER_RIGHT);
						hbox.setPadding(new Insets(5,5,5,10));
						Text text = new Text(message);
						TextFlow textFlow = new TextFlow(text);
						textFlow.setStyle("-fx-color: rgb(239,242,255);"
								+ "-fx-background-color: rgb(15,125,242);"
								+ "-fx-background-radius: 20px;"
								+ "-fx-font-size: 20;");		
						textFlow.setPadding(new Insets(5,10,5,10));
						//text.setFill(Color.BLUE);
						hbox.getChildren().add(textFlow);
						vbox.getChildren().add(hbox);
						tf.clear();
						Chat.sendResponse(message);
						event.consume();
						
					}
				}	
			
		});
		
		//thread handling chat
		thread2 = new Thread(new Runnable() {
			@Override
			public void run() {
				while (exit) {
					String message = Chat.getResponse();
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							HBox hbox = new HBox();
							hbox.setAlignment(Pos.CENTER_LEFT);
							hbox.setPadding(new Insets(5,5,5,10));
							Text text = new Text(message);
							TextFlow textFlow = new TextFlow(text);
							textFlow.setStyle("-fx-color: rgb(239,242,255);"
									+ "-fx-background-color: crimson;"
									+ "-fx-background-radius: 20px;"
									+ "-fx-font-size: 20;");		
							textFlow.setPadding(new Insets(5,10,5,10));
							//text.setFill(Color.RED);
							hbox.getChildren().add(textFlow);
							vbox.getChildren().add(hbox);
							
						}
						
					});
				}	
			}		
		});
		thread1.start();
		thread2.start();
	}
	
	
	//rematch label buttons
	public void onMouseEnteredLabel(MouseEvent e) {
		Label label = (Label) e.getSource();
		label.setUnderline(true);
	}
	public void onMouseExitLabel(MouseEvent e) {
		Label label = (Label) e.getSource();
		label.setUnderline(false);
	}
	public void onMouseClickedLabel (MouseEvent e) {
		Utils.sendResponse("rematch?");
		label2.setDisable(true);
		label3.setDisable(true);
	}
	public void leave(MouseEvent e) throws IOException {
		exit = false;
		Utils.changeScene("MainMenu.fxml", e);
		
		Utils.sendResponse("opponentleft");
		
	}
	
	
	
	//game label buttons
	public void onMouseEntered(MouseEvent e) {
		GridPane pane = (GridPane) e.getSource();
		pane.add(Utils.newPlayerLabel(),0,0);
	}
	public void onMouseExit(MouseEvent e) {
		GridPane pane = (GridPane) e.getSource();
		pane.getChildren().remove(0);
	}
	public void onMouseClicked (MouseEvent e) {
		GridPane pane = (GridPane) e.getSource();
		int i = GridPane.getColumnIndex(pane)==null ? 0 : GridPane.getColumnIndex(pane);
		int j = GridPane.getRowIndex(pane)==null ? 0 : GridPane.getRowIndex(pane);
		pane.setDisable(true);
		Utils.increaseDrawCounter();
		updateTurnLabel();
		labels.add(Utils.newPlayerLabel());
		gp.add(labels.peek(), i, j);
		if (Utils.checkGameState(i, j)!=null) {
			Utils.sendResponse(Integer.toString(i)+j+Utils.getGameOver());
			label1.setText("YOU WON!");
			wins.setText(Integer.toString(Integer.valueOf(wins.getText())+1));
			gameEnd(Utils.getGameOver());
		}
		else
			if (Utils.getDraw()==9) {
				Utils.sendResponse(Integer.toString(i)+j+"draw");
				label1.setText("DRAW");
				gameEnd("draw");
			}
			else
			Utils.sendResponse(Integer.toString(i)+j);
		Utils.changeTurn();
	}
	
	
	
	//add post game lines for rows collumns and diagonals
    public void addLineRow (int col,int row) {		
		line = new Line();
		gp.add(line, col, row);
		line.setStartX(-100);
		line.setEndX(100);
		line.setStartY(0);
		line.setEndY(0);
		line.setStrokeWidth(5);
		labels.add(line);
	}
    public void addLineCol (int col,int row) {
		line = new Line();
		gp.add(line, col, row);
		line.setStartX(-100);
		line.setEndX(-100);
		line.setStartY(0);
		line.setEndY(200);
		line.setStrokeWidth(5);
		labels.add(line);
	}
    public void addLineDiag (int col,int row) {
		line = new Line();
		gp.add(line, col, row);
		line.setStartX(-100);
		line.setEndX(100);
		line.setStartY(0);
		line.setEndY(200);
		line.setStrokeWidth(5);
		labels.add(line);
	}
    public void addLineDiago (int col,int row) {
		line = new Line();
		gp.add(line, col, row);
		line.setStartX(-100);
		line.setEndX(-300);
		line.setStartY(0);
		line.setEndY(200);
		line.setStrokeWidth(5);
		labels.add(line);
	}
    
    
    
    //handle opponet turn responses
    public void update() {    	
    	response = Utils.getResponse();
    	int i;
    	int j;
    	switch (response.length()) {
    	
    	
    	//just update opponent move
    	case 2:
    		i = Integer.parseInt(response.substring(0, 1));
    		j = Integer.parseInt(response.substring(1, 2));
    		addOpponentLabel(i,j);
    		Utils.changeTurn();
    		updateTurnLabel();
    		Utils.increaseDrawCounter();
    		break;
    		
    	case 6:
    		i = Integer.parseInt(response.substring(0, 1));
    		j = Integer.parseInt(response.substring(1, 2));
    		addOpponentLabel(i,j);
    		Platform.runLater(new Runnable() {

				@Override
				public void run() {
					label1.setText("DRAW");
					
				}
    			
    		});
    		gameEnd("draw");
    		break;
    	
    	//update move and game lost	
    	case 4:
    		i = Integer.parseInt(response.substring(0, 1));
    		j = Integer.parseInt(response.substring(1, 2));
    		addOpponentLabel(i,j);
    		Utils.setGameOver(response.substring(2));    		
    		Platform.runLater(new Runnable() {
				@Override
				public void run() {
					label1.setText("YOU LOST!");
					losses.setText(Integer.toString(Integer.valueOf(losses.getText())+1));
					gameEnd(Utils.getGameOver());
				}
			});
    		break;
    		
    	//opponent left
    	case 12:
    		Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Utils.showError("Opponent Left!", "Your opponent has left. Returning to main menu!");
					exit = false;
					try {
						Utils.changeScene("MainMenu.fxml", gp);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
    		
    	//response to rematch request
    	case 7:
    		rematch();
    		Utils.rematch();
    		Utils.changeTurn();
    		updateTurnLabel();
    		resetRematchButton();
    		break;
    	
    	//rematch request
    	case 8:
    		label2.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					
					Utils.sendResponse("rematch");
					label2.setDisable(true);
					label3.setDisable(true);
					rematch();
					Utils.rematch();
					resetRematchButton();
				}
    			
    		});
    		break;
    	}
    	
    }
    
    
    //update gui according to win scenario
    public void gameEnd (String key) {
			switch (key) {
			case "c0":
				turn.setVisible(false);
				addLineCol(0, 0);
				addLineCol(0, 1);
				addLineCol(0, 2);
				gp.setDisable(true);
				Utils.delay(2000, new Runnable() {
					@Override
					public void run() {
						
						turnOff(gp);
						turnOn(label1);
						turnOn(label2);
						turnOn(label3);
					}
				});
				break;
			case "c1":
				turn.setVisible(false);
				addLineCol(1, 0);
				addLineCol(1, 1);
				addLineCol(1, 2);
				gp.setDisable(true);
				Utils.delay(2000, new Runnable() {
					@Override
					public void run() {
						
						turnOff(gp);
						turnOn(label1);
						turnOn(label2);
						turnOn(label3);
					}
				});
				break;
			case "c2":
				turn.setVisible(false);
				addLineCol(2, 0);
				addLineCol(2, 1);
				addLineCol(2, 2);
				gp.setDisable(true);
				Utils.delay(2000, new Runnable() {
					@Override
					public void run() {
						
						turnOff(gp);
						turnOn(label1);
						turnOn(label2);
						turnOn(label3);
					}
				});
				break;
			case "r0":
				turn.setVisible(false);
				addLineRow(0, 0);
				addLineRow(1, 0);
				addLineRow(2, 0);
				gp.setDisable(true);
				Utils.delay(2000, new Runnable() {
					@Override
					public void run() {
						
						turnOff(gp);
						turnOn(label1);
						turnOn(label2);
						turnOn(label3);
					}
				});
				break;
			case "r1":
				turn.setVisible(false);
				addLineRow(0, 1);
				addLineRow(1, 1);
				addLineRow(2, 1);
				gp.setDisable(true);
				Utils.delay(2000, new Runnable() {
					@Override
					public void run() {
						
						turnOff(gp);
						turnOn(label1);
						turnOn(label2);
						turnOn(label3);
					}
				});
				break;
			case "r2":
				turn.setVisible(false);
				addLineRow(0, 2);
				addLineRow(1, 2);
				addLineRow(2, 2);
				gp.setDisable(true);
				Utils.delay(2000, new Runnable() {
					@Override
					public void run() {
					
						turnOff(gp);
						turnOn(label1);
						turnOn(label2);
						turnOn(label3);
					}
				});
				break;
			case "d0":
				turn.setVisible(false);
				addLineDiag(0, 0);
				addLineDiag(1, 1);
				addLineDiag(2, 2);
				gp.setDisable(true);
				Utils.delay(2000, new Runnable() {
					@Override
					public void run() {

						turnOff(gp);
						turnOn(label1);
						turnOn(label2);
						turnOn(label3);
					}					
				});
				break;
			case "d1":
				turn.setVisible(false);
				addLineDiago(2, 0);
				addLineDiago(1, 1);
				addLineDiago(0, 2);
				gp.setDisable(true);
				Utils.delay(2000, new Runnable() {
					@Override
					public void run() {
						
						turnOff(gp);
						turnOn(label1);
						turnOn(label2);
						turnOn(label3);						
					}					
				});
				break;
			case "draw":
				turn.setVisible(false);
				gp.setDisable(true);
				Utils.delay(2000, new Runnable() {
					@Override
					public void run() {
						
						turnOff(gp);
						turnOn(label1);
						turnOn(label2);
						turnOn(label3);						
					}					
				});
				break;
			}
		}
    
    
    //update gui with opponent response
    public void addOpponentLabel(int i, int j) {
    	ObservableList<Node> list;
		list = gp.getChildren();
		for (Node node : list) {
			if ((GridPane.getColumnIndex(node)==null? 0: GridPane.getColumnIndex(node)) == i
				&& 
				(GridPane.getRowIndex(node)==null? 0: GridPane.getRowIndex(node)) == j) {		
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						node.setDisable(true);
						System.out.println("Running");
					}
				});
				break;
			}	
		}		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				System.out.println("Running");
				labels.add(Utils.newOpponentLabel());
				gp.add(labels.peek(), i, j);				
			}	
		});
    }
    
    
    
    //reset gui for rematch
    public void rematch () {
    	Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ObservableList<Node> list;
				gp.getChildren().removeAll(labels);
				list = gp.getChildren();
				for (Node node: list) 
					node.setDisable(false);
				turnOff(label1);
				turnOff(label2);
				turnOff(label3);
				turn.setVisible(true);
				gp.setVisible(true);
				
			}
    		
    	});}
    
    //util that i made for literally no reason other than to write one less line of code
    public void turnOn(Node node) {
    	node.setVisible(true);
    	node.setDisable(false);
    }
    public void turnOff(Node node) {
    	node.setVisible(false);
    	node.setDisable(true);
    }
    
    
    
    //reset rematch label button to rematch request instead of response
    public void resetRematchButton() {
    	label2.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				Utils.sendResponse("rematch?");
				label2.setDisable(true);
				label3.setDisable(true);				
			}
    	});
    }
    
    
    //update gui to display turn
    public void updateTurnLabel() {
    	Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (Utils.getTurn())
					turn.setText("YOUR TURN");
				else
					turn.setText("OPPONET'S TURN");
			}
		});
    }   
}

