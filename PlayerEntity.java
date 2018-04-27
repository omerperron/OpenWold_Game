package player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.ModelHandler;
import entities.VisualEntity;
import guis.GuiTexture;
import guis.Inventory;
import models.TexturedModel;
import objects.Armour;
import objects.Fists;
import objects.Projectile;
import objects.ProjectileWeapon;
import objects.ShortBow;
import objects.Weapon;
import pathfinding.PathFinder;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderer;
import skillHandler.SkillHandler;
import terrain.MasterTerrain;
import terrain.Terrain;
import terrain.TerrainNode;
import toolbox.Maths;
import world.World;

public abstract class PlayerEntity extends Entity{
	
	private SkillHandler skills;
	
	private float health;
	private float attackRadius;
	private Inventory inventory;
	private ArrayList<Armour> armour = new ArrayList<Armour>();
	private Weapon weapon;
	private Map<TerrainNode, Terrain> path = new LinkedHashMap<TerrainNode, Terrain>();

	//private boolean isControlled = false;
	private boolean isInAir = false;
	
	private static final float GRAVITY = -50;
	private float JUMP_POWER = 50;
	private float TURN_SPEED = 200;
	private float RUN_SPEED = 40;
	private float currentTurnSpeed;
	private float currentSpeed;
	private float upwardsSpeed;
	
	private Entity curInteracting;
	private Entity curAttacking;

	
	public PlayerEntity(TexturedModel model, Vector3f position, int specialty, float health, 
			float attackRadius, GuiTexture inventory, GuiTexture skillTexture) {
		
		super(model, position, 0, 1, false, true);
		this.health = health;
		this.attackRadius = attackRadius;
		this.inventory = new Inventory(inventory);
		skills = new SkillHandler(skillTexture);
		weapon = new ShortBow();
		
	}
	

	

	public void update(MasterTerrain masterTerrain,	MasterRenderer renderer){

		move(masterTerrain, renderer);
		rotate(masterTerrain);
		interact(masterTerrain);
		
		
	}
	
	public void interact(MasterTerrain masterTerrain){
//		if(curInteracting != null && curInteracting.isInteractable()){
//			curInteracting.interact(this, masterTerrain);
//		}
		attack(masterTerrain);
	}
	
	public void attack(MasterTerrain masterTerrain){
		if(curAttacking == null) return;
		if(weapon == null) weapon = new Fists();
		if(Maths.getVector3fDistance(getPosition(), curAttacking.getPosition())
				< weapon.getRange()){
			path.clear();
			weapon.attack(this, curAttacking);

		}else if(!isMoving()){
			TerrainNode node = masterTerrain.getTerrainNode(curAttacking.getPosition().x
					, curAttacking.getPosition().z);

			moveToPosition(node, masterTerrain);
		}
		
		if(weapon instanceof ProjectileWeapon){
			ProjectileWeapon projWeapon = (ProjectileWeapon) weapon;
			for(Projectile curProj: projWeapon.getShooting()){
				World.renderer.processEntity(curProj);
				curProj.update();
			}
		}

	}
	
	public void setInteracting(Entity interacting, int interaction){
		clearEvents();
//		curInteracting = interacting;
//		curInteracting.setInteraction(interaction);
		curAttacking = interacting;
	}
	
	public void setAttacking(Entity entity){
		this.curAttacking = entity;
	}
	
	
	public TerrainNode findPathToInteract(Entity entity, MasterTerrain masterTerrain){
		
		
		float shortestDistance = -1;
		TerrainNode bestNode = null;
		for(TerrainNode node: entity.getInteractableNodes()){
			float nodeDistance = Maths.getVector2fDistance(new Vector2f(getPosition().x, getPosition().z), 
					new Vector2f(node.getWorldX(), node.getWorldZ()));
			if(bestNode == null || nodeDistance < shortestDistance && !masterTerrain.checkNodeCollision(node)){
				bestNode = node;
				shortestDistance = nodeDistance;
			}
		}
		
		return bestNode;
	}
		



	
	public void moveToPosition(TerrainNode destination, MasterTerrain masterTerrain){
		if(destination != null){
			Map<TerrainNode, Terrain> temp = PathFinder.AStarSearch(destination, getPosition(), masterTerrain);
			if(temp != null) {
				path.clear();
				path = temp;
			}
			//clearEvents();
		}
		
	}
	
