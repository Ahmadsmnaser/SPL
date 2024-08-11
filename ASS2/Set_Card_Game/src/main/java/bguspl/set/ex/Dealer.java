package bguspl.set.ex;

import bguspl.set.Env;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class manages the dealer's threads and data
 */
public class Dealer implements Runnable {
    protected volatile LinkedBlockingQueue <pair> playersSet;
    /**
     * The game environment object.
     */
    private final Env env;

    /**
     * Game entities.
     */
    private final Table table;
    private final Player[] players;

    /**
     * The list of card ids that are left in the dealer's deck.
     */
    private final List<Integer> deck;

    /**
     * True iff game should be terminated.
     */
    private volatile boolean terminate;

    /**
     * The time when the dealer needs to reshuffle the deck due to turn timeout.
     */
    private Thread[] playersThreads;
    public Thread TimerThread;
    private long Timer;
    private long reshuffleTime = Long.MAX_VALUE;
    public  boolean TimeToReset;
    private  List<Integer> EmptySlots;




    public Dealer(Env env, Table table, Player[] players) {
        this.env = env;
        this.table = table;
        this.players = players;
        TimeToReset = false;
        this.playersSet = new LinkedBlockingQueue<>();
        this.playersThreads = new Thread[players.length];
        deck = IntStream.range(0, env.config.deckSize).boxed().collect(Collectors.toList());
        EmptySlots = IntStream.range(0, env.config.tableSize).boxed().collect(Collectors.toList());
        TimerThread = new Thread();
        Timer = env.config.turnTimeoutMillis;
}

    /**
     * The dealer thread starts here (main loop for the dealer thread).
     */
    @Override
    public void run() {
        if(env.config.turnTimeoutMillis < 0){  //Not displaying timer part of bonus
        TimerThread = new Thread(() -> {
            reshuffleTime=System.currentTimeMillis()+ env.config.turnTimeoutMillis;
            Timer = reshuffleTime - System.currentTimeMillis();;
            while (!terminate) {
                //env.timerPanel.timerField.setText("");
                try {
                    synchronized (this) {
                        //System.out.println("wait");
                        wait();
                    }
                } catch (InterruptedException e) {}
                while (Timer > 5000) {
                    env.ui.setCountdown(Timer, false);
                    Timer = Timer - 1000;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
                while (Timer<=5000&&Timer >= 0) {
                    env.ui.setCountdown(Timer, true);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    Timer = Timer - 10;

                }
                synchronized (this) {
                    notifyAll();
                }

            }
        });
        TimerThread.start();
        for (int i = 0; i < players.length; i++) {
            playersThreads[i] = new Thread(players[i], "Player " + i);
            playersThreads[i].start();
        }
        env.logger.info("thread " + Thread.currentThread().getName() + " starting.");
        while (!shouldFinish()) {
            Collections.shuffle(deck);
            placeCardsOnTable();                
            updateTimerDisplay(true);
            if(env.config.hints)
            table.hints();
            timerLoop();
            removeAllCardsFromTable();
            TimeToReset = true;
            
        }
        //TimerThread.interrupt();
        terminate();
        announceWinners();
        env.logger.info("thread " + Thread.currentThread().getName() + " terminated.");
    }
    else if(env.config.turnTimeoutMillis > 0){   //no changes part of bonus
        TimerThread = new Thread(() -> {
            reshuffleTime=System.currentTimeMillis()+ env.config.turnTimeoutMillis;
            Timer = reshuffleTime - System.currentTimeMillis();;
            while (!terminate) {
                try {
                    synchronized (this) {
                        System.out.println("wait");
                        wait();

                    }
                } catch (InterruptedException e) {
                }
                while (Timer > 5000) {
                    env.ui.setCountdown(Timer, false);
                    Timer = Timer - 1000;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }

                while (Timer<=5000&&Timer >= 0) {
                    env.ui.setCountdown(Timer, true);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    Timer = Timer - 10;
                }
                synchronized (this) {
                    notifyAll();
                }
            }

        });

        TimerThread.start();
        for (int i = 0; i < players.length; i++) {
            playersThreads[i] = new Thread(players[i], "Player " + i);
            playersThreads[i].start();
        }
        env.logger.info("thread " + Thread.currentThread().getName() + " starting.");
        while (!shouldFinish()) {
            Collections.shuffle(deck);
            placeCardsOnTable();                
            updateTimerDisplay(true);
            if(env.config.hints)
            table.hints();
            timerLoop();
            removeAllCardsFromTable();
            TimeToReset = true;
        }
        terminate();
        announceWinners();
        env.logger.info("thread " + Thread.currentThread().getName() + " terminated.");
    }
    else{
        TimerThread = new Thread(() -> {
            reshuffleTime=System.currentTimeMillis()+ env.config.turnTimeoutMillis;
            Timer = reshuffleTime - System.currentTimeMillis();;
            while (!terminate) {
                try {
                    synchronized (this) {
                        System.out.println("wait");
                        wait();
                    }
                } catch (InterruptedException e) {
                }
                while (Timer > 5000) {
                    env.ui.setCountdown(Timer, false);
                    Timer = Timer + 1000;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                   }
                }
                while (Timer<=5000&&Timer >= 0) {
                    env.ui.setCountdown(Timer, true);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                   Timer = Timer + 10;
                }
                synchronized (this) {
                    notifyAll();
                }
            }
        });
        TimerThread.start();
        for (int i = 0; i < players.length; i++) {
            playersThreads[i] = new Thread(players[i], "Player " + i);
            playersThreads[i].start();
        }

        env.logger.info("thread " + Thread.currentThread().getName() + " starting.");
        while (!shouldFinish()) {
            Collections.shuffle(deck);
            placeCardsOnTable();                
            updateTimerDisplay(true);
            if(env.config.hints)
            table.hints();
            timerLoop();
            removeAllCardsFromTable();
            TimeToReset = true;
        }

        terminate();
        announceWinners();
        env.logger.info("thread " + Thread.currentThread().getName() + " terminated.");
    }

}



