package net.matthiasauer.ecstools.input.base.touch;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.matthiasauer.ecstools.graphics.RenderComponent;
import net.matthiasauer.ecstools.graphics.RenderedComponent;
import net.matthiasauer.ecstools.graphics.texture.archive.RenderTextureArchiveSystem;
import net.matthiasauer.ecstools.utils.ContainerEntities;

/**
 * Catches touch up events and distributes them to the graphic which was
 * touched - pixel perfect detection on the texture is performed,
 * if multiple textures match - then the one with with the highest
 * layer.order is returned !
 * A InputTouchTriggeredComponent is created and attached to the entity
 * with the matching texture 
 */
public class InputTouchGeneratorSystem extends EntitySystem implements InputProcessor {
	private final InputMultiplexer inputMultiplexer;
	private final OrthographicCamera camera;
	private ContainerEntities containerEntities;
	private PooledEngine engine;
	private ImmutableArray<Entity> targetEntities;
	private ComponentMapper<RenderComponent> renderComponentMapper;
	private ComponentMapper<RenderedComponent> renderedComponentMapper;
	private ComponentMapper<InputTouchTargetComponent> targetComponentMapper;
	private final List<InputTouchEventComponent> lastEvents;
	private RenderTextureArchiveSystem archive;
	
	public InputTouchGeneratorSystem(InputMultiplexer inputMultiplexer, OrthographicCamera camera) {
		this.camera = camera;
		this.lastEvents = new LinkedList<InputTouchEventComponent>();
		this.inputMultiplexer = inputMultiplexer;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		this.inputMultiplexer.addProcessor(this);
		
		this.engine = (PooledEngine)engine;
		this.containerEntities =
				new ContainerEntities(this.engine);
		this.renderComponentMapper =
				ComponentMapper.getFor(RenderComponent.class);
		this.renderedComponentMapper =
				ComponentMapper.getFor(RenderedComponent.class);
		this.targetComponentMapper =
				ComponentMapper.getFor(InputTouchTargetComponent.class);
		this.targetEntities =
				this.engine.getEntitiesFor(
						Family.all(
								InputTouchTargetComponent.class,
								RenderComponent.class,
								RenderedComponent.class).get());
		this.archive =
				this.engine.getSystem(RenderTextureArchiveSystem.class);
		
		super.addedToEngine(engine);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		this.inputMultiplexer.removeProcessor(this);
		
		super.removedFromEngine(engine);
	}
	
	private Vector2 getPosition(InputTouchEventComponent event, boolean isProjected) {
		if (isProjected) {
			return event.projectedPosition;
		} else {
			return event.unprojectedPosition;
		}
	}
	
	private Rectangle getRectangle(boolean isProjected, RenderedComponent renderedComponent) {
		if (isProjected) {
			return renderedComponent.renderedTarget;
		} else {
			Rectangle rectangle =
					new Rectangle(renderedComponent.renderedTarget);

			// 'unzoom' the rendered rectangle - because the 
			// position is also 'unzoomed' (unprojected)
			rectangle.x /= renderedComponent.zoomFactor;
			rectangle.y /= renderedComponent.zoomFactor;
			
			return rectangle;
		}
	}
	
	private boolean touchesVisiblePartOfTarget(
			InputTouchEventComponent event,
			Entity targetEntity,
			RenderComponent renderComponent,
			RenderedComponent renderedComponent) {
		boolean isProjected = renderComponent.layer.projected;
		Vector2 position = this.getPosition(event, isProjected);
		Rectangle rectangle = this.getRectangle(isProjected, renderedComponent);
		
		// if in the bounding box
		if (rectangle.contains(position)) {
			if (renderComponent.spriteTexture == null) {
				throw new NullPointerException("targetComponent.texture was null !");
			}

			InputTouchTargetComponent targetComponent =
					this.targetComponentMapper.get(targetEntity);

			if (this.isClickedPixelVisible(rectangle, renderComponent, targetComponent, position)) {
				return true;
			}
		}
		
		return false;
	}
	
