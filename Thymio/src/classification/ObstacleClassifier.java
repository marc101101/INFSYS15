package classification;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ObstacleClassifier extends NaiveBayes { // NaiveBayes
	private boolean ready;
	private DataCalc featureCalc;
	private Instances testData;
	public static final int NUM_FEATURES = 6;

	public ObstacleClassifier(String trainingDataFileName) {
		super();
		init(trainingDataFileName);
	}

	private void init(String fileName) {
		try {
			Instances trainingData = DataSource.read(fileName);
			if (trainingData.classIndex() == -1)
				trainingData.setClassIndex(0);
			this.buildClassifier(trainingData);
			ready = true;
			featureCalc = new DataCalc();

			FastVector attrList = new FastVector(NUM_FEATURES);
			for (int i = 0; i < trainingData.numAttributes(); i++)
				attrList.addElement(trainingData.attribute(i));
			testData = new Instances("Live Data", attrList, 0);
			testData.setClassIndex(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("could not initialize classifier: "
					+ e.toString());
			ready = false;
		}
	}

	public double[] classify(List<Short> values) {
		/*
		 * probs => ECKE, FRONTAL, SPITZE, LINKS, RECHTS, FREI
		 */
		double[] probs = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		if (ready) {
			try {
				/*
				 * Tutoren: Abfangen wenn Sensoren keine Werte liefern etc.
				 */
				if (goodSensorData(values)) {
					Instance data = featureCalc.computeFeatures(values);
					testData.delete();
					testData.add(data);
					for (int i = 0; i < this.distributionForInstance(testData
							.firstInstance()).length; i++) {
						probs[i] = this.distributionForInstance(testData
								.firstInstance())[i];
					}
					return probs;

				} else {
					if (values.get(0) != 0 && values.get(1) == 0
							&& values.get(2) == 0 && values.get(3) == 0
							&& values.get(4) == 0) {
						probs[3] = 1.0;
						return probs;
					} else if (values.get(0) == 0 && values.get(1) == 0
							&& values.get(2) == 0 && values.get(3) == 0
							&& values.get(4) != 0) {
						probs[4] = 1.0;
						return probs;
					} else if (values.get(0) == 0 && values.get(1) == 0
							&& values.get(2) != 0 && values.get(3) == 0
							&& values.get(4) == 0) {
						probs[1] = 1.0;
						return probs;
					} else {
						probs[5] = 1.0;
						return probs;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println("could not classify instance: "
						+ e.toString());
				return null;
			}
		} else
			return null;
	}

	private boolean goodSensorData(List<Short> values) {
		if (values.get(0) == 0 && values.get(1) == 0 && values.get(2) == 0
				&& values.get(3) == 0 && values.get(4) == 0) {
			return false;
		}
		if (values.get(0) != 0 && values.get(1) == 0 && values.get(2) == 0
				&& values.get(3) == 0 && values.get(4) == 0) {
			return false;
		}
		if (values.get(0) == 0 && values.get(1) == 0 && values.get(2) == 0
				&& values.get(3) == 0 && values.get(4) != 0) {
			return false;
		}
		if (values.get(0) == 0 && values.get(1) == 0 && values.get(2) != 0
				&& values.get(3) == 0 && values.get(4) == 0) {
			return false;
		}

		return true;
	}
}
