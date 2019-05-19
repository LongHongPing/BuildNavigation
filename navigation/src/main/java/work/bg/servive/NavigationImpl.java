/**
 * Author:   hplong
 * Description: 路线规划实现
 */
package work.bg.servive;

import org.springframework.stereotype.Service;
import work.bg.pojo.Stair;

import java.util.HashMap;
import java.util.Vector;

@Service
public class NavigationImpl implements Navigation{
    private Stair[] s = new Stair[14];
    private Stair stair = new Stair();      //起始楼梯
    private Stair sta = new Stair();        //目标楼梯
    private int roomPosition;               //目标教室位置
    private int door;                      //入口
    private int[] jump = new int[4];       //记上几层
    private int[] turn = new int[6];       //记转向

    private Vector<String> vector = new Vector<>();                    //结果集
    private HashMap<Integer,Integer> stairMap = new HashMap<>();       //楼梯/位置映射，位置-楼梯
    private HashMap<Integer,Integer> roomMap = new HashMap<>();     //教室/位置映射，教室-位置
    private HashMap<Integer,Integer> doorMap = new HashMap<>();     //方位/门映射，方位-门
    private HashMap<Integer,Stair> doorStairMap = new HashMap<>();  //门/楼梯映射，门-楼梯


    @Override
    public void init(int position,int room) {
        //初始化获取参数
        initRoom();
        roomPosition = roomMap.get(room);
        initDoor();
        door = doorMap.get(position);
        initStair();
        initDoorStair();
        stair = getStair(door);
        //可能会向下走的
        if(roomPosition/100-stair.getStart() < 0){
            stair = s[0];
        }
        //调用导航
        if(room == 107 || room == 320){
            turn[0] = 1;
        }else{
            navigation(stair,roomPosition);
        }

    }

    @Override
    public void navigation(Stair stair, int roomPosition){
        int floor = roomPosition/100;        //目标教室所在楼层
        sta = findStair();   //目标教室对应楼梯
        //起始楼梯和目标楼梯相同
        int i = 0;      //记录有几组数据
        int j = 0;
        if(sta.getId() == stair.getId()){
            jump[i++] = floor-stair.getStart();
            //记左/右走,负为左，正为右
            int room = findRoom();
            if(room < 0){
                if(roomPosition == 401|| roomPosition == 301 || roomPosition == 201 || roomPosition == 101){
                    turn[j++] = -2;
                }else{
                    turn[j++] = -1;
                }
            }else{
                turn[j++] = 1;
            }
        }else if(sta.getId() > stair.getId()){      //目标楼梯在起始楼梯右边
            if(floor <= stair.getEnd()){     //可以从起始楼梯直达目标教室所在层
                int jumpTemp = floor-stair.getStart();
                jump[i++] = jumpTemp;
                int near = findStaRoom(stair.getRoom(jumpTemp),1);
                if(near > 0){
                    turn[j++] = roomPosition-near+1;
                }else{
                    turn[j++] = roomPosition-near;
                }
            }else{                                          //不可以直达所在层
                int jumpTemp = stair.getEnd()-stair.getStart();      //上到当前楼梯最高可达
                jump[i++] = jumpTemp;
                int near = findStaRoom(stair.getRoom(jumpTemp),1);
                jumpTemp = floor-stair.getEnd();
                jump[i++] =jumpTemp;
                int index = 0;
                if(sta.getId() == 2 || sta.getId() == 7 || sta.getId() >= 10){
                    index = sta.getRoom().length-2-(floor-stair.getEnd());
                }else{
                    index = sta.getRoom().length-1-(floor-stair.getEnd());
                }
                if(index == 3 && sta.getId() > 6){
                    index--;
                }
                int positon = findStaRoom(sta.getRoom(index),1);
                turn[j++] = positon-near;
                turn[j++] = 1;
            }
        }else{                          //目标楼梯在起始楼梯左边
            if(floor <= stair.getEnd()){     //肯定可以从起始楼梯直达目标教室所在层
                int jumpTemp = floor-stair.getStart();
                jump[i++] = jumpTemp;
                int near = findStaRoom(stair.getRoom(jumpTemp),-1);
                if(near > 0){
                    turn[j++] = roomPosition-near;
                }else{
                    turn[j++] = roomPosition-Math.abs(near)-1;
                }
            }
        }
    }

    @Override
    public Vector<String> transfer(int[] jump,int[] turn) {
        //添加终点教室ID
        vector.addElement(""+roomPosition);
        //添加入口ID
        addDoor();

        int stairID = stair.getId();
        int positID = sta.getId();
        int dir = positID-stairID;
        //添加楼梯ID
        for(int i = 0;i < jump.length;i++){
            if(jump[i] == 0){
                break;
            }
            if(i == 0){
                for(int j = 1;j <= jump[i];j++){
                    vector.addElement("s"+stairID+j);
                }
            }else{
                int index = roomPosition/100-sta.getStart();
                for(int j = 0;j < jump[i];j++){
                    if(positID == 12){
                        positID--;
                    }
                    vector.addElement("s"+positID+index);
                    index--;
                }
            }
        }
        //添加板子ID
        for(int i = 0;i < turn.length;i++){
            if(turn[i] == 0){
                break;
            }
            if(Math.abs(turn[i]) == 1){
                vector.addElement("b"+roomPosition);
                continue;
            }
            if(i == 0){
                int[] room = stair.getRoom(jump[i]);
                int pos = findStaRoom(room,dir);
                findBoard(Math.abs(pos),vector,turn[i]);
            }else{
                int[] room = sta.getRoom(roomPosition/100-sta.getStart());
                int pos = findStaRoom(room,dir);
                findBoard(Math.abs(pos),vector,turn[i]);
            }
        }
        return vector;
    }

