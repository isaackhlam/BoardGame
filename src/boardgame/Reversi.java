/**
 * CSCI1130 Java Assignment 6 BoardGame Reversi
 * Aim: Practise subclassing, method overriding
 *      Learn from other subclass examples
 * 
 * I declare that the assignment here submitted is original
 * except for source material explicitly acknowledged,
 * and that the same or closely related material has not been
 * previously submitted for another course.
 * I also acknowledge that I am aware of University policy and
 * regulations on honesty in academic work, and of the disciplinary
 * guidelines and procedures applicable to breaches of such
 * policy and regulations, as contained in the website.
 *
 * University Guideline on Academic Honesty:
 *   http://www.cuhk.edu.hk/policy/academichonesty
 * Faculty of Engineering Guidelines to Academic Honesty:
 *   https://www.erg.cuhk.edu.hk/erg/AcademicHonesty
 *
 * Student Name: Lam Kin Ho
 * Student ID  : 1155158095
 * Date        : 11-Dec-2021
 */

package boardgame;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 * Reversi is a TurnBasedGame
 */
public class Reversi extends TurnBasedGame/* TO-DO */ {
    
    public static final String BLANK = " ";
    String winner;



    /*** TO-DO: STUDENT'S WORK HERE ***/
    public Reversi(){ // set the default and only Reversi constructor
        super(8,8,"BLACK","WHITE"); //set the size of gameboard and player name as BLACK WHITE
        this.setTitle("Reversi");   //set the game title
    }

    @Override
    protected void initGame(){  
        for (int y = 0; y < yCount; y++)    //create a nested for loop
            for (int x = 0; x < xCount; x++)//to set all tile 
                pieces[x][y].setText(" ");  //BLANK
        pieces[3][4].setText("BLACK");          //and then initialize 2 WHITE AND BLACK tile
        pieces[3][4].setBackground(Color.BLACK);
        pieces[4][3].setText("BLACK");
        pieces[4][3].setBackground(Color.BLACK);
        pieces[3][3].setText("WHITE");
        pieces[3][3].setBackground(Color.WHITE);
        pieces[4][4].setText("WHITE");
        pieces[4][4].setBackground(Color.WHITE);
    }
    
    @Override
    protected void gameAction(JButton triggeredButton, int x, int y){

        if (!isValidMove(x,y,true)){         //check the triggered tile is valid, and set flip as true, when triggered tile is valid,
            addLineToOutput("Invalid move!");//it also flip the captured tile, if triggered tile is invalid, output invalid move
            return;                          //return to wait player choose another tile
        }

        triggeredButton.setText(currentPlayer);//if isValidMove is true, set the triggered tile's text to currentPlayer
        triggeredButton.setBackground(currentPlayer.equals("BLACK") ? Color.BLACK : Color.WHITE);//and also set the corresponding color
        addLineToOutput(currentPlayer + " piece at (" + x + ", " + y + ")");   //output which tile player placed
        changeTurn();                                                          //change Turn

        if(mustPass()){                     //check mustPass, if yes
            addLineToOutput("Pass!");       //output Pass!
            changeTurn();                   //and changeTurn
            if(mustPass()){                 //check again for 2 consecutive pass, if yes
                addLineToOutput("Pass!");   //output Pass!
                checkEndGame(x, y);         //call the EndGame method
                addLineToOutput("Game ended!");//output game ended!
                JOptionPane.showMessageDialog(null, "Game ended!");//show game Ended!
            }
        }
    }


