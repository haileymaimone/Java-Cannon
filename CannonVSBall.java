package CannonVSBall;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

// Ricky Peddicord, Hailey Maimone, Kelli Huff
// Group 1
// CET 350
// ped8697@calu.edu, mai5013@calu.edu, huf2203@calu.edu


public class CannonVSBall implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable, MouseListener, MouseMotionListener, ItemListener {
    private static final long serialVersionUID = 21L; // serial version ID

    // declare constants
    private final int BUTTONH = 20; // button height
    private final int ANGLE = 45; // initial speed
    private final int VELOCITY = 155;
    private final int SBvisible = 10; // visible Scroll Bar
    private final int SBunit = 1; // Scroll Bar unit step size
    private final int SBblock = 10; // Scroll Bar block step size
    private final int SOBJ = 37; // initial object width

    // declare other variables
    private int WinTop = 10; // top of frame
    private int WinLeft = 10; // left side of frame
    private int BUTTONW = 50; // initial button width
    private Insets I; // insets of frame
    private int SObj = SOBJ; // initial object width
    private int angleMin = 0; // speed scrollbar minimum value
    private int angleMax = 90 + SBvisible; // speed scrollbar maximum value with visible offset
    private int angleInit = ANGLE; // initial speed scrollbar value
    private int velocityMin = 100; // "The initial muzzle velocity should be between 100 and 1200 ft/sec"
    private int velocityMax = 1200 + SBvisible;
    private int velocityInit = VELOCITY;
    private int ScrollBarW; // scrollbar width
    private Ball ball; // object to draw
    private int velocityInt = VELOCITY;
    private int angleInt = ANGLE;
    private Label velocityLabel = new Label("Initial Velocity: " + velocityInt); // label for speed scroll bar
    private Label angleLabel = new Label("Angle: " + angleInt); // label for size scroll bar
    private boolean running; // boolean for run method
    private boolean TimePause; // boolean for pause mode
    private int speed; // int for scrollbar speed
    private int i; // loop counter integer
    private int delay = 3; // int for timer delay
    private boolean ok; // boolean flag for while loop
    private Timer timer = new Timer(); // timer for generating program runtime
    private Panel sheet = new Panel(); // panel for drawing canvas
    private Panel control = new Panel(); // panel for button area
    GridBagLayout gbl = new GridBagLayout(); // GridBagLayout for the control panel
    GridBagConstraints con = new GridBagConstraints(); // create GridBagConstraints

    private Point FrameSize = new Point(740, 500); // initial frame size
    private Point Screen = new Point(FrameSize.x - 1, FrameSize.y - 1); // drawing screen size
    private Point m1 = new Point(0, 0); // first mouse point
    private Point m2 = new Point(0, 0); // second mouse point
    private Rectangle Perimeter = new Rectangle(0, 0, Screen.x, Screen.y); // bouncing perimeter
    private Rectangle db = new Rectangle(); // drag box rectangle

    private Frame theFrame = new Frame("Group 1 - Cannon VS Ball Program"); // create the frame and give it a title
    private MenuBar MB = new MenuBar(); // create the menubar
    private Menu controlsMenu = new Menu("Control"); // create a menu for controls
    private MenuItem Runm, Restartm, Pausem, Quitm; // create menu items
    private Menu parametersMenu = new Menu("Parameters"); // create a menu for parameters
    private Menu environmentsMenu = new Menu("Environments"); // create a menu for environments
    private Menu sizeCheck, speedCheck; // create menu's for size and speed
    private CheckboxMenuItem xsm, sm, md, lg, xlg; // create checkbox items for size
    private CheckboxMenuItem xsl, sl, mds, fst, xfst; // create checkbox items for speed
    private CheckboxMenuItem p1, p2, p3, p4, p5, p6, p7, p8, p9, moon; // create checkbox items for planets
    private CheckboxMenuItem prev; // create checkbox item placeholder
    private long time = 0; // long for holding runtime, initialized to 0
    private boolean noResize = false; // boolean to indicate resize status, initialized to false
    private boolean shootProj = false; // boolean to indicate whether to move the projectile, initialized to false
    private boolean projReturn = false;
    private Label timeLabel = new Label("Time: " + time); // label for displaying program runtime
    private Label ballLabel = new Label("Ball: " + 0); // label for displaying ball score
    private Label playerLabel = new Label("Player: " + 0); // label for displaying player score
    private Label boundsLabel = new Label(""); // label for displaying boundary status, initially empty

    Thread thethread; // thread for timer delay
    Scrollbar velocityScrollBar, angleScrollBar; // scroll bars
    Button Start, Stop, Quit; // buttons

    CannonVSBall() {
        shootProj=false;
        speed = 9;
        MakeSheet(); // Determine the sizes for the sheet
        try {
            initComponents(); // try to initialize the components
        } catch (Exception e) {
            e.printStackTrace();
        }
        SizeScreen(); // size the screen
        start(); // start the thread
    }
    public static void main(String[] args) {
        CannonVSBall b = new CannonVSBall(); // create an object
    }

    private void MakeSheet() { // gets the insets and adjusts the sizes of the items
        I = theFrame.getInsets();
        Screen.x = FrameSize.x - I.left - I.right;
        Screen.y = FrameSize.y - I.top - I.bottom - 2 * BUTTONH;
        theFrame.setSize(FrameSize.x, FrameSize.y);
        BUTTONW = Screen.y / 11; // determine the width of the buttons (11 units)
        ScrollBarW = 2 * BUTTONW; // determine the scroll bar width
        theFrame.setBackground(Color.lightGray);
    }

    public void SizeScreen() {
        ball.setBounds(I.left, I.top, Screen.x, Screen.y);
    }

