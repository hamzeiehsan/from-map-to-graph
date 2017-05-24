package hamzei.ehsan.model;

import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import java.util.Collection;

/**
 * Created by Ehsan Hamzei on 5/11/2017.
 */
public class DBModel {
    private Long id;
    private BoundingBox box;
    private Collection<Tag> tags;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Collection<Tag> getTags() {
        return tags;
    }

    public void setTags(Collection<Tag> tags) {
        this.tags = tags;
    }

    public BoundingBox getBox() {
        return box;
    }

    public void setBox(BoundingBox box) {
        this.box = box;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DBModel)
            if (this.id == ((DBModel) obj).id)
                return true;
        return false;
    }
}
