package objects;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.TexturedModel;
import renderEngine.DisplayManager;
import toolbox.Maths;
import world.World;

public abstract class Projectile extends Entity {

	private float resilience;
	private float damage;
	private float velocity;
	private Vector3f direction;
	private float xVelocity;
	private float yVelocity;

	public Projectile(TexturedModel model, int textureIndex, Vector3f position, float resilience, 
			float damage, float speed, Vector3f direction, float angle, float v) {
		super(model, textureIndex, position, 0, 1, false);
		
		this.direction = direction;
		this.resilience = resilience;
		this.damage = damage;
		this.velocity = v ;
		xVelocity = (float) (velocity * Math.cos(Math.toRadians(angle)));
		yVelocity = (float) (velocity * Math.sin(Math.toRadians(angle)));

		
	}
	
	public void update(){

		
		getPosition().x += direction.x * xVelocity * DisplayManager.getFrameTimeSeconds()*5;
		getPosition().z += direction.z * xVelocity * DisplayManager.getFrameTimeSeconds()*5;
		yVelocity += (World.getGravity() * DisplayManager.getFrameTimeSeconds()*5);
		getPosition().y += yVelocity * DisplayManager.getFrameTimeSeconds()*5;
		//this.setRotX(90);
		
		
		double angle = yVelocity / xVelocity;
		angle = Math.toDegrees(Math.atan(angle));
		
		
		this.setRotZ((float) angle * direction.x); 
		this.setRotX((float) -angle * direction.z); 
		
		Vector2f origin = new Vector2f(0, 1);
		float dotProduct = Maths.dotProduct2f(origin, 
				new Vector2f(direction.x, direction.z));
		float angleY = (float) Math.toDegrees(Math.acos(dotProduct));
//		System.out.println(Maths.getAngleBetweenTwoVectors(direction, origin));
		System.out.println(angleY);
		//this.setRotX(0); 
		//this.setRotY(-angleY); 

	}


	public float getResilience() {
		return resilience;
	}


	public float getDamage() {
		return damage;
	}


	public float getVelocity() {
		return velocity;
	}

	
	
}
