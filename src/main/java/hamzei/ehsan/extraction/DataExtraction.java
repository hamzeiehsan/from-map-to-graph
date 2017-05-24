package hamzei.ehsan.extraction;

import hamzei.ehsan.config.Configuration;
import hamzei.ehsan.model.BoundingBox;
import hamzei.ehsan.model.DBModel;
import hamzei.ehsan.model.Point;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;
import org.openstreetmap.osmosis.xml.v0_6.XmlReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Created by Ehsan Hamzei on 5/11/2017.
 */
public class DataExtraction {
    private static final Logger LOG = LoggerFactory.getLogger(DataExtraction.class);
    private static DataExtraction instance;

    private DataExtraction() {
        LOG.info("DataExtraction Module is Loaded...");
    }

    public static DataExtraction getInstance() {
        if (instance == null)
            instance = new DataExtraction();
        return instance;
    }

    public void extract() {
        XmlReaderFactory factory = new XmlReaderFactory();
        XmlReader reader = new XmlReader(new File(Configuration.OSM_FILE_ADDRESS), true, CompressionMethod.None);
        reader.setSink(new SinkImpl());
        reader.run();
    }

    public List<DBModel> loadBuildings() {
        if (EntityStorage.getRelations().size() == 0)
            extract();
        List<DBModel> models = new ArrayList<DBModel>();
        Set<String> uniqueType = new HashSet<String>();
        for (Relation r : EntityStorage.getRelations().values()) {
            LOG.info("R-ID: " + r.getId());
            Map<String, String> tagsInfo = new HashMap<String, String>();
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
                                points.add(new Point(EntityStorage.getNodes().get(member.getMemberId()).getLatitude(), EntityStorage.getNodes().get(member.getMemberId()).getLongitude()));
                            else if (member.getMemberType() == EntityType.Way) {
                                Way w = EntityStorage.getWays().get(member.getMemberId());
                                for (WayNode wN : w.getWayNodes())
                                    points.add(new Point(EntityStorage.getNodes().get(wN.getNodeId()).getLatitude(), EntityStorage.getNodes().get(wN.getNodeId()).getLongitude()));
                            }
                        }
                        DBModel model = new DBModel();
                        model.setBox(new BoundingBox(points));
                        model.setId(id);
                        model.setType(t.getValue());
                        model.setTags(tags);
                        models.add(model);
                    }
                }
            LOG.info("-------------------------------");
        }
        return models;
    }
}