/**
 * Author:   hplong
 * Description: 返回最短路径
 */
package work.bg.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.bg.servive.NavigationImpl;
import java.util.Vector;

@RestController
@RequestMapping("/v1")
public class PathController {
    @RequestMapping("/path")
    public Vector<String> findPath(String relative_location, String room_number){
        Vector<String> vec;
        NavigationImpl navigation = new NavigationImpl();
        int position = Integer.valueOf(relative_location);
        int room = Integer.valueOf(room_number);
        navigation.init(position,room);
        vec = navigation.transfer(navigation.getJump(),navigation.getTurn());
        return vec;
    }
}

