/**
 * Author:   hplong
 * Description: 测试教室间导航
 */
package work.bg.navigation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import work.bg.servive.NavigationImpl;

import java.util.Iterator;
import java.util.Vector;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NavigationClassroomTests {
    @Test
    public static void main(String[] args){
        NavigationImpl navigation = new NavigationImpl();
        navigation.initClassroom(320,601);
        Vector<String> vec = navigation.transferClassroom(navigation.getJump(),navigation.getTurn());
        Iterator iterator = vec.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}

