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
 * Student Name: LAW TSZ WING
 * Student ID  : 1155154829
 * Date        : 12/12/2021
 */

package boardgame;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 * Reversi is a TurnBasedGame
 */
public class Reversi extends TurnBasedGame {
    
    public static final String BLANK = " ";
    String winner;


    /*** TO-DO: STUDENT'S WORK HERE ***/
    
    int passCounter = 0; //init passCounter using the the coming code segment
    
    //Reversi constructor with BLACK as Player 1, WHITE as Player2
    public Reversi(){
        super(8, 8,"BLACK", "WHITE");
        this.setTitle("Reversi");        
    }
    
    //initialize Revesi board with all blanks
    @Override
    protected void initGame(){
        for(int y = 0; y < yCount; y++)
            for(int x = 0; x < xCount; x++)
                    pieces[x][y].setText(BLANK);
        
        /*
        setting the middle area as
            WHITE | BLACK
            BLACK | WHITE  
        */
        pieces[3][3].setText("WHITE");
        pieces[3][3].setBackground(Color.WHITE);
        pieces[4][3].setText("BLACK");
        pieces[4][3].setBackground(Color.BLACK);
        pieces[3][4].setText("BLACK");
        pieces[3][4].setBackground(Color.BLACK);
        pieces[4][4].setText("WHITE");
        pieces[4][4].setBackground(Color.WHITE);
    }
    
        
    @Override
    protected void gameAction(JButton triggerButton, int x, int y){
        triggerButton = null;
         
        //checking is there anymore possible valid move
        if(mustPass()){  
            addLineToOutput("Pass!"); //output Pass! if there isn't any valid move for the current palyer
            passCounter++;
            changeTurn(); //2 consecutive pass turn -> need check next turn as well
            
            if(passCounter == 2){ //counting for the 2 consecutive pass turn
                if(checkEndGame(x, y)){ //excute the end game when there is 2 consecutive pass turn
                    addLineToOutput("Game ended!");
                    JOptionPane.showMessageDialog(null, "Game ended!");
                }
            }
            return;
        }
        
        passCounter = 0; //if there is the valid move, then reset the counter
        
        //checking the move is valid or not, also filp the chess if it is valid, boolean means filp can be finish in this step
        if(!isValidMove(x, y, true)){
            addLineToOutput("Invalid move!");
            return;
        }
        
        //set color
        Color color = currentPlayer.equals("BLACK")? Color.BLACK : Color.WHITE;
        pieces[x][y].setText(currentPlayer);
        pieces[x][y].setBackground(color);
        pieces[x][y].setOpaque(true);
        
        addLineToOutput(currentPlayer + " piece at (" + x + ", " + y + ")");
        changeTurn();
    }
    
    @Override
    protected boolean checkEndGame(int moveX, int moveY){
        
        //init the counter of black and white chess
        int black = 0, white= 0;
        black = countPieces("BLACK");
        white = countPieces("WHITE");
        //output
        addLineToOutput("BLACK score: " + black);
        addLineToOutput("WHITE score: " + white);
        if(black == white)
            addLineToOutput("Draw Game!");
        if(black > white)
            addLineToOutput("Winner is BLACK!");
        else
            addLineToOutput("Winner is WHITE!");
        return true;
    }
    
        
    //helper methods
    private boolean isBlank(int x, int y){
        return pieces[x][y].getText().equals(BLANK);
    }
    
    private boolean isFriend(int x, int y){
        return pieces[x][y].getText().equals(currentPlayer);
    }
    
    private boolean isOpponent(int x, int y){
        return pieces[x][y].getText().equals(getOpponent());
    }
    
    //check valid move
    private boolean isValidMove(int moveX, int moveY, boolean flip){
        boolean valid = false;
        
        //return false if not Blank
        if(!isBlank(moveX, moveY))
            return valid;
        
        //inspried from the Connet4Advanced
        //there are 9 directon vectors
        for(int dx = -1; dx <= 1 ; dx++)
            for(int dy = -1; dy <= 1; dy++){    
                if(dx == 0 && dy == 0) 
                    continue; //skip the origin which is self
                int count = 1; //init a counter marking the move
                try{
                    if(isOpponent(moveX + count * dx, moveY + count * dy)){ //try the nearest 1 block
                        count++; //there is 1 opponent's chess at the direction now, check anymore at the direction by adding count
                       while(true){
                           if(isOpponent(moveX + count * dx, moveY + count * dy)) //still have, still add
                                count++;
                           
                           else if(isFriend(moveX + count * dx, moveY + count * dy)){ //meet same chess, stop counting
                               if(flip){ //flip the chess if it is not for checking the pass condition
                                    for(int i = 1; i < count; i++){
                                        int x = moveX + i * dx;  
                                        int y = moveY + i * dy;
                                        pieces[x][y].setText(currentPlayer);
                                        Color color = currentPlayer.equals("BLACK")? Color.BLACK : Color.WHITE;
                                        pieces[x][y].setText(currentPlayer);
                                        pieces[x][y].setBackground(color);
                                        pieces[x][y].setOpaque(true);
                                    }
                               }
                               valid = true; //Due to meeting our friend, it is a valid move
                               break; //turn to next direction
                           }
                           else
                               break; //turn to next direction
                       }
                    }
                }
                catch(ArrayIndexOutOfBoundsException e) {}                
            }
        return valid;
    }
 
    //go throught the whole gameboard and check is there any valid move    
    private boolean mustPass(){
        for(int x = 0; x < xCount; x++)
            for(int y = 0; y < yCount; y++)
                if(isValidMove(x, y, false)) //there is valid move
                    return false;  //since there is valid move so return false
        return true; //after go through the whole gameboard, there is no valid move, so the player has to pass
    }
    
    //counting the numbers of the player
    private int countPieces(String player){        
        int count = 0;
        for(int x = 0; x < xCount; x++)
            for(int y = 0; y < yCount; y++)
                if(pieces[x][y].getText().equals(player))
                    count++;
        return count;
    } 
    
    public static void main(String[] args)
    {
        Reversi reversi;
        reversi = new Reversi();
        System.out.println("You are running class Reversi");
        reversi.setLocation(400, 20);
        reversi.verbose = false;

        // the game has started and GUI thread will take over here
    }
}