    public void initComponents() throws Exception, IOException {
        calcTime();
        prev = sm;
        Start = new Button("Run"); // create the start button
        Stop = new Button("Pause"); // create the stop button
        Quit = new Button("Quit"); // create the quick button
        Start.setEnabled(false); // do we start in run mode or pause mode?
        Stop.setEnabled(true);
        velocityScrollBar = new Scrollbar(Scrollbar.HORIZONTAL); // create the speed scroll bar
        velocityScrollBar.setMaximum(velocityMax); // set the max speed
        velocityScrollBar.setMinimum(velocityMin); // set the min speed
        velocityScrollBar.setUnitIncrement(SBunit); // set the unit increment
        velocityScrollBar.setBlockIncrement(SBblock); // set the block increment
        velocityScrollBar.setValue(velocityInit); // set the initial value
        velocityScrollBar.setVisibleAmount(SBvisible); // set the visible size
        velocityScrollBar.setBackground(Color.gray); // set the background color
        angleScrollBar = new Scrollbar(Scrollbar.HORIZONTAL); // create the size scroll bar
        angleScrollBar.setMaximum(angleMax); // set the max speed
        angleScrollBar.setMinimum(angleMin); // set the min speed
        angleScrollBar.setUnitIncrement(SBunit); // set the unit increment
        angleScrollBar.setBlockIncrement(SBblock); // set the block increment
        angleScrollBar.setValue(angleInit); // set the initial value
        angleScrollBar.setVisibleAmount(SBvisible); // set the visible size
        angleScrollBar.setBackground(Color.gray); // set the background color
        velocityScrollBar.addAdjustmentListener(this); // add the speed scroll bar listener
        angleScrollBar.addAdjustmentListener(this); // add the size scroll bar listener

        m1.setLocation(0, 0);
        m2.setLocation(0, 0);
        theFrame.setLayout(new BorderLayout()); // layout border
        theFrame.setBounds(WinLeft, WinTop, FrameSize.x, FrameSize.y); // set frame bounds
        theFrame.setBackground(Color.lightGray); // set frame background color

        Runm = controlsMenu.add(new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R))); // add run button with shortcut to controls menu
        controlsMenu.addSeparator(); // add a separator
        Pausem = controlsMenu.add(new MenuItem("Pause", new MenuShortcut(KeyEvent.VK_P))); // add pause button with shortcut to controls menu
        controlsMenu.addSeparator(); // add a separator
        Restartm = controlsMenu.add(new MenuItem("Restart")); // add restart button to controls menu
        controlsMenu.addSeparator(); // add a separator
        Quitm = controlsMenu.add(new MenuItem("Quit")); // add quit button to controls menu
        sizeCheck = new Menu("Size"); // create size menu
        // create and add checkbox menu items for size
        sizeCheck.add(xsm = new CheckboxMenuItem("X-Small"));
        sizeCheck.add(sm = new CheckboxMenuItem("Small"));
        sizeCheck.add(md = new CheckboxMenuItem("Medium"));
        sizeCheck.add(lg = new CheckboxMenuItem("Large"));
        sizeCheck.add(xlg = new CheckboxMenuItem("X-Large"));
        sm.setState(true); // set the initial state to be small
        speedCheck = new Menu("Speed"); // create speed menu
        // create and add checkbox menu items for speed
        speedCheck.add(xsl = new CheckboxMenuItem("X-Slow"));
        speedCheck.add(sl = new CheckboxMenuItem("Slow"));
        speedCheck.add(mds = new CheckboxMenuItem("Medium"));
        speedCheck.add(fst = new CheckboxMenuItem("Fast"));
        speedCheck.add(xfst = new CheckboxMenuItem("X-Fast"));
        mds.setState(true); // set the initial state to be medium
        parametersMenu.add(sizeCheck); // add size menu to parameters menu
        parametersMenu.add(speedCheck); // add speed menu to parameters menu
        // create and add checkbox menu items for environments
        p1 = new CheckboxMenuItem("Mercury");
        p2 = new CheckboxMenuItem("Venus");
        p3 = new CheckboxMenuItem("Earth");
        moon = new CheckboxMenuItem("Moon");
        p4 = new CheckboxMenuItem("Mars");
        p5 = new CheckboxMenuItem("Jupiter");
        p6 = new CheckboxMenuItem("Saturn");
        p7 = new CheckboxMenuItem("Uranus");
        p8 = new CheckboxMenuItem("Neptune");
        p9 = new CheckboxMenuItem("Pluto");
        environmentsMenu.add(p1);
        environmentsMenu.add(p2);
        environmentsMenu.add(p3);
        environmentsMenu.add(moon);
        environmentsMenu.add(p4);
        environmentsMenu.add(p5);
        environmentsMenu.add(p6);
        environmentsMenu.add(p7);
        environmentsMenu.add(p8);
        environmentsMenu.add(p9);
        p3.setState(true); // set initial state to Earth
        theFrame.setMenuBar(MB); // add the frame to the menubar
        theFrame.setVisible(true); // make the frame visible
        MB.add(controlsMenu); // add the controls menu to the menubar
        MB.add(parametersMenu); // add the parameters menu to the menubar
        MB.add(environmentsMenu); // add the environments menu to the menubar

        // setup GridBagLayout for labels and scrollbars
        control.setLayout(gbl);
        con.gridx = 0;
        con.gridy = 0;
        con.weightx = 0.5;
        con.weighty = 0.5;
        con.ipadx = ScrollBarW / 2;
        con.anchor = GridBagConstraints.LINE_START;
        con.insets = new Insets(0, 30, 0, 0);
        gbl.setConstraints(velocityScrollBar, con);
        control.add(velocityScrollBar);
        con.gridx = GridBagConstraints.RELATIVE;
        con.anchor = GridBagConstraints.CENTER;
        con.ipadx = BUTTONW / 2;

        con.insets = new Insets(0, 90, 0, 0);
        con.weightx = 0;
        con.weighty = 0;
        gbl.setConstraints(timeLabel, con);
        control.add(timeLabel);
        con.insets = new Insets(0, 0, 0, 40);
        gbl.setConstraints(ballLabel, con);
        control.add(ballLabel);
        con.insets = new Insets(0, -40, 0, 90);
        gbl.setConstraints(playerLabel, con);
        control.add(playerLabel);
        con.weightx = 0.5;
        con.weighty = 0.5;
        con.anchor = GridBagConstraints.LINE_END;
        con.ipadx = ScrollBarW / 2;
        con.insets = new Insets(0, 0, 0, 30);
        gbl.setConstraints(angleScrollBar, con);
        control.add(angleScrollBar);
        con.gridx = 0;
        con.gridy = 1;
        con.anchor = GridBagConstraints.LINE_START;
        con.insets = new Insets(0, 27, 0, 0);
        gbl.setConstraints(velocityLabel, con);
        control.add(velocityLabel);
        con.insets = new Insets(0, 140, 0, 0);
        con.gridx = 1;
        con.gridwidth = 2;
        con.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(boundsLabel, con);
        control.add(boundsLabel);
        con.fill = GridBagConstraints.NONE;
        con.anchor = GridBagConstraints.LINE_END;
        con.gridx = 4;
        con.insets = new Insets(0, 0, 0, -3);
        gbl.setConstraints(angleLabel, con);
        control.add(angleLabel);



        control.setBackground(Color.lightGray); // set the control layout background color
        control.setSize(FrameSize.x, 2 * BUTTONH); // size control
        control.setVisible(true); // set control layout to visible

        sheet.setLayout(new BorderLayout(0, 0)); // sheet border layout;
        ball = new Ball(SObj, Screen); // create with a ball size and drawing size
        ball.setBackground(Color.lightGray); // set canvas background color
        sheet.add("Center", ball); // add the canvas object to sheet
        sheet.setVisible(true); // set the sheet layout to visible
        I = theFrame.getInsets(); // get the insets
        Perimeter.setBounds(0, 0, Screen.x - I.left - I.right - 1, Screen.y -  I.top - I.bottom - 8); // set the perimeter bounds
        Perimeter.grow(-1, -1); // grow the perimeter
        theFrame.add("Center", sheet); // add sheet panel to center of BorderLayout
        theFrame.add("South", control); // add control panel to south of BorderLayout
        // add listeners
        ball.addMouseMotionListener(this);
        ball.addMouseListener(this);
        Runm.addActionListener(this);
        Pausem.addActionListener(this);
        Restartm.addActionListener(this);
        Quitm.addActionListener(this);
        xsm.addItemListener(this);
        sm.addItemListener(this);
        md.addItemListener(this);
        lg.addItemListener(this);
        xlg.addItemListener(this);
        xsl.addItemListener(this);
        sl.addItemListener(this);
        mds.addItemListener(this);
        fst.addItemListener(this);
        xfst.addItemListener(this);
        p1.addItemListener(this);
        p2.addItemListener(this);
        p3.addItemListener(this);
        moon.addItemListener(this);
        p4.addItemListener(this);
        p5.addItemListener(this);
        p6.addItemListener(this);
        p7.addItemListener(this);
        p8.addItemListener(this);
        p9.addItemListener(this);
        theFrame.addComponentListener(this);
        theFrame.addWindowListener(this);
        TimePause = false; // set pause mode to false
        running = true; // set run mode to true
        theFrame.validate(); // validate the layout
    }

    public void start() {
        if (thethread == null) { // create a thread if it does not exist
            thethread = new Thread(this); // create a new thread
            thethread.start(); // start the thread
            ball.repaint(); // force a repaint
        }
    }

    public void stop() {
        // set running flag to false, interrupt the thread
        // remove all listeners and exit
        running = false;
        timer.cancel();
        thethread.interrupt();
        Runm.removeActionListener(this);
        Pausem.removeActionListener(this);
        Restartm.removeActionListener(this);
        Quitm.removeActionListener(this);
        xsm.removeItemListener(this);
        sm.removeItemListener(this);
        md.removeItemListener(this);
        lg.removeItemListener(this);
        xlg.removeItemListener(this);
        xsl.removeItemListener(this);
        sl.removeItemListener(this);
        mds.removeItemListener(this);
        fst.removeItemListener(this);
        xfst.removeItemListener(this);
        p1.removeItemListener(this);
        p2.removeItemListener(this);
        p3.removeItemListener(this);
        moon.removeItemListener(this);
        p4.removeItemListener(this);
        p5.removeItemListener(this);
        p6.removeItemListener(this);
        p7.removeItemListener(this);
        p8.removeItemListener(this);
        p9.removeItemListener(this);
        velocityScrollBar.removeAdjustmentListener(this);
        angleScrollBar.removeAdjustmentListener(this);
        ball.removeMouseMotionListener(this);
        ball.removeMouseListener(this);
        theFrame.removeComponentListener(this);
        theFrame.removeWindowListener(this);
        theFrame.dispose();
        System.exit(0);
    }

    public void run() {
        int counter = 0; // create and initialize a counter to 0
        while (running) {
            counter = counter + 3; // increment counter by multiple of delay on each loop iteration
            if (!TimePause) { //// if the program isn't paused
                ball.Size(); // apply new size
                try {
                    Thread.sleep(delay); // try to sleep the thread for the new speed delay
                } catch (InterruptedException e) {
                }
                ball.repaint(); // force a repaint
                if (counter % speed == 0) { // if counter is a multiple of speed value
                    ball.move(); // move the object
                    if (ball.ballCollideCannon()) { // if the ball collides with the cannon
                        ballLabel.setText("Ball: " + ball.getBallScore()); // update the label for the ball score
                        ball.initialPos(); // reset the ball's position
                        shootProj = false; // stop moving the projectile
                    }
                }
                ball.collisionSide(); // check for a collision
                if (shootProj) { // if the projectile is moving
                    ball.moveProj(); // continue moving it
                    if(ball.projCollideCannon()) { // if the projectile collides with the cannon
                        ballLabel.setText("Ball: " + ball.getBallScore()); // update the label for the ball score
                        shootProj = false; // stop moving the projectile
                    }
                    if (ball.projCollideBall()) { // if the projectile collides with the ball
                        playerLabel.setText("Player: " + ball.getPlayerScore()); // update the label for the ball score
                        ball.initialPos(); // reset the ball's position
                        shootProj = false; // stop moving the projectile
                    }
                    if (shootProj) projBounds(); // if the projectile had no collisions up to now, check if the projectile is in bounds
                    if (ball.projCollideRect()) shootProj = false; // if the projectile collides with a rectangle, stop moving the projectile
                    if (ball.inBounds()) { // if projectile is in bounds
                        boundsLabel.setText("Within Bounds"); // set bounds label to indicate it is in bounds
                    } else {
                        boundsLabel.setText("Out of Bounds"); // otherwise set bounds label to indicate it is out of bounds
                    }
                }
            }
            try {
                Thread.sleep(1); // try to sleep the thread for 1 ms so that the loop
                // has a chance to be interrupted
            } catch (InterruptedException exception) {

            }
        }
    }

    public void projBounds() {
            Rectangle projRect = new Rectangle(ball.getProjx(), ball.getProjy(), 17, 17); // get rectangle of the projectile
            if (Perimeter.contains(projRect)) { // if rectangle is within the perimeter
                ball.setShoot(true); // set the boolean to true to continue drawing the projectile
            } else {
                ball.setShoot(false); // otherwise set the boolean to false to stop drawing the projectile
            }
        shootProj = ball.getProjx() > 0 && ball.getProjy() < Screen.y - 17; // if ball is within bounds, continue moving it
        // otherwise stop moving it
    }

    public void calcTime() { // method to update time label to display current runtime
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                time++;
                timeLabel.setText("Time: " + time);
            }
        }, 0, 1000);
    }

    public void resetGame() { // reset game to default state
        shootProj=false;
        SObj = 37;
        speed = 9;
        xsm.setState(false);
        sm.setState(true);
        md.setState(false);
        lg.setState(false);
        xlg.setState(false);
        xsl.setState(false);
        sl.setState(false);
        mds.setState(true);
        fst.setState(false);
        xfst.setState(false);
        p1.setState(false);
        p2.setState(false);
        p3.setState(true);
        moon.setState(false);
        p4.setState(false);
        p5.setState(false);
        p6.setState(false);
        p7.setState(false);
        p8.setState(false);
        p9.setState(false);
        velocityInit = VELOCITY;
        angleInit = ANGLE;
        velocityScrollBar.setValue(velocityInit);
        velocityLabel.setText("Initial Velocity: " + velocityInit);
        ball.setVelocity(velocityInit);
        angleScrollBar.setValue(angleInit);
        angleLabel.setText("Angle: " + angleInit);
        ball.setAngle(angleInit);
        ball.setBallScore(0);
        ball.setPlayerScore(0);
        ballLabel.setText("Ball: " + ball.getBallScore());
        playerLabel.setText("Player: " + ball.getPlayerScore());
        boundsLabel.setText("");
        ball.initialPos();
        ball.clearWalls();
    }

    public void checkObjSize() {
        int x = ball.getX(); // get current object x
        int y = ball.getY(); // get current object y
        int obj = ball.getObjSize(); // get current object size
        int right = x + (obj - 1 / 2) + 1; // right-side object calculation
        int bottom = y + (obj - 1 / 2) + 1; // bottom-side object calculation
        if (right > Screen.x) { // check if object will leave right-side of screen
            ball.setX(Screen.x - (obj - 1 / 2) - 2); // if it does, reposition x
        }

        if (bottom > Screen.y) { // check if object will leave bottom of screen
            ball.setY(Screen.y - (obj - 1/ 2) - 2); // if it does, reposition y
        }
    }
    public void componentResized(ComponentEvent e) {
        FrameSize.x = theFrame.getWidth(); // get current frame width
        FrameSize.y = theFrame.getHeight(); // get current frame height

        Rectangle r = new Rectangle(); // create new rectangle called r
        Rectangle b = new Rectangle(ball.getX() - (SObj - 1) / 2, ball.getY() - (SObj - 1) / 2, SObj, SObj);
        // create a rectangle copy of the ball
        int mr = 0; // maximum right integer
        int mb = 0; // maximum bottom integer
        MakeSheet(); // make the sheet
        I = sheet.getInsets(); // get the canvas' insets
        checkObjSize(); // make sure the object will be inside of the screen on resize
        SizeScreen(); // size the canvas
        I = theFrame.getInsets(); // get the frame's insets
        if (ball.getWallSize() != 0) { // as long as the rectangle vector isn't empty
            r.setBounds(ball.getOne(0)); // get 0th rectangle
            mr = r.x + r.width; // initialize max right
            mb = r.y + r.height; // initialize max bottom

            for (int i = 0; i < ball.getWallSize(); i++) {
                r.setBounds(ball.getOne(i)); // get ith rectangle
                mr = Math.max((r.x + r.width), mr); // keep max right
                mb = Math.max((r.y + r.height), mb); // keep max bottom
            }

            r.setBounds(b); // process the ball
            mr = Math.max((r.x + r.width), mr); // keep max right
            mb = Math.max((r.y + r.height), mb); // keep max bottom
            if (mr > Screen.x || mb > Screen.y) { // if max right or max bottom is greater than current screen width or screen height
                theFrame.setSize(Math.max((mr + 5), Screen.x) + I.left + I.right, Math.max((mb + 5), Screen.y) + I.top + I.bottom + 2 * BUTTONH);
                // set the new frame size
                theFrame.setExtendedState(Frame.ICONIFIED); // set extended state iconified
                theFrame.setExtendedState(Frame.NORMAL); // set extended state normal
            }
        }
        Screen.setLocation(sheet.getWidth() - 1 , sheet.getHeight() - 1); // update the screen point
        Perimeter.setBounds(ball.getBounds()); // update the perimeter rectangle
        Perimeter.width = Screen.x;
        Perimeter.height = Screen.y;
        Perimeter.grow(-1, -1); // shrink the perimeter rectangle by -1 all around
        ball.reSize(Screen); // resize the ball screen
        ball.repaint(); // repaint
    }

    public void setPlanet() {
        // set projectile gravity depending on which planet checkbox menu item is selected
        if (p1.getState()) {
            ball.setGravity(3.7);
        }
        if (p2.getState()) {
            ball.setGravity(8.87);
        }
        if (p3.getState()) {
            ball.setGravity(9.8);
        }
        if (moon.getState()) {
            ball.setGravity(1.62);
        }
        if (p4.getState()) {
            ball.setGravity(3.71);
        }
        if (p5.getState()) {
            ball.setGravity(24.92);
        }
        if (p6.getState()) {
            ball.setGravity(10.44);
        }
        if (p7.getState()) {
            ball.setGravity(8.87);
        }
        if (p8.getState()) {
            ball.setGravity(11.15);
        }
        if (p9.getState()) {
            ball.setGravity(0.58);
        }
    }

    public void setSize(int size) { // set ball size depending on which size checkbox menu item is selected
        int objSize = ball.getObjSize();
        int TS = size;
        TS = (TS / 2) * 2 + 1; // Make odd to account for center position
        int half = (TS - 1) / 2; // half the size of the ball
        noResize = false;
        if (objSize == 21) {
            prev = xsm;
        }
        if (objSize == 37) {
            prev = sm;
        }
        if (objSize == 55) {
            prev = md;
        }
        if (objSize == 73) {
            prev = lg;
        }
        if (objSize == 91) {
            prev = xlg;
        }
        Rectangle t;
        Rectangle b = new Rectangle(ball.getX() - half - 1, ball.getY() - half - 1, TS + 2, TS + 2);
        // rectangle copy of the ball
        if (b.equals(Perimeter.intersection(b))) { // if the ball is inside of the perimeter
            i = 0;
            ok = true;
            while ((i < ball.getWallSize()) && ok) {
                t = ball.getOne(i); // get a rectangle from the vector
                if (t.intersects(b)) { // if the rectangle intersects with the ball's new size
                    ok = false; // set boolean to false
                } else { // otherwise increment
                    i++; // increment i
                }
            }
        }
        if (ok && ball.checkSize(TS) || size < objSize) { // if no intersection with the new ball size
            ball.newSize(TS); // set new ball size
            ball.Size(); // size the ball
            noResize = false;
        } else {
            noResize = true;
        }
        ball.repaint(); // force a repaint
    }

    public void setSpeed() { // set the ball's speed depending on which speed checkbox menu item is selected
        if (xsl.getState()) {
            speed = 15;
        }
        if (sl.getState()) {
            speed = 12;
        }
        if (mds.getState()) {
            speed = 9;
        }
        if (fst.getState()) {
            speed = 6;
        }
        if (xfst.getState()) {
            speed = 3;
        }
        thethread.interrupt(); // interrupt the thread
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source == Runm) { // if start button is clicked
            TimePause = false; // set run mode flag
            thethread.interrupt(); // interrupt the thread
        }

        if (source == Pausem) { // if pause button is clicked
            TimePause = true; // set pause mode flag
            thethread.interrupt(); // interrupt the thread
        }

        if (source == Restartm) {
            resetGame();
            thethread.interrupt();
        }

        if (source == Quitm) { // if quit button is clicked
            stop(); // stop the program and exit
        }
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        Scrollbar sb = (Scrollbar)e.getSource(); // get the scrollbar that triggered the event
        if (sb == velocityScrollBar) { // if velocity scrollbar is triggered
            ball.setVelocity((double) sb.getValue()); // set the projectile velocity to the scrollbar value
            velocityInt = sb.getValue(); // set the velocity integer to the scrollbar value
            velocityLabel.setText("Initial Velocity: " + velocityInt); // update the velocity label to have the new velocity value

        }

        if (sb == angleScrollBar) { // if angle scrollbar is triggered
            ball.setAngle((double) sb.getValue()); // set the cannon angle to the scrollbar value
            angleInt = sb.getValue(); // set the angle integer to the scrollbar value
            angleLabel.setText("Angle: " + angleInt); // update the angle label to have the new angle value
        }
        ball.repaint(); // force a repaint
    }

    public void componentHidden(ComponentEvent e) {

    }

    public void componentShown(ComponentEvent e) {

    }

    public void componentMoved(ComponentEvent e) {

    }

    public void windowClosing(WindowEvent e) {
        stop();
    }

    public void windowClosed(WindowEvent e) {

    }

    public void windowOpened(WindowEvent e) {

    }

    public void windowActivated(WindowEvent e) {

    }

    public void windowDeactivated(WindowEvent e) {

    }

    public void windowIconified(WindowEvent e) {

    }

    public void windowDeiconified(WindowEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
        Point p = new Point(e.getX(), e.getY()); // get the current mouse clicked point
        if (e.getClickCount() == 2) { // if click count is 2, or a double-click
            Rectangle b;
            i = 0;
            while ((i < ball.getWallSize())) {
                b = ball.getOne(i); // get a vector rectangle
                if (b.contains(p)) { // if the rectangle contains the clicked point
                    ball.removeOne(i); // delete it from the vector
                } else { // otherwise
                    i++; // increment i
                }
            }
        }

        Rectangle cannonCirc = new Rectangle(Screen.x - 60, Screen.y - 60, 55, 55); // get rectangle of the circle portion of the cannon
        if ((cannonCirc.contains(p) || ball.getPoly().getBounds().contains(p)) && !shootProj) { // if the point is inside of either part of the cannon
            // and the projectile is not currently in motion
            ball.gravityCalc(); // calculate current gravity and initiate shooting the projectile
            shootProj = true; // start moving the projectile
        }
        ball.repaint(); // force a repaint
    }

    public void mousePressed(MouseEvent e) {
        m1.setLocation(e.getPoint()); // set mouse point 1 location to current pressed location
    }

    public void mouseReleased(MouseEvent e) {
        Rectangle b = new Rectangle(ball.getX() - (SObj - 1) / 2, ball.getY() - (SObj - 1) / 2, SObj, SObj);
        // create a rectangle copy of the ball
        b.grow(1, 1); // grow the ball by 1 all around
        Rectangle ZERO = new Rectangle(0, 0, 0, 0); // create a zero rectangle
        Rectangle r = getDragBox(e); // get the current drawn rectangle
        Rectangle vect; // rectangle representing a rectangle from the vector
        Rectangle cannonSpace = new Rectangle(Screen.x - 60 - 65, Screen.y - 60 - 65, 122, 122);
        Rectangle projRect = new Rectangle(ball.getProjx(), ball.getProjy(), 17, 17);

        i = 0; // index integer
        boolean store = true; // boolean representing if we should store the drawn rectangle

        if (!Perimeter.contains(r)) { // if the rectangle leaves the screen
            r = Perimeter.intersection(r); // push it back within the perimeter
        }
        if (r.intersects(b)) { // if the rectangle intersects the ball
            store = false; // we won't stop the rectangle in the vector
        }
        if (r.intersects(cannonSpace)) { // if the rectangle intersects the cannon's space
            store = false; // don't store it in the vector
        }
        if (r.intersects(projRect)) { // if the rectangle intersects the projectile
            store = false; // don't store it in the vector
        }

        while ((i < ball.getWallSize()) && store) {
            vect = ball.getOne(i); // get a rectangle from the vector
            if (r.intersection(vect).equals(r)) { // if the new rectangle is covered by any rectangle in the vector
                store = false; // don't store the new rectangle
            }
            if (r.intersection(vect).equals(vect)) { // if the new rectangle covers any rectangle in the vector
                ball.removeOne(i); // delete that rectangle from the vector
            } else { // otherwise
                i++; // increment i
            }
        }
        if (store) { // if the store boolean is true
            ball.addOne(r); // then we store the drawn rectangle
        }
        ball.setDragBox(ZERO); // delete the drawn rectangle because it either is covered by the stored rectangle
        // or were not storing it so it should be removed anyways
        ball.repaint(); // force a repaint
    }

    public void mouseEntered(MouseEvent e) {
        ball.repaint(); // force a repaint
    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
        db.setBounds(getDragBox(e)); // set db the bounds of the drawn rectangle
        if (Perimeter.contains(db)) { // if db is within the perimeter of the screen
            ball.setDragBox(db); // send it to the canvas to be drawn
            ball.repaint(); // force a repaint
        }
    }

    public Rectangle getDragBox(MouseEvent e) {
        Rectangle dragRect = new Rectangle(); // make a new rectangle
        m2.setLocation(e.getPoint()); // get second mouse point location
        // mathematical functions to determine dragbox coordinates to draw the rectangle
        int x = Math.min(m1.x, m2.x);
        int y = Math.min(m1.y, m2.y);
        int width = Math.max(m1.x - m2.x, m2.x - m1.x);
        int height = Math.max(m1.y - m2.y, m2.y - m1.y);
        dragRect.setBounds(x, y, width, height); // set the bounds of the drawn rectangle
        return dragRect; // return the rectangle
    }

    public void mouseMoved(MouseEvent e) {

    }

    public void itemStateChanged(ItemEvent e) {
        CheckboxMenuItem checkbox = (CheckboxMenuItem) e.getSource();
        if (checkbox == xsm || checkbox == sm || checkbox == md || checkbox == lg || checkbox == xlg) {
            // when a size checkbox menu item is selected
            // set all size checkbox menu item's to false
            xsm.setState(false);
            sm.setState(false);
            md.setState(false);
            lg.setState(false);
            xlg.setState(false);
            // set the ball's new size to the selected checkbox menu item's size value
            if (checkbox == xsm) {
                setSize(21);
            }
            if (checkbox == sm) {
                setSize(37);
            }
            if (checkbox == md) {
                setSize(55);
            }
            if (checkbox == lg) {
                setSize(73);
            }
            if (checkbox == xlg) {
                setSize(91);
            }
            if (noResize) {
                // noResize returns true
                checkbox.setState(false); // set selected checkbox menu item's state to false
                prev.setState(true); // set the prev state to true, or the previous state it was in
            } else {
                checkbox.setState(true); // otherwise set the checkbox menu item's state to true
            }

        }
        if (checkbox == xsl || checkbox == sl || checkbox == mds || checkbox == fst || checkbox == xfst) {
            // when a speed checkbox menu item is selected
            // set all speed checkbox menu item's to false
            xsl.setState(false);
            sl.setState(false);
            mds.setState(false);
            fst.setState(false);
            xfst.setState(false);
            checkbox.setState(true); // set the selected checkbox menu item's state to true
            setSpeed(); // set the ball's new speed
        }
        if (checkbox == p1 | checkbox == p2 || checkbox == p3 || checkbox == moon || checkbox == p4 || checkbox == p5 || checkbox == p6 || checkbox == p7 || checkbox == p8 || checkbox == p9) {
            // when a planet checkbox menu item is selected
            // set all planet checkbox menu item's to false
            p1.setState(false);
            p2.setState(false);
            p3.setState(false);
            moon.setState(false);
            p4.setState(false);
            p5.setState(false);
            p6.setState(false);
            p7.setState(false);
            p8.setState(false);
            p9.setState(false);
            checkbox.setState(true); // set the selected checkbox menu item's state to true
            setPlanet(); // set the projectile's new gravity
        }
    }
}

