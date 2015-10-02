package net.matthiasauer.ecstools.utils;

import java.util.Collection;
import java.util.LinkedList;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

/**
 * Allows to store components without manually creating entities
 */
public class ContainerEntities {
	private final PooledEngine engine;
	private final Collection<Entity> entities;
	
	public ContainerEntities(PooledEngine engine) {
		this.engine = engine;
		this.entities = new LinkedList<Entity>();
	}
	
	/**
	 * Removes all entities in the container
	 */
	public void clear() {
		for (Entity entity : this.entities) {
			// remove components from the entity
			entity.removeAll();
			
			// remove the entity
			this.engine.removeEntity(entity);
		}
		
		this.entities.clear();
	}
	
	/**
	 * Adds the component to an entity that is newly created
	 */
	public void add(Component component) {
		Entity entity = this.engine.createEntity();
		entity.add(component);
		this.engine.addEntity(entity);
		this.entities.add(entity);
	}
}
