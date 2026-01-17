public class Throw {
    private Stick[] sticks = new Stick[4];

    public Throw() {
        for (int i = 0; i < 4; i++)
            sticks[i] = new Stick();
    }

    public int makeThrow() {
        int total = 0;
        for (int i = 0; i < 4; i++) {
            if (sticks[i].throwIt() == 'D')
                total++;
        }
        if (total == 0)
            total = 5;
        printThrow(total);
        return total;
    }

    private void printThrow(int value) {
        String RESET = "\u001B[0m";
        String BLUE = "\u001B[34m";
        String YELLOW = "\u001B[33m";

        System.out.print("Sticks: ");
        for (int i = 0; i < 4; i++) {
            char face = sticks[i].getFace();
            String visual = sticks[i].getVisual();

            if (face == 'D') {
                System.out.print(BLUE + visual + RESET);
            } else {
                System.out.print(YELLOW + visual + RESET);
            }
            if (i < 3)
                System.out.print(" ");
        }

        System.out.println(" → Roll: " + value);
    }
}