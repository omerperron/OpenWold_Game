package objects;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.ModelHandler;
import models.TexturedModel;
import toolbox.Maths;
import world.World;

public class ShortBow extends ProjectileWeapon {
	
	private static final String name = "short bow";

	private static final float damage = 50;
	private static final float accuracy = 50;
	private static final float speed = 2f;
	private static final float range = 150;
	private static final int textureId = -1;
	private static final TexturedModel model = ModelHandler.get(0);
	private float angle = 45;

	public ShortBow() {
		super(name, damage, accuracy, speed, range, model, textureId);
	}

	@Override
	public void attack(Entity player, Entity enemy) {
		if(Keyboard.isKeyDown(Keyboard.KEY_5)) angle--;
		if(Keyboard.isKeyDown(Keyboard.KEY_6)) angle++;
		//System.out.println("angle" + angle);
		if(getLastHit() + getSpeed() < World.getTime()){
			System.out.println("hitting");
			setLastHit();
			Vector3f direction = Maths.vectorSub3f(enemy.getPosition(), player.getPosition());
			direction.normalise();
			Vector3f position = Maths.vectorClone(player.getPosition());
			
			float xDistance = enemy.getPosition().x - player.getPosition().x;
			float yDistance = enemy.getPosition().y - player.getPosition().y;
			float zDistance = enemy.getPosition().z - player.getPosition().z;
			float distance = (float) Math.sqrt(xDistance*xDistance + zDistance*zDistance);

			float g = World.getGravity();
			float v = 1;
			
			double angleA = 0;
			double angleB = 0;
			double angle = 0;
			while(true){
				float A = (g * distance * distance)/( 2 * v * v);
				float B = distance;
				float C = A - yDistance;
				
			    angleA = ((-B + Math.sqrt(B*B - 4 * A * C))/(2 * A));
				angleB = ((-B - Math.sqrt(B*B - 4 * A * C))/(2 * A));

				angleA = Math.toDegrees(Math.atan(angleA));
				angleB = Math.toDegrees(Math.atan(angleB));
				
				if (!Double.isNaN(angleB)) {					
					angle = angleA;
					System.out.println("angleB found, breaking " + angleB);
					break;
				}else if(!Double.isNaN(angleA)){
					System.out.println("angleB found, breaking");

					angle = angleB;
					break;
				}else if(v > Arrow.getMaxSpeed()){
					System.out.println("correct speed not reachable, returning");
					return;
				}else{
					v += 3;
				}
				System.out.println("v:" + v);
			}
			
			System.out.println(angleA + " " + angleB + ":" + angle);
			Arrow arrow = new Arrow(position, direction, (float) angleA, v);
			addToShooting(arrow);

		}		
	}
	

	@Override
	public void defence() {
		// TODO Auto-generated method stub
		
	}

}
