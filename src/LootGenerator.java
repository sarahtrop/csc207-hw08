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
			monsterStatsArray = monsterStats.split(" ");
		} else {
			dataIn = new Scanner(new File("data/small/monstats.txt"));
			monsterStatsArray = dataIn.nextLine().split(" ");
		}
		dataIn.close();
		return monsterStatsArray;
	}
	
	/**
	 * Extracts the treasure data from the array of monster statistics from monstats.txt.
	 * @param monsterStatsArray	an array of strings
	 * @return					a string
	 */
	public static String fetchTreasureClass(String[] monsterStatsArray) {
		String[] initialStats = new String[4];
		String treasureString = new String();
		System.arraycopy(monsterStatsArray, 3, initialStats, 0, monsterStatsArray.length);
		for (int i = 0; i < initialStats.length; i++) {
			treasureString = treasureString.concat(initialStats[i]);
		}
		return treasureString;
	}
	
	/**
	 * Finds the treasure to drop
	 * @param statsString	a string
	 * @param treasureClass	an ArrayList of ArrayLists of strings
	 * @return treasure		a string
	 * @throws FileNotFoundException
	 */
	public static String generateBaseItem(String statsString, ArrayList<ArrayList<String>> treasureClass) throws FileNotFoundException  {
		int index = 0;
		for (int i = 0; i < treasureClass.size(); i++) {
			if (statsString.equals(treasureClass.get(i).get(0))) {
				index = i;
				break;
			}
		}
		Random r = new Random();
		int treasureID = r.nextInt(3);
		String treasure = treasureClass.get(index).get(treasureID);
		
		if (treasure.contains("armo")) {
			return treasure;
		} else {
			return generateBaseItem(treasure, treasureClass);
		}
	}
	
	/**
	 * Returns the defense stats of the treasure dropped by the monster
	 * @param treasure		a string
	 * @param size			a string
	 * @return defenseValue	an integer
	 * @throws FileNotFoundException
	 */
	public static String[] generateBaseStats(String treasure, String size) throws FileNotFoundException {
		Scanner dataIn;
		int numArmor = 0;
		if (size.equals("large")) {
			dataIn = new Scanner(new File("data/large/armor.txt"));
			numArmor = 68;
		} else {
			dataIn = new Scanner(new File("data/small/armor.txt"));
			numArmor = 6;
		}
		
		String[] armor = new String[3];
		for (int i = 0; i < numArmor; i++) {
			String armorStats = dataIn.nextLine();
			armor = armorStats.split("\t");
			if (!treasure.equals(armor[0])) {
				armorStats = dataIn.nextLine();
			}
		}
		dataIn.close();
		return armor;
	}
	
	public static int getDefenseValue(String[] armor) {
		Random r = new Random();
		int defenseValue = r.nextInt(Integer.parseInt(armor[2]) - 
				Integer.parseInt(armor[1]) + 1) + Integer.parseInt(armor[1]);
		
		return defenseValue;
	}
	
	/**
	 * 
	 * @param item
	 * @param size
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String[] generateAffix(String[] item, String size) throws FileNotFoundException {
		Scanner prefix;
		Scanner suffix;
		int numAffix = 0;
		String[] ret = new String[2];
		ret[0] = item[0];
		
		if (size.equals("large")) {
			prefix = new Scanner(new File("data/large/MagicPrefix.txt"));
			suffix = new Scanner(new File("data/large/MagicSuffix.txt"));
			numAffix = 372;
		} else {
			prefix = new Scanner(new File("data/small/MagicPrefix.txt"));
			suffix = new Scanner(new File("data/small/MagicSuffix.txt"));
			numAffix = 5;
		}
		
		Random r = new Random();
		// 0 = neither, 1 = prefix, 2 = suffix, 3 = both
		int affixes = r.nextInt(4);
		int whichAffix = r.nextInt(numAffix);
		
		for (int i = 0; i < numAffix; i++) {
			if (affixes == 0) { return item; }
			else if (affixes == 1 || affixes == 3) { 
				String[] affixArr = findAffix(whichAffix, prefix).split("\t");
				ret[0] =  affixArr[0] + ret[0];
				int val = r.nextInt(Integer.parseInt(affixArr[4]) - 
						Integer.parseInt(affixArr[3]) + 1) + Integer.parseInt(affixArr[3]);
				ret[1] = val + affixArr[1];
			}
			if (affixes == 2 || affixes == 3) {
				String[] affixArr = findAffix(whichAffix, suffix).split("\t");
				ret[0] =  ret[0] + affixArr[0];
				int val = r.nextInt(Integer.parseInt(affixArr[4]) - 
						Integer.parseInt(affixArr[3]) + 1) + Integer.parseInt(affixArr[3]);
				ret[1] = val + affixArr[1];
			}
			
		}
		prefix.close();
		suffix.close();
		return ret;
	}
	
	/**
	 * 
	 * @param index
	 * @param in
	 * @return
	 */
	public static String findAffix(int index, Scanner in) {
		String ret = new String();
		for (int i = 0; i < index; i++) {
			ret = in.nextLine();
		}
		return ret;
	}
	
	/**
	 * Method builds an ArrayList of ArrayLists of strings out of TreasureClassEx.txt.
	 * @param  size				a string
	 * @return treasureClass	an ArrayList of ArrayLists of Strings
	 * @throws FileNotFoundException
	 */
	public static ArrayList<ArrayList<String>> buildTreasureClass(String size) throws FileNotFoundException {
		Scanner dataIn;
		int numTreasures = 0;
		if (size.equals("large")) {
			dataIn = new Scanner(new File("data/large/TreasureClassEx.txt"));
			numTreasures = 68;
		} else {
			dataIn = new Scanner(new File("data/small/TreasureClassEx.txt"));
			numTreasures = 6;
		}
		ArrayList<ArrayList<String>> treasureClass = new ArrayList<>();
		ArrayList<String> lineLst;
		for (int i = 0; i < numTreasures; i++) {
			String line = dataIn.nextLine();
			String[] lineArray = line.split("\t");
			lineLst = new ArrayList<>();
			for (int j = 0; j < lineArray.length; j++) {
				lineLst.add(lineArray[j]);
			}
			treasureClass.add(lineLst);
		}
		dataIn.close();
		return treasureClass;
	}
	
	public static void main (String[] args) throws FileNotFoundException {
		boolean playing = true;
		Scanner in = new Scanner(System.in);
		String size = "small";
		String[] monsterStats = pickMonster(size);
		String monsterName = monsterStats[0];
		String treasure = generateBaseItem(fetchTreasureClass(monsterStats), buildTreasureClass(size));
		String[] treasurePlusAffix = generateAffix(generateBaseStats(treasure, size), size);
		String enhancedTreasure = treasurePlusAffix[0];
		String extraDefense = treasurePlusAffix[1];
		
		while(playing) {
			System.out.println("Fighting " + monsterName + "...");
			System.out.println("You have slain " + monsterName + "!");
			System.out.println(monsterName + " dropped:" + "\n");
			System.out.println(enhancedTreasure);
			System.out.println("Defense: " + getDefenseValue(generateBaseStats(treasure, size)));
			System.out.println(extraDefense);
			
			System.out.print("Fight again [y/n]?");
			String answer = in.nextLine();
			if (answer.equals("n")) { playing = false; }
		}
		in.close();
	}
}
