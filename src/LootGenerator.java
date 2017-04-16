import java.io.*;
import java.util.*;

public class LootGenerator {

	/**
	 * Randomly chooses a monster to fight
	 * In the large file, there are 49 monsters.
	 * In the small file, there is 1 monster.
	 * @param  size					a string
	 * @return monsterStatsArray	an array of strings
	 * @throws FileNotFoundException
	 */
	public static String[] pickMonster(String size) throws FileNotFoundException {
		Scanner dataIn;
		Random r = new Random();
		int monsterID = r.nextInt(49);
		String monsterStats = new String();
		String[] monsterStatsArray;
		
		if (size.equals("large")) {
			dataIn = new Scanner(new File("data/large/monstats.txt"));
			for (int i = 0; i < monsterID; i++) {
				monsterStats = dataIn.nextLine();
			}
			monsterStatsArray = monsterStats.split("\t");
		} else {
			dataIn = new Scanner(new File("data/small/monstats.txt"));
			monsterStatsArray = dataIn.nextLine().split("\t");
		}
		dataIn.close();
		return monsterStatsArray;
	}
	
	public static void main (String[] args) throws FileNotFoundException {
		boolean playing = true;
		Scanner in = new Scanner(System.in);
		String size = "large";
		while(playing) {
			String[] monsterStats = pickMonster(size);
			String monsterName = monsterStats[0];
			Treasure t = new Treasure(size);
			String treasure = t.generateBaseItem(monsterStats[monsterStats.length - 1]);
			t.generateBaseStats(treasure);
			String[] baseStats = t.getBaseStats();
			t.generateAffix(baseStats);
			String[] treasurePlusAffix = t.getAffix();
			String enhancedTreasure = treasurePlusAffix[0];
			String extraDefense = treasurePlusAffix[1];
		
			System.out.println("Fighting " + monsterName + "...");
			System.out.println("You have slain " + monsterName + "!");
			System.out.println(monsterName + " dropped:" + "\n");
			System.out.println(enhancedTreasure);
			System.out.println("Defense: " + t.getDefenseValue());
			System.out.println(extraDefense);
			
			System.out.print("Fight again [y/n]? ");
			String answer = in.nextLine();
			if (answer.equals("n")) { playing = false; }
			else { System.out.println("\n"); }
		}
		in.close();
	}
}
