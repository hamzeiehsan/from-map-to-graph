package hamzei.ehsan.extraction;

import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ehsan Hamzei on 5/11/2017.
 */
public class EntityStorage {
    private static final Logger LOG = LoggerFactory.getLogger(EntityStorage.class);
    private static Map<Long, Relation> relations = new HashMap<Long, Relation>();
    private static Map<Long, Node> nodes = new HashMap<Long, Node>();
    private static Map<Long, Bound> bounds = new HashMap<Long, Bound>();
    private static Map<Long, Way> ways = new HashMap<Long, Way>();

    private static void addRelation(Relation r) {
        relations.put(r.getId(), r);
    }

    private static void addNode(Node n) {
        nodes.put(n.getId(), n);
    }

    private static void addBound(Bound b) {
        bounds.put(b.getId(), b);
    }

    private static void addWay(Way w) {
        ways.put(w.getId(), w);
    }

    public static void addEntity(Entity e) {
        if (e instanceof Relation)
            addRelation((Relation) e);
        else if (e instanceof Node)
            addNode((Node) e);
        else if (e instanceof Bound)
            addBound((Bound) e);
        else if (e instanceof Way)
            addWay((Way) e);
        else
            LOG.error("Invalid Entity in XML/OSM File :|");

    }

    public static Map<Long, Way> getWays() {
        return ways;
    }


    public static Map<Long, Bound> getBounds() {
        return bounds;
    }


    public static Map<Long, Node> getNodes() {
        return nodes;
    }


    public static Map<Long, Relation> getRelations() {
        return relations;
    }
}
