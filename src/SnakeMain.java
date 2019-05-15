import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Random;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SnakeMain extends Application {


    private AtomicBoolean isExit = new AtomicBoolean(false);
    private AtomicBoolean isPause = new AtomicBoolean(true);
    private AtomicBoolean isOver = new AtomicBoolean(false);
    private AtomicInteger score = new AtomicInteger(0);
    private AtomicBoolean isEaten = new AtomicBoolean(true);

    private Snake snake;
    private Pane region;
    private Rectangle fruit;
    public static void main(String[] args) {
        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Snake game");
        Pane gameWindow = createGameWindow();
        StackPane root = new StackPane();
        root.setAlignment(gameWindow, Pos.CENTER);
        root.getChildren().add(gameWindow);
        Scene sc = new Scene(root, 700, 700);
        primaryStage.setOnCloseRequest(event -> {
            isExit.set(true);
        });
        setKeyEvents(sc);
        primaryStage.setScene(sc);
        primaryStage.show();
    }

    private void setKeyEvents(Scene sc) {
        sc.setOnKeyPressed(e -> {
            KeyCode a = e.getCode();
               Node n =   snake.getSnakenodelist().get(0);
               double x= n.getLayoutX();
               double y = n.getLayoutY();

            switch (a) {
                case M:
                    isExit.getAndSet(true);
                    Platform.exit();
                    break;
                case A:
                    if (snake.getSnakeDirection() != Direction.RIGHT && !isPause.get()==true) {
                        snake.setSnakeDirection(Direction.LEFT,x,y);
                    }
                    break;
                case D:
                    if (snake.getSnakeDirection() != Direction.LEFT  && !isPause.get()==true) {
                        snake.setSnakeDirection(Direction.RIGHT,x,y);
                    }
                    break;
                case W:
                    if (snake.getSnakeDirection() != Direction.DOWN  && !isPause.get()==true) {
                        snake.setSnakeDirection(Direction.UP,x,y);
                    }
                    break;
                case S:
                    if (snake.getSnakeDirection() != Direction.UP && !isPause.get()==true) {
                        snake.setSnakeDirection(Direction.DOWN,x,y);
                    }
                    break;
                case P:
                    isPause.set(!isPause.get());
                    break;
                case E:
                    increaseSnakeHeight();
                    break;

            }
        });
    }

    private Pane createGameWindow() {

        region = new Pane();
        region.setPrefHeight(500);
        region.setMaxWidth(500);
        region.setMaxHeight(500);
        region.setPrefWidth(500);
        region.setLayoutX(150);
        region.setLayoutY(100);
        snake = new Snake();
        region.getChildren().addAll(snake.getSnakenodelist());
        region.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        new Thread(createWorker()).start();
        return region;
    }

    int n=0;
    public FutureTask createWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {

                while (isExit.get() == false) {

                    if (isPause.get() == false && isOver.get()==false) {
                        Platform.runLater(() -> {
                            snake.move();
                            checkoutOfBoundsCollison(region);
                            checkSnakeBodyCollision();
                            fruit= (Rectangle) createFruit();
                            eatfruit((Rectangle) snake.getSnakenodelist().get(0),fruit);
                        });
                    }

                    Thread.sleep(50);
                }

                return true;
            }
        };
    }

    public synchronized void checkoutOfBoundsCollison(Pane pane) {
        Rectangle node = (Rectangle) snake.getSnakenodelist().get(0);


        if (node.getLayoutX() < 0
                || node.getLayoutY() < 0
                || node.getLayoutX() > (pane.getWidth() - node.getWidth())
                || node.getLayoutY() > (pane.getHeight() - node.getHeight())) {
            isOver.set(true);
        }


    }

    public synchronized void  increaseSnakeHeight(){
         Node node =snake.appendToEnd();
         region.getChildren().add(node);
    }

    public synchronized void checkSnakeBodyCollision(){
        ObservableList<Node> snakenodelist = snake.getSnakenodelist();
        Rectangle n = (Rectangle)snakenodelist.get(0);
        for(int i=3;i<snakenodelist.size();i++){
            Rectangle currentNode = (Rectangle) snakenodelist.get(i);
            if(checkCollision(n,currentNode)){
                isOver.set(true);
            }

        }
    }

    public boolean checkCollision(Rectangle n,Rectangle currentNode){
           if(n==null || currentNode==null){
              return false;
           }

        double nX = n.getLayoutX();
        double ny = n.getLayoutY();
        double nxw =n.getLayoutX()+n.getWidth();
        double nyh =n.getLayoutY()+n.getHeight();
        double cX = currentNode.getLayoutX();
        double cy = currentNode.getLayoutY();
        double cHeight=currentNode.getHeight();
        double cWidth = currentNode.getWidth();

        if(nX>=cX && (nX<=cX+cWidth) && ny>=cy && (ny<=cy+cHeight) ||
                nxw>=cX && (nxw<=cX+cWidth) && nyh>=cy && (nyh<=cy+cHeight)||
                nX>=cX && (nX<=cX+cWidth) && nyh>=cy && (nyh<=cy+cHeight) ||
                nxw>=cX && (nxw<=cX+cWidth) && ny>=cy && (ny<=cy+cHeight)
        ){
            return true;
        }else{
            return false;
        }

    }


    public Node createFruit(){

        if(isEaten.get()){
            Random random = new Random();
            Rectangle r = new Rectangle(25,25);
            r.setFill(Color.PURPLE);
            r.setLayoutX(random.nextDouble()*(region.getWidth()-50));
            r.setLayoutY(random.nextDouble()*(region.getHeight()-50));
            region.getChildren().add(r);
            isEaten.set(false);
            return  r;
        }

        return  fruit;
    }

     public void eatfruit(Rectangle r , Rectangle n){
         if(checkCollision(r,n)){
             region.getChildren().remove(n);
              increaseSnakeHeight();
             score.set(score.get()+10);
             isEaten.set(true);
            fruit= (Rectangle) createFruit();
         }
     }


}
