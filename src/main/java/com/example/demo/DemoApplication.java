package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@CrossOrigin(origins = { "*", "null" })
@SpringBootApplication
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@RequestMapping("/getKey")
	public String greeting() {
		long currentTimeMillis = System.currentTimeMillis();
		String sendPost = sendPost("http://leagueskin.net/k.php",
				"KeyID=5709fa10689c1bec47fcec956bfdd053&v=" + currentTimeMillis);
		return sendPost;
	}

	@RequestMapping("/getData")
	public Object getData() {
		LOLSkinModel instance = LOLSkinModel.getInstance();
		instance.setDate((new java.text.SimpleDateFormat("yyyy-MM-dd")).format(new Date()));
		return instance;
	}

	@RequestMapping("/execUpdate")
	public Object execUpdate() {
		try {

			String url = "http://leagueskin.net/p/download-mod-skin-lol-pro-2016-chn";
			final WebClient webClient = new WebClient(BrowserVersion.CHROME);// 新建一个模拟谷歌Chrome浏览器的浏览器客户端对象

			webClient.getOptions().setThrowExceptionOnScriptError(false);// 当JS执行出错的时候是否抛出异常, 这里选择不需要
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);// 当HTTP的状态非200时是否抛出异常, 这里选择不需要
			webClient.getOptions().setActiveXNative(false);
			webClient.getOptions().setCssEnabled(false);// 是否启用CSS, 因为不需要展现页面, 所以不需要启用
			webClient.getOptions().setJavaScriptEnabled(true); // 很重要，启用JS
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());// 很重要，设置支持AJAX
			webClient.getOptions().setTimeout(5000);

			HtmlPage page = null;
			try {
				page = webClient.getPage(url);// 尝试加载上面图片例子给出的网页
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				webClient.close();
			}

			webClient.waitForBackgroundJavaScript(10000);// 异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束

			String html = page.asXml();// 直接将加载完成的页面转换成xml格式的字符串
			Document doc = Jsoup.parse(html);
			String linkId = doc.getElementById("link_download3").text();
			String[] split = linkId.split(" ");
			String version = split[3];
			String link = "http://dl2.modskinpro.com/LEAGUESKIN_" + version + ".zip?update=2177";
			LOLSkinModel instance = LOLSkinModel.getInstance();
			instance.setUrl(link);
			instance.setVersion(version);
			System.out.println(doc);
			System.err.println(version);
			System.err.println(link);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url   发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}