	private Entity iterateOverAllEntitiesToFindTouched(InputTouchEventComponent event) {
		int orderOfCurrentTarget = -1;
		Entity touchedEntity = null;

		// go over all entities
		for (Entity targetEntity : targetEntities) {
			RenderComponent renderComponent =
					this.renderComponentMapper.get(targetEntity);
			RenderedComponent renderedComponent =
					this.renderedComponentMapper.get(targetEntity);
			
			// search for the one that is touched and has the highest order of the layer
			if (this.touchesVisiblePartOfTarget(event, targetEntity, renderComponent, renderedComponent)) {

				if (renderComponent.layer.order > orderOfCurrentTarget) {
					orderOfCurrentTarget = renderComponent.layer.order;
					touchedEntity = targetEntity;
				}
			}
		}
		
		return touchedEntity;
	}
	
	@Override
	public void update(float deltaTime) {
		// remove any previous events
		this.containerEntities.clear();
		Entity touchedEntity = null;
		
		// we only need to get the entity once - because the mouse is at the same position
		// at the moment of time
		if (!this.lastEvents.isEmpty()) {
			touchedEntity =
					this.iterateOverAllEntitiesToFindTouched(this.lastEvents.get(0));
		}
		
		for (InputTouchEventComponent eventToProcess : this.lastEvents) {
			Gdx.app.debug(
					"InputTouchGeneratorSystem",
					eventToProcess.inputType + " - " + eventToProcess.target);

			eventToProcess.target = touchedEntity;
			
			// save the event in an entity
			this.containerEntities.add(eventToProcess);
		}
		
		this.lastEvents.clear();
	}
	
	private boolean isClickedPixelVisible(Rectangle renderedRectangle, RenderComponent renderComponent, InputTouchTargetComponent targetComponent, Vector2 position) {
		// http://gamedev.stackexchange.com/questions/43943/how-to-detect-a-touch-on-transparent-area-of-an-image-in-a-libgdx-stage
		Pixmap pixmap =
				this.archive.getPixmap(renderComponent.spriteTexture.getTexture());

		// we want the position of the pixel in the texture !
		// first add the offset of the region inside the texture, then add the position inside the texture !
		// -> because we need the position inside the texture
		int pixelX =
				(int)(renderComponent.spriteTexture.getRegionX() + position.x - renderedRectangle.x);
		
		// the same goes for the Y component, BUT the Y axis is inverted, therefore
		// we need to invert the position INSIDE the texture !
		// --> that's why we use regionHeigth - positionInsideTexture
		int pixelY =
				(int)(renderComponent.spriteTexture.getRegionY() + renderComponent.spriteTexture.getRegionHeight() - (position.y - renderedRectangle.y));

		int pixel =
				pixmap.getPixel(pixelX, pixelY);

		return (pixel & 0x000000ff) != 0;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void saveEvent(int screenX, int screenY, InputTouchEventType inputType, int argument) {
		Vector3 projected = new Vector3(screenX, screenY, 0);
		Vector3 unprojected = this.camera.unproject(projected);
		InputTouchEventComponent newEvent =
				this.engine.createComponent(InputTouchEventComponent.class);
		newEvent.argument = argument;
		newEvent.target = null;
		newEvent.inputType = inputType;
		newEvent.projectedPosition.x = unprojected.x;
		newEvent.projectedPosition.y = unprojected.y;
		newEvent.unprojectedPosition.x = screenX - (Gdx.graphics.getWidth() / 2);
		newEvent.unprojectedPosition.y = (Gdx.graphics.getHeight() / 2) - screenY;
		
		this.lastEvents.add(newEvent);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		this.saveEvent(screenX, screenY, InputTouchEventType.TouchDown, button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		this.saveEvent(screenX, screenY, InputTouchEventType.TouchUp, button);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		this.saveEvent(screenX, screenY, InputTouchEventType.Dragged, pointer);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		this.saveEvent(screenX, screenY, InputTouchEventType.Moved, 0);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
