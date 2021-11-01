package rhymes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;


/*
 * Создать непосредственный словарь транскрипций в виде
 * несложной базы данных, при помощи класса Properties.
 */
public class GetDictionary {
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("rhymes//resources//adapted-dictionary.txt"));
		FileWriter fw = new FileWriter("dictionary.dat");
		
		String s;
		Properties p = new Properties();
		while ((s = br.readLine()) != null) {
			p.setProperty(s, Transcription.getTransсription(s));
		}
		p.store(fw, "Dictionary");
		br.close();
		fw.close();
	}
}
