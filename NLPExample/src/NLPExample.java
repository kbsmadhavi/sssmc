
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

public class NLPExample {

	public static void main(String[] args) {
		try {
			SentenceDetect();
			//Tokenize();
			findName();
			//POSTag();
			//chunk();
			//Parse();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void SentenceDetect() throws InvalidFormatException,
			IOException {
		String paragraph = "Hi. How are you? This is Mike.";
		System.out.println(new File(".").getAbsoluteFile());
		// always start with a model, a model is learned from training data
		InputStream is = new FileInputStream("resources/en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);

		String sentences[] = sdetector.sentDetect(paragraph);

		System.out.println(sentences[0]);
		System.out.println(sentences[1]);
		is.close();
	}
	public static void Tokenize() throws InvalidFormatException, IOException {
		InputStream is = new FileInputStream("resources/en-token.bin");
	 
		TokenizerModel model = new TokenizerModel(is);
	 
		Tokenizer tokenizer = new TokenizerME(model);
	 
		String tokens[] = tokenizer.tokenize("Hi. How are you? This is Mike.");
	 
		for (String a : tokens)
			System.out.println(a);
	 
		is.close();
	}
	public static void findName() throws IOException {
		InputStream is = new FileInputStream("resources/en-ner-person.bin");
	 
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();
	 
		NameFinderME nameFinder = new NameFinderME(model);
	 
		String []sentence = new String[]{
			    "Mike",
			    "Smith",
			    "is",
			    "a",
			    "good",
			    "person"
			    };
	 
			Span nameSpans[] = nameFinder.find(sentence);
	 
			for(Span s: nameSpans)
				System.out.println(s.toString());			
	}

	public static void POSTag() throws IOException {
		POSModel model = new POSModelLoader()	
			.load(new File("resources/en-pos-maxent.bin"));
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(model);
	 
		String input = "Hi. How are you? This is Mike.";
		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new StringReader(input));
	 
		perfMon.start();
		String line;
		while ((line = lineStream.read()) != null) {
	 
			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
					.tokenize(line);
			String[] tags = tagger.tag(whitespaceTokenizerLine);
	 
			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
			System.out.println(sample.toString());
	 
			perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();
	}
	public static void chunk() throws IOException {
		POSModel model = new POSModelLoader()
				.load(new File("resources/en-pos-maxent.bin"));
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(model);
	 
		String input = "Hi. How are you? This is Mike.";
		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new StringReader(input));
	 
		perfMon.start();
		String line;
		String whitespaceTokenizerLine[] = null;
	 
		String[] tags = null;
		while ((line = lineStream.read()) != null) {
			whitespaceTokenizerLine = WhitespaceTokenizer.INSTANCE
					.tokenize(line);
			tags = tagger.tag(whitespaceTokenizerLine);
	 
			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
			System.out.println(sample.toString());
				perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();
	 
		// chunker
		InputStream is = new FileInputStream("resources/en-chunker.bin");
		ChunkerModel cModel = new ChunkerModel(is);
	 
		ChunkerME chunkerME = new ChunkerME(cModel);
		String result[] = chunkerME.chunk(whitespaceTokenizerLine, tags);
	 
		for (String s : result)
			System.out.println(s);
	 
		/*String[] string = chunkerME.chunk(whitespaceTokenizerLine, tags);
		for (String s : string)
			System.out.println(s.toString());*/
	}
	public static void Parse() throws InvalidFormatException, IOException {
		// http://sourceforge.net/apps/mediawiki/opennlp/index.php?title=Parser#Training_Tool
		InputStream is = new FileInputStream("resources/en-parser-chunking.bin");
	 
		ParserModel model = new ParserModel(is);
	 
		Parser parser = ParserFactory.create(model);
	 
		String sentence = "Programcreek is a very huge and useful website.";
		Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
	 
		for (Parse p : topParses)
			p.show();
	 
		is.close();
	 
		/*
		 * (TOP (S (NP (NN Programcreek) ) (VP (VBZ is) (NP (DT a) (ADJP (RB
		 * very) (JJ huge) (CC and) (JJ useful) ) ) ) (. website.) ) )
		 */
	}
}
