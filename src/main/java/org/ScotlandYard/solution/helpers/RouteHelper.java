package solution.helpers;

import solution.development.models.ViewRoute;

/**
 * A helper utility used throughout
 */
public class RouteHelper {

    /**
     * Determines whether or not a given route contains two position ids
     *
     * @param route The containing route
     * @param id1 the id of the first position
     * @param id2 the id of the second position
     * @return true if the route contains the position ids
     */
    public static boolean routeContains(ViewRoute route, int id1, int id2){
        return (route.id1 == id1 && route.id2 == id2) || (route.id2 == id1 && route.id1 == id2);
    }
}