class Ball extends Canvas {
    private static final long serialVersionUID = 26L; // serial version uid
    private static final Rectangle ZERO = new Rectangle(0, 0, 0, 0); // zero rectangle
    private Point Screen; // point for height and width of screen
    private int SObj; // object size
    private int NewSize; // new object size
    private int x, y; // x and y integer
    private int xmin, xmax, ymin, ymax; // minimum and maximum x and y value integers
    private boolean right, down;
    private Vector<Rectangle> Walls = new Vector <Rectangle>();
    private Rectangle dbox = new Rectangle(ZERO); // create dbox rectangle and initially set it to zero rectangle
    private Polygon poly = new Polygon();
    private Image buffer; // create doublebuffering canvas
    private Graphics g; // create graphics g
    private double angle; // double for angle
    private double initVelocity; // double for initial velocity
    private int ax, ay, cx, cy, x1, x2, y1, y2; // integers for cannon construction
    private int canX, canY; // integers for cannon construction
    private int a1x, a1y, a2x, a2y, c1x, c1y, c2x, c2y; // integers for polygon points
    int projx; // integer for projectile x location
    int projy; // integer for projectile y location
    private boolean shoot = false; // boolean for drawing the projectile, initialized to false
    double planetGravity; // double for gravity
    double anglerad; // double for angle in radians
    double dt = 0.05; // double for time delay, initialized to 0.05
    double vel_x, vel_y; // doubles for x and y velocity components
    private int ballScore = 0; // integer for ball score
    private int playerScore = 0; // integer for player score



