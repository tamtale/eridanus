package com.week1.game.AIMovement;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Limiter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.week1.game.Model.Unit;

import java.util.concurrent.ThreadLocalRandom;

// A simple steering agent for 2D.
// Of course, for 3D (well, actually for 2.5D) you have to replace all occurrences of Vector3 with  Vector3.
public class SteeringAgent implements Steerable<Vector3> {

    private final Unit unit;
    private Vector3 goal;

    private static SteeringAcceleration<Vector3> steeringOutput =
            new SteeringAcceleration<Vector3>(new Vector3());

    private static final String tag = "Steering Agent";
    public SteeringAgent(Unit unit){
        Gdx.app.log(tag, "Building a SteeringAgent");
        this.unit = unit;
        this.goal = new Vector3(unit.getX(), unit.getY(), 0);
        this.steeringBehavior = arrive;
//        this.steeringBehavior = new Wander<>(this);
    }
    Vector3 position;
    float orientation = 0;
    Vector3 linearVelocity = new Vector3(0, 0, 0);
    float angularVelocity = 0;
    float maxSpeed = .5f;
    boolean independentFacing = false;
    SteeringBehavior<Vector3> steeringBehavior;
    private float maxAngularAcceleration;
    private boolean tagged;
    private float zeroLinearSpeedThreshold = 3;
    private float maxLinearAcceleration = 3;
    private float maxAngularSpeed = 2;

    private SteeringBehavior<Vector3> arrive = new Arrive<>(this, new Location<Vector3>() {
        @Override
        public Vector3 getPosition() {
            return goal;
        }

        @Override
        public float getOrientation() {
            return 0;
        }

        @Override
        public void setOrientation(float orientation) {

        }

        @Override
        public float vectorToAngle(Vector3 vector) {
            return (float)Math.atan2(vector.x, vector.y);
        }

        @Override
        public Vector3 angleToVector(Vector3 outVector, float angle) {
            outVector.x = (float)Math.sin(angle);
            outVector.y = (float)Math.cos(angle);
            return outVector;
        }

        @Override
        public Location<Vector3> newLocation() {
            return this;
        }
    }).setArrivalTolerance(0).setDecelerationRadius(50).setTimeToTarget(10);
    public Vector3 getGoal() {
        return goal;
    }

    public void setGoal(Vector3 goal) {
        System.out.println(goal);
        this.goal = goal;
        this.steeringBehavior = arrive;
    }
    /* Here you should implement missing methods inherited from Steerable */
    @Override
    public Vector3 getLinearVelocity() {
        return linearVelocity;
    }

    @Override
    public float getAngularVelocity() {
        return angularVelocity;
    }

    @Override
    public float getBoundingRadius() {
        System.out.println("get that bounding rad");
        return 0;
    }

    public SteeringBehavior<Vector3> getSteeringBehavior() {
        return steeringBehavior;
    }

    public void setSteeringBehavior(SteeringBehavior<Vector3> steeringBehavior) {
        this.steeringBehavior = steeringBehavior;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return zeroLinearSpeedThreshold;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        this.zeroLinearSpeedThreshold = value;
    }

    @Override
    public float getMaxLinearSpeed() {
        return this.maxSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return this.maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return this.maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector3 getPosition() {
        return new Vector3(unit.getX(), unit.getY(), 0);
    }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    public void setSteeringOutputLinear(Vector3 acceleration) {
        this.steeringOutput.linear = acceleration;
    }
    // Actual implementation depends on your coordinate system.
    // Here we assume the y-axis is pointing upwards.
    @Override
    public float vectorToAngle (Vector3 vector) {
        return (float)Math.atan2(-vector.x, vector.y);
    }

    // Actual implementation depends on your coordinate system.
    // Here we assume the y-axis is pointing upwards.
    @Override
    public Vector3 angleToVector (Vector3 outVector, float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }

    @Override
    public Location<Vector3> newLocation() {
        return null;
    }

    public void update (float delta) {
        if (steeringBehavior != null) {
            Gdx.app.debug(tag, "Updating position " + getPosition() +  " and velocity " + getLinearVelocity());
            // Calculate steering acceleration
            steeringBehavior.calculateSteering(steeringOutput);
            /*
             * Here you might want to add a motor control layer filtering steering accelerations.
             *
             * For instance, a car in a driving game has physical constraints on its movement:
             * - it cannot turn while stationary
             * - the faster it moves, the slower it can turn (without going into a skid)
             * - it can brake much more quickly than it can accelerate
             * - it only moves in the direction it is facing (ignoring power slides)
             */

            // Apply steering acceleration to move this agent
            applySteering(steeringOutput, delta);
        }
    }

    public SteeringAcceleration<Vector3> getSteeringOutput(){
        return steeringOutput;
    }
    private void applySteering (SteeringAcceleration<Vector3> steering, float time) {
        // Update position and linear velocity. Velocity is trimmed to maximum speed
        boolean anyAccelerations = false;

        unit.setPosition(unit.getX() + linearVelocity.x, unit.getY() + linearVelocity.y);
//        System.out.println(steeringOutput.linear);
//        System.out.println(steeringOutput.angular);
        // Update position and linear velocity.
        if (!steeringOutput.linear.isZero()) {
            // this method internally scales the force by deltaTime

            this.linearVelocity.add(steeringOutput.linear);
            anyAccelerations = true;
        }

        // Update orientation and angular velocity
        if (isIndependentFacing()) {
            if (steeringOutput.angular != 0) {
                // this method internally scales the torque by deltaTime
                this.orientation += angularVelocity * time;
                this.angularVelocity += steering.angular * time;


                anyAccelerations = true;
            }
        } else {

            // For non-independent facing we have to align orientation to linear velocity
            float newOrientation = calculateOrientationFromLinearVelocity(this);
            if (newOrientation != this.orientation) {
                this.angularVelocity = (newOrientation - this.orientation) * time;
                this.orientation = newOrientation;
            }

        }

        if (anyAccelerations) {
            // Cap the linear speed
            Vector3 velocity = this.linearVelocity;
            float currentSpeedSquare = velocity.len2();
            float maxLinearSpeed = getMaxLinearSpeed();
            if (currentSpeedSquare > (maxLinearSpeed * maxLinearSpeed)) {
                this.setLinearVelocity(velocity.scl(maxLinearSpeed / (float)Math.sqrt(currentSpeedSquare)));
            }
            // Cap the angular speed
            float maxAngVelocity = getMaxAngularSpeed();
            if (this.getAngularVelocity() > maxAngVelocity) {
                this.setAngularVelocity(maxAngVelocity);
            }
        }

    }

    private void setAngularVelocity(float angVelocity) {
        this.angularVelocity = angVelocity;
    }

    public void setLinearVelocity(Vector3 scl) {
        this.linearVelocity = scl;
    }

    private boolean isIndependentFacing() {
        return independentFacing;
    }

    public static <T extends Vector<T>> float calculateOrientationFromLinearVelocity (Steerable<T> character) {
        // If we haven't got any velocity, then we can do nothing.
        if (character.getLinearVelocity().isZero(character.getZeroLinearSpeedThreshold()))
            return character.getOrientation();

        return character.vectorToAngle(character.getLinearVelocity());
    }


}
