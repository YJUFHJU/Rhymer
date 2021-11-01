package rhymes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Properties;


/*
 * Принимает слово от пользователя и ищет его в предварительно подготовленном словаре,
 * после чего ищет в этом словаре рифмующиеся с ним слова и выводит их с ударением,
 * показанным в виде заглавной буквы.
 */
public class Rhymes {

	public static void main(String[] args) throws java.io.IOException {
		File f = new File("rhymes//resources//dictionary.dat");
		if (!f.exists())
			MakeDictionary.makeDictionary();
		
		BufferedReader br = new BufferedReader(new FileReader(f));
		PrintStream ps = new PrintStream(System.out, false, "Cp866");
		Properties dict = new Properties();
		dict.load(br);
		br.close();
		Transcription.fillMap(Transcription.pairedCons, "бвгджзпфктшс", "пфктшсбвгджз");
		
		ps.print("Введите первое слово (чтобы выйти из программы введите стоп!): ");
		String s;
		br = new BufferedReader(new InputStreamReader(System.in, "Cp866"));
		while (!(s = br.readLine().strip()).equals("стоп!")) {
			String tr = dict.getProperty(s, null);
			if (tr != null) {
				boolean hasRhymes = false;
				int letters = s.length() - Integer.parseInt(tr.substring(0, tr.indexOf(" ")));
				int len = getCheckablePart(tr);
				if (len == 1) {
					if (tr.charAt(tr.length() - 2) == '\'') {
						len += 2;
					} else {
						len++;
					}
				}
				tr = tr.substring(tr.length() - len);
				int i = 1;
				
				for (Object t: dict.keySet()) {
					String key = (String) t;
					if (key.equals(s)) continue;
					String temp = (String) dict.get(t);
					if (temp.length() >= len && canRhyme(temp.substring(temp.length() - len), tr, letters)) {
						int stress = Integer.parseInt(temp.substring(0, temp.indexOf(" ")));
						char letter = key.charAt(stress);
						if (letter == 'ё') {
							letter = 'Ё';
						} else {
							letter -= 32;
						}
						if (i % 10 == 0) {
							ps.println();
						}
						ps.print(key.substring(0, stress));
						ps.print((char) letter);
						ps.print(key.substring(stress + 1));
						ps.print(" ");
						i++;
						hasRhymes = true;
					}
				}
				if (!hasRhymes) {
					ps.print("К сожалению, у меня нет хороших рифм для этого слова.");
				}
			} else {
				ps.print("Похоже я не знаю такое слово :(");
			}
			ps.print("\n\nВведите следующее слово: ");
			ps.println();
		}
		ps.flush();
	}
	
	/*
	 * Возвращает транскрипцию без заглавной ударной буквы.
	 */
	static String toLowerCase(String s) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) < 'а') {
				if (s.charAt(i) == 'Ё') {
					sb.append('ё');
				} else {
					sb.append((char) (s.charAt(i) + 32));
				}
			} else {
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}
	
	
	/*
	 * Возвращает индекс позиции ударения в транскрипции.
	 */
	static int getCheckablePart(String tr) {
		for (int i = tr.length() - 1; i >= 0; i--) {
			char cur = tr.charAt(i);
			if (cur < 'а' && cur != '\'') {
				return tr.length() - i;
			}
		}
		return -1;
	}
	
	
	/*
	 * Сверяет часть транскрипции введенного слова до ударения
	 * с частью транскрипции слова из словаря до ударения.
	 * 
	 * Возвращает true, если части отличаются не более чем по одной позиции,
	 * соответствующей парной согласной, при общей длине >= 4,
	 * или соответсвующей любой согласной, при общей длине >= 5.
	 */
	static boolean canRhyme(String sub1, String sub2, int letters) {
		int paCo = 0;
		int letCnt = 0;
		int difCons = 0;
		
		for (int i = 0; i < sub1.length(); i++) {
			char c1 = sub1.charAt(i);
			char c2 = sub2.charAt(i);
			
			if (c2 != '\'')
				letCnt++;
			
			if (c1 != c2) {
				if (Transcription.pairedCons.containsKey(c1) && Transcription.pairedCons.get(c1) == c2) {
					paCo++;
				} else if (difCons == 0 && Transcription.cons.contains(c1) && Transcription.cons.contains(c2)
						   && !Transcription.hardSoftCons.contains(c1) && !Transcription.hardSoftCons.contains(c2)
						   && (i != sub1.length() - 1)) {
							difCons++;
				} else if ((c1 == 'И' && c2 == 'Ы') || (c1 == 'Ы' && c2 == 'И')) {
					//поменять
				} else if ((i == sub1.length() - 1) && (difCons == 0) &&
						  ((c1 == '\'' && c2 == sub1.charAt(i - 1)) || (c2 == '\'' && c1 == sub2.charAt(i - 1)))) {
					//поменять
				} else {
					return false;
				}
			}
		}
		if (paCo > 1 || (paCo == 1 && letCnt < 4) || (difCons == 1 && (letters < 5 || paCo > 0))) return false;
		return true;
	}
}