	public void moveToPosition(Vector3f destination, MasterTerrain masterTerrain){
		
		if(destination != null){
			TerrainNode gridDest = masterTerrain.getTerrainNode(destination.getX(), destination.getZ());
			Map<TerrainNode, Terrain> temp = PathFinder.AStarSearch(gridDest, getPosition(), masterTerrain);
			if(temp != null) {
				path.clear();
				path = temp;

			}

			//clearEvents();
		}
		
	}
	
	
	public void rotate(MasterTerrain masterTerrain){
		Iterator<Entry<TerrainNode, Terrain>> iterator = path.entrySet().iterator();
		TerrainNode node = null;
		for (int i = 0; i < 3; i++){
			if(iterator.hasNext()){
				node = iterator.next().getKey();
			}else{
				break;
			}
		}
		if(node != null && path.size() > 0){
			Terrain terrain = masterTerrain.getTerrain(getPosition().x, getPosition().z);

			float distanceX = node.getGridX() - terrain.getTerrainNode(getPosition().x, getPosition().z).getGridX();   
			float distanceZ = node.getGridZ() - terrain.getTerrainNode(getPosition().x, getPosition().z).getGridZ();   
			float angle = (float) Math.toDegrees(Math.atan(distanceZ/distanceX));
			if(Float.isNaN(angle)) return;
			this.setRotY(-angle + 90);
			
		
		}
	}
	
	private void move(MasterTerrain masterTerrain, MasterRenderer renderer){
	//	if(!isControlled){
		
			if (path.isEmpty()){
				return;
			}
			
			TerrainNode node = path.entrySet().iterator().next().getKey();
			Terrain curTerrain = masterTerrain.getTerrain(getPosition());
			TerrainNode playerNode = curTerrain.getTerrainNode(getPosition().x, getPosition().z);
			float distanceX = node.getWorldX() - playerNode.getWorldX();     
			float distanceZ = node.getWorldZ() - playerNode.getWorldZ();   
			float timeX = distanceX * DisplayManager.getFrameTimeSeconds()*5;
			float timeZ = distanceZ * DisplayManager.getFrameTimeSeconds()*5;

			super.increasePosition(timeX, 0, timeZ);
			setYPos(curTerrain.getHeightOfTerrain(getPosition().x, getPosition().z));
			
			if(node.equals(playerNode)) {
				
				path.remove(node);
				

			}
			
			
			
			for(TerrainNode curPath: path.keySet()){
				float x = curPath.getWorldX() + 4;
				float z = curPath.getWorldZ() + 4;
				renderer.processEntity(new VisualEntity(ModelHandler.get(7), new Vector3f(x, 0, z), 0, 1, false, false));
			}
		
//		}else{
//			checkInputs();
//			increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
//			float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
//			float dx = (float) (distance * Math.sin(Math.toRadians(getRotY())));
//			float dz = (float) (distance * Math.cos(Math.toRadians(getRotY())));
//			upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
//			increasePosition(dx, upwardsSpeed, dz);
//
//			float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
//			if(super.getPosition().y < terrainHeight){
//				upwardsSpeed = 0;
//				super.getPosition().y = terrainHeight;
//				isInAir = false;
//			}
//		}

	}
	
	public boolean isMoving(){
		if(!path.isEmpty())	return true;
		return false;
	}
	
	public void clearEvents(){
		if(curInteracting != null) curInteracting.setInteraction(-1);
		curInteracting = null;
		curAttacking = null;
		path.clear();
	}
	
	private void jump(){
		if(!isInAir){
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	
	public void clearPath(){
		this.path.clear();
	}
	

	
	//--------------------------------INPUTS--------------------------------//
	
	private void checkInputs(){

		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.currentSpeed = RUN_SPEED;

		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){

			this.currentSpeed = -RUN_SPEED;
		}else{
			this.currentSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			this.currentTurnSpeed = -TURN_SPEED;
			
		}else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			this.currentTurnSpeed = TURN_SPEED;
			
		}else{
			this.currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			jump();
		}

	}
	
	
	
	//------------------------- SETTERS AND GETTERS -----------------------//
	
	public void setXPos(float pos){
		getPosition().x = pos;
	}
	public void setYPos(float pos){
		getPosition().y = pos;
	}
	public void setZPos(float pos){
		getPosition().z = pos;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public void setAttackRadius(float attackRadius) {
		this.attackRadius = attackRadius;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}
	
	public void setIsInAir(boolean b){
		this.isInAir = b;
	}
	
	public void setUpwardsSpeed(int speed){
		this.upwardsSpeed = speed;
	}
	
	public float getHealth() {
		return health;
	}

	public float getAttackRadius() {
		return attackRadius;
	}

	public ArrayList<Armour> getArmour() {
		return armour;
	}

	public Weapon getWeapon() {
		return weapon;
	}
	
	public Inventory getInventory(){
		return this.inventory;
	}
	public SkillHandler getSkillHandler(){
		return this.skills;
	}
	
	//do this properly later
	@Override
	public boolean equals(Object player){
		if(getPosition().equals(((PlayerEntity) player).getPosition())) return true;
		return false;
	}
	

	
	

}
