import javafx.scene.*;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import java.util.*;

public class SimplePhysicsV1_5 extends Application implements EventHandler<KeyEvent>
{
   Scanner userInput = new Scanner(System.in);
      
   final int WIDTH = 1080;
   final int HEIGHT = 720;
   
   double gravity = 0.0;
   double friction = 0.99; // number less than 1 = more friction
   
   double ballRadius = 50.0;
   double particleMass = 2.0;
   int objectCap = 50;
   
   Ball [] particles = new Ball[objectCap];
   Color [] colorArray = new Color[3];
   
   GraphicsContext g2d;
   
   
   public SimplePhysicsV1_5()
   {
      // instantiate the ball objects
      
      for (int i = 0; i < objectCap; i++)
      {
         particles[i] = new Ball();
         //particles[i].radius = ballRadius;
      }
      
      colorArray[0] = Color.rgb(0, 188, 255); // light blue
      colorArray[1] = Color.rgb(0, 255, 137); // light green
      colorArray[2] = Color.rgb(252, 211, 76); // light orange
   }
   public static void main(String[] args)
   {
      launch(args);
   }
   public void init() throws Exception
   {
      spawnBalls(); // sets the initial x and y coordinates for every ball
   }
   
   public void start(Stage theStage) throws Exception 
   {
           
      theStage.setTitle("Simple Physics");
      Group root = new Group();
      Scene scene = new Scene(root, WIDTH, HEIGHT);
      
      final Box keyboardNode = new Box();
      keyboardNode.setFocusTraversable(true);
      keyboardNode.requestFocus();
      keyboardNode.setOnKeyPressed(this);
      
      root.getChildren().add(keyboardNode);
      for (int i = 0; i < objectCap; i++)
      {
      
         particles[i].xVelocity = 3;//Math.random() * 10 - 10;
         particles[i].yVelocity = 3;//(int)Math.random() * 10 - 10;
         particles[i].radius = ballRadius;
         particles[i].ballList = particles;
         particles[i].color = colorArray[(int)(Math.random() * 3)];
      }
      
      Canvas canvas = new Canvas(WIDTH, HEIGHT);
      g2d = canvas.getGraphicsContext2D();
      
      root.getChildren().add(canvas);
      
      theStage.setScene(scene);
      
      AnimationTimer animator = new AnimationTimer()
      {
         @Override
         public void handle(long arg0)
         {
            // RENDER
            g2d.clearRect(0,0,WIDTH,HEIGHT);
            // clear the text written previously(try removing above line to see why)
            
            for (int j = 0; j < objectCap; j++)
            {
               particles[j].checkCollisions();
               particles[j].update();
            }
         }
      };
      animator.start();
      theStage.show();
      
   }
   public void handle(KeyEvent arg0)
   {
      if (arg0.getCode() == KeyCode.SPACE)
      {
         particles[0].xVelocity += 1;
         particles[0].yVelocity += 1;
         
      }
   }
   
   public void spawnBalls()
   {
      double possibleX;
      double possibleY;
      
      for (int i = 0; i < particles.length; i++)
      {
         possibleX = Math.random() * WIDTH;
         possibleY = Math.random() * HEIGHT;
         if (i != 0)
         {
            // the first ball can be placed anywhere, this pertains to every other ball
            for (int k = 0; k < particles.length; k++)
            {
               if ((
               Math.hypot(
               particles[k].xPos - possibleX, particles[k].yPos - possibleY
               ) - (particles[k].radius + particles[i].radius) < 0) && 
               (particles[i] != particles[k])
               )
               {
                  // if the distance between the ball and another one on the list
                  // is too small, get new random x and y positions
                  possibleX = Math.random() * WIDTH;
                  possibleY = Math.random() * HEIGHT;
                  k = -1; // start over
               }
               else
               {
                  particles[i].xPos = possibleX;
                  particles[i].yPos = possibleY;
               }
            }
            System.out.println("Balls placed: " + (i+1));
         }
      }
   }
   
