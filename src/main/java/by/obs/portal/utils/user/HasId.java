package by.obs.portal.utils.user;

import org.springframework.util.Assert;

public interface HasId {

    Integer getId();

    void setId(Integer id);

    default boolean isNew() {
        return getId() == null;
    }

    default int id() {
        Assert.notNull(getId(), "Entity must has id");
        return getId();
    }
}