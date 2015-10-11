package control;

import java.util.Scanner;

public class TestPositionDetermination {

	private double[] startProb = { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };

	public static void main(String[] args) {
		TestPositionDetermination test = new TestPositionDetermination();
	}

	public TestPositionDetermination() {
		PosDetermination posDet = new PosDetermination(startProb);
		System.out.println("Waiting for Request...");

		double[] probs = new double[6];
		Scanner in = new Scanner(System.in);

		while (true) {
			String input = in.nextLine();

			if (input.equals("stop")) {
				System.out.println("Stopped!");
				break;
			} else {

				probs[0] = Double.parseDouble(input.split(" ")[0]);
				probs[1] = Double.parseDouble(input.split(" ")[1]);
				probs[2] = Double.parseDouble(input.split(" ")[2]);
				probs[3] = Double.parseDouble(input.split(" ")[3]);
				probs[4] = Double.parseDouble(input.split(" ")[4]);
				probs[5] = Double.parseDouble(input.split(" ")[5]);

				posDet.updatePos(probs);

				System.out.println(posDet.getResult());
			}
		}

	}
}