import java.util.*;

public class Main {

    public static void main(String[] args) {

        // INITIAL SETUP
        int[] board = new int[Board.BOARD_SIZE];
        Scanner scanner = new Scanner(System.in);
        int currentPlayer = Board.HUMAN;

        final int TOTAL_PIECES = 5;
        int humanExited = 0;
        int aiExited = 0;

        // (Expectiminimax)
        AI ai = new AI();
        final int AI_DEPTH = 3;

        // Senet starting position: first 14 squares alternating
        for (int i = 0; i < 14; i++) {
            board[i] = (i % 2 == 0) ? Board.HUMAN : Board.AI;
        }

        System.out.println(Board.WHITE + "=== Senet – Ancient Egyptian Board Game ===" + Board.RESET);
        System.out.println("Human: " + Board.BLUE + "♙" + Board.RESET +
                " | AI: " + Board.RED + "♟" + Board.RESET);
        System.out.println("First player to exit all " + TOTAL_PIECES + " pieces wins!\n");

        while (true) {

            System.out.println("\n" +
                    (currentPlayer == Board.HUMAN ? "🔵 Your Turn" : "🔴 AI's Turn"));

            Board.printBoard(board);

            Throw sticks = new Throw();
            int roll = sticks.makeThrow();

            List<Move> moves = Rules.getPossibleMoves(board, currentPlayer, roll);

            if (moves.isEmpty()) {
                System.out.println(Board.YELLOW +
                        "❌ No valid moves available. Turn skipped." + Board.RESET);
            } else {

                if (currentPlayer == Board.HUMAN) {

                    System.out.println(Board.CYAN +
                            "🎯 Possible moves (roll: " + roll + "):" + Board.RESET);

                    for (int i = 0; i < moves.size(); i++) {
                        System.out.println((i + 1) + ") " + moves.get(i));
                    }

                    System.out.print("Enter choice (1–" + moves.size() + "): ");
                    int choice = -1;

                    try {
                        choice = scanner.nextInt() - 1;
                    } catch (Exception e) {
                        scanner.nextLine();
                    }

                    if (choice >= 0 && choice < moves.size()) {
                        Move selectedMove = moves.get(choice);

                        // ✅ VERBOSE = TRUE (real move)
                        Board.applyMove(board, selectedMove, Board.HUMAN, true);

                        if (selectedMove.to == -1 ||
                                Rules.applySpecialSquareEffect(selectedMove.to) == -1) {
                            humanExited++;
                            System.out.println(Board.BLUE +
                                    "🎉 Human piece exited! (" +
                                    humanExited + "/" + TOTAL_PIECES + ")" +
                                    Board.RESET);
                        }
                    } else {
                        System.out.println(Board.RED +
                                "❌ Invalid choice. Turn skipped." + Board.RESET);
                    }

                } else {

                    Move aiMove = ai.getBestMove(board, roll, AI_DEPTH);

                    if (aiMove == null) {
                        System.out.println(Board.YELLOW +
                                "🤖 AI has no valid moves." + Board.RESET);
                    } else {
                        System.out.println(Board.RED +
                                "🤖 AI chooses: " + aiMove + Board.RESET);

                        // ✅ VERBOSE = TRUE (real move)
                        Board.applyMove(board, aiMove, Board.AI, true);

                        if (aiMove.to == -1 ||
                                Rules.applySpecialSquareEffect(aiMove.to) == -1) {
                            aiExited++;
                            System.out.println(Board.RED +
                                    "🎉 AI piece exited! (" +
                                    aiExited + "/" + TOTAL_PIECES + ")" +
                                    Board.RESET);
                        }
                    }
                }

                System.out.println("\n" + Board.YELLOW +
                        "✨ Updated board after move:" + Board.RESET);
                Board.printBoard(board);
            }

            if (humanExited >= TOTAL_PIECES) {
                System.out.println("\n" + Board.BLUE +
                        "🏆 HUMAN WINS! All pieces exited!" + Board.RESET);
                break;
            }

            if (aiExited >= TOTAL_PIECES) {
                System.out.println("\n" + Board.RED +
                        "🏆 AI WINS! All pieces exited!" + Board.RESET);
                break;
            }

            currentPlayer = -currentPlayer;

            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        scanner.close();
        System.out.println(Board.WHITE +
                "Game Over. Thank you for playing!" + Board.RESET);
    }
}
