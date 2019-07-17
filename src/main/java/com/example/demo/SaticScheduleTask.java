package com.example.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@Component
@Configuration // 1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling // 2.开启定时任务
public class SaticScheduleTask {
	// 3.添加定时任务
	@Scheduled(cron = "0 0 6,12,20 * * ?")
	// @Scheduled(fixedRate=5000)
	private void configureTasks() {
		try {

			String url = "http://leagueskin.net/p/download-mod-skin-lol-pro-2016-chn";
			final WebClient webClient = new WebClient(BrowserVersion.CHROME);// 新建一个模拟谷歌Chrome浏览器的浏览器客户端对象

			webClient.getOptions().setThrowExceptionOnScriptError(false);// 当JS执行出错的时候是否抛出异常, 这里选择不需要
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);// 当HTTP的状态非200时是否抛出异常, 这里选择不需要
			webClient.getOptions().setActiveXNative(false);
			webClient.getOptions().setCssEnabled(false);// 是否启用CSS, 因为不需要展现页面, 所以不需要启用
			webClient.getOptions().setJavaScriptEnabled(true); // 很重要，启用JS
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());// 很重要，设置支持AJAX

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
			LOLSkinModel instance = LOLSkinModel.getInstance();
			String link = "http://dl2.modskinpro.com/LEAGUESKIN_" + version + ".zip?update=2177";
			instance.setUrl(link);
			instance.setVersion(version);
			System.out.println(doc);
			System.out.println(link);
			System.out.println(version);
			FileDownload.downLoadFromUrl(link, "LEAGUESKIN_" + version + ".zip", "/var/www/html/lolskin/");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}