import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CalcTest {
    private static Calc calc;

    @BeforeClass
    public static void setUpClass(){
        calc = new Calc();
    }

    @Parameterized.Parameter(0)
    public String expression;
    @Parameterized.Parameter(1)
    public double expectedAnswer;

    @Parameterized.Parameters(name = "{index}: Evaluating expression {0} = {1}")
    public static Collection<Object[]> expressions() {
        return Arrays.asList(new Object[][] {
                {"1+2*3+4", 11.0},
                {"_", 11.0},
                {"(1+2)*(3+4)", 21.0},
                {"sqrt(2)*sqrt(2)", 2.0},
                {"pi=3.14159265359", 3.14159265359},
                {"cos(pi)", -1}
        });
    }

    @Test
    public void testEvaluate(){
        Assertions.assertEquals(calc.eval(expression), expectedAnswer, 0.0001);
    }
}
