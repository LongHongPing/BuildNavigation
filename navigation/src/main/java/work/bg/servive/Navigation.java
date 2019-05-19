package work.bg.servive;

import work.bg.pojo.Stair;

import java.util.Vector;

public interface Navigation {
     void init(int position,int room);
     void navigation(Stair stair, int roomPosition);
     Vector<String> transfer(int[] jump, int[] turn);
}
