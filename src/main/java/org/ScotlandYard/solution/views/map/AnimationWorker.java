package solution.views.map;


import java.awt.*;
import java.util.ArrayList;

/**
 * This class handles animation and threading. Implementations of {@link solution.views.map.AnimationWorker.AnimationInterface} can add themselves
 * to this through {@link solution.views.map.AnimationWorker#addWork(solution.views.map.AnimationWorker.AnimationInterface)} and receive appropriate
 * callbacks on ticks.
 */
public class AnimationWorker implements Runnable{

    private final Component repaintableComponent;
    private ArrayList<AnimationInterface> callbacks;
    private boolean isRunning;
    private Thread thread;

    public interface AnimationInterface {

        /**
         * Called on every tick
         *
         * @return true if the animation is finished and should be cleared up
         */
        public boolean onTick();

        /**
         * Called after {@link solution.views.map.AnimationWorker.AnimationInterface#onTick()} returns true. Implementations may run
         * tidy up code here.
         */
        public void onFinished();
    }
    public AnimationWorker (Component component) {
        repaintableComponent = component;
        callbacks = new ArrayList<AnimationInterface>();
    }

    public void addWork(AnimationInterface callback){
        callbacks.add(callback);
        if(!isRunning){
            startThread();
        }
    }

    private void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        ArrayList<AnimationInterface> dirtyList = new ArrayList<AnimationInterface>();

        while(callbacks.size() > 0){

            isRunning = true;

            dirtyList.clear();

            //find all callbacks that are done and add to a dirty list
            for(AnimationInterface callback : callbacks){
                if(callback.onTick()){
                    dirtyList.add(callback);
                }
            }

            repaintableComponent.repaint();

            //remove dirty callbacks - rinse & repeat
            for(AnimationInterface dirtyCallback : dirtyList){
                dirtyCallback.onFinished();
                callbacks.remove(dirtyCallback);
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        callbacks.clear();
        isRunning = false;
    }


}
