package net.katsuster.strview.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * ユニットテストクラス。
 */
@RunWith(Suite.class)
@SuiteClasses({
        //Test.class,
})
public class AllTest {
    protected AllTest() {
        //do nothing
    }

    public static void main(String[] args) {
        JUnitCore.main(AllTest.class.getName());
    }
}
