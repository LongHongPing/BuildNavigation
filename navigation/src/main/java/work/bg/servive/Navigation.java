package work.bg.servive;

import work.bg.pojo.Stair;

import java.util.Vector;

public interface Navigation {
     void init(int position,int room);
     void initClassroom(int roomFrom,int roomTo);
     void navigation(Stair stair, int roomPosition);
     void naviClassroom(int origin,int destination);
     Vector<String> transfer(int[] jump, int[] turn);
     Vector<String> transferClassroom(int[] jump,int[] turn);
}
