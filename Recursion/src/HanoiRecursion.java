
public class HanoiRecursion {

	public static String HanoiRecursion(int nDisks,int fromPeg,int toPeg) {
		if(nDisks ==1)
		{
			return fromPeg + " Move to " + toPeg + ";";
		}
		else {
			String sol1, sol2, sol3;
			int helpPeg = 6 - fromPeg-toPeg;
			
			sol1 = HanoiRecursion(nDisks -1, fromPeg, helpPeg);
			sol2 = fromPeg + " Move to " + toPeg + ";";
			sol3 = HanoiRecursion(nDisks-1,helpPeg,toPeg);
			
			return sol1 +sol2+sol3;
		}
		
	}

	public static void main(String[] args) {
		int nDisks =4;
		String StepsToSolution = HanoiRecursion(nDisks,1,3);
		
		for(String step : StepsToSolution.split(";"))
		{
			System.out.println(step);
			
		}

	}

}
