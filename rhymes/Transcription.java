package rhymes;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Transcription {
	
	//гласные
	final static HashSet<Character> vows = new HashSet<>(Arrays.asList('а', 'о', 'у', 'э', 'ы', 'и', 'е', 'ё', 'ю', 'я'));
	
	//смягчающие гласные
	final static HashSet<Character> vowsMakeSoft = new HashSet<>(Arrays.asList('и', 'е', 'ё', 'ю', 'я', 'И', 'Е', 'Ё', 'Ю', 'Я'));
	
	//изменяющиеся гласные
	final static HashMap<Character, Character> changingVows = new HashMap<>();
	
	//согласные
	final static HashSet<Character> cons = new HashSet<>(Arrays.asList('б', 'в', 'г', 'д', 'ж', 'з', 'й', 'к', 'л', 
													     'м', 'н', 'п', 'р', 'с', 'т', 'ф', 'х', 'ц', 'ч', 'ш', 'щ'));
	
	//мягкие согласные
	final static HashSet<Character> softCons = new HashSet<>(Arrays.asList('й', 'ч', 'щ'));
	
	//твердые согласные
	final static HashSet<Character> hardCons = new HashSet<>(Arrays.asList('ж', 'ш', 'ц'));
	
	//твердые и мягкие согласные
	final static HashSet<Character> hardSoftCons = new HashSet<>(Arrays.asList('ж', 'ш', 'ц', 'й', 'ч', 'щ'));
	
	//парные согласные
	final static HashMap<Character, Character> pairedCons = new HashMap<>();
	
	//звонкие парные согласные
	final static HashMap<Character, Character> voicedPairedCons = new HashMap<>();
	
	//глухие парные согласные
	final static HashMap<Character, Character> voicelessPairedCons = new HashMap<>();
	
	//сочетания из которых выпадает вторая буква
	final static String[] prolapse1 = {
									  "стн", "стл", "здн", "рдц", "рдч", "стц",
								      "здц", "нтск", "ндск", "ндц", "нтств", "стск"
								      };
	
	//сочетания из которых выпадает первая буква
	final static String[] prolapse2 = {"лнц", "вств"};
	
	//сочетания дающие звук 'ц'
	final static String[] ts = {"тс", "тьс"};
	
	//сочетания дающие звук 'щ'
	final static String[] tsch = {"жч", "здч", "зч", "сч"};
	
	
	
	/*
	 * Произвести транскрибирование слова и вернуть позицию ударения в слове и саму транскрипцию слова через пробел.
	 * 
	 * Полученная транскрипция будет не абсолютно достоверна, она будет точна лишь в той степени,
	 * чтобы проанализировать, могут ли слова рифмоваться.
	 */
	static String getTransсription(String w) {
		fillMap(voicedPairedCons, "бвгджз", "пфктшс");
		fillMap(voicelessPairedCons, "пфктшс", "бвгджз");
		fillMap(changingVows, "еёюяЕЁЮЯ", "эоуаЭОУА");
		
		int lastStress = 0; //позиция ударения в слове
		for (int i = 0; i < w.length(); i++) {
			if (w.charAt(i) < 'а') {
				lastStress = i;
			}
		}
		
		w = prolapse(prolapse1, 1, w);
		w = prolapse(prolapse2, 2, w);
		w = removeDoubleCons(w);
		w = toTsOrTsch(ts, 1, w);
		w = toTsOrTsch(tsch, 2, w);
		w = changeO(w);
		w = toH(w);
		w = removeDoubleCons(changeTones(w));
		w = addJ(w);
		w = makeSoft(w);
		w = tobI(w);
		w = toE(w);
		w = changeChangingVows(w);
		
		return lastStress + " " + w;
	}
	
	
	/*
	 * Заполнить коллекцию, в которой буква-значение соответствует тому, во что будет переходить буква-ключ, при определенных условиях.
	 */
	static void fillMap(HashMap<Character, Character> m, String keys, String values) {
		for (int i = 0; i < keys.length(); i++) {
			m.put(keys.charAt(i), values.charAt(i));
		}
	}
	
	
	/*
	 * Убрать выпадающие буквы.
	 */
	private static String prolapse(String[] p, int c, String w) {
		for (int i = 0; i < p.length; i++) {
			String pr = p[i];
			if (w.contains(pr)) {
				String change;
				if (c == 1) {
					change = pr.charAt(0) + pr.substring(2, pr.length());
				} else {
					change = pr.substring(1);
				}
				w = w.replace(pr, change);
			}
		}
		return w;
	}
	
	
	/*
	 * Заменить сочетания дающие звук 'ц' или 'щ'.
	 */
	private static String toTsOrTsch(String[] t, int n, String w) {
		String repl;
		if (n == 1) {
			repl = "ц";
		} else {
			repl = "щ";
		}
		for (int i = 0; i < t.length; i++) {
			if (w.contains(t[i])) {
				w = w.replace(t[i], repl);
			}
		}
		return w;
	}
	
	
	/*
	 * Убрать из сочетаний двойных согласных одну согласную.
	 */
	private static String removeDoubleCons(String w) {
		StringBuilder sb = new StringBuilder();
		char prev = 0;
		for (int i = 0; i < w.length(); i++) {
			if (!(w.charAt(i) == prev && cons.contains(w.charAt(i)))) {
				sb.append(w.charAt(i));
			}
			prev = w.charAt(i);
		}
		return sb.toString();
	}
	
	
	/*
	 * Перевести безударные буквы 'о' -> 'а'.
	 */
	private static String changeO(String w) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < w.length(); i++) {
			if (w.charAt(i) == 'о') {
				sb.append('а');
			} else {
				sb.append(w.charAt(i));
			}
		}
		return sb.toString();
	}
	
	
	/*
	 * Перевести сочетания "гк"/"гч" -> "хк"/"хч".
	 */
	private static String toH(String w) {
		StringBuilder sb = new StringBuilder(w.substring(0, 1));
		for (int i = 1; i < w.length(); i++) {
			if ((w.charAt(i) == 'ч' || w.charAt(i) == 'к') && w.charAt(i - 1) == 'г') {
				sb.deleteCharAt(sb.length() - 1);
				sb.append('х');
			}
			sb.append(w.charAt(i));
		}
		return sb.toString();
	}
	
	
	/*
	 * Провести оглушение и озвончание соответствующих букв, если требуется.
	 */
	private static String changeTones(String w) {
		StringBuilder sb = new StringBuilder();
		
		//оглушение
		char prev = 'п';
		for (int i = w.length() - 1; i >= 0; i--) {
			char cur = w.charAt(i);
			if (voicelessPairedCons.containsKey(prev) && voicedPairedCons.containsKey(cur)) {
				sb.append(voicedPairedCons.get(cur));
			} else {
				sb.append(cur);
			}
			if (!"ъь".contains(String.valueOf(cur))) {
				prev = sb.charAt(sb.length() - 1);
			}
		}
		
		//озвончание
		prev = sb.charAt(0);
		w = sb.reverse().toString();
		sb = new StringBuilder();
		for (int i = w.length() - 1; i >= 0; i--) {
			char cur = w.charAt(i);
			if (voicedPairedCons.containsKey(prev) && voicelessPairedCons.containsKey(cur) && prev != 'в') {
				sb.append(voicelessPairedCons.get(cur));
			} else {
				sb.append(cur);
			}
			if (!"ъь".contains(String.valueOf(cur))) {
				prev = sb.charAt(sb.length() - 1);
			}
		}
		
		return sb.reverse().toString();
	}
	
	
	/*
	 * Добавить звуки 'й' в соответствующих позициях, если требуется.
	 */
	private static String addJ(String w) {
		ArrayDeque<Integer> ad = new ArrayDeque<>();
		StringBuilder sb = new StringBuilder();
		boolean isStressed = false;
		char prev = w.charAt(0);
		
		if (prev < 'а') {
			ad.offer(0);
			isStressed = true;
			if (prev == 'Ё') {
				prev = 'ё';
			} else {
				prev += 32;
			}
		}
		if (changingVows.containsKey(prev)) {
			sb.append('й');
			if (isStressed) {
				ad.offer(ad.poll() + 1);
			}
		}
		sb.append(prev);
		for (int i = 1; i < w.length(); i++) {
			isStressed = false;
			char cur = w.charAt(i);
			if (cur < 'а') {
				ad.offer(sb.length());
				isStressed = true;
				if (cur == 'Ё') {
					cur = 'ё';
				} else {
					cur += 32;
				}
			}
			if (changingVows.containsKey(cur) && (vows.contains(prev) || "ъь".contains(String.valueOf(prev)))) {
				sb.append('й');
				if (isStressed) {
					ad.offer(ad.poll() + 1);
				}
			}
			sb.append(cur);
			prev = cur;
		}
		for (int i: ad) {
			char ch = sb.charAt(i);
			if (ch != 'ё') {
				sb.setCharAt(i, (char) (ch - 32));
			} else {
				sb.setCharAt(i, 'Ё');
			}
		}
		return sb.toString();
	}
	
	
	/*
	 * Добавить символы, сигнализирующие о мягкости согласного звука: ', если требуется.
	 */
	private static String makeSoft(String w) {
		StringBuilder sb = new StringBuilder(w.substring(0, 1));
		char prev = w.charAt(0);
		for (int i = 1; i < w.length(); i++) {
			char cur = w.charAt(i);
			if ((cons.contains(prev) && !hardCons.contains(prev) && (vowsMakeSoft.contains(cur) || cur == 'ь')) || softCons.contains(prev)) {
				sb.append('\'');
			}
			if (cur != 'ъ' && cur != 'ь') {
				sb.append(cur);
			}
			prev = cur;
		}
		if (softCons.contains(prev)) {
			sb.append('\'');
		}
		return sb.toString();
	}
	
	
	/*
	 * Перевести 'и' -> 'ы', если требуется.
	 */
	private static String tobI(String w) {
		StringBuilder sb = new StringBuilder(w.substring(0, 1));
		char prev = w.charAt(0);
		for (int i = 1; i < w.length(); i++) {
			char cur = w.charAt(i);
			if ((cur == 'и' || cur == 'И') && hardCons.contains(prev)) {
				if (cur == 'и') {
					sb.append('ы');
				} else {
					sb.append('Ы');
				}
			} else {
				sb.append(cur);
			}
			prev = cur;
		}
		return sb.toString();
	}
	
	
	/*
	 * Перевести 'а'/'е'/'я' -> 'и', если требуется.
	 */
	private static String toE(String w) {
		StringBuilder sb = new StringBuilder(w.substring(0, 1));
		int lastVow = 0;
		for (int i = 0; i < w.length(); i++) {
			if (vows.contains(w.charAt(i)) || (w.charAt(i) < 'а' && w.charAt(i) != '\'')) {
				lastVow = i;
			}
		}
		char prev = w.charAt(0);
		for (int i = 1; i < w.length(); i++) {
			char cur = w.charAt(i);
			if ("аея".contains(w.substring(i, i + 1)) && prev == '\'' && i < lastVow) {
				sb.append('и');
			} else {
				sb.append(cur);
			}
			prev = cur;
		}
		return sb.toString();
	}
	
	
	/*
	 * Перевести оставшиеся 'е'/'ё'/'ю'/'я' -> 'э'/'о'/'у'/'а', если требуется.
	 */
	private static String changeChangingVows(String w) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < w.length(); i++) {
			char cur = w.charAt(i);
			if (changingVows.containsKey(cur)) {
				sb.append(changingVows.get(cur));
			} else {
				sb.append(cur);
			}
		}
		return sb.toString();
	}
}
