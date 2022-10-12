import com.ibm.rally.*;
/**
 * This is the class that you must implement to enable your car within
 * the CodeRally track. Adding code to these methods will give your car
 * it's personality and allow it to compete.
 */
public class RallyCar extends Car {
	IObject checkPoints[];
	IObject gasStations[];
	ICar opponents[];
	
	int waitBack = 0;
	int nextNo, prevNo;
	boolean gasFlag = false;
	boolean startFlag = false;
	/**
	 * @see com.ibm.rally.Car#getName()
	 */
	public String getName() {
		return "Risa";
	}

	/**
	 * @see com.ibm.rally.Car#getSchoolName()
	 */
	public String getSchoolName() {
		return "A-9";
	}

	/**
	 * @see com.ibm.rally.Car#getColor()
	 */
	public byte getColor() {
		return CAR_BLUE;
	}

	/**
	 * @see com.ibm.rally.Car#initialize()
	 */
	public void initialize() {
		// put implementation here
		checkPoints = getCheckpoints();
		gasStations = getFuelDepots();
		
		startFlag = true;
		prevNo = getPreviousCheckpoint();
		nextNo = getNearIObject(checkPoints);

		if (getDistanceTo(checkPoints[nextNo]) < 80){
			nextNo++;
		}
	}

	/**
	 * @see com.ibm.rally.Car#move(int, boolean, ICar, ICar)
	 * Put the car in reverse for a few moves if you collide with another car.
	 * Go toward the first gas depot.
	 */
	public void move(int lastMoveTime, boolean hitWall, ICar collidedWithCar, ICar hitBySpareTire) {
		// put implementation here
		
		opponents = getOpponents();
	
		
		if (collidedWithCar != null && waitBack == 0 && gasFlag == false) {
			waitBack = 13;
		}
		if (hitWall == true && waitBack == 0 && gasFlag == false) {
			waitBack = 3;
		}
		
		if (getFuel() < 50 && gasFlag == false) {
			prevNo = getPreviousCheckpoint();
			gasFlag = true;
		}
		
		if (waitBack > 0) {
			waitBack--;
			back();
		} else if (gasFlag == true) {
			gotoGas(getNearIObject(gasStations));
		} else {
			int targetNo;
			if (startFlag == true) {
				targetNo = nextNo;
				if (prevNo != getPreviousCheckpoint()) {
					startFlag = false;
				}
			} else {
				targetNo = getPreviousCheckpoint() + 1;
			}
			for (int i = 0; i < 100; i++){
				
			}
			if (targetNo >= checkPoints.length) {
				targetNo = 0;
			}
			drive(checkPoints[targetNo]);
		}

	}
	

	
	private void drive(IObject target){

		double rate1 = 0.8, rate2 = 0.9;
		int targetAngle = getHeadingTo(target) - getHeading();
		targetAngle = changeAngle(targetAngle);
		
		if (targetAngle > 10) {
			setSteeringSetting(MAX_STEER_RIGHT);
			setThrottle((int)(MAX_THROTTLE * rate2));
		} else if (targetAngle < -10) {
			setSteeringSetting(MAX_STEER_LEFT);
			setThrottle((int)(MAX_THROTTLE * rate2));
		} else if (getDistanceTo(target) > 30) {
			setSteeringSetting(targetAngle / 2);
			setThrottle(MAX_THROTTLE);
		} else {
			setSteeringSetting(targetAngle);
			setThrottle((int)(MAX_THROTTLE * rate1));
		}
	}
	
	private void gotoGas(int number) {
		int targetAngle = getHeadingTo(gasStations[number]) - getHeading();
		targetAngle = changeAngle(targetAngle);
		double distance = getDistanceTo(gasStations[number]);
		
		if (getFuel() > 80) {
			gasFlag = false;
			startFlag = true;
			nextNo = getNearIObject(checkPoints);
			if (getDistanceTo(checkPoints[nextNo]) < 80) {
				nextNo++;
			}
		} else if (distance < 25) {
			if (isInProtectMode() == false) {
				enterProtectMode();
			}
			setSteeringSetting(0);
			setThrottle(0);
			opponents = getOpponents();
			int oppoNo = getNearIObject(opponents);
			if (getDistanceTo(opponents[oppoNo]) < 80) {
				attack(opponents[oppoNo]);
			}
		} else if (distance < 80) {
			setSteeringSetting(targetAngle);
			setThrottle(40);
		} else if (distance < 200) {
			setSteeringSetting(targetAngle);
			setThrottle(60);
		} else {
			drive(gasStations[number]);
		}
	}
	
	private int changeAngle(int angle) {
		if (angle > 180) {
			angle = angle - 360;
		}
		if (angle < -180) {
			angle = angle + 360;
		}
		return angle;
	}
	
	private void setLight(IObject p){
		double distance = getDistanceTo(p); 
		
		if (distance < 50) {
			setHeadlightsOn(true);
		} else {
			setHeadlightsOn(false);
		}
	}
	
	private int getNearIObject(IObject io[]) {
		int j = 0;
		double distance = getDistanceTo(io[j]);
		
		for(int i = 0; i < io.length; i++) {
			double distance2 = getDistanceTo(io[i]);
			if(distance > distance2) {
				distance = distance2;
				j = i;
			}
		}
		return j;
	}
	
	
	public void protectMode(ICar[] enemys) {
		
		for ( int i = 0; i < enemys.length; i++) {
			double distance = getDistanceTo(enemys[i]);
			if(distance < 100) {
				enterProtectMode();
			} 
		}
	}
	
	private void attack(ICar icar) {
		int enemyAngle = getHeadingTo(icar) - getHeading();
		if (enemyAngle > -30 && enemyAngle < 30 && isReadyToThrowSpareTire()) {
			throwSpareTire();
		}
	}
	
	private void back() {
		opponents = getOpponents();
		int oppoNo = getNearIObject(opponents);
		int targetAngle = getHeadingTo(opponents[oppoNo]) - getHeading();
		if (targetAngle > 0) {
			setSteeringSetting(10);
		} else {
			setSteeringSetting(-10);
		}
		setThrottle(MIN_THROTTLE);
		attack(opponents[oppoNo]);
	}
}