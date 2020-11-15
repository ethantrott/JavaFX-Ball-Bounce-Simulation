//Ethan Trott
//an application to animate bouncing physics

package assignment3;

import java.util.ArrayList;

import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.application.*;
import javafx.geometry.Insets;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.*;
import javafx.scene.control.*;

public class assignment3 extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private ToggleGroup groupBalls;				//the ToggleGroup of ball types
	private TextField tfBounces;				//displays amount of bounces
	
	private Pane paneRight;						//Pane to hold the ball
	private Circle ball;						//Circle object, represents ball
	
	@Override
	public void start(Stage primaryStage) {
		//----Create the UI----
		Text textHeading = new Text("Bounce the Ball");		//create the heading
		textHeading.setFont(new Font(20));					//set the font size
		
		Label lblTypes = new Label("Ball Types");			//label for types
		groupBalls = new ToggleGroup();						//Group for type toggles
		RadioButton rdoB = new RadioButton("Basketball");	//create the toggles
		RadioButton rdoT = new RadioButton("Tennis ball");
		RadioButton rdoM = new RadioButton("Marble");
		rdoB.setSelected(true);					//set basketball to be default
		
		rdoB.setToggleGroup(groupBalls);		//set the ToggleGroup
		rdoT.setToggleGroup(groupBalls);
		rdoM.setToggleGroup(groupBalls);
		
		Label lblBounces = new Label("Number of Bounces:");	//label for bounces
		
		tfBounces = new TextField();					//create the field
		tfBounces.setEditable(false);					//disable editing
		tfBounces.setPrefColumnCount(20);				//set the width
		tfBounces.setMaxWidth(Double.MAX_VALUE);		//set the maxwidth
		
		
		//put them in a VBox
		VBox paneLeft = new VBox(10, textHeading, lblTypes, rdoB, rdoT, rdoM, 
														lblBounces, tfBounces);
		
		Button btnStart = new Button("Start");			//create the Start Button
		btnStart.setPrefWidth(80);						//set the width
		btnStart.setOnAction(e -> btnStart_Click());	//create Event for click
		
		//put it in an HBox
		HBox paneBottom = new HBox(btnStart);
		
		paneRight = new Pane();								//create the Pane
		paneRight.setMinWidth(300);							//set the minimum width
		paneRight.setStyle("-fx-border-color: black;");		//add the border
		
		ball = new Circle(20, Color.RED);			//create the ball
		paneRight.getChildren().add(ball);			//add the ball to the pane
		
		
		//----Assemble the Scene----
		BorderPane mainPane = new BorderPane();			//create the main pane
		mainPane.setPadding(new Insets(50,10,20,10));	//set the padding
		mainPane.setLeft(paneLeft);						//add the left pane
		mainPane.setRight(paneRight);					//add the right pane
		mainPane.setBottom(paneBottom);					//add the bottom pane
		
		Scene scene = new Scene(mainPane, 600, 600);	//create the scene
		primaryStage.setScene(scene);		  //put the scene on the stage
		primaryStage.setTitle("Bouncing Ball");			//set the title
		primaryStage.show();							//show the stage
		
		//after the pane dimensions are calculated...
		ball.setCenterX(paneRight.getWidth()/2);		//set the ball's X
		ball.setCenterY(paneRight.getHeight()/3);		//set the ball's Y
	}
	
	private void btnStart_Click() {
		//POST: set the ball properties and then animate it
		
		//get the selected radio button
		RadioButton selectedRdo = (RadioButton)groupBalls.getSelectedToggle();
		
		//get the character representing the ball type
		//b: basketball, t: tennis ball, m: marble
		char ballType = selectedRdo.getText().toLowerCase().charAt(0);
		
		Color ballColor;	//the color of the ball
		int ballSize;		//the radius of the ball
		
		//get the ball properties
		switch (ballType) {
			case 'b':	//basketball
				ballColor = Color.BROWN;
				ballSize = 20;
				break;
			case 't':	//tennis ball
				ballColor = Color.GREENYELLOW;
				ballSize = 10;
				break;
			case 'm':	//marble
				ballColor = Color.CORNFLOWERBLUE;
				ballSize = 5;
				break;
			default:	//unknown
				ballColor = Color.BLACK;
				ballSize = 20;
		}
		ball.setFill(ballColor);	//set the color
		ball.setRadius(ballSize);	//set the radius
		
		tfBounces.clear();	//clear the bounces textfield to avoid confusion
		
		animateBall(ballType, ballSize);	//animate the ball
	}
	
	private void animateBall(char ballType, int size) {
		//PRE: ballType is 'b', 't', or 'm' and size > 0
		//POST: animate the ball and display the amount of bounces
		
		//get the coefficient of restitution
		double RESTITUTION = 0;
		switch (ballType) {
			case 'b':
				RESTITUTION = 0.853;
				break;
			case 't':	
				RESTITUTION = 0.712;
				break;
			case 'm':
				RESTITUTION = 0.658;
				break;
		}
		
		//the acceleration due to gravity in pixels/sec^2, to calculate time
		final double GRAVITY = 1243.764;
		
		int bounces = 0; 	//to count amount of bounces
		
		double paneWidth = paneRight.getWidth();	//get the paneWidth
		double paneHeight = paneRight.getHeight();	//get the paneHeight
		
		//a list of all PathTransitions for the bounce animations
		ArrayList <PathTransition> paths = new ArrayList<PathTransition>();
		
		double startHeight = paneHeight/3;				//the starting Y value
		double endHeight = paneHeight - size;			//the ending Y value
		double distance = endHeight - startHeight;		//the distance to move
		
		final double MIN_DISTANCE = 10;			//the distance to stop bouncing
		
		//while the distance is greater than the minimum..
		while (distance > MIN_DISTANCE) {
			//create the Line for falling
			Line line = new Line(paneWidth/2, startHeight, paneWidth/2, endHeight);
			
			//calculate time in air using kinematics
			//d = 1/2 at^2 -> t=sqrt(2d/a);
			double time = Math.sqrt(2*distance/GRAVITY)*1000;	//time in millis
			
			//add a PathTransition of the ball on the line to the list
			paths.add(new PathTransition (Duration.millis(time), line, ball));
			
			//calculate the Y value to return to
			double returnHeight = RESTITUTION * distance;
			
			startHeight = endHeight;					//the new starting Y
			endHeight = startHeight - returnHeight;		//the new ending Y
			distance = startHeight - endHeight;			//the distance to move
			
			//create the Line for going back up
			Line lineBack = new Line(paneWidth/2, startHeight, paneWidth/2, endHeight);
			
			//calculate the travel time to go back up
			time = Math.sqrt(2*distance/GRAVITY)*1000;	//time in millis

			//create the PathTransition for going back up
			PathTransition goBackUp = new PathTransition (Duration.millis(time), lineBack, ball);
			
			/*
			 *  Note: Technically the ball should change velocity while
			 * 		falling, due to acceleration. But this is difficult, if 
			 * 		not impossible to do with PathTransition. Whatevs.
			 */
			
			
			//calculate heights + distance for next (possible) bounce
			startHeight = endHeight;					//the new starting Y
			endHeight = paneHeight - size;				//the new ending Y
			distance = endHeight - startHeight;			//the distance to move
			
			if (distance > MIN_DISTANCE) { 	//if there will be a next bounce:
				paths.add(goBackUp);			//add the path to go back up
				bounces++;	//since the ball is going back up, increase counter
			}
		}
		
		//create the SequentialTransition to play the bounces in sequence
		SequentialTransition animations = new SequentialTransition();
		for (int i=0;  i<paths.size(); i++) {	//for each PathTransition:
			PathTransition path = paths.get(i);		//get the PathTransition
			animations.getChildren().add(path);		//add it to the S.Transition
		}
		animations.play();	//play the bounce animations
		
		final int finalAmount = bounces;	//the final amount of bounces
		
		//use EventHandler to display the bounces after animating
		animations.setOnFinished(e -> displayBounces(finalAmount));
	}
	
	private void displayBounces(int bounces) {
		//POST: display the amount of boxes in the TextField
		
		tfBounces.setText(String.valueOf(bounces));
	}

}
