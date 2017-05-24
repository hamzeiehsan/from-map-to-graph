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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;


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
}