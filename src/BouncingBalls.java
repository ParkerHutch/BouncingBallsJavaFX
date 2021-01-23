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

public class BouncingBalls extends Application implements EventHandler<KeyEvent>
{
   Scanner userInput = new Scanner(System.in);
      
   final int WIDTH = 600;//600;
   final int HEIGHT = 600;//600;
   
   double gravity = 0.0;
   double friction = 0.99; // number less than 1 = more friction
   
   double ballRadius = 50.0;
   double particleMass = 10.0;
   double xVelocitySetting = 2.0;
   double yVelocitySetting = 2.0;
   
   
   int objectCap = 40;
   
   Ball [] particles = new Ball[objectCap];
   Color [] colorArray = new Color[3];
   
   GraphicsContext g2d;
   
   
   public BouncingBalls()
   {
      colorArray[0] = Color.rgb(0, 188, 255); // light blue
      colorArray[1] = Color.rgb(0, 255, 137); // light green
      colorArray[2] = Color.rgb(252, 211, 76); // light orange
      
      // instantiate the ball objects
      
      for (int i = 0; i < objectCap; i++)
      {
         particles[i] = new Ball();
         particles[i].radius = 50.0;//Math.random() * ballRadius + 15;
         particles[i].xVelocity = (Math.random() * 8) - 4;
         particles[i].yVelocity = (Math.random() * 8) - 4;
         particles[i].ballList = particles;
         particles[i].color = colorArray[(int)(Math.random() * 3)];
         particles[i].mass = Math.pow(particles[i].radius / 40, 3);
      }
      
      
   }
   public static void main(String[] args)
   {
      launch(args);
   }
   public void init() throws Exception
   {
      ballExplosion();
      //spawnBalls(); // sets the initial x and y coordinates for every ball
   }
   
