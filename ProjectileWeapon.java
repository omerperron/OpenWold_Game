package objects;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import models.TexturedModel;

public abstract class ProjectileWeapon extends Weapon {
		private Map<Projectile, Integer> quiver = new HashMap<Projectile, Integer>();
		private ArrayList<Projectile> curShooting = new ArrayList<Projectile>();

	
	public ProjectileWeapon(String name, float damage, float accuracy, float speed, 
			float range, TexturedModel model, int textureId) {
		super(name, damage, accuracy, speed, range, model, null, textureId);

	}
	
	public Map<Projectile, Integer> getQuiver(){
		return this.quiver;
	}
	
	public void addToQuiver(Projectile projectile, int amount){
		if(quiver.containsKey(projectile)){
			quiver.put(projectile, quiver.get(projectile) + amount);
		}else{
			quiver.put(projectile, amount);
		}
	}
	
	public void addToShooting(Projectile projectile){
		curShooting.add(projectile);
	}
	
	public ArrayList<Projectile> getShooting(){
		return curShooting;
	}

	

	
	
	

}
