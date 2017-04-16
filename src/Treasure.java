import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Treasure {
	private ArrayList<ArrayList<String>> t;
	private String size;
	private String[] a;
	private String[] p;
	private String[] s;
	private String[] armor;
	
	/**
	 * Builds an ArrayList of ArrayLists of strings that holds the treasure class information.
	 * @param size	a string
	 * @throws FileNotFoundException
	 */
	public Treasure(String size) throws FileNotFoundException {
		Scanner dataIn;
		this.size = size;
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
		this.t = treasureClass;
	}
	
	public ArrayList<ArrayList<String>> fetchTreasureClass() {
		return this.t;
	}
	
	public String getSize() {
		return this.size;
	}
	
	/**
	 * Finds the treasure to drop
	 * @param statsString	a string
	 * @throws FileNotFoundException
	 */
	public String generateBaseItem(String statsString) {
		int index = 0;
		for (int i = 0; i < this.t.size(); i++) {
			if (statsString.equals(this.t.get(i).get(0))) {
				index = i;
				break;
			}
		}
		Random r = new Random();
		int treasureID = r.nextInt(3);
		String treasure = this.t.get(index).get(treasureID);
		
		if (treasure.contains("armo")) {
			for (int i = 0; i < this.t.size(); i++) {
				if (treasure.equals(this.t.get(i).get(0))) {
					index = i;
					break;
				}
			}
			treasureID = r.nextInt(3);
			return this.t.get(index).get(treasureID);
		} else {
			return generateBaseItem(treasure);
		}
	}
	
	/**
	 * Generates prefixes and suffixes for defense and treasure.
	 * @param item	an array of strings
	 * @param size	a string
	 * @throws FileNotFoundException
	 */
	public void generateAffix(String[] item) throws FileNotFoundException {
		Scanner prefix;
		Scanner suffix;
		int numAffix = 0;
		String[] ret = new String[2];
		
		if (this.size.equals("large")) {
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
		generatePrefix(prefix, whichAffix);
		generateSuffix(suffix, whichAffix);
		
		for (int i = 0; i < numAffix; i++) {
			if (affixes == 0) { 
				prefix.close();
				suffix.close();
				a = item; 
				return;
			} else if (affixes == 1) {
				ret[0] = p[0] + " " + item[0];
				ret[1] = p[1] + " " + p[2];
			} else if (affixes == 2) {
				ret[0] = item[0] + " " + s[0];
				ret[1] = s[1] + " " + s[2];
			} else if (affixes == 3) {
				ret[0] = p[0] + " " + item[0] + " " + s[0];
				int val = Integer.parseInt(s[1]) + Integer.parseInt(p[1]);
				ret[1] = val + " " + p[2];
			}
			
		}
		prefix.close();
		suffix.close();
		this.a = ret;
	}
	
	/**
	 * Splits up suffix to differentiate between suffix and values. 
	 * @param in	a scanner
	 * @param index	an integer
	 * @throws FileNotFoundException
	 */
	public void generateSuffix(Scanner in, int index) throws FileNotFoundException {
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
		this.s = ret;
	}
	
	/**
	 * Splits up suffix to differentiate between prefix and values. 
	 * @param in	a scanner
	 * @param index	an integer
	 * @throws FileNotFoundException
	 */
	public void generatePrefix(Scanner in, int index) throws FileNotFoundException {
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
		this.p = ret;
	}
	
	/**
	 * Helper method that finds the appropriate suffix or prefix
	 * @param index	an integer
	 * @param in	a scanner
	 */
	public String findAffix(int index, Scanner in) {
		String ret = new String();
		for (int i = 0; i < index; i++) {
			if (in.hasNext()) { ret = in.nextLine(); }
			else {	return null; }
		}
		return ret;
	}
	
	public String[] getAffix() {
		return a;
	}
	
	/**
	 * Returns the defense stats of the treasure dropped by the monster
	 * @param treasure		a string
	 * @throws FileNotFoundException
	 */
	public void generateBaseStats(String treasure) throws FileNotFoundException {
		Scanner dataIn;
		if (this.size.equals("large")) {
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
				this.armor = armor;
				return;
			}
		}
		dataIn.close();
		this.armor = armor;
	}
	
	/**
	 * Finds the value of a string that 
	 * represents the defense statistics of armor.
	 * @param armor	an array of strings
	 * @return		an integer
	 */
	public int getDefenseValue() {
		Random r = new Random();
		int defenseValue = r.nextInt(Integer.parseInt(this.armor[2]) - 
				Integer.parseInt(this.armor[1]) + 1) + Integer.parseInt(this.armor[1]);
		
		return defenseValue;
	}
	
	public String[] getBaseStats() {
		return armor;
	}
}
