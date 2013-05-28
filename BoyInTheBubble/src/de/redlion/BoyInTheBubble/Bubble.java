package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;

public class Bubble {
	
	final int NUM_SEGMENTS = 20;
	
	Body center;
	Array<Body> circles;
	Array<DistanceJoint> spokes;
	Array<DistanceJoint> rads;
	
	Body mouseBody;
	MouseJoint mouseJoint;
	
	float dist;
	
	ContactListener contactListener;
	
	public Bubble(float distance, Vector3 pos) {
		
		distance/=2;
		
		dist = distance;
		
		circles = new Array<Body>();
		spokes = new Array<DistanceJoint>();
		rads = new Array<DistanceJoint>();
		
		BodyDef circleDef = new BodyDef();
		
		circleDef.type = BodyType.DynamicBody;

		circleDef.position.set(-pos.x, -pos.y);
		
		center = GameScreen.world.createBody(circleDef);
		center.setFixedRotation(true);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(0);
		
		FixtureDef fixDef = new FixtureDef();
		fixDef.shape = circleShape;
		fixDef.density = 11111;
		fixDef.restitution = 0f;
		fixDef.friction = 0;
		
		center.createFixture(fixDef);
		circleShape.dispose();
		
		for(int i=0;i<NUM_SEGMENTS;i++){
	        float x = MathUtils.cosDeg(360/NUM_SEGMENTS*i) * distance;
	        float y = MathUtils.sinDeg(360/NUM_SEGMENTS*i) * distance;
	        BodyDef subCircleDef = new BodyDef();
	        subCircleDef.type = BodyType.DynamicBody;
	        subCircleDef.position.set(center.getPosition()).add(x, y);
	        Body subCircle = GameScreen.world.createBody(subCircleDef);
	        subCircle.setFixedRotation(true);
	        CircleShape subCircleShape = new CircleShape();
	        subCircleShape.setRadius(0.1f);
	        FixtureDef subCircleData = new FixtureDef();
	        subCircleData.shape = subCircleShape;
	        subCircleData.density = 1f;
	        subCircleData.restitution = 0f;
	        subCircleData.friction = 0;
	        subCircleData.filter.groupIndex = -1;
	        subCircle.createFixture(subCircleData);
	        subCircle.setFixedRotation(true);
	        circles.add(subCircle);
	        subCircleShape.dispose();
	}
		
		for (int i = 0; i < NUM_SEGMENTS; i++) {
		    int neighborIndex = (i + 1) % NUM_SEGMENTS;
		    //speichen
		    DistanceJointDef joint1Def = new DistanceJointDef();
		    joint1Def.bodyA = center;
		    joint1Def.bodyB = circles.get(i);
		    joint1Def.collideConnected = false;
		    joint1Def.frequencyHz = 1160;
		    joint1Def.dampingRatio = 1000f;
		    joint1Def.length = distance;
		    DistanceJoint joint1 = (DistanceJoint) GameScreen.world.createJoint(joint1Def);
			spokes.add(joint1);

			DistanceJointDef joint2Def = new DistanceJointDef();
			joint2Def.bodyA = circles.get(neighborIndex);
			joint2Def.bodyB = circles.get(i);
			joint2Def.collideConnected = false;
			joint2Def.frequencyHz = 110;
			joint2Def.dampingRatio = 10000;
			DistanceJoint joint2 = (DistanceJoint) GameScreen.world.createJoint(joint2Def);
			rads.add(joint2);
		}
		
		contactListener = new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				
				center.getFixtureList().get(0).getShape().setRadius(0.1f);
				center.getFixtureList().get(0).setDensity(11111);
				center.resetMassData();
				
				for(DistanceJoint dj : spokes) {
					dj.setFrequency(100);
					dj.setDampingRatio(10000);
				}
				
				contact.getFixtureA().getBody().setAwake(false);
				if (mouseJoint == null) {
					center.setAwake(false);

					for (Body c : circles) {
						c.setAwake(false);
					}
				}
				
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				
				
				
			}
			
			@Override
			public void endContact(Contact contact) {
				
				center.getFixtureList().get(0).getShape().setRadius(0);
				center.getFixtureList().get(0).setDensity(0);
				center.resetMassData();
				
				for(DistanceJoint dj : spokes) {
					dj.setFrequency(1160);
					dj.setDampingRatio(1000);
				}
				
				for (Body c : circles) {
					c.setAwake(false);
				}
			}
			
			@Override
			public void beginContact(Contact contact) {
				
				contact.getFixtureA().getBody().setAwake(false);
				
				for (Body c : circles) {
					c.setAwake(false);
				}
				
				center.setAwake(false);
				if(mouseBody != null)
					mouseBody.setAwake(false);
				
				for(DistanceJoint dj : rads) {
					dj.setDampingRatio(100);
				}
				for(DistanceJoint dj : spokes) {
					dj.setDampingRatio(100);
				}
			}
		};
	
	}
	
	public void createMouseJoint(){
        
		float x = GameScreen.boy.getPosition().x + GameScreen.boy.getOrigin().x;
		float y = GameScreen.boy.getPosition().y + GameScreen.boy.getOrigin().y;
		
		BodyDef mouseDef = new BodyDef();
		
		mouseBody = GameScreen.world.createBody(mouseDef);

		MouseJointDef mjd = new MouseJointDef();
		
		Vector2 v = new Vector2(x,y);

		mjd.bodyA = mouseBody;
		mjd.bodyB = center;
		mjd.dampingRatio = 0f;
		mjd.frequencyHz = 1130;
		mjd.maxForce = 1000000f;
		mjd.collideConnected = false;
		mjd.target.set(v);

		mouseJoint = (MouseJoint) GameScreen.world.createJoint(mjd);

}

	public void destroyMouseJoint() {
		
		for(Body b : circles) {
			b.setAwake(false);
		}
		center.setAwake(false);
		
		for(DistanceJoint dj : rads) {
			dj.setDampingRatio(10000);
		}
		for(DistanceJoint dj : spokes) {
			dj.setDampingRatio(1000);
		}
		
		if(mouseJoint != null && mouseBody != null) {
			GameScreen.world.destroyJoint(mouseJoint);
			mouseJoint = null;
			GameScreen.world.destroyBody(mouseBody);
			mouseBody = null;
		}
		
	}

	public void updateTarget() {
		
		boolean wallTouch = false;
		
		if(GameScreen.world.getContactCount() > 0) {
			for(Contact c : GameScreen.world.getContactList()) {
				if(c.getFixtureA().getBody().getUserData().equals("wall")) {
					wallTouch = true;
					break;
				}
			}
		}
		
		if(!wallTouch) {
			
			for(DistanceJoint d : spokes) {
				d.setDampingRatio(0);
			}
			for(DistanceJoint j : rads) {
				j.setDampingRatio(0);
			}
		} 
		
		float x = GameScreen.boy.getPosition().x + GameScreen.boy.getOrigin().x;
		float y = GameScreen.boy.getPosition().y + GameScreen.boy.getOrigin().y;
		
		mouseJoint.setTarget(new Vector2(x,y));
		
	}
}
