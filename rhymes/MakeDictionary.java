package rhymes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;


/*
 * Создать непосредственный словарь транскрипций в виде
 * несложной базы данных, при помощи класса Properties.
 */
public class MakeDictionary {
	
	public static void makeDictionary() throws java.io.IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("rhymes//resources//adapted-dictionary.txt"), "cp1251"));
		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream("rhymes//resources//dictionary.dat"), "cp1251");
		
		String s;
		Properties p = new Properties();
		while ((s = br.readLine()) != null) {
			p.setProperty(s.toLowerCase(), Transcription.getTransсription(s));
		}
		p.store(fw, "Dictionary");
		br.close();
		fw.close();
	}
}