    private boolean isValidMove(int x,int y,boolean flip){ //recieve x,y which is coodinate of triggered tile, flip is true when player clicking, false when checking mustPass
        boolean valid = false;                  //default is invalid, only when check at least one tile can be flipped, change it to valid.

        if(!pieces[x][y].getText().equals(" ")) //if triggered tiles is not BLANK
            return valid;                       //return false, i.e. invalid move

        for(int dx=-1;dx<=1;dx++)       //nest-loop to check adjcent tile
            for(int dy=-1;dy<=1;dy++){  //i.e. all 8 direction from triggered tile
                if(dx==0&&dy==0) continue;  //skip the center one
                int count = 2;              //reset the count to 2 for each new direction checking
                try{
                    if(pieces[x+dx][y+dy].getText().equals(getOpponent())){                             //if adjcent tile have opponent color, keep checking, otherwise end that direction
                        while(true){                                                                    //check next tile
                            if(pieces[x+count*dx][y+count*dy].getText().equals(getOpponent()))          //if there is still opponent color
                                count++;                                                                //keep checking same direction
                            else if(pieces[x+count*dx][y+count*dy].getText().equals(currentPlayer)){    //if there is same color at the other end
                                if(flip) flipCapturedPieces(x,y,dx,dy,count);                           //if flip is true, flip the middle token
                                valid = true;                                                           //if exist at least 1 piece can be flip, change valid to true
                                break;                                                                  //check next direction
                            }else                                                                       //if it is BLANK at the other end
                                break;                                                                  //check next direction
                        }
                    }    
                }catch(ArrayIndexOutOfBoundsException e ){      //the checking may out of the bound of tile(<0||>7) when 2 situation
                }                                               //1. triggered tile is at edge. 2. there is no currentplayer color along that direction
            }                                                   //so we have to catch such exception.
        return valid;                                           //return valid or not.
    }

    private void flipCapturedPieces(int x,int y,int dx,int dy,int count){               //method for flip the coin
        for(int i=1;i<count;i++){                                                       //create for loop to count how many tile need to be flip
            pieces[x+i*dx][y+i*dy].setText(currentPlayer);                                  //set the word
            pieces[x+i*dx][y+i*dy].setBackground(currentPlayer.equals("BLACK") ? Color.BLACK : Color.WHITE);    //set the color
        }
    }
    
    private boolean mustPass(){                     //method check mustPass
        for (int x = 0; x < xCount; x++)            //create a nested loop to check whole gameboard
            for (int y = 0; y < yCount; y++)        //while checking mustPass, set flip as false, hence do not trigger flipCapturedPieces.
                if(isValidMove(x,y,false))          //if exist any one valid tile
                    return false;                   //return false i.e. cannot pass
        return true;                                //otherwise all tile isnt valid, return true
    }

    @Override
    protected boolean checkEndGame(int x,int y){    //method for End Game check winner
        int numOfBlack,numOfWhite;                  //declare no. of each player's tile
        numOfBlack = countPieces("BLACK");          //call the count method for Black
        numOfWhite = countPieces("WHITE");          //call the count method for White
        addLineToOutput("BLACK score: "+numOfBlack);//output the score for Black
        addLineToOutput("WHITE score: "+numOfWhite);//output the score for White
        if(numOfBlack==numOfWhite)                  //if Black and White are equal score
            addLineToOutput("Draw Game!");          //the game is draw
        else                                        //otherwise, check Black higher than White, then output winner
            addLineToOutput(numOfBlack > numOfWhite ? "Winner is BLACK!" : "Winner is WHITE!"); 
        return true;
    }

    private int countPieces(String player){         //method for count pieces
        int count = 0;                              //declare counter and set to 0
        for (int x = 0; x < xCount; x++)            //create nested loop
            for (int y = 0; y < yCount; y++)        //to check whole gameboard
                if(pieces[x][y].getText().equals(player))//if the tile text equal player
                    count++;                        //count++
        return count;                               //return no. of tile counted
    }
    public static void main(String[] args)
    {
        Reversi reversi;
        reversi = new Reversi();
        
        // TO-DO: run other classes, one by one
        System.out.println("You are running class Reversi");
        
        // TO-DO: study comment and code in other given classes
        
        // TO-DO: uncomment these two lines when your work is ready
        reversi.setLocation(400, 20);
        reversi.verbose = false;

        // the game has started and GUI thread will take over here
    }
}