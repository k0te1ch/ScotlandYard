package solution.helpers;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

/**
 * This class allows paths made up of one or two points to be interpolated into {@link java.awt.geom.Path2D}s with
 * many points to allow for animations along paths
 */
public class PathInterpolator {

    private Path2D path;
    private ArrayList<Segment> segments;
    private int currentSegmentIndex = 0;

    public PathInterpolator(Path2D path2D){
        this.path = path2D;
    }

    /**
     * This method reverses the point list so that animations run in the right direction
     *
     * @return this {@link solution.helpers.PathInterpolator}
     */
    public PathInterpolator reverse(){

        PathIterator iterator = path.getPathIterator(null);

        ArrayList<float[]> steps = new ArrayList<float[]>();

        while(!iterator.isDone()){
            float[] coords = new float[2];
            iterator.currentSegment(coords);
            steps.add(coords);
            iterator.next();
        }

        Path2D newPath = new Path2D.Double();

        for (int i = 0; i < steps.size(); i++) {
            float[] coords = steps.get(steps.size()-1-i);

            if(i == 0){
                newPath.moveTo(coords[0], coords[1]);
            }else{
                newPath.lineTo(coords[0], coords[1]);
            }

        }

        path = newPath;

        return this;
    }

    /**
     * Interpolates {@link solution.helpers.PathInterpolator#path} with the given maximum step
     *
     * @param step the maximum step between points
     * @return this {@link solution.helpers.PathInterpolator}
     */
    public PathInterpolator interpolate(float step){
        PathIterator iterator = path.getPathIterator(null);

        ArrayList<Float> xs = new ArrayList<Float>();
        ArrayList<Float> ys = new ArrayList<Float>();
        ArrayList<Float> rs = new ArrayList<Float>();

        //first empty the path's contents into lists of x and y coordinates
        while(!iterator.isDone()){
            float[] coords = new float[2];
            iterator.currentSegment(coords);
            xs.add(coords[0]);
            ys.add(coords[1]);
            iterator.next();
        }

        int i = 0;
        while(i < xs.size()-1){
            Float curX = xs.get(i + 1);
            Float curY = ys.get(i + 1);
            float dx = curX - xs.get(i);
            float dy = curY - ys.get(i);

            float newDx = 0;
            float newDy = 0;

            //if the y step is larger than the x step we
            // need to check the y step
            if(Math.abs(dy) > Math.abs(dx)){

                //if the y step is too large then create a smaller step
                if(Math.abs(dy) > step){
                    newDy = Math.signum(dy) * (step);
                    newDx = (newDy / dy) * dx;
                }

            }else{

                //if the x step is too large then create a smaller step
                if(Math.abs(dx) > step){
                    newDx = Math.signum(dx) * (step);
                    newDy = (newDx / dx) * dy;
                }

            }

            //if our new step isn't defined then we save the rotation and go to the next segment
            if(newDx == 0){
                i++;
                float rotation = (float) Math.atan2(dy,dx);
                rs.add(rotation);
                //otherwise we save the new step and iterate again until the step is
                //small enough to go to the next segment
            }else {
                float newX = curX - newDx;
                float newY = curY - newDy;

                xs.add(i + 1, newX);
                ys.add(i + 1, newY);
            }
        }

        //add a final rotation to complete the list
        rs.add(0,rs.get(0));

        segments = new ArrayList<Segment>();

        path = new Path2D.Double(Path2D.WIND_EVEN_ODD, xs.size());

        for (int i1 = 0; i1 < xs.size(); i1++) {
            float x = xs.get(i1);
            float y = ys.get(i1);
            float r = rs.get(i1);

            if(i1 == 0){
                path.moveTo(x,y);
            }else{
                path.lineTo(x,y);
            }

            segments.add(new Segment(x,y,r));

        }

        System.out.println("segments count: "+segments.size());

        return this;

    }

    public void nextSegment(){
        currentSegmentIndex++;
    }
    public Segment getCurrentSegment() {
        return segments != null ? segments.get(currentSegmentIndex) : null;
    }

    public boolean isDone() {
        return segments == null || currentSegmentIndex > segments.size()-1;
    }

    /**
     * This small class encapsulates segment positions and rotations
     */
    public class Segment {

        private final float x;
        private final float y;
        private final float rotation;

        public Segment(float x, float y, float rotation) {
            this.x = x;
            this.y = y;
            this.rotation = rotation;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getRotation() {
            return rotation;
        }
    }

    public Path2D getPath() {
        return path;
    }
}
