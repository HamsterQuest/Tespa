import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AsyncTest {

	public static List<String> scrapeAsync(ArrayList<String> links) throws InterruptedException, ExecutionException {
		List<String> webPageLinks = links;// A list of 100 web page links
		// Download contents of all the web pages asynchronously
		List<CompletableFuture<String>> pageContentFutures = webPageLinks.stream()
				.map(webPageLink -> downloadWebPage(webPageLink)).collect(Collectors.toList());

		// Create a combined Future using allOf()
		CompletableFuture<Void> allFutures = CompletableFuture.allOf(
		        pageContentFutures.toArray(new CompletableFuture[pageContentFutures.size()])
		);
		
		// When all the Futures are completed, call `future.join()` to get their results and collect the results in a list -
		CompletableFuture<List<String>> allPageContentsFuture = allFutures.thenApply(v -> {
		   return pageContentFutures.stream()
		           .map(pageContentFuture -> pageContentFuture.join())
		           .collect(Collectors.toList());
		});
		
		/*CompletableFuture<Long> countFuture = allPageContentsFuture.thenApply(pageContents -> {
			return pageContents.stream()
		            .filter(pageContent -> pageContent.contains("CompletableFuture"))
		            .count();
		});*/

		//System.out.println("Number of Web Pages having the keyword - " + 
		return allPageContentsFuture.get();
	}

	private static CompletableFuture<String> downloadWebPage(String pageLink) {
		return CompletableFuture.supplyAsync(() -> {
			// Code to download and return the web page's content
			try {
				return con(pageLink);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "broke";
			}
		});
	}
    private static Matcher reg(String regex, String text) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m;
    }
	private static String con(String u) throws IOException {

		String encodedURL = URI.create(u).toASCIIString();
		URL url = new URL(encodedURL);

		BufferedReader in = null;
		try {
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			conn.connect();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			in = new BufferedReader(isr);
		} catch (Exception e) {
			return "No page";
		}
		BufferedReader bufferedReader = new BufferedReader(in);
		StringBuilder inputBuilder = new StringBuilder();
		try {
			inputBuilder = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				inputBuilder.append(line);
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputBuilder.toString();
	}

}
