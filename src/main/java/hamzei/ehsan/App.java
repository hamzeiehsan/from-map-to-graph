package hamzei.ehsan;

import hamzei.ehsan.database.QueryHandler;
import hamzei.ehsan.extraction.DataExtraction;
import hamzei.ehsan.extraction.EntityStorage;
import hamzei.ehsan.extraction.SinkImpl;
import hamzei.ehsan.model.BoundingBox;
import hamzei.ehsan.model.DBModel;
import hamzei.ehsan.model.Point;
import hamzei.ehsan.spatial.DirectionRelations;
import hamzei.ehsan.spatial.SpatialUtil;
import org.apache.log4j.PropertyConfigurator;
import org.neo4j.driver.v1.*;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;
import org.openstreetmap.osmosis.xml.v0_6.XmlReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;


/**
 * Hello world!
 */
public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    static {
        PropertyConfigurator.configure("src/main/resource/log4j.properties");
    }

    public static void main(String[] args) {
        QueryHandler handler = QueryHandler.getInstance();
        handler.flush();
        DataExtraction extraction = DataExtraction.getInstance();
        List<DBModel> models = extraction.loadBuildings();
        //Insert Nodes
        for (DBModel model : models) {
            handler.insertEntity(model);
        }
        //Calculate the relationships
        for (int i = 0; i < models.size(); i++) {
            DBModel model = models.get(i);
            for (int j = i+1; j < models.size(); j++) {
                DBModel otherModel = models.get(j);
                if (!model.equals(otherModel)) {
                    if (SpatialUtil.isNeighbour(model, otherModel, models)) {
                        List<DirectionRelations> relations = SpatialUtil.calculateRelations(model.getBox(), otherModel.getBox());
                        if (relations != null)
                            if (relations.size() == 1 && relations.get(0) == DirectionRelations.unknown) {
                                LOG.info("Not Specific!");
                            } else if (relations.size() != 0) {
                                handler.insertRelationship(model, otherModel, relations);
                            }
                    }
                }
            }
        }
    }

    public static void osmTest() {
        XmlReaderFactory factory = new XmlReaderFactory();
        XmlReader reader = new XmlReader(new File("src/main/resource/map.osm"), true, CompressionMethod.None);
        reader.setSink(new SinkImpl());
        reader.run();
        Map<Long, Bound> bounds = EntityStorage.getBounds();
        Map<Long, Node> nodes = EntityStorage.getNodes();
        Map<Long, Way> ways = EntityStorage.getWays();
        Map<Long, Relation> relations = EntityStorage.getRelations();
        Map<Long, List<Point>> buildings = new HashMap<Long, List<Point>>();
        Map<Long, BoundingBox> boxes = new HashMap<Long, BoundingBox>();
        Set<String> uniqueType = new HashSet<String>();
        for (Relation r : relations.values()) {
            LOG.info("R-ID: " + r.getId());
            Collection<Tag> tags = r.getTags();
            for (Tag t : tags)
                if (t.getKey().equals("type")) {
                    LOG.info("R-TYPE: " + t.getValue());
                    uniqueType.add(t.getValue());
                    if (t.getValue().equals("building")) {
                        Long id = r.getId();
                        List<Point> points = new ArrayList<Point>();
                        List<RelationMember> members = r.getMembers();
                        boolean swap = true;
                        for (RelationMember member : members) {
                            swap = !swap;
                            if (member.getMemberType() == EntityType.Node)
                                points.add(new Point(nodes.get(member.getMemberId()).getLatitude(), nodes.get(member.getMemberId()).getLongitude()));
                            else if (member.getMemberType() == EntityType.Way) {
                                Way w = ways.get(member.getMemberId());
                                for (WayNode wN : w.getWayNodes())
                                    points.add(new Point(nodes.get(wN.getNodeId()).getLatitude(), nodes.get(wN.getNodeId()).getLongitude()));
                            }
                        }
                        buildings.put(id, points);
                        boxes.put(id, new BoundingBox(points));
                    }
                }
            LOG.info("-------------------------------");
        }
        LOG.info("END :)");
//        OsmHandler handler = new OsmHandler()
    }

    public static void neo4jTest() {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "13321332"));
        Session session = driver.session();
        session.run("CREATE (a:Person {name: {name}, title: {title}})", Values.parameters("name", "Arthur", "title", "King"));
        StatementResult result = session.run("MATCH (a:Person) WHERE a.name = {name} " +
                        "RETURN a.name AS name, a.title AS title",
                Values.parameters("name", "Arthur"));
        while (result.hasNext()) {
            Record record = result.next();
            System.out.println(record.get("title").asString() + " " + record.get("name").asString());
        }
        session.close();
        driver.close();
    }
}