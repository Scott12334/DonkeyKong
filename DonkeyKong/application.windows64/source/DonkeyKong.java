import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.effects.*; 
import ddf.minim.signals.*; 
import ddf.minim.spi.*; 
import ddf.minim.ugens.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class DonkeyKong extends PApplet {








//import processing.minim.*;

ArrayList<Ground> scaffolding;
ArrayList<Ladder> ladders;
int numberPerRow=13;
Player mario;
int lives;
int celebrate=0;
//movement variables
boolean right=false;
boolean left=false;
int lastJump;
PImage barrel;
PImage life;
boolean canJump;
//Kong
Gorrilla kong;
int lastRoll=0;
boolean canDrop=true;
boolean canMove=false;
//barrells
ArrayList<Barrell> barrells;
//GUI variables
PFont newFont;
boolean gameStarted;
boolean gameOver;
int score;

//enimies
PImage[] blueBarrell;
int currentBlueBarrell;
int barrelStartTime;
ArrayList<Ghost> ghosts;
int ghostSpawn;
PImage wires;

//explosion
PImage[] explosion;
int currentExplosion;
int startExplosion;

//mallet
ArrayList<Mallet> mallets;
boolean marioHammer=false;
int hammerStart;

//princess peach
PImage[] peachImages;
int startWalking;
int peachFrame;
boolean gameWon;
float princessX;
float princessY;

//name variables
boolean needName;

//level variables
int currentLevel;
int startLevel;
boolean newLevel;
int rollSpeed;
int flySpeed;
int ghostSpawnTime;
int barrellSpawnTime;
//Pie variables
ArrayList<Pie> pies;

//rain variables
PImage tiltedBarrell;
ArrayList<Rain> drops;
int lastRain=0;
//Sound Files
Minim minim;
AudioPlayer jump;
AudioPlayer hammer;
AudioPlayer pickup;
AudioPlayer loseLife;
AudioPlayer gameLost;
AudioPlayer wonGame;
public void setup()
{
  
  scaffolding= new ArrayList<Ground>();
  ladders= new ArrayList<Ladder>();
  barrells= new ArrayList<Barrell>();
  pies=new ArrayList<Pie>();
  kong= new Gorrilla(width/2-240, height-564);
  mario=new Player((width/14)*2, height-42);
  barrel= loadImage("barrel1.png");
  lives=3;
  life= loadImage("marioR1.png");
  newFont=createFont("ARCADECLASSIC.TTF", 60);
  blueBarrell= new PImage[2];
  blueBarrell[0]=loadImage("blueBarrell1.png");
  blueBarrell[1]=loadImage("blueBarrell2.png");
  currentBlueBarrell=0;
  gameStarted=false;
  gameOver=false;
  barrelStartTime=millis();
  ghosts= new ArrayList<Ghost>();
  ghostSpawn=millis();
  score=0;
  mallets=new ArrayList<Mallet>();
  marioHammer=false;
  hammerStart=millis();
  scores= new int[10];
  scoreBoardNames= new String[10];
  loadScore();
  peachImages= new PImage[2];
  peachImages[0]= loadImage("peach1.png");
  peachImages[1]= loadImage("peach2.png");
  startWalking=millis();
  peachFrame=0;
  gameWon=false;
  needName=false;
  lastJump=0;
  currentLevel=1;
  startLevel=0;
  newLevel=false;
  name1="";
  flySpeed=3;
  rollSpeed=4;
  barrellSpawnTime=3000;
  ghostSpawnTime=5000;
  minim = new Minim(this);
  jump=minim.loadFile("jump.mp3");
  hammer=minim.loadFile("hammer.mp3");
  pickup=minim.loadFile("itemget.mp3");
  loseLife=minim.loadFile("loseLife.mp3");
  gameLost=minim.loadFile("gameOver.mp3");
  wonGame=minim.loadFile("gameWon.mp3");
  canJump=true;
  explosion= new PImage[4];
  explosion[0]=loadImage("e1.png");
  explosion[1]=loadImage("e2.png");
  explosion[2]=loadImage("e3.png");
  explosion[3]=loadImage("e4.png");
  wires= loadImage("wire.png");
  tiltedBarrell=loadImage("tiltedBarrell.png");
  lastRain=millis();
  drops= new ArrayList<Rain>();
  level1Setup();
}
public void draw()
{
  background(0);
  if (gameStarted==false)
  {
    startScreen();
  } else if (needName==true)
  {
    getUsername();
  } else if (gameOver==true)
  {
    endScreen();
  } else if (gameWon==true)
  {
    gameWonScreen();
    println(lives);
  } else {
    scaffolding();
    ladders();
    blueEnemies();
    mario.display();
    barrelDisplay();
    kongControl();
    barrellControl();
    lifeDisplay();
    malletControl();
    princessPeach();
    levelControl();
    pieControl();
    rainControl();
  }
}
public void rainControl()
{
  if (currentLevel==1)
  {
    image(tiltedBarrell, width/3+50, height/2-242);
    for (int i=0; i<drops.size(); i++)
    {
      drops.get(i).display();
      if (dist(mario.getX(), mario.getY(), drops.get(i).getX(), drops.get(i).getY())<10)
      {
        lives--;
        kong.setFrame(0);
        celebrate=millis();
        mario.setX((width/14)*2);
        mario.setY(height-42);
        loseLife.play(0);
        drops.clear();
        break;
      }
    }
    if (lastRain+1000<millis())
    {
      drops.add(new Rain(width/3+28+random(-10, 2), height/2-225));
      lastRain=millis();
    }
  }
}
public void pieControl()
{
  for (int i=0; i<pies.size(); i++)
  {
    pies.get(i).display();
    if (pies.get(i).isTouched()==true && marioHammer==false)
    {
      lives--;
      kong.setFrame(0);
      celebrate=millis();
      mario.setX((width/14)*2);
      mario.setY(height-42);
      ghosts.clear();
      ghostSpawn=millis();
      loseLife.play(0);
      pies.remove(i);
      break;
    } else if (pies.get(i).isTouched()==true && marioHammer==true)
    {
      score+=200;
      pies.remove(i);
      break;
    }
  }
}
public void levelControl()
{
  if (startLevel>millis()-1500)
  {
    textFont(newFont);
    textAlign(CENTER);
    fill(255);
    textSize(75);
    text("Level "+currentLevel, width/2, height/2);
  }
  if (newLevel==true)
  {
    kong.setFrame(0);
    celebrate=millis();
    mario.setX((width/14)*2);
    mario.setY(height-42);
    barrells.clear();
    ghosts.clear();
    mallets.clear();
    ghostSpawn=millis();
    startLevel=millis();
    scaffolding.clear();
    ladders.clear();
    currentLevel++;
    barrellSpawnTime-=500;
    ghostSpawnTime-=750;
    if (currentLevel==2)
    {
      level2Setup();
    }
    if (currentLevel==3)
    {
      level3Setup();
    }
    newLevel=false;
  }
}
public void gameWonScreen()
{
  textFont(newFont);
  textAlign(CENTER);
  fill(255, 0, 0);
  textSize(50);
  text("You Won!", width/2, height/2-100);
  textSize(45);
  text("Press  the  spacebar to  restart", width/2, (height/2)-50);
  scoreBoard();
}
public void scoreBoard()
{
  textFont(newFont);
  textAlign(CENTER);
  fill(255, 0, 0);
  textSize(32);
  int y=height/2;
  for (int i=0; i<10; i++)
  {
    textSize(25);
    int number=10-i;
    text(number+" "+scoreBoardNames[i].substring(scoreBoardNames[i].indexOf(":")+1)+" "+scores[i], width/2, y);
    y+=20;
  }
}
public void getUsername()
{
  textFont(newFont);
  textAlign(CENTER);
  fill(255, 0, 0);
  textSize(32);
  text("Congrats you are one of the top ten scorers!", width/2, height/2-200);
  text("type your name then press enter", width/2, height/2-170);
  textSize(25);
  text(name1, width/2, (height/2));
}
public void princessPeach()
{
  if (millis()>startWalking+200)
  {
    if (peachFrame!=1)
    {
      peachFrame=1;
    } else if (peachFrame==1)
    {
      peachFrame=0;
    }
    startWalking=millis();
  }
  imageMode(CENTER);
  image(peachImages[peachFrame], princessX, princessY);
  if (dist(princessX, princessY, mario.getX(), mario.getY())<20)
  {
    score+=400;
    if (currentLevel==3)
    {
      gameWon=true;
      if (score>scores[0])
      {
        needName=true;
      } else
      {
        saveScore1();
      }
      wonGame.play();
    } else
    {
      newLevel=true;
    }
  }
}
public void blueEnemies()
{
  if (currentLevel==1)
  {
    for (int i=0; i<ghosts.size(); i++)
    { 
      ghosts.get(i).display();
      if (marioHammer==true)
      {
        if (ghosts.get(i).isScared()==false) {
        }
        ghosts.get(i).scared(true);
      } else
      {
        if (ghosts.get(i).isScared()==true) {
        }
        ghosts.get(i).scared(false);
      }
      if (dist(mario.getX(), mario.getY(), ghosts.get(i).getX(), ghosts.get(i).getY())<20 && marioHammer==false)
      {
        lives--;
        kong.setFrame(0);
        celebrate=millis();
        mario.setX((width/14)*2);
        mario.setY(height-42);
        barrells.clear();
        ghosts.clear();
        ghostSpawn=millis();
        loseLife.play(0);
        break;
      } else if (dist(mario.getX(), mario.getY(), ghosts.get(i).getX(), ghosts.get(i).getY())<20 && marioHammer==true)
      {
        score+=200;
        ghosts.get(i).setRemove();
      }
      if (ghosts.get(i).shouldRemove()==true)
      {
        ghosts.remove(i);
      }
    }
  }
  if (currentLevel==2)
  {
    imageMode(CENTER);
    image(blueBarrell[currentBlueBarrell], (width/14)*2-50, height-57);
    if (millis()>barrelStartTime+200)
    {
      if (currentBlueBarrell!=1)
      {
        currentBlueBarrell=1;
      } else if (currentBlueBarrell==1)
      {
        currentBlueBarrell=0;
      }
      barrelStartTime=millis();
    }
    for (int i=0; i<ghosts.size(); i++)
    { 
      ghosts.get(i).display();
      if (marioHammer==true)
      {
        if (ghosts.get(i).isScared()==false) {
          ghosts.get(i).switchDirection();
        }
        ghosts.get(i).scared(true);
      } else
      {
        if (ghosts.get(i).isScared()==true) {
          ghosts.get(i).switchDirection();
        }
        ghosts.get(i).scared(false);
      }
      if (dist(mario.getX(), mario.getY(), ghosts.get(i).getX(), ghosts.get(i).getY())<20 && marioHammer==false)
      {
        lives--;
        kong.setFrame(0);
        celebrate=millis();
        mario.setX((width/14)*2);
        mario.setY(height-42);
        barrells.clear();
        ghosts.clear();
        ghostSpawn=millis();
        loseLife.play(0);
        break;
      } else if (dist(mario.getX(), mario.getY(), ghosts.get(i).getX(), ghosts.get(i).getY())<20 && marioHammer==true)
      {
        score+=200;
        ghosts.get(i).setRemove();
      }
      if (ghosts.get(i).shouldRemove()==true)
      {
        ghosts.remove(i);
      }
    }
    if (millis()>ghostSpawn+ghostSpawnTime && marioHammer==false)
    {
      ghosts.add(new Ghost((width/14)*2-50, height-80));
      ghostSpawn=millis();
      currentExplosion=0;
    } else if (millis()==ghostSpawn+4000)
    {
      startExplosion=millis();
    } else if (millis()>ghostSpawn+4000 && marioHammer==false)
    {
      if (currentExplosion<4)
      {
        image(explosion[currentExplosion], (width/14)*2-50, height-90);
        if (millis()>startExplosion+500)
        {
          currentExplosion++;
        }
      }
    }
  }
  if (currentLevel==3)
  {
    imageMode(CENTER);
    image(wires, width/2, height/2+103);
    image(blueBarrell[currentBlueBarrell], width/2, height/2+37);
    if (millis()>barrelStartTime+200)
    {
      if (currentBlueBarrell!=1)
      {
        currentBlueBarrell=1;
      } else if (currentBlueBarrell==1)
      {
        currentBlueBarrell=0;
      }
      barrelStartTime=millis();
    }
    if (mario.getX()<width/2+blueBarrell[currentBlueBarrell].width/2 && mario.getX()>width/2-blueBarrell[currentBlueBarrell].width/2 && mario.getY()<(height/2+37)+wires.height/2 && mario.getY()>(height/2+37)-wires.height/2)
    {
      lives--;
      kong.setFrame(0);
      celebrate=millis();
      mario.setX((width/14)*2);
      mario.setY(height-42);
      barrells.clear();
      ghosts.clear();
      ghostSpawn=millis();
      loseLife.play(0);
    }
    if (millis()>ghostSpawn+3000 && marioHammer==false)
    {
      int spawnLoc= (int)random(0, 7);
      switch(spawnLoc)
      {
      case 0:
        ghosts.add(new Ghost(width/3, height-380));
        break;

      case 1:
        ghosts.add(new Ghost(2*width/3, height-380));
        break;

      case 2:
        ghosts.add(new Ghost(width/3, height-140));
        break;

      case 3:
        ghosts.add(new Ghost(2*width/3, height-140));
        break;

      case 4:
        ghosts.add(new Ghost(width/2, height-140));
        break;

      case 5:
        ghosts.add(new Ghost(width/3, height-500));
        break;

      case 6:
        ghosts.add(new Ghost(2*width/3, height-500));
        break;
      }
      ghostSpawn=millis();
    }
    for (int i=0; i<ghosts.size(); i++)
    {
      ghosts.get(i).display();
      if (dist(mario.getX(), mario.getY(), ghosts.get(i).getX(), ghosts.get(i).getY())<20 && marioHammer==false)
      {
        lives--;
        kong.setFrame(0);
        celebrate=millis();
        mario.setX((width/14)*2);
        mario.setY(height-42);
        barrells.clear();
        ghosts.clear();
        ghostSpawn=millis();
        loseLife.play(0);
        break;
      } else if (dist(mario.getX(), mario.getY(), ghosts.get(i).getX(), ghosts.get(i).getY())<20 && marioHammer==true)
      {
        score+=200;
        ghosts.get(i).setRemove();
      }
      if (marioHammer==true)
      {
        ghosts.get(i).scared(true);
      } else
      {
        ghosts.get(i).scared(false);
      }
      if (ghosts.get(i).shouldRemove()==true)
      {
        ghosts.remove(i);
      }
    }
  }
}
public void malletControl()
{
  for (int i=0; i<mallets.size(); i++)
  {
    mallets.get(i).display();
    if (mallets.get(i).isTouched()==true)
    {
      mallets.remove(i);
      marioHammer=true;
      hammerStart=millis()+6000;
      pickup.play();
      hammer.play();
    }
  }
  if (hammerStart<millis() && marioHammer==true)
  {
    mario.setFrame(2);
    marioHammer=false;
    hammer.pause();
  }
}
public void startScreen()
{
  textFont(newFont);
  textAlign(CENTER);
  fill(255, 0, 0);
  textSize(50);
  text("Donkey Kong!", width/2, height/2);
  textSize(45);
  text("Press  the  spacebar to  begin", width/2, (height/2)+50);
}
public void endScreen()
{
  textFont(newFont);
  textAlign(CENTER);
  fill(255, 0, 0);
  textSize(50);
  text("Game Over!", width/2, height/2-100);
  textSize(45);
  text("Press  the  spacebar to  restart", width/2, (height/2)-50);
  scoreBoard();
}
public void lifeDisplay()
{
  int lifeX= ((width/14)/2)-15;
  for (int i=0; i<lives; i++)
  {
    imageMode(CENTER);
    image(life, lifeX, 25);
    lifeX+=25;
  }
  if (lives<=0)
  {
    if (score>scores[0])
    {
      needName=true;
    } else
    {
      saveScore1();
    }
    gameOver=true;
    gameLost.play();
  }
  textFont(newFont);
  textAlign(CENTER);
  fill(255);
  textSize(50);
  text(score, width/2, 50);
}
public void barrellControl()
{
  for (int i=0; i<barrells.size(); i++)
  {
    barrells.get(i).display();
    if (dist(mario.getX(), mario.getY(), barrells.get(i).getX(), barrells.get(i).getY())<20 && marioHammer==false)
    {
      lives--;
      kong.setFrame(0);
      celebrate=millis();
      barrells.get(i).setRemove();
      mario.setX((width/14)*2);
      mario.setY(height-42);
      barrells.clear();
      ghosts.clear();
      ghostSpawn=millis();
      loseLife.play(0);
      break;
    } else if (dist(mario.getX(), mario.getY(), barrells.get(i).getX(), barrells.get(i).getY())<20 && marioHammer==true)
    {
      score+=200;
      barrells.get(i).setRemove();
    }
    if (barrells.get(i).shouldRemove()==true)
    {
      barrells.remove(i);
    }
  }
}
public void kongControl()
{
  kong.display();
  if (currentLevel==2)
  {
    if (celebrate+2000>millis())
    {
      kong.chestPump();
    } else if (millis()>lastRoll+barrellSpawnTime)
    {
      int dropOrRoll= (int)random(2);
      if (dropOrRoll==0)
      {
        kong.barrell(true);
      } else
      {
        kong.barrell(false);
      }
      lastRoll=millis();
    }
  } else if (currentLevel==1)
  {
    //drop barrell
    if (canDrop==true)
    {
      kong.barrell(true);
      canDrop=false;
    }
    //move left
    if (canMove==true)
    {
      kong.chestPump();
      kong.moveX(1);
    }
    //drop barrell
    if (kong.getX()-width/3<10 && kong.left()==false) {
      canDrop=true; 
      canMove=false; 
      kong.arrivedLeft();
    }
    if (abs(kong.getX()-width/2)<10 && kong.middle()==false) {
      canDrop=true; 
      canMove=false; 
      kong.arrivedMiddle();
    }
    if ((2*width/3)-kong.getX()<10 && kong.right()==false) {
      canDrop=true; 
      canMove=false; 
      kong.arrivedRight();
    }
  } else if (currentLevel==3)
  {
    //drop barrell
    if (canDrop==true)
    {
      kong.barrell(true);
      canDrop=false;
    }
    //move left
    if (canMove==true)
    {
      kong.chestPump();
      kong.moveX(1);
    }
    //drop barrell
    if (kong.getX()-width/7<10 && kong.left()==false) {
      canDrop=true; 
      canMove=false; 
      kong.arrivedLeft();
    }
    if (abs(kong.getX()-width/2)<10 && kong.middle()==false) {
      canDrop=true; 
      canMove=false; 
      kong.arrivedMiddle();
    }
    if ((width-width/7)-kong.getX()<10 && kong.right()==false) {
      canDrop=true; 
      canMove=false; 
      kong.arrivedRight();
    }
  }
}
public void ladders()
{
  for (int i=0; i<ladders.size(); i++)
  {
    ladders.get(i).display();
    if (dist(mario.getX(), mario.getY(), ladders.get(i).getX(), ladders.get(i).getY())<100 && ladders.get(i).playerOnLadder(mario.getX(), mario.getY())==false)
    {
      mario.onLadder=false;
      canJump=true;
    }
    for (int j=0; j<ghosts.size(); j++)
    {
      if (ghosts.get(j).getX()+20>ladders.get(i).getX()-5 && ghosts.get(j).getX()-20<ladders.get(i).getX()+5 && ghosts.get(j).getY()+17<ladders.get(i).getY()+30 && ghosts.get(j).getY()-17>ladders.get(i).getY()-30 && ghosts.get(j).isScared()==false)
      {
        ghosts.get(j).addX(5);
        ghosts.get(j).onLadder=true;
        ghosts.get(j).switchDirection();
      } else if (dist(ghosts.get(j).getX(), ghosts.get(j).getY(), ladders.get(i).getX(), ladders.get(i).getY())>50 && dist(ghosts.get(j).getX(), ghosts.get(j).getY(), ladders.get(i).getX(), ladders.get(i).getY())<80)
      {
        ghosts.get(j).onLadder=false;
      }
    }
  }
}


public void scaffolding()
{
  for (int i=0; i<scaffolding.size(); i++)
  {
    scaffolding.get(i).display();
    //pie movement
    for (int j=0; j<pies.size(); j++)
    {
      if (pies.get(j).getX()>scaffolding.get(i).getX()-29.5f && pies.get(j).getX()<scaffolding.get(i).getX()+29.5f && scaffolding.get(i).isBelt()==true)
      {
        if (pies.get(j).getY()+20>scaffolding.get(i).getY()-20 && pies.get(j).getY()<scaffolding.get(i).getY())
        {
          if (scaffolding.get(i).isBelt()==true)
          {
            pies.get(j).move(1.2f*scaffolding.get(i).getDirection());
          }
        }
      }
    }
    if (mario.getX()>scaffolding.get(i).getX()-29.5f && mario.getX()<scaffolding.get(i).getX()+29.5f)
    {
      if (mario.getY()+18>scaffolding.get(i).getY()-10 && mario.getY()<scaffolding.get(i).getY() && mario.isJumping()==false)
      {
        mario.setOnFloor(true);
        if (scaffolding.get(i).isBelt()==true)
        {
          mario.move(2*scaffolding.get(i).getDirection());
        }
        mario.setY((scaffolding.get(i).getY()-10)-18);
      } else if (mario.getY()+18<scaffolding.get(i).getY()+15)
      {
        mario.setOnFloor(false);
      }
    }
    for (int j=0; j<barrells.size(); j++)
    {
      if (barrells.get(j).getX()<scaffolding.get(i).getX()+29.5f && barrells.get(j).getX()>scaffolding.get(i).getX()-29.5f)
      {
        if (barrells.get(j).getY()+18>scaffolding.get(i).getY()-10 && barrells.get(j).getY()<scaffolding.get(i).getY())
        {
          barrells.get(j).setOnFloor(true);
        } else if (barrells.get(j).getY()+16<scaffolding.get(i).getY()+15)
        {
          barrells.get(j).setOnFloor(false);
        }
      }
    }
    for (int j=0; j<ghosts.size(); j++)
    {
      if (ghosts.get(j).getX()<scaffolding.get(i).getX()+29.5f && ghosts.get(j).getX()>scaffolding.get(i).getX()-29.5f)
      {
        if (ghosts.get(j).getY()+18>scaffolding.get(i).getY()-10 && ghosts.get(j).getY()<scaffolding.get(i).getY())
        {
          ghosts.get(j).setOnFloor(true);
          ghosts.get(j).setY((scaffolding.get(i).getY()-10)-18);
          if (scaffolding.get(i).isBelt()==true)
          {
            ghosts.get(j).move(1.5f*scaffolding.get(i).getDirection());
          }
        } else if (ghosts.get(j).getY()+16<scaffolding.get(i).getY()+15)
        {
          ghosts.get(j).setOnFloor(false);
        }
      }
    }
  }
}
public void barrelDisplay()
{
  if (currentLevel==2)
  {
    imageMode(CENTER);
    image(barrel, width/14-30, height-538.5f);
    image(barrel, width/14-5, height-538.5f);
    image(barrel, width/14+20, height-538.5f);
    image(barrel, width/14-30, height-576.5f);
    image(barrel, width/14-5, height-576.5f);
    image(barrel, width/14+20, height-576.5f);
    image(barrel, width/14-30, height-613);
    image(barrel, width/14-5, height-613);
    image(barrel, width/14+20, height-613);
  }
}
public void keyPressed()
{
  if (needName==false)
  {
    if (key== 'a' || keyCode==LEFT)
    {
      left=true;
    }
    if (key== 'd' || keyCode==RIGHT)
    {
      right=true;
    }
    if (key==' ' && mario.isJumping()==false && gameStarted==true && lastJump+300<millis() && canJump==true)
    {
      if (mario.getFrame()==0 || mario.getFrame()==1) {
        mario.setFrame(4);
        mario.startJump(0);
        jump.play(0);
        lastJump=millis();
        canJump=false;
      } else if (mario.getFrame()==2 || mario.getFrame()==3) {
        mario.setFrame(5);
        mario.startJump(1);
        jump.play(0);
        lastJump=millis();
        canJump=false;
      }
    } else if (key==' '&& gameStarted==false)
    {
      gameStarted=true;
      startLevel=millis();
    } else if (key==' '&& (gameOver==true || gameWon==true))
    {
      setup();
    }
    if (keyCode==UP || key=='w')
    {
      for (int i=0; i<ladders.size(); i++)
      {
        if (ladders.get(i).playerOnLadder(mario.getX(), mario.getY()))
        {
          mario.changeY(-6);
          mario.onLadder(true);
          canJump=false;
        }
      }
    }
  } else
  {
    if (key!=ENTER)
    {
      name1+=key;
    } else
    {
      needName=false;
      saveScore1();
    }
  }
}
public void keyReleased()
{
  if (key== 'a' || keyCode==LEFT)
  {
    left=false;
    mario.setFrame(2);
  }
  if (key== 'd' || keyCode==RIGHT)
  {
    right=false;  
    mario.setFrame(0);
  }
  if (key== ' ' && canJump==false && mario.onLadder==false)
  {
    canJump=true;
  }
}
public class Barrell
{
  float x;
  float y;
  int type;
  PImage[] images;
  int currentFrame=0;
  int startAnimation;
  int speed=4;
  boolean onFloor=false;
  boolean shouldRemove=false;
  public Barrell(float x, float y, int type)
  {
    this.x=x;
    this.y=y;
    this.type=type;
    images= new PImage[2];
    if (type==0) //drop
    {
      images[0]= loadImage("drop1.png");
      images[1]= loadImage("drop2.png");
    } else //roll
    {
      images[0]= loadImage("roll1.png");
      images[1]= loadImage("roll2.png");
    }
    speed=rollSpeed;
  }
  public void display()
  {
    imageMode(CENTER);
    image(images[currentFrame], x, y);
    if (millis()>startAnimation+400)
    {
      if (currentFrame!=1)
      {
        currentFrame=1;
      } else if (currentFrame==1)
      {
        currentFrame=0;
      }
      startAnimation=millis();
    }
    movement();
  }
  public void movement()
  {
    if (type==0)
    {
      y+=speed;
    }
    else
    {
      if(onFloor==true){x+=speed;}
      gravity();
      if(x>width-((width/14)) || x<(width/14))
      {
        speed*=-1;
      }
    }
    if(y>height)
    {
      shouldRemove=true;
    }
    if(y>height-100 && x<(width/14)/2)
    {
      shouldRemove=true;
    }
  }
  public void gravity()
  {
    if (onFloor==false)
    {
      y+=4;
    }
  }
  public float getX() {
    return x;
  }
  public float getY() {
    return y;
  }
  public boolean isOnFloor() {
    return onFloor;
  }
  public void setOnFloor(boolean update) {
    onFloor=update;
  }
  public void addSpeed(){speed+=2;}
  public boolean shouldRemove(){return shouldRemove;}
  public int getType(){return type;}
  public void setRemove(){shouldRemove=true;}
}
public class Ghost
{
  float x;
  float y;
  boolean scared=false;
  int currentFrame=0;
  PImage[] images;
  boolean onFloor=false;
  boolean onLadder=false;
  int speed=3;
  boolean shouldRemove=false;
  public Ghost(float x, float y)
  {
    this.x=x;
    this.y=y;
    images= new PImage[4];
    images[0]=loadImage("ghost1.png");
    images[1]=loadImage("ghost2.png");
    images[2]=loadImage("ghost3.png");
    images[3]=loadImage("ghost4.png");
    speed=flySpeed;
  }
  public void display()
  {
    facing();
    imageMode(CENTER);
    image(images[currentFrame], x, y);
    if (currentLevel==2) {
      move();
    } else if (currentLevel==1) {
      move1();
    }
    gravity();
    if (dist((width/2)+95, 255, x, y)<50)
    {
      shouldRemove=true;
    }
  }
  public void move1()
  {
    for(int i=0; i<ladders.size();i++)
    {
      if(dist(x,y,ladders.get(i).getX(),ladders.get(i).getY())<40)
      {
        println(1);
        switchDirection();
      }
    }
    x+=speed;
  }
  public void facing()
  {
    if (speed>0 && scared==false) {
      currentFrame=0;
    } else if (speed<0 && scared==false) {
      currentFrame=1;
    }
    if (speed>0 && scared==true) {
      currentFrame=2;
    } else if (speed<0 && scared==true) {
      currentFrame=3;
    }
  }
  public void move()
  {
    if (scared==false)
    {
      if (onLadder==false) {
        x+=speed;
      } else {
        y-=4;
      }
    } else
    {
      x+=speed;
      if (x>width-((width/14)/1.6f) || x<(width/14))
      {
        speed*=-1;
      }
    }
  }
  public void gravity()
  {
    if (onFloor==false && onLadder==false)
    {
      y+=2;
    }
  }
  public float getX() {
    return x;
  }
  public float getY() {
    return y;
  }
  public boolean isOnFloor() {
    return onFloor;
  }
  public int getSpeed() {
    return speed;
  }
  public void setOnFloor(boolean update) {
    onFloor=update;
  }
  public void setY(float newY) {
    this.y=newY;
  }
  public void switchDirection() {
    speed*=-1;
  }
  public void addX(int x) {
    if (speed>0)
    {
      this.x+=x;
    } else
    {
      this.x-=x;
    }
  }
  public void scared(boolean isScared) {
    this.scared=isScared;
  }
  public boolean shouldRemove() {
    return shouldRemove;
  }
  public void setRemove() {
    shouldRemove=true;
  }
  public boolean isScared() {
    return scared;
  }
  public void move(float amount) {
    x+=amount;
  }
}
public class Gorrilla
{
  float x;
  float y;
  PImage[] images;
  int currentFrame=0;
  int startAnimation=0;
  int startDrop=0;
  boolean barrellSpawn=false;
  boolean drop;
  boolean middle;
  boolean right;
  boolean left;
  int direction=-1; //-1 left, 1 right
  public Gorrilla(float x, float y)
  {
    this.x=x;
    this.y=y;
    images= new PImage[6];
    images[0]=loadImage("gorrilla1.png");
    images[1]=loadImage("gorrilla2.png");
    images[2]=loadImage("gorrilla3.png");
    images[3]=loadImage("gorrilla4.png");
    images[4]=loadImage("gorrilla5.png");
    images[5]=loadImage("gorrilla6.png");
    middle=true;
    left=false;
    right=false;
  }
  public void display()
  {
    imageMode(CENTER);
    image(images[currentFrame], x, y);
    if (barrellSpawn==true)
    {
      if (drop==true)
      {
        drop();
      } else
      {
        roll();
      }
    }
  }
  public void chestPump()
  {
    if (millis()>startAnimation+400)
    {
      if (currentFrame!=1)
      {
        currentFrame=1;
      } else if (currentFrame==1)
      {
        currentFrame=2;
      }
      startAnimation=millis();
    }
  }
  public void barrell(boolean drop)
  {
    barrellSpawn=true;
    this.drop=drop;
    this.startDrop=millis();
  }
  public void drop()
  {
    if (millis()<startDrop+1400)
    {
      currentFrame=3;
      if (millis()>startDrop+700)
      {
        currentFrame=4;
      }
    } else
    {
      currentFrame=0;
      barrellSpawn=false;
      drop=false;
      barrells.add(new Barrell(x, y+30, 0));
      canMove=true;
    }
  }
  public void roll()
  {
    if (millis()<startDrop+1500)
    {
      currentFrame=3;
      if (millis()>startDrop+500 &&millis()<startDrop+1000)
      {
        currentFrame=4;
      } else if (millis()>startDrop+1000)
      {
        currentFrame=5;
      }
    } else
    {
      barrells.add(new Barrell(x+70, y+30, 1));
      currentFrame=0;
      barrellSpawn=false;
    }
  }
  public void setFrame(int frame) {
    currentFrame=frame;
  }
  public void setX(int newX) {
    this.x=newX;
  }
  public void setY(int newY) {
    this.y=newY;
  }
  public void moveX(int newX) {
    x+= (newX*direction);
  }
  public boolean middle() {
    return middle;
  }
  public boolean left() {
    return left;
  }
  public boolean right() {
    return right;
  }
  public void arrivedLeft() {
    left=true;
    right=false;
    middle=false; 
    direction=1;
  }
  public void arrivedMiddle() {
    left=false;
    right=false;
    middle=true;
  }
  public void arrivedRight() {
    left=false;
    right=true;
    middle=false; 
    direction=-1;
  }
  public float getX() {
    return x;
  }
  public float getY() {
    return y;
  }
}
public class Ground
{
  float x;
  float y;
  PImage[] ground;
  PImage[] belt;
  PImage[] controller;
  boolean end;
  boolean conveyor;
  int currentFrame=0;
  int startTime;
  int direction=1;
  boolean right;
  int currentGround=0;
  public Ground(float x, float y)
  {
    this.x=x;
    this.y=y;
    ground=new PImage[3];
    ground[0]=loadImage("Scaffolding.png");
    ground[1]=loadImage("Scaffolding2.png");
    ground[2]=loadImage("Scaffolding3.png");
    conveyor=false;
  }
  public Ground(float x, float y, boolean end, boolean right)
  {
    this.x=x;
    this.y=y;
    conveyor=true;
    this.end=end;
    if (end==true)
    {
      if (right==true)
      {
        controller=new PImage[4];
        controller[0]= loadImage("beltControl1.png");
        controller[1]= loadImage("beltControl2.png");
        controller[2]= loadImage("beltControl3.png");
        controller[3]= loadImage("beltControl4.png");
      }
      else
      {
        controller=new PImage[4];
        controller[0]= loadImage("beltControlL1.png");
        controller[1]= loadImage("beltControlL2.png");
        controller[2]= loadImage("beltControlL3.png");
        controller[3]= loadImage("beltControlL4.png");
      }
    } else
    {
      belt= new PImage[4];
      belt[0]= loadImage("belt1.png");
      belt[1]= loadImage("belt2.png");
      belt[2]= loadImage("belt3.png");
      belt[3]= loadImage("belt4.png");
    }
  }
  public void display()
  {
    imageMode(CENTER);
    switch(currentLevel)
    {
      case 1:
      currentGround=1;
      break;
      
      case 2:
      currentGround=0;
      break;
      
      case 3:
      currentGround=2;
      break;
    }
    if (conveyor==false) {
      image(ground[currentGround], x, y);
    }
    if (conveyor==true && end==false) {
      image(belt[currentFrame], x, y);
      animate();
    }
    if (conveyor==true && end==true) {
      image(controller[currentFrame], x, y);
      animate();
    }
  }
  public void animate()
  {

    if (startTime+750<millis() && currentFrame<3 && direction==1)
    {
      currentFrame++;
      startTime=millis();
    } else if (startTime+750<millis() && currentFrame>0 && direction==-1)
    {
      currentFrame--;
      startTime=millis();
    } else if (currentFrame==3 || currentFrame==0)
    {
      direction*=-1;
    }
  }
  public int getDirection() {
    return direction;
  }
  public boolean isBelt() {
    return conveyor;
  }
  public float getX() {
    return x;
  }
  public float getY() {
    return y;
  }
}
public class Ladder
{
  float x;
  float y;
  float ladderHeight;
  PImage[] ladder;
  boolean changeLadder;
  int currentLadder=0;
  public Ladder(float x, float y)
  {
    this.x=x;
    this.y=y;
    ladder=new PImage[3];
    ladder[0]=loadImage("Ladder.png");
    ladder[1]=loadImage("Ladder2.png");
    ladder[2]=loadImage("Ladder3.png");
    this.ladderHeight=ladder[0].height;
  }
  public Ladder(float x, float y,float ladderHeight)
  {
    this.x=x;
    this.y=y;
    ladder=new PImage[3];
    ladder[0]=loadImage("Ladder.png");
    ladder[1]=loadImage("Ladder2.png");
    ladder[2]=loadImage("Ladder3.png");
    this.ladderHeight=ladderHeight;
    changeLadder=true;
  }
  public void display()
  {
    imageMode(CENTER);
    switch(currentLevel)
    {
      case 1:
      currentLadder=1;
      break;
      
      case 2:
      currentLadder=0;
      break;
      
      case 3:
      currentLadder=2;
      break;
    }
    if(changeLadder==false){image(ladder[currentLadder],x,y);}
    else{image(ladder[currentLadder],x,y,ladder[currentLadder].width,ladderHeight);}
  }
  public boolean playerOnLadder(float playerX, float playerY)
  {
    if(playerX-15<x+ladder[currentLadder].width/2 && playerX+15>x-ladder[currentLadder].width/2 && playerY-17<y+ladderHeight/2 && playerY+17>y-ladderHeight/2)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
  public float getX(){return x;}
  public float getY(){return y;}
}
public class Mallet
{
  float x;
  float y;
  boolean scared=false;
  int currentFrame=0;
  PImage images;
  boolean onFloor=false;
  boolean onLadder=false;
  int speed=3;
  public Mallet(float x, float y)
  {
    this.x=x;
    this.y=y;
    images=loadImage("mallet.png");
  }
  public void display()
  {
    imageMode(CENTER);
    image(images,x,y);
  }
  public float getX() {
    return x;
  }
  public float getY() {
    return y;
  }
  public boolean isTouched()
  {
    if(dist(mario.getX(),mario.getY(),x,y)<40)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
}
public class Pie
{
  float x;
  float y;
  PImage pie;
  public Pie(float x, float y)
  {
    this.x=x;
    this.y=y;
    pie=loadImage("pie.png");
  }
  public void display()
  {
    imageMode(CENTER);
    image(pie,x,y);
  }
  public float getX() {
    return x;
  }
  public float getY() {
    return y;
  }
  public void move(float amount){x+=amount;}
  public boolean isTouched()
  {
    if(dist(mario.getX(),mario.getY(),x,y)<40)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
}
public class Player
{
  PImage[] mario;
  float x;
  float y;
  int speed=5;
  boolean onFloor=false;
  boolean jump=false;
  int startJump=0;
  int startWalk=0;
  int upSpeed=2;
  int currentFrame=0;
  boolean onLadder=false;
  int heading=0; //right is 0, left is 1
  public Player(float x, float y)
  {
    this.x=x;
    this.y=y-30;
    mario= new PImage[10];
    mario[0]=loadImage("marioR1.png");
    mario[1]=loadImage("marioR2.png");
    mario[2]=loadImage("marioL1.png");
    mario[3]=loadImage("marioL2.png");
    mario[4]=loadImage("marioR3.png");
    mario[5]=loadImage("marioL3.png");
    mario[6]=loadImage("marioR4.png");
    mario[7]=loadImage("marioR5.png");
    mario[8]=loadImage("marioL4.png");
    mario[9]=loadImage("marioL5.png");
  }
  public void display()
  {
    imageMode(CENTER);
    if (marioHammer==true && right==false && left==false)
    {
      if (currentFrame==0 || currentFrame==6 || currentFrame==7 )
      {
        if (millis()>startWalk+50)
        {
          if (currentFrame!=6)
          {
            currentFrame=6;
          } else if (currentFrame==6)
          {
            currentFrame=7;
          }
          startWalk=millis();
        }
      } else if (currentFrame==2 || currentFrame==8 || currentFrame==9)
      {
        if (millis()>startWalk+50)
        {
          if (currentFrame!=8)
          {
            currentFrame=8;
          } else if (currentFrame==8)
          {
            currentFrame=9;
          }
          startWalk=millis();
        }
      }
    }
    if(currentFrame!=7 && currentFrame!=9){image(mario[currentFrame], x, y);}
    else{image(mario[currentFrame], x, y-20);}
    movement();
    gravity();
  }
  public void movement()
  {
    if (right==true && x+speed<width-10)
    {
      x+=speed;
      //println(millis()+ "sec-"+startWalk);
      if (millis()>startWalk+50 && marioHammer==false)
      {
        if (currentFrame!=1)
        {
          currentFrame=1;
        } else if (currentFrame==1)
        {
          currentFrame=0;
        }
        startWalk=millis();
      } else if (millis()>startWalk+50 && marioHammer==true)
      {
        if (currentFrame!=6)
        {
          currentFrame=6;
        } else if (currentFrame==6)
        {
          currentFrame=7;
        }
        startWalk=millis();
      }
    }
    if (left==true && x-speed>0) 
    {
      x-=speed;
      if (millis()>startWalk+50 && marioHammer==false)
      {
        if (currentFrame!=3)
        {
          currentFrame=3;
        } else if (currentFrame==3)
        {
          currentFrame=2;
        }
        startWalk=millis();
      } else if (millis()>startWalk+50 && marioHammer==true)
      {
        if (currentFrame!=8)
        {
          currentFrame=8;
        } else if (currentFrame==8)
        {
          currentFrame=9;
        }
        startWalk=millis();
      }
    }
    jump();
  }
  public void gravity()
  {
    println(onLadder);
    if (onFloor==false && onLadder==false)
    {
      y+=2;
    }
  }
  public void startJump(int heading)
  {
    jump=true;
    upSpeed=9;
    startJump=millis();
    this.heading=heading;
  }
  public void jump()
  {
    if (jump==true)
    {
      y-=upSpeed;
      if (upSpeed>=0)
      {
        upSpeed-=1;
      } else
      {
        jump=false;
        if (heading==0) {
          currentFrame=0;
        } else if (heading==1) {
          currentFrame=2;
        }
      }
    }
  }
  public void setOnFloor(boolean update) {
    onFloor=update;
  }
  public void changeY(float change) {
    y+=change;
  }
  public float getX() {
    return x;
  }
  public float getY() {
    return y;
  }
  public boolean isJumping() {
    return jump;
  }
  public boolean isOnFloor() {
    return onFloor;
  }
  public void onLadder(boolean value) {
    onLadder=value;
  }
  public void setY(float newY) {
    this.y=newY;
  }
  public void setX(float newX) {
    this.x=newX;
  }
  public void startWalk() {
    startWalk=millis();
  }
  public void setFrame(int frame) {
    currentFrame=frame;
  }
  public int getFrame() {
    return currentFrame;
  }
  public void move(int amount){x+=amount;}
}
public class Rain
{
  float x;
  float y;
  PImage drop;
  public Rain(float x, float y)
  {
    this.x=x;
    this.y=y;
    drop= loadImage("drop.png");
  }
  public void display()
  {
    imageMode(CENTER);
    image(drop,x,y);
    fall();
  }
  public void fall()
  {
    y+=3;
  }
  public float getX() {
    return x;
  }
  public float getY() {
    return y;
  }
}
public void level1Setup()
{
  scaffolding= new ArrayList<Ground>();
  ladders= new ArrayList<Ladder>();
  int newRow=14;
  float y=height-10.5f;
  for (int i=0; i<6; i++)
  {
    float x=((width/14)/2) +(width/14)*i;
    for (int j=0; j<newRow; j++)
    {
      scaffolding.add(new Ground(x, y));
      x+=width/14+0.6f;
    }
    if(i==2)
    {
      mallets.add(new Mallet(width/2, y-50));
    }
    if(i>0 && i<4)
    {
      ghosts.add(new Ghost(width/2,y-20));
    }
    newRow-=2;
    y-=120;
  }
  y=height-70;
  float x=(width/14)/2+width/14;
  for (int i=0; i<5; i++)
  {
    ladders.add(new Ladder(x, y, 100));
    ladders.add(new Ladder(width-x, y, 100));
    x+=width/14;
    y-=120;
  }
  kong.setX(width/2);
  kong.setY(255);
  princessX=width/2;
  princessY=150;
}
public void level2Setup()
{
  mallets.add(new Mallet(width/2, 635));
  mallets.add(new Mallet(width/2, 435));
  kong= new Gorrilla(width/2-240, height-564);
  princessX=(width/2)-100;
  princessY=130;
  float x=(width/14)/2;
  float y=height-10.5f;
  //row 1
  for (int i=0; i<numberPerRow+1; i++)
  {
    scaffolding.add(new Ground(x, y));
    x+=width/14-.6f;
    if (i>=6)
    {
      y-=3;
    }
  }
  //row 2
  x=(width/14)/2;
  y=height-130.5f;
  for (int i=0; i<numberPerRow; i++)
  {
    scaffolding.add(new Ground(x, y));
    x+=width/14-.6f;
    y+=3;
  }
  //row 3
  x=(width/14)*1.5f;
  y=height-195.5f;
  for (int i=0; i<numberPerRow; i++)
  {
    scaffolding.add(new Ground(x, y));
    x+=width/14-.6f;
    y-=3;
  }
  //row 4
  x=(width/14)/2;
  y=height-335.5f;
  for (int i=0; i<numberPerRow; i++)
  {
    scaffolding.add(new Ground(x, y));
    x+=width/14-.6f;
    y+=3;
  }
  //row 5
  x=(width/14)*1.5f;
  y=height-395.5f;
  for (int i=0; i<numberPerRow; i++)
  {
    scaffolding.add(new Ground(x, y));
    x+=width/14-.6f;
    y-=3;
  }
  //row 6
  x=(width/14)/2;
  y=height-510.5f;
  for (int i=0; i<numberPerRow; i++)
  {
    scaffolding.add(new Ground(x, y));
    x+=width/14-.6f;
    if (i>=8)
    {
      y+=3;
    }
  }
  //ladder setup
  ladders.add(new Ladder(width-(width/14)-40, height-60));
  ladders.add(new Ladder(width-(width/14)-40, height-263));
  ladders.add(new Ladder(width-(width/14)-40, height-463));
  ladders.add(new Ladder((width/14)+30, height-160));
  ladders.add(new Ladder((width/14)+30, height-365));
  ladders.add(new Ladder((width/2)+95, 230, 100));
  ladders.add(new Ladder((width/2)-100, 230, 100));
  scaffolding.add(new Ground((width/2)-90, 169));
  scaffolding.add(new Ground((width/2)-90+width/14-.6f, 169));
  scaffolding.add(new Ground((width/2)-90+width/14-.6f+width/14-.6f, 169));
  scaffolding.add(new Ground((width/2)-90+width/14-.6f+width/14-.6f+width/14-.6f, 169));
}
public void level3Setup()
{
  pies.add(new Pie(width/4,height-160));
  pies.add(new Pie(width/2,height-160));
  pies.add(new Pie(3*width/4,height-160));
  mallets.clear();
  mallets.add(new Mallet(width/2,height-175));
  
  //row 1
  float x=(width/14)/2;
  float y=height-10.5f;
  scaffolding= new ArrayList<Ground>();
  ladders=new ArrayList<Ladder>();
  for (int i=0; i<numberPerRow+1; i++)
  {
    scaffolding.add(new Ground(x, y));
    x+=width/14-.6f;
  }
  ladders.add(new Ladder(width/5, height-70, 100));
  ladders.add(new Ladder(2*width/5, height-70, 100));
  ladders.add(new Ladder(3*width/5, height-70, 100));
  ladders.add(new Ladder(4*width/5, height-70, 100));
  
  //row 2
  y=height-130;
  x=(width/14)/2+width/14;
  scaffolding.add(new Ground(x-48, y,true,true));
  for (int i=0; i<numberPerRow-1; i++)
  {
    scaffolding.add(new Ground(x, y,false,true));
    x+=width/14-.6f;
  }
  scaffolding.add(new Ground(x-10, y,true,false));
  ladders.add(new Ladder(width/3, height-190, 100));
  ladders.add(new Ladder(2*width/3, height-190, 100));
  
  //row 3
  y=height-250;
  x=(width/14)/2+width/14;
  for (int i=0; i<numberPerRow-1; i++)
  {
    if (i!=2 && i!=9) {
      scaffolding.add(new Ground(x, y));
    }
    x+=width/14-.6f;
  }
  ladders.add(new Ladder(width/7, height-310, 100));
  mallets.add(new Mallet(width/7-20,height-310));
  ladders.add(new Ladder(2*width/5, height-310, 100));
  ladders.add(new Ladder(3*width/5, height-310, 100));
  ladders.add(new Ladder(width-width/7, height-310, 100));
  
  //row 4
  y=height-370;
  x=(width/14)/2+5;
  for (int i=0; i<numberPerRow+1; i++)
  {
    if (i!=6 && i!=7) {
      scaffolding.add(new Ground(x, y,false,true));
    }
    if(i==6)
    {
      scaffolding.add(new Ground(x-10, y,true,false));
    }
    x+=width/14-.6f;
    if(i==7)
    {
      scaffolding.add(new Ground(x-48, y,true,true));
    }
  }
  ladders.add(new Ladder(width/7-40, height-430, 100));
  ladders.add(new Ladder(width-width/7+40, height-430, 100));
  
  //row 5
  y=height-490;
  x=(width/14)/2+width/14+5;
  scaffolding.add(new Ground(x-48, y,true,true));
  for (int i=0; i<numberPerRow-1; i++)
  {
    scaffolding.add(new Ground(x, y,false,true));
    x+=width/14-.6f;
  }
  scaffolding.add(new Ground(x-10, y,true,false));
  ladders.add(new Ladder(2*width/5, height-550, 100));
  ladders.add(new Ladder(3*width/5, height-550, 100));
  
  //row 6
  x=(width/14)/2;
  y=height-610;
  for (int i=0; i<numberPerRow+1; i++)
  {
    if (i>4 && i<9) {
      scaffolding.add(new Ground(x, y));
    }
    x+=width/14-.6f;
  }
  princessX=width/2;
  princessY=height-650;
  kong.setX(width/2);
  kong.setY(height-540);
}
boolean names[];
int[] scores;
String[] scoreBoardNames;
int changedSpot;
String name1= "";
public void loadScore()
{
  String[] inputs= loadStrings("scores.txt");
  for (int i=0; i<10; i++)
  {
    String currentLine=inputs[i+1];
    scores[i]= Integer.parseInt(currentLine.substring(currentLine.indexOf(",")+2, currentLine.length()));
    scoreBoardNames[i]= currentLine.substring(currentLine.indexOf(":"), currentLine.indexOf(","));
    println(scores[i]+scoreBoardNames[i]);
  }
}
public void saveScore1()
{

  if (score>scores[0])
  {
    scores[0]=score;
    scoreBoardNames[0]=": "+name1;
    changedSpot=0;
    for (int i=0; i<9; i++)
    {
      if (scores[i]>scores[i+1])
      {
        int tempScore=scores[i+1];
        String tempString=scoreBoardNames[i+1];
        scores[i+1]=scores[i];
        scoreBoardNames[i+1]=scoreBoardNames[i];
        scores[i]=tempScore;
        scoreBoardNames[i]=tempString;
      } else {
        break;
      }
    }
  } 
  println("");
  for (int i=0; i<10; i++)
  {
    print(scoreBoardNames[i]+ ", ");
    print(scores[i]+ ", ");
  }
  String[] outputText= new String[11];
  outputText[0]= "**TOP TEN SCORES**";
  for (int i=0; i<10; i++)
  {
    int place=10-i;
    outputText[i+1]=place+scoreBoardNames[i]+", "+scores[i];
  }
  saveStrings("scores.txt", outputText);
}
  public void settings() {  size(826, 800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "DonkeyKong" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
