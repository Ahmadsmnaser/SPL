package bguspl.set.ex;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.StyledEditorKit.BoldAction;
import javax.swing.text.html.HTMLDocument.BlockElement;

import bguspl.set.Env;

/**
 * This class manages the players' threads and data
 *
 * @inv id >= 0
 * @inv score >= 0
 */
public class Player implements Runnable {
    private Dealer dealer;
    private Boolean getFreeze; 
    protected Boolean dealerans;
    protected Boolean dealeranswered;
    public Object lock;
    public volatile boolean start;
    /**
     * The tokens that choosed by the player.
     */
    private Integer[] tokens ;
    /**
     * The game environment object.
     */
    private final Env env;

    /**
     * Game entities.
     */
    private final Table table;

    /**
     * The id of the player (starting from 0).
     */
    public final int id;

    /**
     * The thread representing the current player.
     */
    public Thread playerThread;

    /**
     * The thread of the AI (computer) player (an additional thread used to generate key presses).
     */
    private Thread aiThread;

    /**
     * True iff the player is human (not a computer player).
     */
    private final boolean human;

    /**
     * True iff game should be terminated.
     */
    protected volatile boolean terminate;

    /**
     * The current score of the player.
     */
    private int score;

    /**
     * The class constructor.
     *
     * @param env    - the environment object.
     * @param dealer - the dealer object.
     * @param table  - the table object.
     * @param id     - the id of the player.
     * @param human  - true iff the player is a human player (i.e. input is provided manually, via the keyboard).
     */
    public Player(Env env, Dealer dealer, Table table, int id, boolean human) {
        this.env = env;
        this.table = table;
        this.dealer = dealer;
        this.id = id;
        this.human = human;
        this.getFreeze = false;
        this.tokens = new Integer[3];
        for(int i=0 ; i<3 ; i++){
            this.tokens[i] = null;
        }
        dealerans= false;
        dealeranswered= false;
        lock = new Object();
        start = false;
    }

    /**
     * The main player thread of each player starts here (main loop for the player thread).
     */
    @Override
    public void run() {
        playerThread = Thread.currentThread();
        env.logger.info("thread " + Thread.currentThread().getName() + " starting.");
        if (!human) createArtificialIntelligence();                                                                              
        while (!terminate) {
            if(dealeranswered){
                dealeranswered =false;
                if(dealerans == true){
                    clear();
                    point();
                    dealer.TimeToReset = true;
                }else{
                    penalty();
                }
                dealerans = false;







                
            }
        }
        if (!human) try { aiThread.join(); } catch (InterruptedException ignored) {}
        env.logger.info("thread " + Thread.currentThread().getName() + " terminated.");
    }

    /**
     * Creates an additional thread for an AI (computer) player. The main loop of this thread repeatedly generates
     * key presses. If the queue of key presses is full, the thread waits until it is not full.
     */
    private void createArtificialIntelligence() { 
        // note: this is a very, very smart AI (!)
        aiThread = new Thread(()  -> {
        env.logger.info("Thread " + Thread.currentThread().getName() + " starting.");
        while (!terminate) {
             // Simulate player key press every second
             try {
                   Thread.sleep(500);
                   int randomslot = (int) (Math.random() * env.config.tableSize); 
                   keyPressed(randomslot);
                   } catch (InterruptedException ignored) {
                   // Thread interrupted, ignore and continue
                   } catch (Exception e) {
                           restartAI();
                    }}
                    env.logger.info("thread " + Thread.currentThread().getName() + " terminated.");
                 }, "computer-" + id);
      aiThread.start();
}
private void restartAI() {
    if (aiThread != null) {
    aiThread.interrupt();
    try {
         aiThread.join();
    } catch (InterruptedException e) {}
}
    createArtificialIntelligence();
}

    /**
     * Called when the game should be terminated.
     * @pre  - terminate = false.
     * @post - terminate = true.
     */
    public void terminate() {
        terminate = true;
    }

