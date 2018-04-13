
import javax.swing.JLabel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alu20908719v
 */
public class ScoreBoard extends JLabel implements IncrementScore{
    
    
    private int score;
    
    
    public ScoreBoard(){
        super();
        score=0;
    }
   
        public void increment(int points) {
         score += points;
        setText(""+score);
        
    }
    public void reset(){
        score=0;
         setText(""+score);
    }

    @Override
    public int getScore() {
        return score;
    }




   
}
