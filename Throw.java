import java.util.List;

public class Throw {
    private Stick[] sticks = new Stick[4];
    public Throw(){
        for (int i=0;i<4;i++)
            sticks[i]=new Stick();
    }
    public int makeThrow(){
        int total = 0;
        for(int i=0;i<4;i++){
            if(sticks[i].throwIt() == 'D')
                total++;
        }
        if(total == 0)
            return 5;

        return total;
    }
}
