package net.matthiasauer.abocr.graphics.texture.archive;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class RenderTextureArchiveSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private final static Family family =
			Family.all(RenderTextureArchiveEventComponent.class).get();
	private final ComponentMapper<RenderTextureArchiveEventComponent> archiveEventMapper;
	private final ComponentMapper<RenderTextureArchiveEntryComponent> archiveEntryMapper;
	private ImmutableArray<Entity> archiveEntities;
	private PooledEngine engine;

	public RenderTextureArchiveSystem() {
		super(family);

		this.archiveEntryMapper =
				ComponentMapper.getFor(RenderTextureArchiveEntryComponent.class);
		this.archiveEventMapper =
				ComponentMapper.getFor(RenderTextureArchiveEventComponent.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		this.archiveEntities =
				engine.getEntitiesFor(
						Family.all(RenderTextureArchiveEntryComponent.class).get());
		this.engine = (PooledEngine) engine;
		
		super.addedToEngine(engine);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// TODO Auto-generated method stub
		RenderTextureArchiveEventComponent event =
				this.archiveEventMapper.get(entity);
		
		switch (event.type) {
		case Updated:
			this.updateEntry(event);
			break;
		case Deleted:
			this.deleteEntry(event);
			break;
		default:
			throw new NullPointerException("Unknown RenderTextureArchiveEventType !");
		}
		
		// remove that event
		entity.remove(RenderTextureArchiveEventComponent.class);
	}
	
	public Pixmap getPixmap(Texture texture) {
		Entity entity =
				this.getEntityForTexture(texture, false);
		
		if (entity == null) {
			// no entry - create
			entity = this.addEntry(texture);
		}
		
		// if there is an entry - return it
		RenderTextureArchiveEntryComponent entry =
				this.archiveEntryMapper.get(entity);
			
		return entry.pixmap;
	}
	
	private Entity addEntry(Texture texture) {
		Entity newEntity = this.engine.createEntity();
		RenderTextureArchiveEntryComponent newEntry =
				this.engine.createComponent(RenderTextureArchiveEntryComponent.class);
		
		newEntry.texture = texture;
		newEntry.pixmap = this.createPixmap(newEntry.texture);
		
		// add the entry
		this.engine.addEntity(newEntity);
		newEntity.add(newEntry);
		
		return newEntity;
	}
	
	private Entity getEntityForTexture(Texture texture, boolean expectEntry) {
		for (Entity entity : this.archiveEntities) {
			RenderTextureArchiveEntryComponent entry =
					this.archiveEntryMapper.get(entity);
			
			if (entry.texture == texture) {
				return entity;
			}
		}
		
		if (expectEntry) {
			throw new NullPointerException(
					"found no RenderTextureArchiveEntryComponent that matches the texture !");
		} else {
			return null;
		}
	}
	
	private void deleteEntry(RenderTextureArchiveEventComponent event) {
		Entity entity = 
				this.getEntityForTexture(event.texture, true);
		RenderTextureArchiveEntryComponent entry =
				entity.getComponent(RenderTextureArchiveEntryComponent.class);
		
		this.freePixmap(entry.pixmap);
		entity.remove(RenderTextureArchiveEntryComponent.class);
		this.engine.removeEntity(entity);
	}
	
	private void updateEntry(RenderTextureArchiveEventComponent event) {
		Entity entity = 
				this.getEntityForTexture(event.texture, true);
		RenderTextureArchiveEntryComponent entry =
				entity.getComponent(RenderTextureArchiveEntryComponent.class);
		
		entry.texture = event.texture;
		
		// remove old one and create new one
		this.freePixmap(entry.pixmap);
		entry.pixmap = this.createPixmap(entry.texture);
	}

	private Pixmap createPixmap(Texture texture) {
		// http://gamedev.stackexchange.com/questions/43943/how-to-detect-a-touch-on-transparent-area-of-an-image-in-a-libgdx-stage
		texture.getTextureData().prepare();
		
		return
				texture.getTextureData().consumePixmap();
	}
	
	private void freePixmap(Pixmap pixmap) {
		pixmap.dispose();
	}
}
