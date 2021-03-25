import com.worm.annotations.Column;
import com.worm.annotations.Table;

import java.util.Random;

@Table(name = "Table #1")
public class RandomizedStuff {
    @Column(name = "Column #1", isPrimaryKey = true)
    int anInt;
    @Column(name = "Column #2", isPrimaryKey = true)
    double aDouble;
    @Column(name = "Column #3")
    String aString;

    String bString;

    public RandomizedStuff() {
        Random random = new Random();
        anInt = random.nextInt();
        aDouble = random.nextDouble();
        aString = "Hello World!!!";
        bString = "BYE WORLD!!!!";
    }

    @Override
    public String toString() {
        return "RandomizedStuff{" +
                "anInt=" + anInt +
                ", aDouble=" + aDouble +
                ", aString='" + aString + '\'' +
                ", bString='" + bString + '\'' +
                '}';
    }
}
