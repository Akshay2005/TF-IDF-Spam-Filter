package spamfilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ReadFile
{
	public HashMap<Integer, String> readFileLineByLine(String path)
	{
		HashMap<Integer, String> spamDictMap = new HashMap<>();
		int i = 1;
		String line = "";
		File file = new File(path);

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null)
			{
				spamDictMap.put(i++, line.toLowerCase());
			}
			br.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return spamDictMap;
	}

	public String readCharBychar(String path)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(path));
			int c = 0;
			while ((c = br.read()) != -1)
			{
				sb.append((char) c);
			}
			br.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return sb.toString().toLowerCase();
	}
}

class TFIDFCalc
{
	public double tf(int termFreq)
	{
		if (termFreq == 0)
			return 0;

		return 1 + Math.log10(termFreq);
	}

	public double idf(int docCollectionSize, int docWithTerm)
	{
		return Math.log10(docCollectionSize / (1 + docWithTerm));
	}

	public double tfIdf(int termFreq, int docCollectionSize, int docWithTerm)
	{
		return tf(termFreq) * idf(docCollectionSize, docWithTerm);
	}
}

public class TFIDFSpamFilter
{

	public static int numberOfOccurrences(String haystack, String needle)
	{
		int count = 0;

		Pattern pattern = Pattern.compile(".*\\b" + needle + "\\S*\\b.*");
		Matcher matcher = pattern.matcher(haystack);

		while (matcher.find())
		{
			count++;
		}

		return count;
	}

	public static void main(String[] args)
	{
		HashMap<Integer, String> spamDictMap = new HashMap<>();
		spamDictMap = new ReadFile().readFileLineByLine("src/spamfilter/spamDictFile.txt");
		String[] doc = new String[6];
		for (int i = 0; i < 6; i++)
		{
			doc[i] = new ReadFile().readCharBychar("src/spamfilter/doc" + (i + 1) + ".txt");
		}

		Integer[][] st = new Integer[doc.length][spamDictMap.size()];

		System.out.println("Term  Frequency Matrix (Term vs Documents)");
		for (int i = 0; i < st.length; i++)
		{
			for (int j = 0; j < st[i].length; j++)
			{
				st[i][j] = numberOfOccurrences(doc[i], spamDictMap.get(j + 1));
				System.out.print(st[i][j] + " ");
			}
			System.out.println("\n");
		}

		Integer[] docWithTerm = new Integer[spamDictMap.size()];
		Arrays.fill(docWithTerm, 0);
		for (int i = 0; i < st.length; i++)
		{
			for (int j = 0; j < st[i].length; j++)
			{
				if (st[i][j] != 0)
					docWithTerm[j]++;
			}
		}

		System.out.println("Addition of TF-IDF of each term in Spam Dictionary found in document. ");
		for (int i = 0; i < st.length; i++)
		{
			double count = 0;
			TFIDFCalc calc = new TFIDFCalc();
			for (int j = 0; j < st[i].length; j++)
			{
				// System.out.println("doc" + (i + 1) + " Term: " +
				// spamDictMap.get(j + 1) + " TFIDF: "
				// + calc.tfIdf(st[i][j], doc.length, docWithTerm[j]));
				count += calc.tfIdf(st[i][j], doc.length, docWithTerm[j]);
			}
			System.out.println("doc" + (i + 1) + " : " + count);
		}
	}

}
