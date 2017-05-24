package hamzei.ehsan.util;

import hamzei.ehsan.config.InnerConfiguration;
import hamzei.ehsan.model.BoundingBox;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ehsan Hamzei on 5/11/2017.
 */
public class Convertor {
    public static Map<String, Double> boundingBoxToMap(BoundingBox box) {
        Map<String, Double> data = new HashMap<String, Double>();
        data.put(InnerConfiguration.MAX_LAT, box.getNW().getLat());
        data.put(InnerConfiguration.MAX_LON, box.getNW().getLon());
        data.put(InnerConfiguration.MIN_LAT, box.getSE().getLat());
        data.put(InnerConfiguration.MIN_LON, box.getSE().getLon());
        return data;
    }

    public static Map<String, String> tagCollectionToMap(Collection<Tag> tags) {
        Map<String, String> data = new HashMap<String, String>();
        for (Tag t : tags)
            data.put(t.getKey(), t.getValue());
        return data;
    }
}
