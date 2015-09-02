package classification;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instance;

public class DataCalc {

	private static final double SIN_ANGLE_SIDE = 0.766044443119;
	private static final double COS_ANGLE_SIDE = 0.642787609687;
	private static final double SIN_ANGLE_MIDDLE = 0.939692620786;
	private static final double COS_ANGLE_MIDDLE = 0.342020143326;
	private static final double SIN_ANGLE = 1.0;
	private static final double COS_ANGLE = 0.0;

	private double[] sensors_cm;
	private ArrayList<Short> sensorLeft;
	private ArrayList<Short> sensorRight;

	private double dist = 0;
	private double angle = 0;

	private static final double INFINITY_SENSOR = -1;

	private Instance thisDataInstance;
	
	public DataCalc() {
		sensors_cm = new double[5];
		sensorLeft = new ArrayList<Short>();
		sensorRight = new ArrayList<Short>();

	}

	public Instance computeFeatures(List<Short> values) {
		thisDataInstance = new Instance(ObstacleClassifier.NUM_FEATURES);
		calculateCentimeter(values);

		calculateData(sensorLeft, 1);
		calculateData(sensorRight, 3);

		sensorLeft.clear();
		sensorRight.clear();

		System.out.println("INSTANCE: " + thisDataInstance);
		return thisDataInstance;
	}

	private void calculateData(List<Short> sensorSide, int index) {
		if (sensorSide.size() == 0) {
			dist = Double.POSITIVE_INFINITY;
			angle = Double.NaN;
		} else if (sensorSide.size() == 1) {
			dist = sensors_cm[sensorSide.get(0)];
			angle = getLot(sensorSide.get(0));
		} else if (sensorSide.size() >= 2) {
			if (sensors_cm[sensorSide.get(0)] != INFINITY_SENSOR
					&& sensors_cm[sensorSide.get(1)] != INFINITY_SENSOR) {
				Double[] vector = calculateLine(sensors_cm[sensorSide.get(0)],
						sensors_cm[sensorSide.get(1)], sensorSide.get(0),
						sensorSide.get(1));
				angle = Math.round(calculateAngle(vector));
			}
			
			dist = Math.min(sensors_cm[sensorSide.get(0)], sensors_cm[sensorSide.get(1)]);

		}
		
		thisDataInstance.setValue(index, angle);
		thisDataInstance.setValue(index + 1, dist);
	}

	private double getLot(int value) {
		switch(value){
		case 0: return (50);
		case 1: return (70);
		case 2: return (90);
		case 3: return (70);
		case 4: return (50);
		}
		return 0;
	}

	private Double[] calculateLine(double sensor_a, double sensor_b, int first,
			int second) {
		Double[] v1 = { 0.0, 0.0 };

		double sensor_a_x = getCoord(first, sensor_a, "x");
		double sensor_a_y = getCoord(first, sensor_a, "y");
		double sensor_b_x = getCoord(second, sensor_b, "x");
		double sensor_b_y = getCoord(second, sensor_b, "y");

		v1[0] = sensor_a_x - sensor_b_x;
		v1[1] = sensor_a_y - sensor_b_y;

		return v1; 

	}

	private double calculateAngle(Double[] v1) {
		Double[] v_unit = { 0.0, 1.0 };
		double zaehler = (v1[0] * v_unit[0]) + (v1[1] * v_unit[1]);
		double nenner = Math.sqrt((Math.pow(v1[0], 2) + Math.pow(v1[1], 2))
				* (Math.pow(v_unit[0], 2) + Math.pow(v_unit[1], 2)));
		double temp = Math.abs(zaehler / nenner);
		double ang = 180 - Math.toDegrees(Math.acos(temp));
		return ang;
	}

	private double getCoord(int value, double sensorValue, String key) {
		switch (value) {
		case 0:
			if (key == "x")
				return ((-1 * COS_ANGLE_SIDE) * sensorValue);
			if (key == "y")
				return (SIN_ANGLE_SIDE * sensorValue);
			break;
		case 1:
			if (key == "x")
				return ((-1 * COS_ANGLE_MIDDLE) * sensorValue);
			if (key == "y")
				return (SIN_ANGLE_MIDDLE * sensorValue);
			break;
		case 2:
			if (key == "x")
				return (COS_ANGLE * sensorValue);
			if (key == "y")
				return (SIN_ANGLE * sensorValue);
			break;
		case 3:
			if (key == "x")
				return (COS_ANGLE_MIDDLE * sensorValue);
			if (key == "y")
				return (SIN_ANGLE_MIDDLE * sensorValue);
			break;
		case 4:
			if (key == "x")
				return (COS_ANGLE_SIDE * sensorValue);
			if (key == "y")
				return (SIN_ANGLE_SIDE * sensorValue);
			break;
		}
		return -1;
	}

	private void calculateCentimeter(List<Short> sensorValues) {
		for (short i = 0; i < 5; i++) {
			short val = sensorValues.get(i);
			if (val > 1200) {
				sensors_cm[i] = ((-1.829e-01)
						+ (Math.pow(val, 1) * 2.833e-02)
						+ (Math.pow(val, 2) * -1.935e-05)
						+ (Math.pow(val, 3) * 4.701e-09) +
						  (Math.pow(val, 4) * -3.937e-13));
			} else {
				sensors_cm[i] = INFINITY_SENSOR;
			}

			if (i < 3 && sensors_cm[i] != INFINITY_SENSOR) {
				sensorLeft.add(i);
			}
			if (i >= 2 && sensors_cm[i] != INFINITY_SENSOR) {
				sensorRight.add(i);
			}
		}
	}
}
