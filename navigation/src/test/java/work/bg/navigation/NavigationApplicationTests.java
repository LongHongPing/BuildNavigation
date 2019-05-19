package work.bg.navigation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import work.bg.pojo.Stair;
import work.bg.servive.NavigationImpl;

import java.util.Iterator;
import java.util.Vector;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NavigationApplicationTests {
    @Test
    public static void main(String[] args){
        NavigationImpl navigation = new NavigationImpl();
        navigation.init(2,601);
        Vector<String> vec = navigation.transfer(navigation.getJump(),navigation.getTurn());
        Iterator iterator = vec.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
