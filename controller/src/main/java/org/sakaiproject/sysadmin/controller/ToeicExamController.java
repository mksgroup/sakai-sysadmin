package org.sakaiproject.sysadmin.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ToeicExamController extends BaseController {
	private final String BASE_API = "https://xlms-api.myworkspace.vn";

	@GetMapping({ "/", "/sign-in" })
	public ModelAndView signIn(HttpSession session, HttpServletResponse hResponse) {
		ModelAndView mav = new ModelAndView("sign_in");
		return mav;
	}

	@PostMapping("/do-sign-in")
	public ModelAndView doSignIn(@RequestParam(name = "_username") String username,
			@RequestParam(name = "_password") String password, HttpServletRequest request, HttpSession session) {
		ModelAndView mav = new ModelAndView("sign_in");
		String loginUrl = BASE_API + "/direct/session";
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add("_username", username);
		parameters.add("_password", password);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			session.setAttribute("sessionId", response.getBody());
			System.out.println("Login success:: " + response.getBody());
			mav.setView(new RedirectView(request.getContextPath() + "/sites"));
		}
		return mav;
	}

	@GetMapping("/sites")
	public ModelAndView getAllSites(HttpSession session) {
		ModelAndView mav = new ModelAndView("list_site");
		String sitesUrl = BASE_API + "/direct/site.json";
		String sessionId = (String) session.getAttribute("sessionId");
		/* Test 1 */
		/*
		 * try { URL url = new URL(sitesUrl); URLConnection con = url.openConnection();
		 * String cookiesHeader = con.getHeaderField("Set-Cookie"); List<HttpCookie>
		 * cookies = HttpCookie.parse(cookiesHeader); CookieManager cookieManager = new
		 * CookieManager(); cookies.forEach(cookie ->
		 * cookieManager.getCookieStore().add(null, cookie)); Optional<HttpCookie>
		 * jsessionidCookie = cookies.stream() .findAny() .filter(cookie ->
		 * cookie.getName().equals("JSESSIONID")); if (jsessionidCookie.isPresent()) {
		 * jsessionidCookie.get().setValue(sessionId); } else {
		 * cookieManager.getCookieStore().add(null, new HttpCookie("JSESSIONID",
		 * sessionId)); }
		 * 
		 * con = (HttpURLConnection) url.openConnection();
		 * 
		 * String cookieHeaderValue =
		 * cookieManager.getCookieStore().getCookies().stream() .map(cookie ->
		 * cookie.getName() + "=" + cookie.getValue())
		 * .collect(Collectors.joining(";"));
		 * 
		 * con.setRequestProperty("Cookie", cookieHeaderValue);
		 * con.setRequestProperty("Cookie", "SS=k"); con.setUseCaches(true);
		 * con.connect(); String cookieRes = con.getHeaderField("Set-Cookie");
		 * System.out.println("Cookie Res :: " + cookieRes); BufferedReader in = new
		 * BufferedReader( new InputStreamReader(con.getInputStream())); String
		 * inputLine; StringBuffer content = new StringBuffer(); while ((inputLine =
		 * in.readLine()) != null) { content.append(inputLine); } in.close();
		 * System.out.println(content.toString()); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */

		/* Test 2 */
		/*
		 * RestTemplate restTemplate = new RestTemplate(); HttpHeaders requestHeaders =
		 * new HttpHeaders();
		 * 
		 * Duration maxAge = Duration.ofHours(1); String cookieString = "JSESSIONID=" +
		 * sessionId + "; Max-Age=" + maxAge.getSeconds() +
		 * "; HttpOnly; Secure; SameSite=Lax; Path=/";
		 * requestHeaders.add(HttpHeaders.COOKIE, cookieString); HttpEntity<String>
		 * requestEntity = new HttpEntity<>(requestHeaders); ResponseEntity<String>
		 * rssResponse = restTemplate.exchange(sitesUrl, HttpMethod.GET, requestEntity,
		 * String.class);
		 * 
		 * System.out.println("response :: " + rssResponse.getBody()); List<String>
		 * cookies = requestHeaders.get(HttpHeaders.SET_COOKIE);
		 * System.out.println("cookies :: " + cookies);
		 * 
		 * HttpHeaders headers = rssResponse.getHeaders();
		 * System.out.println("headers :: " + headers);
		 */

		/* Test 3 */

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders requestHeaders = new HttpHeaders();
		ResponseEntity<String> rssResponse = restTemplate.getForEntity(sitesUrl, String.class);
		ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
			request.getHeaders().put("Cookie", Collections.singletonList("JSESSIONID=" + sessionId));
			return execution.execute(request, body);
		};
		restTemplate.setInterceptors(Collections.singletonList(interceptor));
		System.out.println("response :: " + rssResponse.getBody());
		List<String> cookies = requestHeaders.get(HttpHeaders.SET_COOKIE);
		System.out.println("cookies :: " + cookies);

		HttpHeaders headers = rssResponse.getHeaders();
		System.out.println("headers :: " + headers);

		return mav;
	}
}

/*
 * ResponseEntity<String> response = restTemplate.getForEntity(sitesApi,
 * String.class); ClientHttpRequestInterceptor interceptor = (request, body,
 * execution) -> { request.getHeaders().put("Cookie",
 * Collections.singletonList("JSESSIONID=" + sessionId + ".xlms-api")); return
 * execution.execute(request, body); };
 * restTemplate.setInterceptors(Collections.singletonList(interceptor));
 */