    /**
     * The inner loop of the dealer thread that runs as long as the countdown did not time out.
     */
    private void timerLoop() {
        updateTimerDisplay(true);
        table.canpress = true;
        while (!terminate && Timer > 0) {
            synchronized (this) {
                try {
                    wait(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            updateTimerDisplay(false);
            removeCardsFromTable();
            placeCardsOnTable();
        }
    }

    /**
     * Called when the game should be terminated.
     * @pre  - All player.terminate = false.
     * @post - All player.terminate = true.
     */
    public void terminate() {
        LinkedList<Integer> list=new LinkedList<>();

        for(int i=0;i<table.slotToCard.length;i++){
            if(table.slotToCard[i]!=null)
                list.add(table.slotToCard[i]);
        }
            if(env.util.findSets(list,1).size()==0){
                terminate=true;
            for(int i=0;i<players.length;i++)
                players[i].terminate();
        }        
        for(int i=0;i<players.length;i++)
                if(!players[i].terminate){
                players[i].terminate();
                }
        terminate =true;
    }

    /**
     * Check if the game should be terminated or the game end conditions are met.
     *
     * @return true iff the game should be finished.
     */
    private boolean shouldFinish() {
        return terminate || env.util.findSets(deck, 1).size() == 0;
    }

    /**
     * Checks cards should be removed from the table and removes them.
     * @pre playerSet is not null
     */
    private void removeCardsFromTable() {
        Boolean isLegal = false;
        if(playersSet == null || playersSet.isEmpty()){
            return;
        }else{
            table.canpress = false;
            pair p = playersSet.remove();
            Player player = p.getPlayer();
            Integer [] array = p.getarray();
            int [] copy = player.getCopy(array);
            int[] testcopy = player.getCopytotest(array); 
            isLegal = env.util.testSet(testcopy);
            if(isLegal == true){
                player.dealeranswered = true;
                player.dealerans = true;
                for(int i=0 ; i<3 ; i++){
                    table.removeToken(player.id, copy[i]);
                    EmptySlots.add(copy[i]);
                    table.removeCard(copy[i]);
                    for(int playerid = 0 ; playerid < players.length ; playerid++){
                        removetokenfromallPlayers(players[playerid] , copy[i]);
                    }
                }
            }else{
                player.dealeranswered = true;
                player.dealerans = false;
            }
            /////////////////////////////////
            try{
               player.lock.notifyAll();
            }catch(Exception e) {}
            ////////////////////////////////
            table.canpress = true;
            }
        }
        private void removetokenfromallPlayers(Player p , int token) {
           if(p.isExist(token) != -1){
                p.toRemove(token, p.isExist(token));
           }
        }
        

    /**
     * Check if any cards can be removed from the deck and placed on the table.
    // * @pre - there is Emptyslots in the array and cards to fill them
    // * @post- all the slots filled by cards from the deck
     */
    private void placeCardsOnTable() {
        synchronized (table) {
            Collections.shuffle(EmptySlots);
            while (!(EmptySlots.isEmpty())) {
                int lastslot = EmptySlots.remove(EmptySlots.size() - 1);
                if (!deck.isEmpty()) {
                    table.placeCard(deck.remove(deck.size() - 1), lastslot);
                }
            }
            table.canpress = true;
        }
        


    }

    /**
     * Sleep for a fixed amount of time or until the thread is awakened for some purpose.
     */
    private void sleepUntilWokenOrTimeout() {
          synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reset and/or update the countdown and the countdown display.
     */
    private void updateTimerDisplay(boolean reset) {
    
    if (TimeToReset) {
        Timer = env.config.turnTimeoutMillis;
         TimeToReset = false;
     }
     synchronized (this) {

         notifyAll();
     }}

    /**
     * Returns all the cards from the table to the deck.
     */
    private void removeAllCardsFromTable() {
        synchronized (table) {
        for (int slot =0 ; slot < env.config.tableSize ; slot++){
            EmptySlots.add(slot);
            for(int player =0 ; player< players.length ; player++){
                players[player].clear();
            }
        }
        Collections.shuffle(EmptySlots);
        for(int i =0; i < env.config.tableSize && !EmptySlots.isEmpty(); i++){
            int j = EmptySlots.remove(EmptySlots.size() - 1);
            if (table.slotToCard[j] != null) {
                deck.add(table.slotToCard[j]);
            }
            table.removeCard(j);
        }
        EmptySlots = IntStream.range(0, env.config.tableSize).boxed().collect(Collectors.toList());
        }
    }
    

    /**
     * Check who is/are the winner/s and displays them.
     */
    private void announceWinners() {
        List<Integer> winners = new ArrayList<>();

        int maxScore = 0;

        for (Player player : players) {
            int score = player.score();
            if (maxScore < score) {
                maxScore = score;
            }
        }

        for (Player player : players) {
            if (maxScore == player.score()) {
                winners.add(player.id);
            }
        }

        int[] listWinner = winners.stream().mapToInt(Integer::intValue).toArray();
        env.ui.announceWinner(listWinner);
    }

    public Boolean setCheck(int id , int[] tokens){
        if(env.util.testSet(tokens) == true){
            return true;
        }
        return false;
    }

}
