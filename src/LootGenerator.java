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
	
	/**
	 * Builds an ArrayList of ArrayLists of strings that holds the treasure class information.
	 * @param size	a string
	 * @return		an ArrayList of ArrayLists of strings
	 * @throws FileNotFoundException
	 */
	public static ArrayList<ArrayList<String>> fetchTreasureClass(String size) throws FileNotFoundException {
		Scanner dataIn;
		if (size.equals("large")) {
			dataIn = new Scanner(new File("data/large/TreasureClassEx.txt"));
		} else {
			dataIn = new Scanner(new File("data/small/TreasureClassEx.txt"));
		}
		
		ArrayList<ArrayList<String>> treasureClass = new ArrayList<>();
		ArrayList<String> lineLst;
		while (dataIn.hasNextLine()) {
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
		if (size.equals("large")) {
			dataIn = new Scanner(new File("data/large/armor.txt"));
		} else {
			dataIn = new Scanner(new File("data/small/armor.txt"));
		}
		
		String[] armor = new String[3];
		while (dataIn.hasNextLine()) {
			String armorStats = dataIn.nextLine();
			armor = armorStats.split("\t");
			if (treasure.equals(armor[0])) {
				dataIn.close();
				return armor;
			}
		}
		dataIn.close();
		return armor;
	}
	
	/**
	 * Finds the value of a string that 
	 * represents the defense statistics of armor.
	 * @param armor	an array of strings
	 * @return		an integer
	 */
	public static int getDefenseValue(String[] armor) {
		Random r = new Random();
		int defenseValue = r.nextInt(Integer.parseInt(armor[2]) - 
				Integer.parseInt(armor[1]) + 1) + Integer.parseInt(armor[1]);
		
		return defenseValue;
	}
	
	/**
	 * Generates prefixes and suffixes for defense and treasure.
	 * @param item	an array of strings
	 * @param size	a string
	 * @return		an array of strings
	 * @throws FileNotFoundException
	 */
	public static String[] generateAffix(String[] item, String size) throws FileNotFoundException {
		Scanner prefix;
		Scanner suffix;
		int numAffix = 0;
		String[] ret = new String[2];
		
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
		String[] prefixArr = generatePrefix(prefix, whichAffix);
		String[] suffixArr = generateSuffix(suffix, whichAffix);
		
		for (int i = 0; i < numAffix; i++) {
			if (affixes == 0) { 
				prefix.close();
				suffix.close();
				return item; 
			} else if (affixes == 1) {
				ret[0] = prefixArr[0] + " " + item[0];
				ret[1] = prefixArr[1] + " " + prefixArr[2];
			} else if (affixes == 2) {
				ret[0] = item[0] + " " + suffixArr[0];
				ret[1] = suffixArr[1] + " " + suffixArr[2];
			} else if (affixes == 3) {
				ret[0] = prefixArr[0] + " " + item[0] + " " + suffixArr[0];
				int val = Integer.parseInt(suffixArr[1]) + Integer.parseInt(prefixArr[1]);
				ret[1] = val + " " + prefixArr[2];
			}
			
		}
		prefix.close();
		suffix.close();
		return ret;
	}
	
	/**
	 * Splits up suffix to differentiate between suffix and values. 
	 * @param in	a scanner
	 * @param index	an integer
	 * @return		an array of strings
	 * @throws FileNotFoundException
	 */
	public static String[] generateSuffix(Scanner in, int index) throws FileNotFoundException {
		Random r = new Random();
		String[] affixArr = new String[4];
		String affix;
		String[] ret = new String[3];
		
		affix = findAffix(index, in);
		
		if (affix != null) { 
			affixArr = affix.split("\t");
			ret[0] =  affixArr[0];
			int val = r.nextInt(Integer.parseInt(affixArr[affixArr.length - 1]) - 
				Integer.parseInt(affixArr[affixArr.length - 2]) + 1) + Integer.parseInt(affixArr[affixArr.length - 2]);
			ret[1] = val + "";
			ret[2] = affixArr[1];
		}
		return ret;
	}
	
	/**
	 * Splits up suffix to differentiate between prefix and values. 
	 * @param in	a scanner
	 * @param index	an integer
	 * @return		an array of strings
	 * @throws FileNotFoundException
	 */
	public static String[] generatePrefix(Scanner in, int index) throws FileNotFoundException {
		Random r = new Random();
		String[] affixArr = new String[4];
		String affix;
		String[] ret = new String[3];
		
		affix = findAffix(index, in);
		
		if (affix != null) { 
			affixArr = affix.split("\t");
			ret[0] =  affixArr[0];
			int val = r.nextInt(Integer.parseInt(affixArr[affixArr.length - 1]) - 
				Integer.parseInt(affixArr[affixArr.length - 2]) + 1) + Integer.parseInt(affixArr[affixArr.length - 2]);
			ret[1] = val + "";
			ret[2] = affixArr[1];
		}
		return ret;
	}
	
	/**
	 * Helper method that finds the appropriate suffix or prefix
	 * @param index	an integer
	 * @param in	a scanner
	 * @return		a string
	 */
	public static String findAffix(int index, Scanner in) {
		String ret = new String();
		for (int i = 0; i < index; i++) {
			if (in.hasNext()) { ret = in.nextLine(); }
			else {	return null; }
		}
		return ret;
	}
	
	public static void main (String[] args) throws FileNotFoundException {
		boolean playing = true;
		Scanner in = new Scanner(System.in);
		String size = "large";
		while(playing) {
			String[] monsterStats = pickMonster(size);
			String monsterName = monsterStats[0];
			String treasure = generateBaseItem(monsterStats[monsterStats.length - 1], fetchTreasureClass(size));
			String[] baseStats = generateBaseStats(treasure, size);
			String[] treasurePlusAffix = generateAffix(baseStats, size);
			String enhancedTreasure = treasurePlusAffix[0];
			String extraDefense = treasurePlusAffix[1];
		
			System.out.println("Fighting " + monsterName + "...");
			System.out.println("You have slain " + monsterName + "!");
			System.out.println(monsterName + " dropped:" + "\n");
			System.out.println(enhancedTreasure);
			System.out.println("Defense: " + getDefenseValue(baseStats));
			System.out.println(extraDefense);
			
			System.out.print("Fight again [y/n]? ");
			String answer = in.nextLine();
			if (answer.equals("n")) { playing = false; }
			else { System.out.println("\n"); }
		}
		in.close();
	}
}
