package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import bohnenspiel.BohnenspielBoard;
import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielPlayer;
import bohnenspiel.BohnenspielMove.MoveType;

/** A Bohnenspiel player submitted by a student. */
public class StudentPlayer extends BohnenspielPlayer {

    public StudentPlayer() { super("260633352"); }
    
    private int eval(BohnenspielBoardState board_state, int turn) {
    	
    	int playerScore = board_state.getScore(player_id);
    	int oppScore = board_state.getScore(opponent_id);
    	int H1 = (playerScore - oppScore);
    			
    	int H2 = (playerScore - 36)*2;
    	int H3 = (36 - oppScore)*2;
    	
    
    	int[][] pits = board_state.getPits();
    	int firstValid = -1;
    	
        int[] my_pits = pits[turn];
        int[] opp_pits = pits[1-turn];
        int sum = 0;
        int oppSum = 0;
        for (int i=0 ; i<my_pits.length; i++) {
            sum += my_pits[i];
            oppSum += opp_pits[i];
            if(firstValid == -1 && my_pits[i] != 0) firstValid = (my_pits.length - i)*2;
        }
        
        int H4 = (sum - oppSum)*3;
        
        return H1 + H2 + H4 + firstValid;
    	
    }


    // negamaxPruning Search
    private int negamaxPruning(BohnenspielBoardState boardState, int depth, int a, int b, BohnenspielMove[] result, int turn, int originalDepth, int color) {
        if ((depth == 0) || boardState.gameOver()) {
            return color * eval(boardState, turn);
        }
        int bestValue = -1500;
		ArrayList<BohnenspielMove> moves = boardState.getLegalMoves();
		for(BohnenspielMove m : moves){
			BohnenspielBoardState clone = (BohnenspielBoardState) boardState.clone();
			clone.move(m);
			int v = -negamaxPruning(clone, depth - 1, -b, -a, result, turn, originalDepth, -color);
            
            if (v > bestValue) {
                bestValue = v;
                if (depth == originalDepth)  {
                    result[0] = m;
                }
            }
            a = Math.max(a, v);
            if( a >= b) break;
		}
        return bestValue;
        
    }



    public BohnenspielMove chooseMove(final BohnenspielBoardState board_state)
    {

        BohnenspielMove storedMove = board_state.getLegalMoves().get(0);

        final BohnenspielMove[] result = {storedMove};

        final int turn = board_state.getTurnPlayer();


        negamaxPruning(board_state, 12, -1000, 1000, result, turn, 12, 1);
        
        return result[0];
    }
}
