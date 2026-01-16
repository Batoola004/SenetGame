import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Rules {
    

    // Probability map for Expectiminimax
    // Indices: 0 (unused), 1, 2, 3, 4, 5
    public static final double[] PROBABILITIES = { 0.0, 0.25, 0.375, 0.25, 0.0625, 0.0625 };

    // Function to simulate throwing sticks
    public static int throwSticks() {
        Random rand = new Random();
        int darkCount = 0;
        for (int i = 0; i < 4; i++) {
            if (rand.nextBoolean()) { // True = Dark side
                darkCount++;
            }
        }
        // If 0 dark sides (all light), move is 5. Otherwise, move is count.
        return (darkCount == 0) ? 5 : darkCount;
    }
    // Returns a new board state if the move is valid, or null if invalid
    public static int[] makeMove(int[] currentBoard, int fromIndex, int steps, int player) {
        if (fromIndex < 0 || fromIndex >= Board.BOARD_SIZE || currentBoard[fromIndex] != player) {
            return null; // Invalid source
        }

        int toIndex = fromIndex + steps;
        int[] newBoard = currentBoard.clone();

        // --- RULE: House of Happiness (26 / Index 25) ---
        // Must land exactly on 26. Cannot overshoot.
        if (fromIndex < 25 && toIndex > 25) {
            return null; // Cannot jump over House of Happiness
        }

        // --- RULE: Bearing Off (Exiting the board) ---
        if (toIndex >= Board.BOARD_SIZE) {
            // Special exit rules for last squares
            if (fromIndex == 29) { // House of Horus (30)
                // Can exit with any roll (implied by rules, or constrained to logic below)
                 newBoard[fromIndex] = Board.EMPTY;
                 return newBoard;
            } else if (fromIndex == 28) { // Re-Atoum (29)
                if (steps == 2) {
                     newBoard[fromIndex] = Board.EMPTY;
                     return newBoard;
                } else {
                    return null; // Must roll 2 to exit
                }
            } else if (fromIndex == 27) { // Three Truths (28)
                if (steps == 3) {
                     newBoard[fromIndex] = Board.EMPTY;
                     return newBoard;
                } else {
                    return null; // Must roll 3 to exit
                }
            } else {
                // Generic bearing off (usually requires exact roll in some variants, 
                // but prompt implies standard bearing off logic isn't the main focus 
                // aside from the special houses. We will assume exact roll required 
                // or standard overflow allowed ONLY if passed House 26).
                // For simplicity based on prompt: 
                // "The winner is the first to extract all stones."
                // We will assume you can bear off if you are past House 26.
                if (fromIndex > 25) {
                    newBoard[fromIndex] = Board.EMPTY;
                    return newBoard;
                }
                return null; 
            }
        }

        // --- Check Destination Occupancy ---
        int targetValue = newBoard[toIndex];

        if (targetValue == player) {
            return null; // Cannot land on own piece
        }
        
        // --- RULE: Swapping (Attacking) ---
        // If enemy is there, swap positions.
        if (targetValue == -player) {
            // Check for protection (optional rule, but standard: 2+ enemies block).
            // Prompt says: "If it occupies a stone of the opponent, swap." 
            // It does not explicitly mention blocking pairs. We will stick to the prompt.
            // Swap: Enemy goes to fromIndex, Player goes to toIndex.
            newBoard[fromIndex] = targetValue; 
            newBoard[toIndex] = player;
        } else {
            // Empty square
            newBoard[fromIndex] = Board.EMPTY;
            newBoard[toIndex] = player;
        }

        // --- RULE: House of Water (27 / Index 26) ---
        if (toIndex == 26) {
            // Go back to Rebirth (House 15 / Index 14)
            // Logic: If 15 is occupied, go to first empty square BEFORE 15.
            int rebirthIndex = 14;
            while (rebirthIndex >= 0 && newBoard[rebirthIndex] != Board.EMPTY) {
                rebirthIndex--;
            }
            if (rebirthIndex >= 0) {
                // Move the piece from Water (26) to Rebirth
                newBoard[26] = Board.EMPTY;
                newBoard[rebirthIndex] = player;
            } else {
                // Board is insanely full, shouldn't happen in standard play, stay put?
                // Or remove piece? We'll leave it at 26 if 0-14 is totally full (edge case).
            }
        }

        // --- RULE: Special exits failing ---
        // If on House 28 (Truths) and didn't roll 3, or House 29 (Atum) and didn't roll 2:
        // The prompt says: "If landed on... in the next turn exit if X... OTHERWISE return to rebirth."
        // This implies the check happens at the start of the turn or during the move attempt from that square.
        // We handle this inside the move logic:
        // If we are currently AT 28 and try to move with steps != 3, we fail to exit.
        // Does it move to a new square? No, the rule says "Return to Rebirth".
        // This means moving *from* 28 with a non-3 roll sends you to Rebirth.
        
        if (fromIndex == 27 && steps != 3) {
            // Failed to exit Three Truths -> Go to Rebirth logic
             return sendToRebirth(currentBoard, fromIndex, player);
        }
        if (fromIndex == 28 && steps != 2) {
             // Failed to exit Re-Atum -> Go to Rebirth logic
             return sendToRebirth(currentBoard, fromIndex, player);
        }
        if (fromIndex == 29 && toIndex <Board. BOARD_SIZE) {
            // House 30: "If not moved in that turn... return".
            // Since we are attempting to move, this is fine. 
            // If we couldn't move, the pass-turn logic handles it.
        }

        return newBoard;
    }

    private static int[] sendToRebirth(int[] board, int fromIndex, int player) {
        int[] newBoard = board.clone();
        newBoard[fromIndex] =Board.EMPTY;
        int rebirthIndex = 14; // House 15
        while (rebirthIndex >= 0 && newBoard[rebirthIndex] != Board.EMPTY) {
            rebirthIndex--;
        }
        if (rebirthIndex >= 0) newBoard[rebirthIndex] = player;
        return newBoard;
    }


    //this is how we are calctuing hureister
    //$$H(state) = (Sum\_Indices_{AI} + BearOffBonus_{AI}) - (Sum\_Indices_{Human} + BearOffBonus_{Human})$$

    // Heuristic Function
    public static double evaluate(int[] board) {
        double score = 0;
        int aiPieces = 0;
        int humanPieces = 0;

        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            if (board[i] == Board.AI) {
                aiPieces++;
                score += (i + 1); // Value position advancement
                if (i > 25) score += 5; // Bonus for being in special houses
            } else if (board[i] == Board.HUMAN) {
                humanPieces++;
                score -= (i + 1);
                if (i > 25) score -= 5;
            }
        }
        
        // Huge bonus for bearing off pieces (fewer pieces on board is better)
        score += (7 - aiPieces) * 50;
        score -= (7 - humanPieces) * 50;

        return score;
    }

    public static long nodesVisited = 0;

    // Expectiminimax
    // nodeType: 0 = Chance, 1 = Max (AI), 2 = Min (Human)
    public static double expectiminimax(int[] board, int depth, int nodeType, int rolledValue) {
        nodesVisited++;
        
        if (depth == 0 || hasWon(board, Board.AI) || hasWon(board, Board.HUMAN)) {
            return evaluate(board);
        }

        // CHANCE NODE (Simulate Rolling)
        // In this implementation structure, we alternate: Move -> Chance -> Move
        if (nodeType == 0) {
            double expectedValue = 0;
            // Iterate all possible dice rolls (1, 2, 3, 4, 5)
            for (int roll = 1; roll <= 5; roll++) {
                double prob = PROBABILITIES[roll];
                // After roll, it becomes the current player's turn to move
                // We need to know WHOSE turn it is. 
                // Let's assume the parent caller tracks this. 
                // However, standard expectiminimax usually implies:
                // Current Node (AI to move) -> Children (Resulting Boards) -> Chance Nodes (Opponent rolling)
                
                // Revised Recursion for cleaner code:
                // We will handle the "Split" inside the MAX/MIN nodes directly 
                // or pass a "Next Player" flag.
            }
        }
        return 0; 
    }
    
    // Cleaner Recursive Structure
    public static double expectiminimaxRecursive(int[] board, int depth, boolean isAiTurn, int fixedRoll) {
        nodesVisited++;
        
        if (hasWon(board, Board.AI)) return 10000 + depth; // Win faster
        if (hasWon(board, Board.HUMAN)) return -10000 - depth;
        if (depth == 0) return evaluate(board);

        // If fixedRoll is 0, it means we are at a CHANCE node (need to roll)
        if (fixedRoll == 0) {
            double weightedAverage = 0;
            for (int r = 1; r <= 5; r++) {
                // Recurse with the specific roll determined
                double val = expectiminimaxRecursive(board, depth - 1, isAiTurn, r);
                weightedAverage += (val * PROBABILITIES[r]);
            }
            return weightedAverage;
        }

        // DECISION NODE (Max or Min)
        // Use the fixedRoll to generate moves
        List<int[]> moves = getPossibleMoves(board, isAiTurn ? Board.AI :Board. HUMAN, fixedRoll);

        if (moves.isEmpty()) {
            // Pass turn
            // Next state is Chance node (fixedRoll=0) for the OTHER player
            return expectiminimaxRecursive(board, depth - 1, !isAiTurn, 0);
        }

        if (isAiTurn) {
            // MAX Node
            double maxEval = Double.NEGATIVE_INFINITY;
            for (int[] nextBoard : moves) {
                // After AI moves, it's Human's turn to ROLL (fixedRoll = 0)
                double eval = expectiminimaxRecursive(nextBoard, depth - 1, !isAiTurn, 0);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            // MIN Node
            double minEval = Double.POSITIVE_INFINITY;
            for (int[] nextBoard : moves) {
                // After Human moves, it's AI's turn to ROLL (fixedRoll = 0)
                double eval = expectiminimaxRecursive(nextBoard, depth - 1, !isAiTurn, 0);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    // Generate all possible resulting boards for a specific roll
    public static List<int[]> getPossibleMoves(int[] board, int player, int steps) {
        List<int[]> moves = new ArrayList<>();
        boolean canMove = false;

        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            if (board[i] == player) {
                int[] result = makeMove(board, i, steps, player);
                if (result != null) {
                    moves.add(result);
                    canMove = true;
                }
            }
        }
        return moves;
    }

    // Check win condition
    public static boolean hasWon(int[] board, int player) {
        for (int i : board) {
            if (i == player) return false;
        }
        return true;
    }
}

