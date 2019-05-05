/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psp_ipr_2;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author dzmitry
 */
public class Window extends JFrame implements MouseListener, MouseMotionListener{
    
    private int windowWidth = 1000,
            windowHeight = 600,
            shipSpeed = 20,
            shipX = 0,
            shipY = 50,
            shipWidth = 200,
            shipHeight = 100,
            gunWidth = 100,
            gunHeight = 200,
            gunX = (windowWidth / 2) - (gunWidth / 2),
            gunY = windowHeight - gunHeight,
            torpedoSpeed = 20,
            torpedoWidth = 50,
            torpedoHeight = 50,
            torpedoX = (windowWidth / 2) - (torpedoWidth / 2),
            torpedoY = windowHeight - gunHeight;

    private static Image background;
    
    private static Image ship;
    private static Image torpedo;
    private static Image gun;
    private static Image fire;
    
    private static boolean hit = false;
    
    JButton bt;

    public Window() {
        setTitle("Морской бой");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(
            this.windowWidth, 
            this.windowHeight
        );
        this.shipSpeed = ThreadLocalRandom.current().nextInt(0, 30);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(new Background());
        Container content = getContentPane();

        bt = new JButton("Старт");
        bt.setPreferredSize(new Dimension(1000,50));
        bt.setBackground(Color.white);
        bt.setForeground(Color.BLACK);
        bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bt.setVisible(false);
                Thread shipMove = new Thread(new ShipThread());
                shipMove.start();
            }
        });
        content.add(bt);
        content.add(new Painter());
        
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        int x = me.getX();
        int y = me.getY();
        
        if (x >= gunX && x <= (gunX + gunWidth) && y >= gunY) {
            Thread torpedoMove = new Thread(new TorpedoThread());
            torpedoMove.start();
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    private static class Background extends JPanel{ // отрисовка нового фона

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            try {
                background = ImageIO.read(new File("/home/dzmitry/NetBeansProjects/psp_ipr_2/src/ocean.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.drawImage(background, 0, 0, null);
        }
    }

    private class Painter extends JPanel{

        public Painter() {
            setOpaque(false);
            setPreferredSize(new Dimension(windowWidth, windowHeight));
            try {
                ship = ImageIO.read(new File("/home/dzmitry/NetBeansProjects/psp_ipr_2/src/ship.jpeg"));
                torpedo= ImageIO.read(new File("/home/dzmitry/NetBeansProjects/psp_ipr_2/src/core.jpg"));
                gun =  ImageIO.read(new File("/home/dzmitry/NetBeansProjects/psp_ipr_2/src/gun.jpg"));
                fire =  ImageIO.read(new File("/home/dzmitry/NetBeansProjects/psp_ipr_2/src/fire.png"));
            }
            catch (IOException exc) {};

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics2D = (Graphics2D)g;
            
            if (!isHit()) {
                graphics2D.drawImage(ship, shipX, shipY, shipWidth, shipHeight, this);
                graphics2D.drawImage(torpedo, torpedoX, torpedoY, torpedoWidth, torpedoHeight, this);
            } else {
                graphics2D.drawImage(fire, shipX, shipY, shipWidth, shipHeight, this);
                hit = false;
            }
            
            graphics2D.drawImage(gun, gunX, gunY, gunWidth, gunHeight, this);
        }
    }

    public class ShipThread implements Runnable{
        @Override
        public void run() {
            while (shipX < windowWidth) {
                shipX += shipSpeed;
                repaint();
                try {
                    Thread.sleep(130);
                }
                catch (Exception exc) {};
                
                if (shipX >= windowWidth) {
                    shipX = 0;
                    shipX -= shipWidth;
                    shipSpeed = ThreadLocalRandom.current().nextInt(0, 50);
                }
            }
            
        }
    }
    
    public boolean isHit() {
                
        if (this.hit) {
            return true;
        }
        
        if (torpedoY <= shipY + (shipHeight / 2) 
                && (torpedoX >= shipX && torpedoX <= (shipX + shipWidth))) {
            this.hit = true;
        }
        
        return this.hit;
    }

    public class TorpedoThread implements Runnable{
        @Override
        public void run() {
            while (torpedoY >= 0) {
                torpedoY -= torpedoSpeed;
                
                repaint();
                try {
                    Thread.sleep(130);
                }
                catch (Exception exc) {};
            }
                        
            if (torpedoY <= 0) {
                torpedoY = windowHeight - gunHeight;
                Thread.currentThread().interrupt();
            }
        }
    }
}