   public class Ball
   {
      // position, radius, color, and velocity variables
      double xPos;
      double yPos;
      double radius;
      Color color;
      double xVelocity;
      double yVelocity;
      
      
      // variables for calculating collisions
      double xDistance;
      double yDistance;
      double distance;
      
      Ball [] ballList;
      
      Ball(){}
      
      Ball(
      double x, double y, double r, double dX, double dY, Color c, Ball[] otherBalls
      )
      {
         // set variables to arguments
         xPos = x;
         yPos = y;
         radius = r;
         xVelocity = dX;
         yVelocity = dY;
         color = c;
         ballList = otherBalls;
         
         this.xPos = xPos;
         this.yPos = yPos;
         this.radius = radius;
         this.xVelocity = xVelocity;
         this.yVelocity = yVelocity;
         this.ballList = ballList;
         this.color = c;
      }
      
      public void update()
      {
         // apply gravity, friction, change x/y vals, draw the ball
         if (yPos + radius >= HEIGHT)
         {
               // when ball hits the ground
               xVelocity = xVelocity * friction;
               if (Math.abs(yVelocity) != yVelocity)
               {
                  // if yVelocity is negative...
                  yVelocity = -yVelocity * friction;
                  // below note explains why I do this
               }
               yVelocity  = -yVelocity * friction;
         }
         else if ( yPos - radius <= 0 )
         {
            // if ball hits the top of the screen
            if (Math.abs(yVelocity) != yVelocity)
            {
               yVelocity *= -1;
            }
         }
         else
         {
            yVelocity += gravity;
         }
         if (((xPos + radius) >= WIDTH))
         {
            // if the ball hits the right wall
            /*
            If the ball hits the right wall we want the xVelocity to be negative.
            Previously, changing the sign of xVelocity sort of worked to make 
            the ball bounce off. However, if the ball went a little far to the 
            left, the xVelocity would just flip over and over again, and the ball
            wouldn't move. The code below is meant to solve that problem by
            ensuring that xVelocity has the desired sign and then stops flipping
            ( negative sign for hitting right wall, positive for left )
            */
                  
            if (Math.abs(xVelocity) == xVelocity)
            {
               // if xVelocity is positive, flip it to get the ball away from the
               // right wall
               xVelocity = -xVelocity;
            }
               // flip horizontal velocity
         }
         else if ((xPos - radius) <= 0)
         {
            // if ball hits the left wall
            if (Math.abs(xVelocity) != xVelocity)
            {
               // if xVelocity is negative, make it positive
               xVelocity = -xVelocity;
            }      
         }
         // if ball is in the air, not touching anything
                 
         yPos += yVelocity;
         xPos += xVelocity;
         
         drawBall(); // will use the xPos and yPos variables
      }
      
      public void drawBall()
      {
         g2d.setFill(this.color);
         g2d.fillOval(xPos, yPos, this.radius, this.radius);
      }
      
      public void checkCollisions()
      {
         
         for (int k = 0; k < ballList.length - 1; k++)
         {
            if (ballList[k] != this)
            {
               // if the ball we're checking a collision for isn't this ball...
               // calculate distance
               // c^2 = a^2 + b^2 -> use this to find distance
               xDistance = Math.abs(this.xPos - ballList[k].xPos);
               yDistance = Math.abs(this.yPos - ballList[k].yPos);
               distance = Math.sqrt( Math.pow(xDistance, 2) + Math.pow(yDistance, 2) );

               // distance = c from above equation
               if ((distance <= (this.radius + ballList[k].radius) / 2))
               {
                  resolveCollision(this, ballList[k]);
               }
            }
            
         }
      }
      public void resolveCollision(Ball particle1, Ball particle2)
      {
         double p1xVelocity = particle1.xVelocity;
         double p1yVelocity = particle1.yVelocity;
         double p2xVelocity = particle2.xVelocity;
         double p2yVelocity = particle2.yVelocity; 
         
         particle1.xVelocity = p2xVelocity;
         particle1.yVelocity = p2yVelocity;
         
         particle2.xVelocity = p1xVelocity;
         particle2.yVelocity = p1yVelocity;
         
      }
   }
}
