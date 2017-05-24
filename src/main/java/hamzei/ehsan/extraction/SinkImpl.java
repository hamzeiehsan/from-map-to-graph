package hamzei.ehsan.extraction;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Ehsan Hamzei on 5/11/2017.
 */
public class SinkImpl implements Sink {
    private static final Logger LOG = LoggerFactory.getLogger(SinkImpl.class);

    public void process(EntityContainer entityContainer) {
        Entity entity = entityContainer.getEntity();
        EntityStorage.addEntity(entity);
    }

    public void initialize(Map<String, Object> map) {

    }

    public void complete() {

    }

    public void release() {

    }
}