    public Ball(int SB, Point res) {
        Screen = res; // set height and width to the passed in point
        SObj = SB; // set object size to passed in value
        NewSize = SObj; // set NewSize to equal objects size upon object creation
        minMax(); // set minimum and maximum allowed x and y values
        initialPos(); // randomize initial x and y position of the ball
        down = true; // set down flag to start at true
        right = true; // set right flag to start at true
        planetGravity = 9.8;
        angle = 45;
        initVelocity = 155 * 0.3048;
    }

    public void initialPos() { // method to randomize the intiial position of the ball
        int xrange = (xmax - xmin) + 1;
        int yrange = ((ymax - ymin) + 1) - 80;
        int xrand = ((int)(Math.random() * xrange) + xmin);
        int yrand = ((int)(Math.random() * yrange) + ymin);
        x = xrand;
        y = yrand;
    }

    public void addOne(Rectangle r) {
        Walls.addElement(new Rectangle(r)); // add a rectangle to the vector
    }

    public void removeOne(int i) {
        Walls.removeElementAt(i); // remove a rectangle from the vector
    }

    public Rectangle getOne(int i) {
        return Walls.elementAt(i); // get a rectangle from the vector
    }

    public int getWallSize() {
        return Walls.size(); // get vector size
    }

    public void clearWalls() {
        Walls.clear();
    }