    private void addDoor() {
        if(door == 0){
            vector.addElement("d"+1);
        }else if(door == 2){
            vector.addElement("d"+3);
        }else{
            vector.addElement("d"+4);
        }
    }

    public int[] getJump(){
        return this.jump;
    }
    public int[] getTurn(){
        return this.turn;
    }
    private Stair getStair(int position) {
        return doorStairMap.get(position);
    }
    //初始化楼梯
    private void initStair(){
        Vector<int[][]> vec = new Vector<>();
        int[][] r1 = {{-101,-102,103},{-201,-202,203},{-301,-302,303},{-401,-402,403},{-501,502,0}};
        vec.addElement(r1) ;
        insert(r1,0);
        int[][] r2 = {{-104,105},{-204,205},{-304,305},{-404,405},{-503,504}};
        vec.addElement(r2);
        insert(r2,1);
        int[][] r3 = {{-205,206},{-305,306},{-405,406},{-504,505}};
        vec.addElement(r3);
        insert(r3,2);
        int[][] r4 = {{-206,207},{-306,307},{-406,407},{-505,506},{-601,601}};
        vec.addElement(r4);
        insert(r4,3);
        int[][] r5 = {{-207,208},{-307,308},{-407,408},{-506,507},{-601,602}};
        vec.addElement(r5);
        insert(r5,4);
        int[][] r6 = {{-208,209},{-308,309},{-408,409},{-507,508},{-602,603}};
        vec.addElement(r6);
        insert(r6,5);
        int[][] r7 = {{-209,210},{-309,310},{-409,410},{-508,509},{-603,604}};
        vec.addElement(r7);
        insert(r7,6);
        int[][] r8 = {{-310,311},{-410,411},{-509,510},{-604,605}};
        vec.addElement(r8);
        insert(r8,7);
        int[][] r9 = {{-311,312},{-411,412},{-510,511},{-605,606},{0,701}};
        vec.addElement(r9);
        insert(r9,8);
        int[][] r10 = {{-313,314},{-413,414},{-512,513},{-607,608},{-702,703}};
        vec.addElement(r10);
        insert(r10,9);
        int[][] r11 = {{-414,415},{-513,514},{-608,609},{-703,704}};
        vec.addElement(r11);
        insert(r11,10);
        int[][] r12 = {{-415,416},{-514,515},{-609,610},{-704,705}};
        vec.addElement(r12);
        insert(r12,11);
        int[][] r13 = {{-416,416},{-515,515},{-610,610},{-705,705}};
        vec.addElement(r13);
        insert(r13,12);
        for(int i = 0;i < 13;i++){
            int high = vec.elementAt(i).length-1;
            int start;
            if(vec.elementAt(i)[0][0] == 0){
                start = Math.abs(vec.elementAt(i)[0][1]/100);
            }else{
                start = Math.abs(vec.elementAt(i)[0][0]/100);
            }
            int end;
            if(vec.elementAt(i)[high][1] == 0){
                end = Math.abs(vec.elementAt(i)[high][0]/100);
            }else{
                end = Math.abs(vec.elementAt(i)[high][1]/100);
            }
            s[i] = new Stair(i,start,end);
            s[i].setRoom(vec.elementAt(i));
        }
    }
    //初始化门和方位关系
    private void initDoor() {
            doorMap.put(0,2);
            doorMap.put(1,4);
            doorMap.put(2,0);
            doorMap.put(3,2);
    }
    //初始化教室和其位置关系
    private void initRoom(){
        roomMap.put(101,101);
        roomMap.put(105,103);
        roomMap.put(106,104);
        roomMap.put(107,105);

        roomMap.put(201,201);
        roomMap.put(203,202);
        roomMap.put(206,203);
        roomMap.put(207,204);
        roomMap.put(208,205);
        roomMap.put(211,206);
        roomMap.put(215,207);
        roomMap.put(216,208);
        roomMap.put(217,209);

        roomMap.put(301,301);
        roomMap.put(303,302);
        roomMap.put(306,303);
        roomMap.put(307,304);
        roomMap.put(308,305);
        roomMap.put(311,306);
        roomMap.put(312,307);
        roomMap.put(314,308);
        roomMap.put(315,309);
        roomMap.put(316,310);
        roomMap.put(318,311);
        roomMap.put(320,312);

        roomMap.put(401,401);
        roomMap.put(403,402);
        roomMap.put(406,403);
        roomMap.put(407,404);
        roomMap.put(408,405);
        roomMap.put(411,406);
        roomMap.put(412,407);
        roomMap.put(414,408);
        roomMap.put(415,409);
        roomMap.put(416,410);
        roomMap.put(418,411);
        roomMap.put(419,412);
        roomMap.put(421,413);
        roomMap.put(422,414);
        roomMap.put(425,415);
        roomMap.put(426,416);

        roomMap.put(501,501);
        roomMap.put(502,503);
        roomMap.put(503,504);
        roomMap.put(506,505);
        roomMap.put(507,506);
        roomMap.put(509,507);
        roomMap.put(510,508);
        roomMap.put(511,509);
        roomMap.put(513,510);
        roomMap.put(514,511);
        roomMap.put(516,512);
        roomMap.put(517,513);
        roomMap.put(520,514);
        roomMap.put(521,515);

        roomMap.put(601,601);
        roomMap.put(602,603);
        roomMap.put(603,604);
        roomMap.put(605,605);
        roomMap.put(606,606);
        roomMap.put(608,607);
        roomMap.put(609,608);
        roomMap.put(612,609);
        roomMap.put(613,610);

        roomMap.put(701,701);
        roomMap.put(703,703);
        roomMap.put(706,704);
        roomMap.put(707,705);
    }
    //初始化门和楼梯关系
    private void initDoorStair() {
        doorStairMap.put(0,s[1]);
        doorStairMap.put(1,s[2]);
        doorStairMap.put(2,s[4]);
        doorStairMap.put(3,s[6]);
        doorStairMap.put(4,s[8]);
    }

