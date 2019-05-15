import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;


public class Snake {

    private int speed =5;
    private ObservableList<Node> snakenodelist = FXCollections.observableArrayList();
    private Node headNode;
    private Node tailNode;
    private static  final double NodeHeight=30;
    private static final double NodeWidth=30;

    private Direction snakeDirection;
    private Direction tailDirection;


    private List<BendPoint> bendPoints = new ArrayList<>();

    private List<Direction> directions = new ArrayList<>();

    public Snake() {
        intialiseSnake();
    }

    private void intialiseSnake() {
        this.snakeDirection =Direction.RIGHT;
        tailDirection=snakeDirection;
        Rectangle r = new Rectangle(NodeWidth, NodeHeight);
        snakenodelist.add(r);
        headNode =r;
        tailNode=headNode;
    }

    public ObservableList<Node> getSnakenodelist() {
        return snakenodelist;
    }


    public synchronized void move() {
        movetoDir(headNode,snakeDirection);
        moveSnakeBody();
    }


    private synchronized void moveSnakeBody(){
        for(int i =1;i<snakenodelist.size();i++){
            Node n = snakenodelist.get(i);
            for(BendPoint bendPoint:bendPoints){
                if(bendPoint.position ==i && n.getLayoutX()==bendPoint.x && n.getLayoutY()==bendPoint.y ){
                    directions.set(i-1,bendPoint.direction);
                    bendPoint.bendmade=true;
                }
             }
            movetoDir(n,directions.get(i-1));
        }

        for(int i=0;i<bendPoints.size();i++){
            BendPoint bendPoint = bendPoints.get(i);
            if(bendPoint.bendmade ==true){
                bendPoint.position++;
                bendPoint.bendmade=false;
            }
            if(bendPoint.position==snakenodelist.size()){
                tailDirection =bendPoint.direction;
                bendPoints.remove(bendPoint);
            }
        }

    }

    public  synchronized void movetoDir(Node node, Direction direction) {

        switch (direction) {

            case LEFT:
                node.setLayoutX(node.getLayoutX() - speed);
                break;
            case RIGHT:
                node.setLayoutX(node.getLayoutX() + speed);
                break;
            case UP:
                node.setLayoutY(node.getLayoutY()-speed);
                break;
            case DOWN:
                node.setLayoutY(node.getLayoutY()+speed);
                break;
        }
    }

    public Node appendToEnd(){
        Rectangle ref = (Rectangle)tailNode;
        Rectangle r = new Rectangle(ref.getWidth(),ref.getWidth());
         switch (tailDirection){
             case LEFT:
                  r.setLayoutX(ref.getLayoutX()+NodeWidth);
                  r.setLayoutY(ref.getLayoutY());
                 break;

             case RIGHT:
                 r.setLayoutX(ref.getLayoutX()-NodeWidth);
                 r.setLayoutY(ref.getLayoutY());
                 break;

             case UP:
                 r.setLayoutX(ref.getLayoutX());
                 r.setLayoutY(ref.getLayoutY()+NodeHeight);
                 break;

             case DOWN:
                 r.setLayoutX(ref.getLayoutX());
                 r.setLayoutY(ref.getLayoutY()-NodeHeight);
                 break;
         }
         tailNode =r;
         directions.add(tailDirection);
         tailDirection= directions.get(directions.size()-1);
         snakenodelist.add(r);
         return  r;
    }

    public synchronized Direction getSnakeDirection() {
        return snakeDirection;
    }


    public synchronized void setSnakeDirection(Direction snakeDirection,double x,double y) {
        this.snakeDirection = snakeDirection;
                BendPoint bendPoint = new BendPoint();
                bendPoint.position = 1;
                bendPoint.x = x;
                bendPoint.y = y;
                bendPoint.direction = snakeDirection;
                bendPoints.add(bendPoint);
    }

    public void setTailDirection(Direction tailDirection) {
        this.tailDirection = tailDirection;
    }

    private class BendPoint{
        int position;
        double x;
        double y;
        Direction direction;
        boolean bendmade =false;
    }

}