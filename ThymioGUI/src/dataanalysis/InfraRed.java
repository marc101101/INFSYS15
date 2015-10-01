package dataanalysis;

public class InfraRed {

	private double[] sensorValues;

	public InfraRed() {

		sensorValues = new double[7];

		for (int i = 0; i < 7; i++)
			sensorValues[i] = Double.POSITIVE_INFINITY;

	}

	private double convertRawToMetric(short raw) {

		return -0.1829 + 0.02833 * raw - 0.00001935 * raw * raw + 4.701e-09 * raw * raw * raw
				- 3.937e-13 * raw * raw * raw * raw;

	}

	public void updateValue(int id, short raw) {

		if (raw == 0) {

			sensorValues[id] = Double.POSITIVE_INFINITY;

		}

		else {

			sensorValues[id] = convertRawToMetric(raw);

		}

	}

	public double getValue(int id) {

		return sensorValues[id];

	}

}
