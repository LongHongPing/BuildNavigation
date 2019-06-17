/**
 * Author:   hplong
 * Description: 楼梯
 */
package work.bg.pojo;

public class Stair {
    private int id;
    private int start;
    private int end;
    private int[][] room;

    public Stair(){
    }
    public Stair(int id,int start,int end){
        this.id = id;
        this.start = start;
        this.end = end;
        this.room = new int[5][3];
    }

    public void setId(int id){
         this.id = id;
    }
    public void setStart(int start){
         this.start = start;
    }
    public void setEnd(int end){
         this.end = end;
    }
    public void setRoom(int[][] room){
        for(int i = 0;i < room.length;i++){
            for(int j = 0;j < room[i].length;j++){
                this.room[i][j] = room[i][j];
            }
        }
    }

    public int getId(){
         return this.id;
    }
    public int getStart(){
         return this.start;
    }
    public int getEnd(){
         return this.end;
    }
    public int[] getRoom(int start){
         return this.room[start];
    }
    public int[][] getRoom(){
        return this.room;
    }
}

