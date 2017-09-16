import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * WikiCrawler Program to crawl the wiki pages and construct web graph
 * @author Smriti Manral
 *
 */
public class WikiCrawler {
	//queue to store vertices in the order they are to be examined
	private Queue<String> Q;
	//hashset to store visited elements
	private Set<String> visited;
	//representing name of a file–The graph will be written to this file
	private String fileName;
	//An integer max representing Maximum number pages to be crawled
	private int max;
	//base url 
	private static final String BASE_URL = "https://en.wikipedia.org";
	
/**
 * WikiCrawler Constructor	
 * @param seedUrl
 * @param max
 * @param fileName
 */
	public WikiCrawler(String seedUrl, int max, String fileName){
		this.fileName=fileName;
		this.max=max;
		visited = new HashSet<String>();
		Q = new LinkedList<String>(); 
		Q.add(seedUrl);
	}
/**
 * This method gets a string (that represents contents of a .html file) as parameter. 
 * This method should return an array list (of Strings) consisting of links from doc. 
 * Type of this method is ArrayList<String>. You may assume that the html page is 
 * the source (html) code of a wiki page. This method must,
    • Extract only wiki links. I.e. only links that are of form /wiki/XXXXX.
	• Only extract links that appear after the first occurrence of the html tag <p> (or <P>).
	• Should not extract any wiki link that contain the characters “#” or “:”.
	• The order in which the links in the returned array list must be exactly the same order in which they appear in doc.
 * @param doc
 * @return
 */  
	public ArrayList<String> extractLinks(String doc){
		int counter=0;
		ArrayList<String> wikiLinks = new ArrayList<String>();
		String paraText="";
		//pattern to match the first occurance of <p> tag
		Pattern firstParaTag = Pattern.compile("<p>(?s).*");
		Matcher wM = firstParaTag.matcher(doc);
		while (wM.find()) {
			paraText = wM.group(0);
		}
		//pattern to match all the wiki tags after first occurance of <p> tag
		Pattern wikiPattern = Pattern.compile("href=\"(/wiki/[^\":#]+)\"[^[<a]]*");
		Matcher wikiMatcher = wikiPattern.matcher(paraText);
			while (wikiMatcher.find()) {
				if(!(wikiLinks.contains(wikiMatcher.group(1)))){ //don't include same link(duplicates) more than 1 time to avoid multiple edges					
					//if we have not visited max many links yet then extract n(where n = max-# of links visited) new links and add those to wikilinks arraylist otherwise only add the ones that are already visited
					if(max - visited.size()!=0){
						if(!(visited.contains(wikiMatcher.group(1)))&&counter<(max-visited.size())){ //this is a new link add it if counter is below the number of new links needed
							wikiLinks.add(wikiMatcher.group(1));
							counter++;
						}
					}
					if(visited.contains(wikiMatcher.group(1))){//if visited contains this matched wikilink then add it to wikilinks anyways
						wikiLinks.add(wikiMatcher.group(1));
					}
				}
			}
		return wikiLinks;
	}
/**
 * This method should construct the web graph over following pages: Consider the first 
 * max many pages that are visited when you do a BFS with seedUrl. The program should 
 * construct the web graph only over those pages and write the graph to the file 
 * fileName.
 */
	public void crawl(){
		String currentUrl="";
		int requestCounter=0; //counter variable to keep track of number of http requests made
		ArrayList<String> wikiLinks = new ArrayList<String>();
		URL wikiUrl;
		try {
			//PrintWriter writer = new PrintWriter(fileName);
			FileWriter fw = new FileWriter(fileName,true);
			fw.write(max+"\n");//writing the number of vertices in the graph to the file
			visited.add(Q.peek());
			while(!Q.isEmpty()){	
				String wikiSrcCode="";
				//Let currentPage be the first element of Q.
				currentUrl=Q.poll();
				wikiUrl = new URL(BASE_URL+currentUrl);
				//Send a request to server at currentPage and download currentPage.
				InputStream input = wikiUrl.openStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(input));
				String sourceCode;
				while ((sourceCode = br.readLine()) != null){
					 wikiSrcCode += sourceCode;
				}
				requestCounter++;
				//Wait for at least 3 seconds after every 100 requests.
				if(requestCounter%100==0){
					Thread.sleep(3000);
				}
				//Extract all links from currentPage.
				wikiLinks=extractLinks(wikiSrcCode);		
				//for every link u that appears in current page; If u is not in visited add u to the end of Q, and add u to visited.
				for(String wLink: wikiLinks){
					if(!(wLink.equals(currentUrl))){ //if wlink is same as currentUrl then don't add it to visited, to avoid self loops
						if(!(visited.contains(wLink))){ //if wLink is in visited set that means its already in queue or has already been examined so don't add wlink to queue 
							Q.add(wLink);
						}
						visited.add(wLink);
						fw.write(currentUrl+" "+wLink+"\n");
					}
				}
				br.close();
			}
			fw.close();	
		}	
		catch (MalformedURLException e) {
				e.printStackTrace();
		} catch (IOException e) {
	            System.out.println("Error in out HTTP request " + e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
