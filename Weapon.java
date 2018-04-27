package objects;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.TexturedModel;
import world.World;

public abstract class Weapon extends GameObject {
	private float damage;
	private float accuracy; 
	private float speed;
	private float range;
	private float lastHit;
	
	public Weapon(String name, float damage, float accuracy, float speed, float range, 
			TexturedModel model, Vector3f position, int textureId) {
		super(name, model, position, textureId);
		this.lastHit = 0;
		this.damage = damage;
		this.accuracy = accuracy;
		this.speed = speed;
		this.range = range;
	}
	
	public abstract void attack(Entity player, Entity enemy);
	public abstract void defence();

	
	public float getDamage(){
		return this.damage;
	}
	
	public float getLastHit(){
		return this.lastHit;
	}
	
	public float getAccuracy(){
		return this.accuracy;
	}
	
	public float getSpeed(){
		return this.speed;
	}
	
	public float getRange(){
		return this.range;
	}
	
	public void setLastHit(){
		lastHit = World.getTime();
	}

}
