/**
 * Author:   hplong
 * Description: 教室间导航控制
 */
package work.bg.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.bg.servive.NavigationImpl;

import java.util.Vector;

@RequestMapping("/v1")
@RestController
public class ClassroomController {
    @RequestMapping("/classroompath")
    public Vector<String> findClassPath(String origin, String destination){
        Vector<String> vec;
        NavigationImpl navigation = new NavigationImpl();
        System.out.println(origin+ " " +destination);
        int roomFrom = Integer.valueOf(origin);
        int roomTo = Integer.valueOf(destination);
       // System.out.println(roomFrom+" "+roomTo);
        navigation.initClassroom(roomFrom,roomTo);
        vec = navigation.transferClassroom(navigation.getJump(),navigation.getTurn());
        return vec;
    }
}