    public int getObjSize() {
        return NewSize; // get current object size
    }

    public void setDragBox(Rectangle db) {
        dbox.setBounds(db.x, db.y, db.width, db.height); // set dbox rectangle to passed in rectangle's bounds
    }


    public Rectangle collisionDetect() {
        Rectangle r = new Rectangle(); // create new rectangle called r
        Rectangle z = new Rectangle(ZERO); // create an empty (0) rectangle
        Rectangle b = new Rectangle(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj, SObj); // create a rectangle copy of the ball

        b.grow(1, 1); // grow the ball rectangle
        int i = 0; // initialize while loop index to 0
        boolean ok = true; // set ok to true
        while ((i < Walls.size()) && ok) {
            r = Walls.elementAt(i); // set r to the current rectangle in the vector
            if (r.intersects(b)) { // if the rectangle intersects with the ball
                ok = false; // set boolean to false
            } else { // otherwise
                i++; // increment the index
            }
        }
        if (!ok) { // if we found a collision
            return r; // return the rectangle that the ball collided with
        }
        return z; // otherwise return the zero rectangle
    }

    public void collisionSide() {
        for (int i = 0; i < Walls.size(); i++) { // loop through the vector of rectangles
            Rectangle b = new Rectangle(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj, SObj); // create a rectangle copy of the ball
            Rectangle r = collisionDetect(); // create a rectangle that is the rectangle that collided with the ball or the zero rectangle
            Rectangle c = getOne(i); // get the current rectangle from the vector
            if (r == c) { // if the current vector rectangle and the rectangle that the ball collided with are the same (not the zero rectangle)
                int ballLeft = b.x + 1; // left side of ball
                int ballRight = (b.x + b.width); // right side of ball
                int ballTop = b.y; // top side of ball
                int ballBottom = (b.y + b.height); // bottom side of ball
                int rectLeft = r.x + 1; // left side of rectangle
                int rectRight = (r.x + r.width); // right side of rectangle
                int rectTop = r.y; // top side of rectangle
                int rectBottom = (r.y + r.height); // bottom side of rectangle

                if (ballRight <= rectLeft) { // if ball collided with left side of rectangle
                    right = false; // set right to false
                }
                if (ballLeft >= rectRight) { // if ball collided with right side of rectangle
                    right = true; // set right to true
                }
                if (ballTop >= rectBottom) { // if ball collided with bottom side of rectangle
                    down = true; // set down to true
                }
                if (ballBottom <= rectTop) { // if ball collided with top side of rectangle
                    down = false; // set down to false
                }
            }
        }
    }

