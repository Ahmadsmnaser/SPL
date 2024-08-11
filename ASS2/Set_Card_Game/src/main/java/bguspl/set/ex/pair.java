package bguspl.set.ex;
//import bguspl.set.Env;
public class pair {
    private Integer[] array;
    private Player player; 

public pair(Integer[] array, Player player){
    this.array = array;
    this.player =player;
}
public Player getPlayer(){
    return player;
}
public Integer[] getarray(){
    return array;
}
}
