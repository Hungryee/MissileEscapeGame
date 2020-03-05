import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.io.*;

public class Main extends JPanel implements KeyListener {
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 800;

    public static final double easy_regularMissileChance = 0.6;
    public static final double easy_slowMissileChance = 0.15;
    public static final double easy_fastMissileChance = 0.15;
    public static final double easy_undeadMissileChance = 0.1;

    public static final double normal_regularMissileChance = 0.36;
    public static final double normal_slowMissileChance = 0.36;
    public static final double normal_fastMissileChance = 0.16;
    public static final double normal_undeadMissileChance = 0.12;

    public static final double hard_regularMissileChance = 0.35;
    public static final double hard_slowMissileChance = 0.25;
    public static final double hard_fastMissileChance = 0.25;
    public static final double hard_undeadMissileChance = 0.15;

    public static final double fuelPowerupChance = 0.4;
    public static final double radarPowerupChance = 0.275;
    public static final double decoyPowerupChance = 0.1;
    public static final double slowmoPowerupChance = 0.175;
    public static final double lifePowerupChance = 0.05;

    public static double[] missileChances;
    public static double[][] relativeMissileChances;
    public static double[] powerupChances;
    public static double[][] relativePowerupChances;

    public static Plane p;
    public static ArrayList<Missile> missiles;
    public static ArrayList<Cloud> clouds;
    public static ArrayList<Powerup> powerups;

    public static int maxMissileCount;
    public static int maxPowerupCount = 5;
    public static int maxLives = 5;
    public static int lives = maxLives;

    public static int time = 0;
    public static int score = 1;
    public static boolean isPaused = false;
    public static int gameStage = 0;

    public static int difficultySelected = 1;
    public static int difficulty = 1;


    public static Image main_frame;
    public static Image heart_img;
    public static Image plane_img;
    public static Image plane_img_left;
    public static Image plane_img_right;
    public static Image decoy_img;
    public static Image cloud_img;
    public static Image missile_img;
    public static Image missile_slow_img;
    public static Image missile_fast_img;
    public static Image missile_undead_img;
    public static Image powerup_fuel_img;
    public static Image powerup_radar_img;
    public static Image powerup_radar_arrow_img;
    public static Image powerup_decoy_img;
    public static Image powerup_slowmo_img;
    public static Image powerup_life_img;

    public static File scoresFile;
    public static ArrayList<String> scores;
    public static String username = "AnonUser";
    public static JTextField usernameField;

    public static GUIBlock easyDiffButton;
    public static GUIBlock normalDiffButton;
    public static GUIBlock hardDiffButton;