   public void start(Stage theStage) throws Exception 
   {
      theStage.setTitle("Bouncing Balls");
      Group root = new Group();
      Scene scene = new Scene(root, WIDTH, HEIGHT);
      
      final Box keyboardNode = new Box();
      keyboardNode.setFocusTraversable(true);
      keyboardNode.requestFocus();
      keyboardNode.setOnKeyPressed(this);
      
      root.getChildren().add(keyboardNode);
      
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
         spawnBalls();
      }
      if (arg0.getCode() == KeyCode.Q)
      {
         ballExplosion();
      }
      if (arg0.getCode() == KeyCode.A)
      {
         for (int i = 0; i < particles.length; i++)
         {
            particles[i].xVelocity = 2.0;
            particles[i].yVelocity = 2.0;
         }
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
   
   public void ballExplosion()
   {
      for (int i = 0; i < particles.length; i++)
      {
         particles[i].xPos = 300;
         particles[i].yPos = 300;
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
      double mass = Math.pow((this.radius / 40), 3);
      
      // variables for calculating collisions
      double xDistance;
      double yDistance;
      double distance;
      
      Ball [] ballList;
      
      Ball(){}
      
      Ball(
      double x, double y, double r, double dX, double dY, Color c, double m, Ball[] otherBalls
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
         mass = m;
         
         this.xPos = xPos;
         this.yPos = yPos;
         this.radius = radius;
         this.xVelocity = xVelocity;
         this.yVelocity = yVelocity;
         this.ballList = ballList;
         this.color = c;
         this.mass = mass;
         
      }
      
      public double getCenterX()
      {
         return (this.xPos + (this.radius / 2));
      }
      public double getCenterY()
      {
         return (this.yPos + (this.radius / 2));
      }
      
      public void update()
      {
         yPos += yVelocity;
         xPos += xVelocity;
         
         drawBall(); // will use the xPos and yPos variables
      }
      
      public void drawBall()
      {
         g2d.setStroke(this.color); // instead of setFill
         g2d.setLineWidth(2.0); // sets line width
         g2d.strokeOval(xPos, yPos, this.radius, this.radius); // instead of fillOval
      }
      
      public void checkCollisions()
      {
         // check wall collisions
         if ( ((this.xPos <= 0) && this.xVelocity < 0) || 
         ((this.xPos + this.radius >= WIDTH) && (this.xVelocity > 0) ))
         {
            // if ball hits a wall
            this.xVelocity *= -1;
         }
         else if ( ((this.yPos <= 0) && this.yVelocity < 0) || 
         ((this.yPos + this.radius >= HEIGHT) && (this.yVelocity > 0) ))
         {
            this.yVelocity *= -1;
         }
         else
         {
            this.yVelocity += gravity;
         }
         
         // ball to ball collisions
         for (int k = 0; k < ballList.length - 1; k++)
         {
            if (ballList[k] != this)
            {
               // if the ball we're checking a collision for isn't this ball...
               double deltaX = ballList[k].getCenterX() - this.getCenterX();
               double deltaY = ballList[k].getCenterY() - this.getCenterY();
               if (colliding(this, ballList[k], deltaX, deltaY))
               {
                  bounce(this, ballList[k], deltaX, deltaY);
               }
            }
         }
      }
      
      public boolean colliding(final Ball b1, final Ball b2, final double deltaX, final double deltaY) {
        // square of distance between balls is s^2 = (x2-x1)^2 + (y2-y1)^2
        // balls are "overlapping" if s^2 < (r1 + r2)^2
        // We also check that distance is decreasing, i.e.
        // d/dt(s^2) < 0:
        // 2(x2-x1)(x2'-x1') + 2(y2-y1)(y2'-y1') < 0
        
        final double radiusSum = (b1.radius + b2.radius) / 2.0; 
        if (deltaX * deltaX + deltaY * deltaY <= (radiusSum * radiusSum)) {
            if ( deltaX * (b2.xVelocity - b1.xVelocity) 
                    + deltaY * (b2.yVelocity - b1.yVelocity) < 0) {
                return true;
            }
        }
        return false;
    }
    
      private void bounce(final Ball b1, final Ball b2, final double deltaX, final double deltaY) {
        final double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY) ;
        //final double distance = Math.sqrt(b * deltaX + deltaY * deltaY) ;
        final double unitContactX = deltaX / distance ;
        final double unitContactY = deltaY / distance ;
        
        final double xVelocity1 = b1.xVelocity;
        final double yVelocity1 = b1.yVelocity;
        final double xVelocity2 = b2.xVelocity;
        final double yVelocity2 = b2.yVelocity;

        final double u1 = xVelocity1 * unitContactX + yVelocity1 * unitContactY ; // velocity of ball 1 parallel to contact vector
        final double u2 = xVelocity2 * unitContactX + yVelocity2 * unitContactY ; // same for ball 2
        
        final double massSum = b1.mass + b2.mass ;
        final double massDiff = b1.mass - b2.mass ;
        
        final double v1 = ( 2*b2.mass*u2 + u1 * massDiff ) / massSum ; // These equations are derived for one-dimensional collision by
        final double v2 = ( 2*b1.mass*u1 - u2 * massDiff ) / massSum ; // solving equations for conservation of momentum and conservation of energy
        
        final double u1PerpX = xVelocity1 - u1 * unitContactX ; // Components of ball 1 velocity in direction perpendicular
        final double u1PerpY = yVelocity1 - u1 * unitContactY ; // to contact vector. This doesn't change with collision
        final double u2PerpX = xVelocity2 - u2 * unitContactX ; // Same for ball 2....
        final double u2PerpY = yVelocity2 - u2 * unitContactY ; 
        
        b1.xVelocity = (v1 * unitContactX + u1PerpX);
        b1.yVelocity = (v1 * unitContactY + u1PerpY);
        b2.xVelocity = (v2 * unitContactX + u2PerpX);
        b2.yVelocity = (v2 * unitContactY + u2PerpY);
        
    }
    
   }
}