    public int getProjx() {
        return projx;
    } // return projectile x location

    public int getProjy() {
        return projy;
    } // return projectile y location

    public int getBallScore() {
        return ballScore;
    } // return ball score

    public int getPlayerScore() {
        return playerScore;
    } // return player score

    public void setBallScore(int ballScore) {
        this.ballScore = ballScore;
    } // set ball score

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    } // set player score

    public boolean ballCollideCannon() {
        Rectangle b = new Rectangle(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj, SObj); // get rectangle of the ball
        Rectangle cannonCircle = new Rectangle(Screen.x - 60, Screen.y - 60, 55, 55); // get rectangle of the circle portion of the cannon
        Rectangle cannonPoly = new Rectangle(poly.getBounds()); // get rectangle of the polygon
        b.grow(1, 1); // grow the rectangle of the ball
        if (b.intersects(cannonCircle) || b.intersects(cannonPoly)) { // if the ball intersects either part of the cannon
            shoot = false; // stop drawing the projectile
            ballScore++; // increment the ball score
            return true; // return true
        }
        return false; // otherwise return false
    }

    public boolean projCollideBall() {
        Rectangle b = new Rectangle(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj, SObj); // get rectangle of the ball
        b.grow(1, 1); // grow the rectangle of the ball
        Rectangle projRect = new Rectangle(projx, projy, 17, 17); // get rectangle of the projectile
        if (projRect.intersects(b)) { // if the projectile intersects the ball
            shoot = false; // stop drawing the projectile
            playerScore++; // increment the player score
            return true; // return true
        }
        return false; // otherwise return false
    }

    public boolean projCollideRect() {
        Rectangle projRect = new Rectangle(projx, projy, 17, 17); // get rectangle of the projectile
        Rectangle r; // make a rectangle
        int i = 0; // loop counter initialized to 0
        while (i < getWallSize()) { // while counter is less than the size of the vector
            r = getOne(i); // get a rectangle and set it to r
            if (projRect.intersects(r)) { // if the projectile intersects the rectangle
                removeOne(i); // remove the rectangle
                shoot = false; // stop drawing the projectile
                return true; // return true
            } else {
                i++; // otherwise increment the loop counter
            }
        }
        return false; // return false
    }


    public boolean projCollideCannon() {
        if (projy > poly.getBounds().y) { // if projectile is above the cannon (it has been shot already)
            Rectangle projRect = new Rectangle(projx, projy, 17, 17); // get rectangle of the projectile
            if (projRect.intersects(poly.getBounds()) && shoot) { // if the projectile intersects with the polygon and the projectile is being drawn
                shoot = false; // stop drawing the projectile
                ballScore++; // increment the ball score
                return true; // return true
            }
        }
        return false; // otherwise return false
    }

    public void setAngle(double angle) {
        this.angle = angle;
    } // set angle to passed in value

    public void setVelocity(double initVelocity) {
        this.initVelocity = initVelocity * 0.3048;
    } // set velocity to passed in value converted to m/s

    public void setGravity(double planetGravity) {
        this.planetGravity = planetGravity;
    } // set gravity to passed in value

    public void calcCannon() {
        // calculate points to draw the polygon for the cannon
        double angleRad = Math.toRadians(angle);
        canX = (int) (90 * Math.cos(angleRad));
        canY = (int) (90 * Math.sin(angleRad));
        ax = Screen.x - 33;
        ay = Screen.y - 33;
        cx = ax - canX;
        cy = ay - canY;
        x1 = (int) ((25 / 2) * Math.cos(angleRad));
        x2 = (int) ((25 / 2) * Math.cos(angleRad));
        y1 = (int) ((25 / 2) * Math.sin(angleRad));
        y2 = (int) ((25 / 2) * Math.sin(angleRad));
        a1x = ax - y1;
        a1y = ay + x1;
        a2x = ax + y2;
        a2y = ay - x2;
        c1x = cx + y1;
        c1y = cy - x1;
        c2x = cx - y2;
        c2y = cy + x2;
    }

    public void gravityCalc() {
        shoot = true; // set shoot boolean to true to draw the projectile
        anglerad=Math.toRadians(angle); // convert angle to radians
        vel_x = initVelocity * Math.cos(anglerad); // initial (and permanent) x component velocity
        vel_y = initVelocity * Math.sin(anglerad);
        projx = Math.min(c1x, c2x); // initial projectile x location
        projy = Math.min(c1y, c2y); // initial projectile y location
    }

    public void moveProj() {
        vel_y = vel_y - planetGravity * dt; // new y component velocity
        projx = (int) (projx - vel_x * dt); // x projectile's new location
        projy = (int) (projy - vel_y * dt - 0.5 * planetGravity * (dt * dt)); // y projectile's new location
    }

    public boolean inBounds() {
        if (projx <= 0 && projy < Screen.y - 17) return false; // if projectile is out of bounds, return false
        return true; // otherwise return true
    }

    public void minMax() {
        xmin = (SObj / 2) + 1; // set minimum allowed x value
        ymin = (SObj / 2) + 1; // set minimum allowed y value
        xmax = Screen.x - (SObj / 2) - 1; // set maximum allowed x value
        ymax = Screen.y - (SObj / 2) - 1; // set maximum allowed y value
    }

    public void move() {
        if (!checkX()) { // if x value is not within allowed range
            right = !right; // complement right flag
        }
        if (!checkY()) { // if y value is not within allowed range
            down = !down; // complement down flag
        }
        if (right) { // if right flag is enabled
            x += 1; // increment x position by 1
        } else { // otherwise
            x -= 1; // decrement x position by 1
        }
        if (down) { // if down flag is enabled
            y += 1; // increment x position by 1
        } else { // otherwise
            y -= 1; // decrement x position by 1
        }
    }

    public void setX(int x) {
        this.x = x; // set objects current x position
    }

    public int getX() {
        return this.x; // returns objects current x position
    }

    public void setY(int y) {
        this.y = y; // sets objects current y position
    }

    public int getY() {
        return this.y; // returns objects current y position
    }

    public boolean checkX() {
        return x > xmin && x < xmax; // check if current x value is within allowed range
    }

    public boolean checkY() {
        return y > ymin && y < ymax; // check if current y value is within allowed range
    }

    public boolean checkSize(int NSObj) { // method to check if theoretical new size would fit in the screen
        int left = x - (NSObj / 2); // calculate left boundary in respect to the new object size
        int right = x + (NSObj / 2); // calculate right boundary in respect to the new object size
        int top = y - (NSObj / 2); // calculate top boundary in respect to the new object size
        int bottom = y + (NSObj / 2); // calculate bottom boundary in respect to the new object size
        return left > xmin && right < xmax && top > ymin && bottom < ymax;
    }


    public void newSize(int NS) {
        NewSize = NS; // set new size to passed value
        minMax(); // set allowable x and y value range
    }

    public void Size() {
        SObj = NewSize; // set object size to the new size
        minMax(); // set allowable x and y value range
    }

    public void reSize(Point res) {
        Screen = res; // set Screen to the passed in Point
        minMax(); // set allowable x and y value range
    }

    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    } // set shoot's value to the passed in value

    public boolean getShoot() {
        return shoot;
    } // return shoot's boolean value

    public Polygon getPoly() {
        return poly;
    } // return the polygon

    public void paint(Graphics cg) {
        buffer = createImage(Screen.x, Screen.y); // create offscreen image
        if (g != null) { // check if g exists
            g.dispose(); // if it does, remove it
        }
        g = buffer.getGraphics(); // s g to the offscreen image
        g.setColor(Color.black); // set the color to blue
        g.drawRect(0, 0, Screen.x - 1, Screen.y - 1); // draw outline of rectangle

        g.setColor(Color.red); // set color of solid circle below to red
        g.fillOval(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj, SObj); // solid circle set
        g.setColor(Color.black); // set outline of circle to black
        g.drawOval(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj, SObj); // outline of circle set

        g.setColor(Color.BLUE);
        g.drawRect(dbox.x, dbox.y, dbox.width, dbox.height); // draws dragbox
        for (int i = 0; i < Walls.size(); i++) { // loops through vector of rectangles
            Rectangle temp = Walls.elementAt(i); // get the rectangle
            g.fillRect(temp.x, temp.y, temp.width, temp.height); // draws the rectangle
        }
        poly.reset(); // reset polygon
        calcCannon(); // draw the cannon
        g.setColor(Color.ORANGE); // set color to blue
        if (shoot) { // if shoot is true
            g.fillOval(projx, projy, 17, 17); // draw the projectile
        }
        g.setColor(Color.BLACK); // set color to black
        poly.addPoint(a1x, a1y); // add point a1
        poly.addPoint(a2x, a2y); // add point a2
        poly.addPoint(c1x, c1y); // add point c1
        poly.addPoint(c2x, c2y); // add point c2
        g.drawPolygon(poly); // draw the polygon
        g.fillPolygon(poly); // fill the polygon
        g.setColor(Color.GREEN); // set the color to green
        g.drawOval(Screen.x - 60, Screen.y - 60, 55, 55); // draw the circle
        g.fillOval(Screen.x - 60, Screen.y - 60, 55, 55); // fill the circle
        cg.drawImage(buffer, 0, 0, null); // switches the canvas with the offscreen image
    }

    public void update(Graphics g) {
        paint(g);
    }
}
