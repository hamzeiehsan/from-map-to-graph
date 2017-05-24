package hamzei.ehsan.database;

import com.google.gson.Gson;
import hamzei.ehsan.model.DBModel;
import hamzei.ehsan.spatial.DirectionRelations;
import org.neo4j.driver.v1.Values;

import java.util.List;

/**
 * Created by Ehsan Hamzei on 5/11/2017.
 */
public class QueryHandler {
    private static QueryHandler instance;
    private static Gson gson = new Gson();
    SessionManager manager;

    private QueryHandler() {
        manager = SessionManager.getInstance();
    }

    public static QueryHandler getInstance() {
        if (instance == null)
            instance = new QueryHandler();
        return instance;
    }

    public void insertEntity(DBModel model) {
        manager.getSession().run("CREATE (a:SpatialEntity {id: {id}, bound: {bound}, tags: {tags}, type: {type}})",
                Values.parameters("id", model.getId(), "bound", gson.toJson(model.getBox()), "tags", gson.toJson(model.getTags()), "type", model.getType()));
        manager.close();
    }

    public void insertRelationship (DBModel model, DBModel otherModel, List<DirectionRelations> relations) {
        for (DirectionRelations relation : relations)
            manager.getSession().run("MATCH (a:SpatialEntity),(b:SpatialEntity)\n" +
                    "WHERE a.id = {id1} AND b.id = {id2}\n" +
                    "CREATE (a)-[r:"+relation.name()+"]->(b)\n" +
                    "RETURN r", Values.parameters("id1", model.getId(), "id2", otherModel.getId()));
        manager.close();
    }

    public void flush() {
        manager.getSession().run("MATCH (n:SpatialEntity)\n" +
                "DETACH DELETE n");
        manager.close();
    }
}