    /**
     * This method is called when a key is pressed.
     *
     * @param slot - the slot corresponding to the key pressed.
     */
    public void keyPressed(int slot) {
        if(getFreeze) return;
        synchronized(table){
            if(!table.canpress)
                return;
            if (table.slotToCard[slot] == null) {
                return;}
            int index = isExist(slot);
            if(isFull()){
                if(index != -1){
                    table.removeToken(this.id, slot);
                    toRemove(slot , index);
                }else{
                    return;
                }
            }else{
                if(index != -1){
                    table.removeToken(this.id, slot);
                    toRemove(slot , index);
                }else{
                    table.placeToken(this.id, slot);
                    toAdd(slot, canAdd(slot));
                }
                if(isFull()){
                    synchronized(dealer.playersSet){
                        pair thispair = new pair(tokens , this);
                        dealer.playersSet.add(thispair);
                    }
                    try{
                       lock.wait();
                    }catch(InterruptedException e){}
                    dealerans = true;
                }
            }
        }
    }

    /**
     * Award a point to a player and perform other related actions.
     *
     * @post - the player's score is increased by 1.
     * @post g- the player's score is updated in the ui.
     */
    public void point() {
        getFreeze = true;
        int ignored = table.countCards(); // this part is just for demonstration in the unit tests
        score = score +1;
        env.ui.setScore(id, score);
        long freezTime = env.config.pointFreezeMillis;
        while (freezTime > 0){
            env.ui.setFreeze(id, freezTime);
            freezTime -= 1000;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){}
        }
        env.ui.setFreeze(id, 0);
        getFreeze = false;

    }

    /**
     * Penalize a player and perform other related actions.
     */
    public void penalty() {
        getFreeze = true;
        long freezTime = env.config.penaltyFreezeMillis;
        while (freezTime > 0) {
            env.ui.setFreeze(id, freezTime);
            freezTime -= 1000;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){}
        }
        env.ui.setFreeze(id, 0);
        getFreeze = false;
        
    }

    public int score() {
        return score;
    }
    //-1 iff the slot isn't exist in the array , else return the index of the slot in the array
    public Integer isExist(int slot){
        for (int i=0 ; i<3 ; i++){
            if(tokens[i] != null){
            if(tokens[i] == slot){
                return i;
            }
        }
        }
        return -1;
    }
    public void toRemove (int slot , int index){
        if(isExist(slot) != -1){
            tokens[index] = null;
            table.removeToken(id, slot);
        }
    }
    //True iff the array is Full
    public Boolean isFull(){
        for(int i=0 ; i< 3 ;i++){
            if(tokens[i] == null){
                return false;
            }
        }
        return true;
    }
    //if we can add the slot , return index in the array
    public Integer canAdd(int slot){
        if(isExist(slot) == -1){
            for(int i=0 ; i<3 ; i++){
                if(tokens[i] == null)
                    return i;
            }
        }
        return -1;
    }
    public void toAdd (int slot , int index){
        if(isExist(slot) == -1 && canAdd(slot) != -1 ){
            tokens[index] = slot;
            table.placeToken(id, slot);
        }
    }
    public void clear(){
        for(int i=0 ; i<3 ; i++){
            tokens[i] = null;
        }
    }
    public int[] getCopy(Integer [] tokens){
        int [] copyOftokens = new int[3];
        for (int i = 0; i < 3; i++) {
            if(tokens[i] == null){
                copyOftokens[i] = -1;
            }else{
            copyOftokens[i] = tokens[i];
            }
        }
        return copyOftokens;
    }
    public int[] getCopytotest(Integer [] tokens){
        int [] copyOftokens = new int[3];
        for (int i = 0; i < 3; i++) {
            if(tokens[i] == null){
                copyOftokens[i] = -1;
            }else{
            copyOftokens[i] = table.slotToCard[tokens[i]];
            }
        }
        return copyOftokens;
    }
    public void setStart(boolean s) {
        start = s;
    }

}
