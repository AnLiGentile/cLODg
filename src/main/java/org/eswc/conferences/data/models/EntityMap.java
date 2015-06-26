package org.eswc.conferences.data.models;

import java.net.URI;

public interface EntityMap {

	void addEntity(Entity person);

	Entity getEntity(String name);

	Entity getEntityByURI(URI uri);

}
