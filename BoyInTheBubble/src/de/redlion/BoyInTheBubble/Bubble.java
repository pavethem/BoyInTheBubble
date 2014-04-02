package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;

public class Bubble {

	final int NUM_SEGMENTS = 24;
	
	Body center;
	Array<Body> circles;
	Array<DistanceJoint> spokes;
	Array<DistanceJoint> rads;
	
	ContactListener contactListener;
	
	float originalDampingSpokes = 0f;
	float originalFrequencySpokes = 0.01f;
	float originalDampingRads = 0f;
	float originalFrequencyRads = 0.01f;
	
	Body mouseBody;
	MouseJoint mouseJoint;
	
	float dist;
	
	public Bubble(float distance, Vector3 pos) {
		
		distance/=2;
		distance /= Constants.PIXELS_PER_METER;
		dist = distance;
		
		circles = new Array<Body>();
		spokes = new Array<DistanceJoint>();
		rads = new Array<DistanceJoint>();
		
		BodyDef circleDef = new BodyDef();
		
		circleDef.type = BodyType.DynamicBody;

		circleDef.position.set(-pos.x/Constants.PIXELS_PER_METER, -pos.y/Constants.PIXELS_PER_METER);
		
		center = GameScreen.world.createBody(circleDef);
//		center.setFixedRotation(true);
		center.setBullet(true);
//		center.setActive(false);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(0);
		
		FixtureDef fixDef = new FixtureDef();
		fixDef.shape = circleShape;
		fixDef.density = 5f;
		fixDef.restitution = 0f;
		fixDef.friction = 0f;
		
		center.createFixture(fixDef);
		center.resetMassData();
		circleShape.dispose();
		
		for(int i=0;i<NUM_SEGMENTS;i++){
	        float x = MathUtils.cosDeg(360/NUM_SEGMENTS*i) * distance;
	        float y = MathUtils.sinDeg(360/NUM_SEGMENTS*i) * distance;
	        BodyDef subCircleDef = new BodyDef();
	        subCircleDef.type = BodyType.DynamicBody;
	        subCircleDef.position.set(center.getPosition()).add(x, y);
	        Body subCircle = GameScreen.world.createBody(subCircleDef);
	        subCircle.setUserData("circle");
	        subCircle.setBullet(true);
	        subCircle.setFixedRotation(true);
	        CircleShape subCircleShape = new CircleShape();
	        subCircleShape.setRadius(0.1f / Constants.PIXELS_PER_METER);
	        FixtureDef subCircleData = new FixtureDef();
	        subCircleData.shape = subCircleShape;
	        subCircleData.density = 5f;
	        subCircleData.restitution = 1f;
	        subCircleData.friction = 0f;
	        subCircleData.filter.groupIndex = -1;
	        subCircle.createFixture(subCircleData);
	        subCircle.resetMassData();
//	        subCircle.setActive(false);
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
		    joint1Def.frequencyHz = originalFrequencySpokes;
		    joint1Def.dampingRatio = originalDampingSpokes;
		    joint1Def.length = distance;
		    DistanceJoint joint1 = (DistanceJoint) GameScreen.world.createJoint(joint1Def);
			spokes.add(joint1);

			//rads
			DistanceJointDef joint2Def = new DistanceJointDef();
			joint2Def.bodyA = circles.get(neighborIndex);
			joint2Def.bodyB = circles.get(i);
			joint2Def.collideConnected = false;
			joint2Def.frequencyHz = originalFrequencyRads;
			joint2Def.dampingRatio = originalDampingRads;
			DistanceJoint joint2 = (DistanceJoint) GameScreen.world.createJoint(joint2Def);
			rads.add(joint2);
		}
		
		contactListener = new ContactListener() {

			@Override
			public void beginContact(Contact contact) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
	}
	
	public void createMouseJoint(){
        
		float x = GameScreen.boy.getPosition().x + GameScreen.boy.getOrigin().x;
		x /= Constants.PIXELS_PER_METER;
		float y = GameScreen.boy.getPosition().y + GameScreen.boy.getOrigin().y;
		y /= Constants.PIXELS_PER_METER;
		
		BodyDef mouseDef = new BodyDef();
		
		mouseBody = GameScreen.world.createBody(mouseDef);

		MouseJointDef mjd = new MouseJointDef();
		
		Vector2 v = new Vector2(x,y);

		mjd.bodyA = mouseBody;
		mjd.bodyB = center;
		mjd.dampingRatio = 0f;
		mjd.frequencyHz = 0.01f;
		mjd.maxForce = 0;
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
			dj.setDampingRatio(originalDampingRads);
		}
		for(DistanceJoint dj : spokes) {
			dj.setDampingRatio(originalDampingSpokes);
		}
		
		if(mouseJoint != null && mouseBody != null) {
			GameScreen.world.destroyJoint(mouseJoint);
			mouseJoint = null;
			GameScreen.world.destroyBody(mouseBody);
			mouseBody = null;
		}
		
	}
	
