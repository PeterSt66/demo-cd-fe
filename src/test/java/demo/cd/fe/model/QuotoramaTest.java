package demo.cd.fe.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class QuotoramaTest {

    private static final int MAX_TRIES = 500;

    private void testGroup(String group) {
      Quotorama q =  new Quotorama();

      for(int i=0; i < MAX_TRIES; i++) {
        String[] quote = q.nextQuote(group);
        System.out.println("@@:#0#"+quote[0]+"#1#"+quote[1]+"#2#"+quote[1+1]+"@@");
        assertEquals(quote[0], group);
      }
    }


    @Test
    public void test1() {
        testGroup("1");
    }

    @Test
    public void test2() {
        testGroup("2");
    }


}
