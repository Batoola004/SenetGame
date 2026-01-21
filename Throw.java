// // Modified Throw.java

// public class Throw {
//     private Stick[] sticks = new Stick[4];

//     public Throw() {
//         for (int i = 0; i < 4; i++)
//             sticks[i] = new Stick();
//     }

//     public int makeThrow() {
//         int total = 0;
//         for (int i = 0; i < 4; i++) {
//             if (sticks[i].throwIt() == 'D')
//                 total++;
//         }
//         if (total == 0)
//             total = 5;
//         return total;
//     }

//     // Optional: Get visual representation of sticks for GUI
//     public String getSticksVisual() {
//         StringBuilder sb = new StringBuilder("Sticks: ");
//         for (int i = 0; i < 4; i++) {
//             sb.append(sticks[i].getVisual());
//             if (i < 3)
//                 sb.append(" ");
//         }
//         return sb.toString();
//     }

//     // Optional: Get numerical value for GUI
//     public int getLastThrowValue() {
//         int total = 0;
//         for (int i = 0; i < 4; i++) {
//             if (sticks[i].getFace() == 'D')
//                 total++;
//         }
//         return total == 0 ? 5 : total;
//     }
// }

import java.util.Random;

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
        return total;
    }
}