    //匹配教室和楼梯关系
    private void insert(int[][] r,int stairID) {
        int len = r.length;
        int wid = r[0].length;
        for(int i = 0;i < len;i++){
            for(int j = 0;j < wid;j++){
                stairMap.put(r[i][j],stairID);
            }
        }
    }
    //确定目标教室对应楼梯
    private Stair findStair() {
        //只在楼梯右边
        if(roomPosition == 103 || roomPosition == 203 || roomPosition == 303 || roomPosition == 403
                || roomPosition == 502 || roomPosition == 701 || roomPosition == 606
                || roomPosition == 511 || roomPosition == 412){
            int StairID = stairMap.get(roomPosition);
            return s[StairID];
        }
        //只在楼梯左边
        if(roomPosition == 702 || roomPosition == 607 || roomPosition == 512 || roomPosition == 413
                || roomPosition == 101 || roomPosition == 102 || roomPosition == 201 || roomPosition == 202
                || roomPosition == 301 || roomPosition == 302 || roomPosition == 401 || roomPosition == 402
                || roomPosition == 501 || roomPosition == 104 || roomPosition == 204 || roomPosition == 304
                || roomPosition == 304 || roomPosition == 404 || roomPosition == 503 || roomPosition == 313){
            int StairID = stairMap.get(-roomPosition);
            return s[StairID];
        }
        int poStairID = stairMap.get(roomPosition);
        int neStairID = stairMap.get(-roomPosition);
        if(Math.abs(poStairID - stair.getId()) >= Math.abs(neStairID - stair.getId())){
            return s[neStairID];
        }
        return s[poStairID];
    }
    //确定楼梯有没有要的教室
    private int findRoom() {
        //只在楼梯左边的
        if(roomPosition == 702 || roomPosition == 607 || roomPosition == 512 || roomPosition == 413
                || roomPosition == 101 || roomPosition == 102 || roomPosition == 201 || roomPosition == 202
                || roomPosition == 301 || roomPosition == 302 || roomPosition == 401 || roomPosition == 402
                || roomPosition == 501 || roomPosition == 104 || roomPosition == 204 || roomPosition == 304
                || roomPosition == 304 || roomPosition == 404 || roomPosition == 503 || roomPosition == 313){
            int stairID = stairMap.get(-roomPosition);
            if(stairID == stair.getId()){
                return -roomPosition;
            }
        }
        //只在楼梯右边
        if(roomPosition == 103 || roomPosition == 203 || roomPosition == 303 || roomPosition == 403
                || roomPosition == 502 || roomPosition == 701 || roomPosition == 606
                || roomPosition == 511 || roomPosition == 412){
            int stairID = stairMap.get(roomPosition);
            if(stairID == stair.getId()){
                return roomPosition;
            }
        }

        int stairID = stairMap.get(roomPosition);
        if(stairID == stair.getId()){
            return roomPosition;
        }
        return -roomPosition;
    }
    //确定楼梯附近的教室
    private int findStaRoom(int[] room,int dir){
        int index = room.length/2;
        int result = room[index];
        if(dir > 0){       //目标在右边，取正
            if(room[index] < 0){
                result = room[index+1];
            }
        }else{          //目标在左边，取负
            if(room[index] > 0){
                result = room[index-1];
            }
        }
        return result;
    }
    //确定要哪些板子
    private void findBoard(int pos,Vector<String> vector,int temp){
        if(temp > 0){
            for(int j = 0;j < temp;j++){
                vector.addElement("b"+pos);
                pos++;
            }
        }else{
            for(int j = 0;j < Math.abs(temp);j++){
                vector.addElement("b"+pos);
                pos--;
            }
        }
    }
}