    public static GUIBlock[] leaderboard_blocks;
    public Main() {
        setFocusable(true);
        addKeyListener(this);
        loadResources();
        p = new Plane();
        missiles = new ArrayList<>();
        clouds = new ArrayList<>();
        powerups = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            clouds.add(new Cloud());
        }
        scores = new ArrayList<>();
        try{
            String path = System.getProperty("user.home")+"/Library/Application Support/MissileEscape/SaveData/scores.txt";
            scoresFile = new File(path);
            scoresFile.getParentFile().mkdirs();
            scoresFile.createNewFile();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        easyDiffButton = new GUIBlock(WIDTH/2, HEIGHT/2-60, 300,50, new Color(0,0,0,20),
                new Font("Arial", Font.PLAIN, 40), "EASY");
        normalDiffButton = new GUIBlock(WIDTH/2, HEIGHT/2, 300,50, new Color(0,0,0,20),
                new Font("Arial", Font.PLAIN, 40), "NORMAL");
        hardDiffButton = new GUIBlock(WIDTH/2, HEIGHT/2+60, 300,50, new Color(0,0,0,20),
                new Font("Arial", Font.PLAIN, 40), "HARD");


        usernameField = new JTextField();
        usernameField.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        usernameField.setBackground(new Color(200,200,0,30));
        usernameField.setBounds(WIDTH/2-150, HEIGHT/2-145, 300,50);
        usernameField.setFont(new Font("Arial", Font.PLAIN,0));
        usernameField.addActionListener(e -> {
            usernameField.setFocusable(false);
            usernameField.setEditable(false);
            username = (usernameField.getText().length()==0)?username:usernameField.getText();
        });

        generateSpawnChances();

        readScoresFromFile();
        leaderboard_blocks = new GUIBlock[10];
        fillLeaderboards();


    }
    public static void fillLeaderboards(){
        scores.sort((e1,e2)-> {
            if (Integer.parseInt(e1.split("-")[1])>Integer.parseInt(e2.split("-")[1])) return -1;
            if (Integer.parseInt(e1.split("-")[1])<Integer.parseInt(e2.split("-")[1])) return 1;
            return 0;
        });
        for (int i = 0; i < 10; i++) {
            if (i<scores.size()) {
                String[] temp = scores.get(i).split("-");
                Color placeColor;
                switch (i){
                    case 0:{
                        placeColor = new Color(255,223,0);
                        break;
                    }
                    case 1:{
                        placeColor = new Color(97, 97, 97);
                        break;
                    }
                    case 2:{
                        placeColor = new Color(205, 127, 50);
                        break;
                    }
                    default:{
                        placeColor = Color.white;
                    }
                }
                leaderboard_blocks[i] = new GUIBlock(WIDTH-180, HEIGHT/2-135+23*i, 300,23, new Color(0,0,0,20),
                        new Font("Arial", Font.PLAIN, 20), ("["+temp[2]+"] "+temp[0]+" - " +temp[1]), placeColor);
            }else{
                leaderboard_blocks[i] = new GUIBlock(WIDTH-180, HEIGHT/2-135+23*i, 300,23, new Color(0,0,0,20),
                        new Font("Arial", Font.PLAIN, 20), "");
            }
        }
    }
    public static void readScoresFromFile(){
        try{
            FileReader fr = new FileReader(scoresFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null) {
                if (line.matches("\\b(\\w*)-(\\d*)-(\\w*)\\b")){
                    scores.add(line);
                }
            }
            br.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static void writeScoresToFile(){
        try{
            FileWriter fr = new FileWriter(scoresFile);
            BufferedWriter br = new BufferedWriter(fr);
            scores.sort((e1,e2)-> {
                if (Integer.parseInt(e1.split("-")[1])>Integer.parseInt(e2.split("-")[1])) return -1;
                if (Integer.parseInt(e1.split("-")[1])<Integer.parseInt(e2.split("-")[1])) return 1;
                return 0;
            });
            for (int i = 0; i < Math.min(scores.size(),10); i++) {
                String[] temp = scores.get(i).split("-");
                br.write(temp[0]+"-"+temp[1]+"-"+temp[2]);
                br.newLine();
            }
            br.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static Image loadImage(Image img, String path, double width, double height){
        img = Toolkit.getDefaultToolkit().createImage(Main.class.getResource(path));
        img = img.getScaledInstance((int) width, (int) height, Image.SCALE_SMOOTH);
        return img;
    }
    public static void loadResources(){
        main_frame = loadImage(main_frame, "img/gui_window_frame.png", WIDTH+26,HEIGHT+6);

        heart_img = loadImage(heart_img,"img/heart.png",30,30);

        plane_img = loadImage(plane_img, "img/plane_default.png", Plane.size, Plane.size);

        plane_img_left = loadImage(plane_img_left, "img/plane_left.png", Plane.size, Plane.size);

        plane_img_right = loadImage(plane_img_right, "img/plane_right.png", Plane.size, Plane.size);

        decoy_img = loadImage(decoy_img, "img/plane_decoy.png", Plane.size, Plane.size);

        cloud_img = loadImage(cloud_img, "img/cloud.png", Cloud.width, Cloud.height);

        missile_img = loadImage(missile_img, "img/missile.png", Missile.width, Missile.height);

        missile_slow_img = loadImage(missile_slow_img, "img/missile_slow.png", Missile.width, Missile.height);

        missile_fast_img = loadImage(missile_fast_img, "img/missile_fast.png", Missile.width, Missile.height);

        missile_undead_img = loadImage(missile_undead_img, "img/missile_undead.png", Missile.width, Missile.height);

        powerup_fuel_img = loadImage(powerup_fuel_img, "img/powerup_fuel.png", Powerup.size, Powerup.size);

        powerup_radar_img = loadImage(powerup_radar_img, "img/powerup_radar.png", Powerup.size, Powerup.size);

        powerup_radar_arrow_img = loadImage(powerup_radar_arrow_img, "img/radar_arrow.png", Powerup.size/2, Powerup.size/2);

        powerup_decoy_img = loadImage(powerup_decoy_img, "img/powerup_decoy.png", Powerup.size, Powerup.size);

        powerup_slowmo_img = loadImage(powerup_slowmo_img, "img/powerup_slowmo.png", Powerup.size, Powerup.size);

        powerup_life_img = loadImage(powerup_life_img, "img/powerup_life.png", Powerup.size, Powerup.size);
    }
    public static void generateSpawnChances(){
        if (difficulty == 0){
            missileChances = new double[]{
                    easy_regularMissileChance,
                    easy_slowMissileChance,
                    easy_fastMissileChance,
                    easy_undeadMissileChance
            };
        }
        if (difficulty == 1){
            missileChances = new double[]{
                    normal_regularMissileChance,
                    normal_slowMissileChance,
                    normal_fastMissileChance,
                    normal_undeadMissileChance
            };
        }
        if (difficulty == 2){
            missileChances = new double[]{
                    hard_regularMissileChance,
                    hard_slowMissileChance,
                    hard_fastMissileChance,
                    hard_undeadMissileChance
            };
        }
        relativeMissileChances = new double[missileChances.length][];
        for (int i = 0; i < relativeMissileChances.length; i++) {
            double start = 0;
            double end;
            for (int j = 0; j < i; j++) {
                start+= missileChances[j];
            }
            end = start+ missileChances[i];
            relativeMissileChances[i] = new double[]{start,end};
        }
        powerupChances = new double[]{
                fuelPowerupChance,
                radarPowerupChance,
                decoyPowerupChance,
                slowmoPowerupChance,
                lifePowerupChance
        };
        relativePowerupChances = new double[powerupChances.length][];
        for (int i = 0; i < relativePowerupChances.length; i++) {
            double start = 0;
            double end;
            for (int j = 0; j < i; j++) {
                start+= powerupChances[j];
            }
            end = start+ powerupChances[i];
            relativePowerupChances[i] = new double[]{start,end};
        }
    }

    public static void drawPausedGui(Graphics2D g2d) {
        if (isPaused) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            g2d.setColor(Color.black);
            g2d.fillRect(WIDTH / 2 - 5 - 20, HEIGHT / 2 - 25, 20, 50);
            g2d.fillRect(WIDTH / 2 + 5, HEIGHT / 2 - 25, 20, 50);
        }
    }

    public static void drawControlsTutorial(Graphics2D g2d){
        g2d.setColor(Color.black);
        g2d.drawRect(30,HEIGHT/2-145, 300,230);
        g2d.setColor(new Color(0,0,0,20));
        g2d.fillRect(30,HEIGHT/2-145, 300,230);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 35));
        int lineHeight = g2d.getFontMetrics().getHeight();
        String text = "[A/D] - Left/Right\n[SPACE] - Boost\n[Q] - Pause";
        int temp = 0;
        String[] spl = text.split("\n");
        for (String s:spl){
            g2d.drawString(s, (int) (180-g2d.getFont().getStringBounds(s, g2d.getFontRenderContext()).getWidth()/2), HEIGHT/2-lineHeight*spl.length/2+temp*lineHeight);
            temp++;
        }
    }
    public static void drawLeaderboard(Graphics2D g2d){
        for (int i = 0; i < leaderboard_blocks.length; i++) {
            if (leaderboard_blocks[i]!=null){
                leaderboard_blocks[i].display(g2d);
            }
        }
    }

    public void drawMainMenu(Graphics2D g2d){
        g2d.setColor(new Color(200,200,0,120));
        g2d.fillRect(0,0,WIDTH,HEIGHT);
        g2d.setColor(Color.gray);
        g2d.setFont(new Font("Arial", Font.PLAIN, 40));
        g2d.drawRect(WIDTH/2-150, HEIGHT/2-145, 300,50);
        g2d.drawString(usernameField.getText(), (int) (WIDTH/2-g2d.getFont().getStringBounds(usernameField.getText(), g2d.getFontRenderContext()).getWidth()/2), (int) (HEIGHT/2-120+g2d.getFont().getStringBounds(usernameField.getText(), g2d.getFontRenderContext()).getHeight()/3));

        easyDiffButton.display(g2d);
        normalDiffButton.display(g2d);
        hardDiffButton.display(g2d);

        drawLeaderboard(g2d);
        drawControlsTutorial(g2d);
    }

    public static void spawnObjects() {
        if (!isPaused) {
            time++;
            score+=(1+difficulty)/2;
            score = Math.max(0, score);

            if (missiles.size() == 0 || (time % (Math.max(500, 2500-500*difficulty-score/25)) == 0 && missiles.size() <= maxMissileCount)) {
                switch (chooseMissile()){
                    case 0:{
                        missiles.add(new Missile());
                        break;
                    }
                    case 1:{
                        missiles.add(new SlowMissile());
                        break;
                    }
                    case 2:{
                        missiles.add(new FastMissile());
                        missiles.add(new FastMissile());
                        break;
                    }
                    case 3:{
                        missiles.add(new UndeadMissile());
                        break;
                    }
                }
            }
            if (time % 2000 == 0 && powerups.size() <= maxPowerupCount) {
                switch (choosePowerup()){
                    case 0:{
                        powerups.add(new Fuel());
                        break;
                    }
                    case 1:{
                        powerups.add(new Radar());
                        break;
                    }
                    case 2:{
                        powerups.add(new DecoyPU());
                        break;
                    }
                    case 3:{
                        powerups.add(new Slowmo());
                        break;
                    }
                    case 4:{
                        powerups.add(new Life());
                        break;
                    }
                }
            }
        }
    }

    public static int chooseMissile(){
        double n = Math.random();
        int res = 0;
        for (int i = 0; i < relativeMissileChances.length; i++) {
            double[] interval = relativeMissileChances[i];
            if (n>=interval[0]&&n<=interval[1]){
                res = i;
            }
        }
        return res;
    }

    public static int choosePowerup(){
        double n = Math.random();
        int res = 0;
        for (int i = 0; i < relativePowerupChances.length; i++) {
            double[] interval = relativePowerupChances[i];
            if (n>=interval[0]&&n<=interval[1]){
                res = i;
            }
        }
        return res;
    }

    public static void drawGui(Graphics2D g2d) {
        if (gameStage==1) {
            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(new Font("Arial", Font.PLAIN, 35));
            g2d.drawString("" + score / 50, 50, 70);

            g2d.setColor(Color.ORANGE);
            g2d.fillRect(WIDTH - 50, (int) (50 + 200 - 200 * p.boostFuel / p.maxBoostFuel), 40, (int) (200 * p.boostFuel / p.maxBoostFuel));
            g2d.setColor(Color.black);
            g2d.drawRect(WIDTH - 50, 50, 40, 200);
            drawPausedGui(g2d);
            if (p.isSlowmoActivated){
                g2d.setColor(new Color(200,200,0,60));
                g2d.fillRect(0,0,WIDTH,HEIGHT);
            }
            for (int i = 0; i < lives; i++) {
                g2d.drawImage(heart_img, 120 + 32 * i, 43, null);
            }
        }
    }

    public static void drawObjects(Graphics2D g2d) {
        for (int i = 0; i < powerups.size(); i++) {
            Powerup pu = powerups.get(i);
            pu.show(g2d);
        }

        p.show(g2d);
        for (int i = 0; i < missiles.size(); i++) {
            Missile m = missiles.get(i);
            m.show(g2d);
        }
        for (int i = 0; i < clouds.size(); i++) {
            clouds.get(i).show(g2d);
        }
    }

    public static void moveObjects() {
        if (!isPaused) {
            p.move();
            for (int i = 0; i < missiles.size(); i++) {
                Missile m = missiles.get(i);
                for (int j = 0; j < missiles.size(); j++) {
                    if (i != j) {
                        Missile other = missiles.get(j);
                        if (m.area.intersects(other.area.getBounds()) && other.area.intersects(m.area.getBounds())) {
                            if (m.isVisible() && other.isVisible()) {
                                m.destroy();
                                other.destroy();
                                score += 25 * 50;
                            }
                        }
                    }
                }
                if (m.area.intersects(p.area.getBounds()) && p.area.intersects(m.area.getBounds())) {
                    m.destroy();
                    if (!p.isInvincible) {
                        score -= 50 * (25 + 12 * (maxLives-lives));
                        lives--;
                        p.isInvincible = true;
                    }
                }

                m.move();
            }
            for (int i = 0; i < clouds.size(); i++) {
                clouds.get(i).move();
            }
        }
    }



    public static void resetGame() {
        String diff = "";
        if (difficulty==0){
            diff = "Easy";
        }
        if (difficulty==1){
            diff = "Normal";
        }
        if (difficulty==2){
            diff = "Hard";
        }
        if (scores.size()<10) {
            scores.add(username + "-" + score / 50+"-"+diff);
        }else{
            scores.add(username + "-" + score / 50+"-"+diff);
            scores.sort((e1,e2)-> {
                String[] temp1 = e1.split("-");
                String[] temp2 = e2.split("-");
                if (temp1[1].length()==0){
                    temp1[1] = "0";
                }
                if (temp2[1].length()==0){
                    temp2[1] = "0";
                }
                if (Integer.parseInt(temp1[1])>Integer.parseInt(temp2[1])) return -1;
                if (Integer.parseInt(temp1[1])<Integer.parseInt(temp2[1])) return 1;
                return 0;
            });
            scores.remove(10);
        }
        fillLeaderboards();
        writeScoresToFile();
        gameStage = 0;
        p = new Plane();
        clouds.clear();
        Cloud.generateAdditionalClouds();
        missiles.clear();
        powerups.clear();
        time = 0;
        score = 1;
        lives = maxLives;
        isPaused = false;
    }

    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        if (gameStage==0) {
            usernameField.setVisible(true);
            drawMainMenu(g2d);
        }
        if (gameStage==1) {
            usernameField.setVisible(false);
            spawnObjects();
            g2d.setColor(new Color(0, 120, 200));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            moveObjects();
            drawObjects(g2d);
            if (lives <= 0) {
                resetGame();
            }
            drawGui(g2d);
        }
        g2d.drawImage(main_frame, -13,-13,null);

        repaint(0, 0, WIDTH, HEIGHT);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameStage==0) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                if (difficultySelected<=0){
                    difficultySelected = 2;
                }else {
                    difficultySelected--;
                }
            }
            if (e.getKeyCode()==KeyEvent.VK_DOWN){
                if (difficultySelected>=2){
                    difficultySelected = 0;
                }else{
                    difficultySelected++;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_ENTER){
                difficulty = difficultySelected;
                gameStage = 1;
                maxMissileCount = 10+2*difficulty;
                repaint();
            }
        }
        if (gameStage == 1) {
            if (e.getKeyCode() == KeyEvent.VK_Q) {
                isPaused = !isPaused;
                repaint(0, 0, WIDTH, HEIGHT);
            }
            if (e.getKeyCode() == KeyEvent.VK_R) {
                resetGame();
                repaint(0, 0, WIDTH, HEIGHT);
            }
            if (e.getKeyCode() == KeyEvent.VK_A) {
                p.isRotating = 1;
                p.img = plane_img_left;
            }
            if (e.getKeyCode() == KeyEvent.VK_D) {
                p.isRotating = 2;
                p.img = plane_img_right;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                p.isBoosted = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameStage == 1){
            if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
                p.isRotating = 0;
                p.img = plane_img;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                p.isBoosted = false;
            }
        }
    }
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        JFrame f = new JFrame("ESCAPE FROM MISSILES");
        f.setSize(WIDTH, HEIGHT);
        f.add(new Main());
        f.setVisible(true);
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.add(usernameField);
    }
}