	public void updateTarget() {
		
//		if(contactCount==0) {
//			
//			for(DistanceJoint d : spokes) {
//				d.setDampingRatio(mouseDragDampingSpokes);
//			}
//			for(DistanceJoint j : rads) {
//				j.setDampingRatio(mouseDragDampingRads);
//			}
//		}
//		
		float x = GameScreen.boy.getPosition().x + GameScreen.boy.getOrigin().x;
		x /= Constants.PIXELS_PER_METER;
		float y = GameScreen.boy.getPosition().y + GameScreen.boy.getOrigin().y;
		y /= Constants.PIXELS_PER_METER;
		
//		Vector2 delta = new Vector2(last.x - x + 1.5f, last.y - y + 1.5f);
//		
		mouseJoint.setTarget(new Vector2(x,y));
		
		center.setTransform(x, y,0);
		for(int i=0;i<NUM_SEGMENTS;i++){
//
			boolean toucheswall = false;
//			
//			circles.get(i).setAwake(false);
//			
//			if(contactCount > 0) {
//				for(Contact c : GameScreen.world.getContactList()) {
//					if(c.getFixtureA().getBody().getUserData() != null && c.getFixtureA().getBody().getUserData().equals("wall")) {
//						toucheswall = true;	
//					}
//				}
//			}
//			
//			if(circles.get(i).getPosition().dst(center.getPosition()) > dist) {
//				circles.get(i).setAwake(true);
//				toucheswall = false;
//			}
			x = MathUtils.cosDeg(360/NUM_SEGMENTS*i) * dist;
	        y = MathUtils.sinDeg(360/NUM_SEGMENTS*i) * dist;
//			
//			if(!toucheswall) {
			if(!toucheswall)
				circles.get(i).setTransform(center.getPosition().add(x,y), 0);
//			} else {
////				y = center.getPosition().y;
////				circles.get(i).setAwake(true);
////				if(circles.get(i).getPosition().x <= 0.1f)
////	        System.out.println(circles.get(i).getPosition().dst(center.getPosition()));
////	        	if(circles.get(i).getPosition().dst(center.getPosition()) >= dist-0.1f)
////					circles.get(i).setTransform(circles.get(i).getPosition().add(delta), 0);
////	        	else if(!toucheswall){
////	        		circles.get(i).setTransform(center.getPosition().add(x,y), 0);
////	        	} else {
////	        		if(circles.get(i).getPosition().x < 0.15f)
////	        			circles.get(i).setTransform(circles.get(i).getPosition().add(0,delta.y), 0);
////	        		if(circles.get(i).getPosition().x > 31.85f)
////	        			circles.get(i).setTransform(circles.get(i).getPosition().add(0,delta.y), 0);
////	        		if(circles.get(i).getPosition().y > 19.85f)
////	        			circles.get(i).setTransform(circles.get(i).getPosition().add(delta.x,0), 0);
////	        		if(circles.get(i).getPosition().y < 0.15f)
////	        			circles.get(i).setTransform(circles.get(i).getPosition().add(delta.x,0), 0);
//	        	}
////			}
		}
	}
	
}
