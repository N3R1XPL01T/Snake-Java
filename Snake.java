//package principal;

import javax.swing.*;
import java.awt.event.*; // Eventos
import java.awt.Toolkit; // Toolkit: Obtener el tamano de la ventana
import java.awt.Point; // Para obtener el punto en el plano
import java.awt.Dimension;
import java.awt.*;

// clase random
import java.util.*;
//import java.awt.Toolkit; // Toolkit: Obtener el tamano de la ventana

public class Snake extends JFrame {
    int width = 640;
    int height = 480;
    
    ArrayList<Point> lista = new ArrayList<Point>(); // Lista de puntos para la serpiente

    boolean gameOver = false;
    
    Point snake;
    Point comida;

    int direction = KeyEvent.VK_LEFT;
    int widthPoint = 10, heightPoint = 10;

    long fps = 50;

    Imagenes snake_body = new Imagenes();
        
    public Snake() {
        setTitle("Snake");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-width/2, dim.height/2-height/2); // Nos posiciona la ventana en el centro de la ventana
        this.addKeyListener(new Teclas()); // Hacemos que la tecla responda a la clase evento
        startGame();
        Moment m = new Moment();
        Thread trid = new Thread(m);
        getContentPane().add(snake_body); // Se hace que se agregue la SNAKE a la pantalla
        setVisible(true);
        trid.start();
        System.out.println(gameOver);
    }
    
    public void spawnFood() {
        Random r = new Random();
        comida.x = r.nextInt(width);
        if ((comida.x % 5) > 0) {
            comida.x = comida.x - (comida.x % 5);
        }
        if (comida.x < 5) {
            comida.x = comida.x + 10;
        }
        
        if (comida.x > width) {
            comida.x = comida.x - 10;
        }

        if ((comida.y % 5) > 0) {
            comida.y = comida.y - (comida.y % 5);
        }
        if (comida.y < 5) {
            comida.y = comida.y + 10;
        }
    }

    public void startGame() {
        comida = new Point(200, 200); // los parametros que no le pasamos, se los assignamos aqui...
        snake = new Point(width/2, height/2);
        lista = new ArrayList<Point>();
        lista.add(snake);
        lista.add(0,new Point(snake.x, snake.y));
        spawnFood();
    }

    public void snakeCollition() {
        if ((snake.x > (comida.x - 10)) && (snake.x < (comida.x + 10)) && (snake.y > (comida.y - 10)) && (snake.y < (comida.y + 10))) {
            lista.add(0,new Point(snake.x, snake.y));
            spawnFood();
        }
    }

    public void reload() {
        lista.add(0, new Point(snake.x, snake.y));
        lista.remove(lista.size()-1);
        // Comprueba si la cabeza de la serpiente(lista[0]) colisiono contra 
        // otro elemento de la lista, contando desde la posicion 1 hasta el numero
        // de posiciones en la lista, por que si contara desde 0, desde el inicio seria gameOver
        // por que nuestra cabeza, colisiona con nuestra cabeza... ese fue el error de ahorita
        for (int i=1;i<lista.size();i++) {
            Point p = lista.get(i);
            if (snake.x == p.x && snake.y == p.y) {
                System.out.println("Error, si toca");
                gameOver = true;
            }
        }
        snakeCollition();
        snake_body.repaint();
    }

    public void reset() {
        gameOver = false;
        startGame();       
    }

    // Main
    public static void main(String [] args) {
        Snake s = new Snake();
    }
    
    public class Imagenes extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (lista.size() > 0) {
                for (int i=0;i<lista.size();i++) {
                    Point p = (Point)lista.get(i);
                    g.fillRect(p.x, p.y, widthPoint, heightPoint);
                }
            }

            g.setColor(new Color(0, 0, 255));
            g.fillRect(snake.x, snake.y, widthPoint, heightPoint);

            g.setColor(new Color(255, 0, 0));
            g.fillRect(comida.x, comida.y, widthPoint, heightPoint);

            if (gameOver) {
                g.setColor(new Color(0, 0, 0));
                g.setFont(new Font("Consolas", Font.BOLD, 35));
                g.drawString("GAME OVER", 200, 200);
                g.drawString("SCORE: "+(lista.size()-1), 210, 240);

                g.setFont(new Font("Consolas", Font.BOLD, 20));
                g.drawString("Press R to start a new game, or press ESCAPE to quit", 20, 260);
            }

        }
    }

    // Event class
    public class Teclas extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    System.out.println("Goodbye!!!");
                    System.exit(0);
                    break;
                case KeyEvent.VK_UP:
                    if (direction != KeyEvent.VK_DOWN)
                        direction = KeyEvent.VK_UP;
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != KeyEvent.VK_UP)
                        direction = KeyEvent.VK_DOWN;
                    break;
                case KeyEvent.VK_LEFT:
                    if (direction != KeyEvent.VK_RIGHT)
                        direction = KeyEvent.VK_LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != KeyEvent.VK_LEFT)
                        direction = KeyEvent.VK_RIGHT;
                    break;
                case KeyEvent.VK_R:
                    reset();
                    break;
            }
        }
    }

    public class Moment extends Thread {
        long last = 0;

        public Moment() {
            
        }

        public void run() {
            while (true) {
                if ((java.lang.System.currentTimeMillis() - last) > fps) {
                    if (!gameOver) {
                        // 
                        /*  [ ATENCION ]
                         *  Estos evaluan si se ha rebasado los extremos del frame, y si es asi, son
                         *  re-direccionados hacia la posicion opuesta de esa parte del frame, con el
                         *  objetivo de dar la ilusion que traspasa esa barrera, y vuelve desde la 
                         *  posicion opuesta
                        */

                        // UP
                        if (direction == KeyEvent.VK_UP) {
                            snake.y = snake.y - heightPoint;
                            if (snake.y < 0) {
                                snake.y = height;   
                            }
                        }

                        // DOWN
                        if (direction == KeyEvent.VK_DOWN) {
                            snake.y = snake.y + heightPoint;
                            if (snake.y > height) {
                                snake.y = 0;
                            }
                        }

                        // RIGHT
                        if (direction == KeyEvent.VK_RIGHT) {
                            snake.x += widthPoint;
                            if (snake.x > width) {
                                snake.x = 0;
                            }
                        }

                        // LEFT
                        if (direction == KeyEvent.VK_LEFT) {
                            snake.x -= widthPoint;
                            if (snake.x < 0) {
                                snake.x = width - widthPoint;
                            }
                        }
                    }
                    reload();
                    last = java.lang.System.currentTimeMillis();
                }
            }       
        }
    }
}
