import com.ibm.rally.*;
/**
 * This is the class that you must implement to enable your car within
 * the CodeRally track. Adding code to these methods will give your car
 * it's personality and allow it to compete.
 */
public class RallyCar extends Car {
	IObject checkPoints[];
	int nextNo;
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
		nextNo = getNearIObject(checkPoints);
	}

	/**
	 * @see com.ibm.rally.Car#move(int, boolean, ICar, ICar)
	 * Put the car in reverse for a few moves if you collide with another car.
	 * Go toward the first gas depot.
	 */
	public void move(int lastMoveTime, boolean hitWall, ICar collidedWithCar, ICar hitBySpareTire) {
		// put implementation here
		
		int targetNo;
		
		if (getPreviousCheckpoint() == -1) {
			targetNo = nextNo;
		} else {
			targetNo = getPreviousCheckpoint() + 1;
		}
		
		if ( targetNo >= checkPoints.length) {
			targetNo = 0;
		}
		
		drive(checkPoints[targetNo]);
		setLight(checkPoints[targetNo]);
		protectMode();
	}
	
	private void drive(double px, double py){
		int target = getHeadingTo(px, py) - getHeading();
		
		if ( target > 0){
			setSteeringSetting(10);
		}
		if ( target < 0){
			setSteeringSetting(-10);
		}
		if ( target == 0){
			setSteeringSetting(0);
		}
		
		setThrottle(100);
	}
	
	private void drive(IObject p){
		drive(p.getX(), p.getY());
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
	
	
	public void protectMode() {
		ICar[] enemys = getOpponents();
		
		for ( int i = 0; i < enemys.length; i++) {
			double distance = getDistanceTo(enemys[i]);
			if(distance < 100) {
				enterProtectMode();
			} 
		}
	}
}