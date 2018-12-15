package com.ehear.aiot.cloud.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ehear.aiot.cloud.common.Constonts;
import com.ehear.aiot.cloud.dao.AlexaDao;
import com.ehear.aiot.cloud.dao.CustomDao;
import com.ehear.aiot.cloud.dao.DeviceDao;
import com.ehear.aiot.cloud.dao.OperationDao;
import com.ehear.aiot.cloud.model.CmdBean;
import com.ehear.aiot.cloud.model.DeviceBean;
import com.ehear.aiot.cloud.model.MultiplyCMD;
import com.ehear.aiot.cloud.model.UserBean;
import com.ehear.aiot.cloud.util.CmdUtil;
import com.ehear.aiot.cloud.util.CustomUtil;
import com.ehear.aiot.cloud.util.HexUtil;
import com.ehear.aiot.cloud.util.MsgException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@Slf4j
public class AlexaServlet {
	private static final String oauthClientAddress = "http://localhost:8080/RealRelaxV2";
	private static final String oauthServerAddress = "http://139.199.80.116:8080/alexaOauth";


	@RequestMapping(value = "/index")
	public void index(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.getSession().setAttribute("OperationBeanList", null);
			String custom_name = "RELAX";
			request.getSession().setAttribute("custom_name", custom_name);
			HttpServletRequest requ = (HttpServletRequest) request;
			HttpSession session = requ.getSession();
			session.removeAttribute("alexa_redirect_uri");
			session.removeAttribute("alexa_state");
			session.removeAttribute("alexa_client_id");
			String alexa_redirect_uri = request.getParameter("redirect_uri");
			String alexa_state = request.getParameter("state");
			String alexa_client_id = request.getParameter("client_id");
			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			if (null == alexa_redirect_uri || null == alexa_state || null == alexa_client_id
					|| "".equals(alexa_redirect_uri) || "".equals(alexa_state) || "".equals(alexa_client_id)) {
				alexa_redirect_uri = "ehear";
				alexa_state = "alexa_state";
				alexa_client_id = "alexa_client_id";
			}
			session.setAttribute("alexa_redirect_uri", alexa_redirect_uri);
			session.setAttribute("alexa_state", alexa_state);
			session.setAttribute("alexa_client_id", alexa_client_id);

			log.info("AlexaRequestMessage:" + request.getParameter("redirect_uri") + request.getParameter("state"));
			response.sendRedirect(request.getContextPath() + "/login.jsp" + "?alexa_redirect_uri=" + alexa_redirect_uri
					+ "&alexa_state=" + alexa_state + "&alexa_client_id=" + alexa_client_id);
		} catch (Exception e) {
		}
	}

	@RequestMapping(value = "/exit")
	public void exit(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 下关机命令
			String mac = (String) request.getSession().getAttribute("macCode");

			request.getSession().setAttribute("stop_flag", "true");
			// 登录的时候打开待机开关
			SessionConnection.addCmdList(mac,
					this.getCMDByStr("addPower;" + "forward" + ";" + "" + ";" + "stop", mac));

			request.getSession().setAttribute("OperationBeanList", null);
			String custom_name = "RELAX";
			request.getSession().setAttribute("custom_name", custom_name);
			HttpServletRequest requ = (HttpServletRequest) request;
			HttpSession session = requ.getSession();
			session.removeAttribute("alexa_redirect_uri");
			session.removeAttribute("alexa_state");
			session.removeAttribute("alexa_client_id");
			String alexa_redirect_uri = request.getParameter("redirect_uri");
			String alexa_state = request.getParameter("state");
			String alexa_client_id = request.getParameter("client_id");
			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			if (null == alexa_redirect_uri || null == alexa_state || null == alexa_client_id
					|| "".equals(alexa_redirect_uri) || "".equals(alexa_state) || "".equals(alexa_client_id)) {
				alexa_redirect_uri = "ehear";
				alexa_state = "alexa_state";
				alexa_client_id = "alexa_client_id";
			}
			session.setAttribute("alexa_redirect_uri", alexa_redirect_uri);
			session.setAttribute("alexa_state", alexa_state);
			session.setAttribute("alexa_client_id", alexa_client_id);

			log.info("AlexaRequestMessage:" + request.getParameter("redirect_uri") + request.getParameter("state"));
			response.sendRedirect(request.getContextPath() + "/login.jsp" + "?alexa_redirect_uri=" + alexa_redirect_uri
					+ "&alexa_state=" + alexa_state + "&alexa_client_id=" + alexa_client_id);
		} catch (Exception e) {
		}
	}

	@RequestMapping(value = "/LoginServlet")
	public void LoginServlet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().removeAttribute("OperationBeanList");
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String username = request.getParameter("uname");
		String password = request.getParameter("upassword");
		String alexa_redirect_uri = request.getParameter("alexa_redirect_uri");
		String alexa_state = request.getParameter("alexa_state");
		String alexa_client_id = request.getParameter("alexa_client_id");
		if (null == alexa_redirect_uri || null == alexa_state || null == alexa_client_id
				|| "".equals(alexa_redirect_uri) || "".equals(alexa_state) || "".equals(alexa_client_id)) {
			alexa_redirect_uri = "e";
			alexa_state = "alexa_state";
			alexa_client_id = "alexa_client_id";
		}
		UserService service = new UserService();
		UserBean user = service.isUser(username, password);
		if (user == null) {
			request.setAttribute("msg", "The username does not exist");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		} else {
			request.getSession().setAttribute("user", user);
			Cookie remNameC = new Cookie("remname", URLEncoder.encode(user.getUname(), "utf-8"));
			remNameC.setPath(request.getContextPath());
			remNameC.setMaxAge(3600 * 24 * 30);
			response.addCookie(remNameC);
			if ("ok".equals(request.getParameter("psd"))) {
				Cookie remNameC1 = new Cookie("psd", password);
				remNameC1.setPath(request.getContextPath());
				remNameC1.setMaxAge(3600 * 24 * 30);
				response.addCookie(remNameC1);
			} else {
				Cookie remNameC1 = new Cookie("psd", "");
				remNameC1.setPath(request.getContextPath());
				remNameC1.setMaxAge(0);
				response.addCookie(remNameC1);
			}
			log.info("<user>login:" + user.toString());
			request.getSession().removeAttribute("OperationBeanList");
			if (!"".equals((String) request.getSession().getAttribute("custom_name"))) {
				request.getSession().setAttribute("OperationBeanList", CustomUtil
						.handlecustom((String) request.getSession().getAttribute("custom_name"), user.getUname()));
			}
			response.sendRedirect(request.getContextPath() + "/requestServerCode" + "?username=" + username + "&psd="
					+ password + "&alexa_redirect_uri=" + alexa_redirect_uri + "&alexa_state=" + alexa_state
					+ "&alexa_client_id=" + alexa_client_id);
		}
	}

	@RequestMapping(value = "/checkUser")
	public void checkUser(HttpServletRequest request, HttpServletResponse response, String uname, String upassword)
			throws ServletException, IOException {
		UserService service = new UserService();
		log.info("/checkUser" + "uname=" + uname + "upassword=" + upassword);
		UserBean user = service.isUser(uname, upassword);
		if (user != null) {
			response.getWriter().print("true");
		} else {
			response.getWriter().print("false");
		}
	}

	@RequestMapping(value = "/RefreshOperationBeanList")
	public void RefreshOperationBeanList(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		try {
			// request.getSession().setAttribute("custom_name", "AUTO1");
			request.getSession().setAttribute("OperationBeanList",
					CustomUtil.handlecustom((String) request.getSession().getAttribute("auto_mode"),
							(String) request.getSession().getAttribute("username")));

		} catch (Exception e) {

		}

	}

	@RequestMapping(value = "/requestServerCode")
	public void requestServerCode(HttpServletRequest request, HttpServletResponse response)
			throws OAuthProblemException, IOException {
		String alexa_state = request.getParameter("alexa_state").trim();
		request.getSession().setAttribute("alexa_state", alexa_state);
		String alexa_redirect_uri = request.getParameter("alexa_redirect_uri").trim();
		request.getSession().setAttribute("alexa_redirect_uri", alexa_redirect_uri);
		String clientId = request.getParameter("alexa_client_id").trim();
		request.getSession().setAttribute("clientId", clientId);
		String accessTokenUrl = "responseCode";
		request.getSession().setAttribute("accessTokenUrl", accessTokenUrl);
		String redirectUrl = oauthClientAddress + "/callbackCode";
		request.getSession().setAttribute("redirectUrl", redirectUrl);
		String response_type = "code";
		request.getSession().setAttribute("response_type", response_type);
		String username = request.getParameter("username").trim();
		request.getSession().setAttribute("username", username);
		String requestUrl = null;
		try {
			OAuthClientRequest accessTokenRequest = OAuthClientRequest.authorizationLocation(accessTokenUrl)
					.setResponseType(response_type).setClientId(clientId).setRedirectURI(redirectUrl)
					.buildQueryMessage();
			requestUrl = accessTokenRequest.getLocationUri();
		} catch (OAuthSystemException e) {
			e.printStackTrace();
		}
		response.sendRedirect(request.getContextPath() + "/" + requestUrl + "&username=" + username
				+ "&alexa_redirect_uri=" + alexa_redirect_uri + "&alexa_state=" + alexa_state);
	}

	@RequestMapping(value = "/responseCode")
	public void responseCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String alexa_redirect_uri = request.getParameter("alexa_redirect_uri");
		String alexa_state = request.getParameter("alexa_state");
		System.out.println("----------服务端/responseCode--------------------------------------------------------------");
		String username = request.getParameter("username").trim();
		try {
			OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
			if (oauthRequest.getClientId() != null && oauthRequest.getClientId() != "") {
				String authorizationCode = "authorizationCode" + System.currentTimeMillis();
				HttpServletRequest requ = (HttpServletRequest) request;
				HttpSession session = requ.getSession();
				session.removeAttribute("code");
				response.setContentType("text/html");
				response.setCharacterEncoding("utf-8");
				session.setAttribute("code", authorizationCode);
				@SuppressWarnings("unused")
				String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
				// 进行OAuth响应构建
				OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse
						.authorizationResponse(request, HttpServletResponse.SC_FOUND);
				// 设置授权码
				builder.setCode(authorizationCode);
				// 得到到客户端重定向地址
				String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
				// 构建响应
				final OAuthResponse response1 = builder.location(redirectURI).buildQueryMessage();
				String responceUri = response1.getLocationUri();
				// 根据OAuthResponse返回ResponseEntity响应
				HttpHeaders headers = new HttpHeaders();
				try {
					headers.setLocation(new URI(response1.getLocationUri()));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				response.sendRedirect(responceUri.replace("http://localhost:8080", "") + "&username=" + username
						+ "&alexa_redirect_uri=" + alexa_redirect_uri + "&alexa_state=" + alexa_state);
			}

		} catch (OAuthSystemException e) {
			e.printStackTrace();
		} catch (OAuthProblemException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 客户端得到code申请token
	 */
	@RequestMapping(value = "/callbackCode")
	public String callbackCode(HttpServletRequest request, HttpServletResponse response) throws OAuthProblemException {
		String clientId = "clientId";
		String clientSecret = "clientSecret";
		String accessTokenUrl = oauthServerAddress + "/responseAccessToken";
		String redirectUrl = oauthClientAddress + "/accessToken";
		request.getSession().setAttribute("accessTokenUrl", accessTokenUrl);
		request.getSession().setAttribute("redirectUrl", redirectUrl);
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String code = httpRequest.getParameter("code");
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		try {
			OAuthClientRequest accessTokenRequest = OAuthClientRequest.tokenLocation(accessTokenUrl)
					.setGrantType(GrantType.AUTHORIZATION_CODE).setClientId(clientId).setClientSecret(clientSecret)
					.setCode(code).setRedirectURI(redirectUrl).buildQueryMessage();
			OAuthAccessTokenResponse oAuthResponse = oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST);
			String accessToken = oAuthResponse.getAccessToken();
			// 查看access token是否过期
			// Long expiresIn = oAuthResponse.getExpiresIn();
			log.info("<Request For accessToken>accessToken=" + accessToken);
			HttpServletRequest requ = (HttpServletRequest) request;
			HttpSession session = requ.getSession();
			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			session.setAttribute("token", accessToken);
			request.getSession().setAttribute("page_num", "0");
			return "device_list.jsp";
		} catch (OAuthSystemException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 服务器颁发token
	 */
	@RequestMapping(value = "/responseAccessToken")
	public void responseAccessToken(HttpServletRequest request, HttpServletResponse response)
			throws OAuthProblemException, IOException {
		System.out
				.println("--------服务端/responseAccessToken-----------------------------------------------------------");
		OAuthIssuer oauthIssuerImpl = null;
		OAuthResponse response1 = null;
		// 构建OAuth请求
		try {
			OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);
			@SuppressWarnings("unused")
			String authCode = oauthRequest.getParam(OAuth.OAUTH_CODE);
			String clientSecret = oauthRequest.getClientSecret();
			if (clientSecret != null || clientSecret != "") {
				oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
				final String accessToken = oauthIssuerImpl.accessToken();
				response1 = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK).setAccessToken(accessToken)
						.buildJSONMessage();
				response.setContentType("application/json; charset=utf-8");
				response.setCharacterEncoding("UTF-8");
				OutputStream out = response.getOutputStream();
				out.write(response1.toString().getBytes());
				out.flush();
			}
			System.out.println(
					"--------服务端/responseAccessToken-----------------------------------------------------------");

		} catch (OAuthSystemException e) {
			e.printStackTrace();
		} catch (OAuthProblemException e) {
			e.printStackTrace();
		}
		System.out
				.println("--------服务端/responseAccessToken-----------------------------------------------------------");
		return;

	}

	@RequestMapping(value = "/getResource")
	public ResponseEntity<String> getResource(HttpServletRequest request, HttpServletResponse response)
			throws OAuthSystemException {
		System.out.println("-----------服务端/userInfo-------------------------------------------------------------");
		String camera_info_Str = "";
		try {
			// 获取客户端传来的OAuth资源请求
			OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);
			// 获取Access Token
			String accessToken = oauthRequest.getAccessToken();
			System.out.println("accessToken");
			// 验证Access Token
			if (accessToken == null || accessToken == "") {
				// 如果不存在/过期了，返回未验证错误，需重新验证
				OAuthResponse oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.setError(OAuthError.ResourceResponse.INVALID_TOKEN).buildHeaderMessage();

				HttpHeaders headers = new HttpHeaders();
				headers.add(OAuth.HeaderType.WWW_AUTHENTICATE,
						oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
				return new ResponseEntity<String>(headers, HttpStatus.UNAUTHORIZED);
			}
			String username = "";
			System.out.println(username);
			System.out.println("服务端/userInfo::::::ppp");
			System.out.println("-----------服务端/userInfo----------------------------------------------------------");
			return new ResponseEntity<String>(camera_info_Str, HttpStatus.OK);
		} catch (OAuthProblemException e) {
			e.printStackTrace();
			// 检查是否设置了错误码
			String errorCode = e.getError();
			if (OAuthUtils.isEmpty(errorCode)) {
				OAuthResponse oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.buildHeaderMessage();

				HttpHeaders headers = new HttpHeaders();
				headers.add(OAuth.HeaderType.WWW_AUTHENTICATE,
						oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
				return new ResponseEntity<String>(headers, HttpStatus.UNAUTHORIZED);
			}

			OAuthResponse oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
					.setError(e.getError()).setErrorDescription(e.getDescription()).setErrorUri(e.getUri())
					.buildHeaderMessage();

			HttpHeaders headers = new HttpHeaders();
			headers.add(OAuth.HeaderType.WWW_AUTHENTICATE, oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
			System.out.println(
					"-----------服务端/userInfo------------------------------------------------------------------------------");
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/ValiImg")
	public void ValiImg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		response.setDateHeader("Expires", -1);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		int height = 30;
		int width = 120;
		int xpyl = 5;
		int ypyl = 22;
		int bang = 20;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLUE);
		g.drawRect(0, 0, width - 2, height - 2);
		for (int i = 0; i < 5; i++) {
			g.setColor(Color.RED);
			// g.drawLine(randNum(0, width), randNum(0, height), randNum(0,
			// width), randNum(0, height));
		}
		String base = "ABCDEFGHIJKLMNOPQRST";
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("宋体", Font.BOLD, bang));
			// int r = randNum(-45, 45);
			// g.rotate(1.0 * r / 180 * Math.PI, xpyl + i * 30, ypyl);
			String s = base.charAt(randNum(0, base.length() - 1)) + "";
			buffer.append(s);
			g.drawString(s, xpyl + i * 30, ypyl);
			// g.rotate(1.0 * -r / 180 * Math.PI, xpyl + i * 30, ypyl);
		}
		request.getSession().setAttribute("valistr", buffer.toString());
		System.out.println(buffer.toString());
		ImageIO.write(img, "jpg", response.getOutputStream());
	}

	private Random rand = new Random();

	private int randNum(int begin, int end) {
		return rand.nextInt((end - begin) + begin);
	}

	@RequestMapping(value = "/RegistServlet")
	public void RegistServlet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			UserService service = new UserService();
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			String valistr = request.getParameter("valistr");
			String valistr2 = (String) request.getSession().getAttribute("valistr");
			if (valistr == null || valistr2 == null || !valistr.equalsIgnoreCase(valistr2)) {
				request.setAttribute("msg", "Verification code error !");
				request.getRequestDispatcher("/regist.jsp").forward(request, response);
				return;
			}
			UserBean user = new UserBean();
			BeanUtils.populate(user, request.getParameterMap());
			user.checkValue();
			service.registUser(user);
			request.getSession().setAttribute("user", user);
			response.getWriter().write("Louis,hello");
			request.getSession().setAttribute("username", user.getUname());
			response.setHeader("refresh", "3;url=" + request.getContextPath() + "/index.jsp");
		} catch (MsgException e) {
			request.setAttribute("msg", e.getMessage());
			request.getRequestDispatcher("/regist.jsp").forward(request, response);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@RequestMapping(value = "/LogOutServlet")
	public void LogOutServlet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (request.getSession(false) != null && request.getSession().getAttribute("user") != null) {
			request.getSession().invalidate();
			response.sendRedirect(request.getContextPath() + "/login.jsp");
		}
	}

	@RequestMapping(value = "/singleServlet")
	public void singleServlet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String macCode = request.getParameter("macCode");
		request.getSession().setAttribute("macCode", macCode);

		// 登录的时候打开待机开关
		SessionConnection.addCmdList(macCode,
				this.getCMDByStr("addPower;" + "forward" + ";" + "" + ";" + "start", macCode));

		response.sendRedirect(request.getContextPath() + "/user.jsp?macCode=" + macCode);
	}

	@RequestMapping(value = "/alexaOperate")
	public void alexaOperate(HttpServletRequest request, HttpServletResponse response, String macCode, String cmd)
			throws IOException {
		SessionConnection.addCmdList(macCode, this.getCMDByStr(cmd, macCode));
	}

	@RequestMapping(value = "/singleServlet_2")
	public void singleServlet_2(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(request.getContextPath() + "/user.jsp?macCode="
				+ (String) request.getSession().getAttribute("macCode"));
	}

	@RequestMapping(value = "/AddDeviceServlet")
	public void AddDeviceServlet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(request.getContextPath() + "/addDevice.jsp");
	}

	@RequestMapping(value = "/createCustom")
	public void createCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(request.getContextPath() + "/createCustom.jsp");
	}

	@RequestMapping(value = "/AddAnoutherServlet")
	public void AddAnoutherServlet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String mac_address = request.getParameter("mac_address");
		if (mac_address.length() != 12) {
			return;
		}
		String decice_nick = request.getParameter("decice_nick");
		DeviceBean deviceBean = new DeviceBean();
		deviceBean.setmac_address(mac_address);
		deviceBean.setDevice_nick(decice_nick);
		deviceBean.setDevice_owner((String) request.getSession().getAttribute("username"));
		DeviceDao.addDevice(deviceBean);
		response.sendRedirect(request.getContextPath() + "/index.jsp");
	}

	@RequestMapping(value = "/addCustom")
	public void addCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf-8");
		String custom_name = request.getParameter("custom_name");
		com.realrelax.alexa.bean.CustomBean cBean = new com.realrelax.alexa.bean.CustomBean();
		cBean.setCustom_name(custom_name);
		cBean.setCustom_user_name((String) request.getSession().getAttribute("username"));
		cBean.setCustom_create_time(new Date());
		CustomDao.addCustom(cBean);
		response.sendRedirect(request.getContextPath() + "/custom.jsp?macCode="
				+ (String) request.getSession().getAttribute("macCode"));
	}

	@RequestMapping(value = "/linktoAlexa")
	public void linktoAlexa(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpServletRequest requ = (HttpServletRequest) request;
		HttpSession session = requ.getSession();
		String alexa_state = (String) session.getAttribute("alexa_state");
		String alexa_redirect_uri = (String) session.getAttribute("alexa_redirect_uri");
		String code = (String) session.getAttribute("code");
		String token = (String) session.getAttribute("token");
		request.getSession().setAttribute("token", token);
		@SuppressWarnings("deprecation")
		String url = URLDecoder.decode(alexa_redirect_uri) + "?state=" + alexa_state + "&code=" + code;
		response.sendRedirect(url);
	}

	@RequestMapping(value = "/token")
	public void token(HttpServletRequest request, HttpServletResponse response)
			throws OAuthProblemException, IOException {
		// 数据库更新token
		AlexaDao.updateToken((String) request.getSession().getAttribute("username"),
				(String) request.getSession().getAttribute("username"));
		response.getWriter().append(" {\"access_token\":\"" + (String) request.getSession().getAttribute("token")
				+ "\",\"token_type\":\"bearer\"}");
	}

	// 单命令组装
	private String getCMDByStr(String str, String macCode) {
		String mac_address = macCode;

		String body = "";
		if (str.contains("Foot")) {
			body = "foot";
		}
		if (str.contains("Back")) {
			body = "back";
		}
		if (str.contains("Waist")) {
			body = "waist";
		}
		if (str.contains("Push") || str.contains("Up") || str.contains("Down")) {
			body = "push";
			if (str.contains("Up")) {
				str.replace("forward", "reverse");
			}
		}
		String reverse = "";
		if (str.contains("forward")) {
			reverse = "forward";
		}
		if (str.contains("reverse") || str.contains("Up")) {
			reverse = "reverse";
		}
		int speed = 0;
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		if (!"".equals(m.replaceAll("").trim())) {
			speed = Integer.parseInt(m.replaceAll("").trim());
		}
		if (str.contains("mid")) {
			speed = 70;
		}
		if (str.contains("high")) {
			speed = 80;
		}
		if (str.contains("low")) {
			speed = 60;
		}

		// 速度需作转换
		/*
		 * if (speed == 10) { speed = 7; } else if (speed == 20) { speed = 15; } else if
		 * (speed == 30) { speed = 16; } else if (speed == 40) { speed = 23; } else if
		 * (speed == 50) { speed = 32; }
		 */

		String speedCode = Integer.toHexString(speed);
		if (speedCode.length() == 1) {
			speedCode = "0" + speedCode;
		}
		if (speedCode.equals("0")) {
			speedCode = Integer.toHexString(100);
		}
		if (speedCode.equals("a")) {
			speedCode = "0a";
		}
		if (speedCode.equals("5")) {
			speedCode = "05";
		}

		if (speedCode.equals("7")) {
			speedCode = "07";
		}
		String reverseCode = "";
		CmdBean motorCmd = new CmdBean();
		motorCmd.setHead(Constonts.HEAD);
		motorCmd.setTail(Constonts.TAIL);
		motorCmd.setMacAddr(mac_address);
		motorCmd.setIndex(CmdUtil.generateIndex());
		if ("back".equals(body)) {
			motorCmd.setCMD(Constonts.BACK_MASSAGE);
		} else if ("waist".equals(body)) {
			motorCmd.setCMD(Constonts.WAIST_MSSAGE);
		} else if ("foot".equals(body)) {
			motorCmd.setCMD(Constonts.FOOT_MASSAGE);
		} else if ("push".equals(body)) {
			motorCmd.setCMD(Constonts.PUSH_ROD_MOTOR);
		}

		if (str.contains("HIGH_END_BACK_MASSAGE")) {
			motorCmd.setCMD(Constonts.HIGH_END_BACK_MASSAGE);
		}
		if (str.contains("HIGH_END_FOOT_ROLLER_MASSAGE")) {
			motorCmd.setCMD(Constonts.HIGH_END_FOOT_ROLLER_MASSAGE);
		}
		if (str.contains("HIGH_END_KNOCK_MASSAGE")) {
			motorCmd.setCMD(Constonts.HIGH_END_KNOCK_MASSAGE);
		}
		if (str.contains("HIGH_END_WALK_MASSAGE")) {
			motorCmd.setCMD(Constonts.HIGH_END_WALK_MASSAGE);
		}
		if (str.contains("HIGH_FOOT_PUSH_MASSAGE")) {
			motorCmd.setCMD(Constonts.HIGH_FOOT_PUSH_MASSAGE);
		}
		if (str.contains("HIGH_BACK_PUSH_MASSAGE")) {
			motorCmd.setCMD(Constonts.HIGH_BACK_PUSH_MASSAGE);
		}

		if ("reverse".equals(reverse)) {
			reverseCode = "02";
		} else if ("forward".equals(reverse)) {
			reverseCode = "01";
		}
		if (str.contains("stop")) {
			reverseCode = "00";
		}
		motorCmd.setDATA(reverseCode + speedCode);
		motorCmd.setDataLen("0002");
		System.out.println(str);
		if (str.contains("Heat")) {
			motorCmd.setCMD(Constonts.HEATING);
			if (str.contains("stop")) {
				motorCmd.setDATA("0000");
			} else {
				motorCmd.setDATA("0101");
			}
			motorCmd.setDataLen("0002");
		}

		if (str.contains("Vibrate")) {
			motorCmd.setCMD(Constonts.VIBRATING);
			if (str.contains("stop")) {
				motorCmd.setDATA("0000");
			} else {
				motorCmd.setDATA("0101");
			}
			motorCmd.setDataLen("0002");
		}

		if (str.contains("Music")) {
			motorCmd.setCMD(Constonts.MUSIC);
			if (str.contains("stop")) {
				motorCmd.setCMD(Constonts.MUTE);
				motorCmd.setDATA("010101010101010101");
				motorCmd.setDataLen("0009");
			} else {
				motorCmd.setCMD(Constonts.MUTE);
				motorCmd.setDATA("010000000000000000");
				motorCmd.setDataLen("0009");
			}

		}

		if (str.contains("Power")) {
			motorCmd.setCMD(Constonts.MUSIC);
			if (str.contains("stop")) {
				motorCmd.setDATA("0000");
			} else {
				motorCmd.setDATA("0101");
			}
			motorCmd.setDataLen("0002");
		}

		if (str.contains("Auto1")) {
			motorCmd.setCMD(Constonts.AUTO_STATE);
			if (str.contains("stop")) {

				motorCmd.setDATA("00FF");
			} else {
				if (str.contains("TIME1")) {
					motorCmd.setDATA("0101");
				} else if (str.contains("TIME2")) {
					motorCmd.setDATA("0102");
				} else if (str.contains("TIME3")) {
					motorCmd.setDATA("0103");
				} else if (str.contains("TIME4")) {
					motorCmd.setDATA("0104");
				} else if (str.contains("TIME5")) {
					motorCmd.setDATA("0105");
				} else if (str.contains("TIME6")) {
					motorCmd.setDATA("0106");
				} else {
					motorCmd.setDATA("0103");
				}

			}

			motorCmd.setDataLen("0002");
		}
		if (str.contains("Auto2")) {
			motorCmd.setCMD(Constonts.AUTO_STATE);
			if (str.contains("stop")) {
				motorCmd.setDATA("00FF");
			} else {
				if (str.contains("TIME1")) {
					motorCmd.setDATA("0201");
				} else if (str.contains("TIME2")) {
					motorCmd.setDATA("0202");
				} else if (str.contains("TIME3")) {
					motorCmd.setDATA("0203");
				} else if (str.contains("TIME4")) {
					motorCmd.setDATA("0204");
				} else {
					motorCmd.setDATA("0203");
				}
			}
			motorCmd.setDataLen("0002");
		}
		if (str.contains("Auto3")) {
			motorCmd.setCMD(Constonts.AUTO_STATE);
			if (str.contains("stop")) {
				motorCmd.setDATA("00FF");
			} else {
				if (str.contains("TIME1")) {
					motorCmd.setDATA("0301");
				} else if (str.contains("TIME2")) {
					motorCmd.setDATA("0302");
				} else if (str.contains("TIME3")) {
					motorCmd.setDATA("0303");
				} else if (str.contains("TIME4")) {
					motorCmd.setDATA("0304");
				} else {
					motorCmd.setDATA("0303");
				}
			}
			motorCmd.setDataLen("0002");
		}
		if (str.contains("Auto4")) {
			motorCmd.setCMD(Constonts.AUTO_STATE);
			if (str.contains("stop")) {
				motorCmd.setDATA("00FF");
			} else {
				if (str.contains("TIME1")) {
					motorCmd.setDATA("0401");
				} else if (str.contains("TIME2")) {
					motorCmd.setDATA("0402");
				} else if (str.contains("TIME3")) {
					motorCmd.setDATA("0403");
				} else if (str.contains("TIME4")) {
					motorCmd.setDATA("0404");
				} else {
					motorCmd.setDATA("0403");
				}
			}
			motorCmd.setDataLen("0002");
		}
		if (str.contains("Zero")) {
			motorCmd.setCMD(Constonts.ZERO);
			motorCmd.setDATA("0101");
			motorCmd.setDataLen("0002");
		}
		if (str.contains("Mute_H_On")) {
			motorCmd.setCMD(Constonts.MUTE);
			motorCmd.setDATA("01FF01FFFFFFFFFFFF");
			motorCmd.setDataLen("0009");
		}
		if (str.contains("Mute_H_Off")) {
			motorCmd.setCMD(Constonts.MUTE);
			motorCmd.setDATA("01FF00FFFFFFFFFFFF");
			motorCmd.setDataLen("0009");
		}

		if (str.contains("Mute_A_On")) {
			motorCmd.setCMD(Constonts.MUTE);
			motorCmd.setDATA("01FFFFFF01FFFFFFFF");
			motorCmd.setDataLen("0009");
		}
		if (str.contains("Mute_A_Off")) {
			motorCmd.setCMD(Constonts.MUTE);
			motorCmd.setDATA("01FFFFFF00FFFFFFFF");
			motorCmd.setDataLen("0009");
		}
		if (str.contains("Mute_V_On")) {
			motorCmd.setCMD(Constonts.MUTE);
			motorCmd.setDATA("01FFFF01FFFFFFFFFF");
			motorCmd.setDataLen("0009");
		}
		if (str.contains("Mute_V_Off")) {
			motorCmd.setCMD(Constonts.MUTE);
			motorCmd.setDATA("01FFFF01FFFFFFFFFF");
			motorCmd.setDataLen("0009");
		}
		if (str.contains("Mute_F_On")) {
			motorCmd.setCMD(Constonts.MUTE);
			motorCmd.setDATA("01FFFFFFFFFFFF01FF");
			motorCmd.setDataLen("0009");
		}
		if (str.contains("Mute_F_Off")) {
			motorCmd.setCMD(Constonts.MUTE);
			motorCmd.setDATA("01FFFFFFFFFFFF00FF");
			motorCmd.setDataLen("0009");
		}
		if (str.contains("Mute_M_On")) {
			motorCmd.setCMD(Constonts.MUTE);
			motorCmd.setDATA("01FFFFFFFF0101FFFF");
			motorCmd.setDataLen("0009");
		}
		if (str.contains("Mute_M_Off")) {
			motorCmd.setCMD(Constonts.MUTE);
			motorCmd.setDATA("01FFFFFFFF0000FFFF");
			motorCmd.setDataLen("0009");
		}
		if (str.contains("Stop")) {
			motorCmd.setCMD(Constonts.RESETALL);
			motorCmd.setDATA("0001");
			motorCmd.setDataLen("0002");
		}

		if (str.contains("addAir") || str.contains("Arm") || str.contains("Thigh") || str.contains("Calf")
				|| str.contains("Shoulder") || str.contains("Instep")) {
			motorCmd.setCMD(Constonts.AIRBAG_MODE);
			motorCmd.setDataLen("0008");
			String des = str;
			if (des.contains("houlder") || des.contains("nstep")) {
				motorCmd.setDATA("xxFFFFFFFFFFFFFF");
			} else if (des.contains("alf")) {
				motorCmd.setDATA("FFxxFFFFFFFFFFFF");
			}

			else if (des.contains("high")) {
				motorCmd.setDATA("FFFFxxFFFFFFFFFF");
			}

			else if (des.contains("rm")) {
				motorCmd.setDATA("FFFFFFxxFFFFFFFF");
			}

			if (des.contains("stop") || des.contains("off")) {
				motorCmd.setDATA(motorCmd.getDATA().replace("xx", "00"));
			} else if (des.contains("low")) {
				motorCmd.setDATA(motorCmd.getDATA().replace("xx", "01"));
			} else if (des.contains("mid")) {
				motorCmd.setDATA(motorCmd.getDATA().replace("xx", "02"));
			} else if (des.contains("strong")) {
				motorCmd.setDATA(motorCmd.getDATA().replace("xx", "03"));
			} else if (des.contains("on")) {
				motorCmd.setDATA(motorCmd.getDATA().replace("xx", "0f"));
			}
		}

		motorCmd.setCheckSum("0004");
		// 调整checksum
		String cmdString = motorCmd.getFinalCMD();
		// 取cmdString前n-8的，每个字节的值相加
		int sum = 0;
		String cmdSubString = cmdString.substring(0, cmdString.length() - 8);
		for (int i = 0; i < cmdSubString.length() / 2; i++) {
			sum += HexUtil.convertHextoInt(cmdString.substring(2 * i, 2 * i + 2));
		}
		motorCmd.setCheckSum(String.format("%04x", sum));
		System.out.println("<startCMD>:" + motorCmd.getFinalCMD());

		return motorCmd.getFinalCMD();
	}

	@RequestMapping(value = "/addAction")
	public void addAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if ("".equals(request.getSession().getAttribute("macCode"))
				|| null == request.getSession().getAttribute("macCode")) {
			response.getWriter().append("Please login first!");
			response.sendRedirect(request.getContextPath() + "/index.jsp");
		}
		String action_name = request.getParameter("action_name");
		String action_speed = request.getParameter("action_speed");
		String action_time = request.getParameter("action_time");
		String auto_time = (String) request.getSession().getAttribute("auto_time");
		String reverse = "forward";
		if (action_speed.contains("-")) {
			reverse = "reverse";
		}
		String state = request.getParameter("state");
		String flag = request.getParameter("flag");
		String macCode = (String) request.getSession().getAttribute("macCode");
		if ("false".equals(flag)) {
			// 执行单命令
			if ("Shoulder".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addAir_shoulder;" + "left;" + action_speed + ";" + state, macCode));
				return;
			} else if ("Back".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addBack;" + reverse + ";" + action_speed + ";" + state, macCode));
				return;
			} else if ("Waist".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addWaist;" + reverse + ";" + action_speed + ";" + state, macCode));
				return;
			} else if ("Arm".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addAir_arm;" + "left" + ";" + action_speed + ";" + state, macCode));
				return;
			} else if ("Thigh".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addAir_thigh;" + "left;" + action_speed + ";" + state, macCode));
				return;
			} else if ("Calf".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addAir_calf;" + "left;" + action_speed + ";" + state, macCode));
				return;
			} else if ("Foot".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addFoot;" + reverse + ";" + action_speed + ";" + state, macCode));
				return;
			} else if ("Heat".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addHeat;" + state, macCode));
				return;
			} else if ("Vibrate".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addVibrate;" + state, macCode));
				return;
			} else if ("Music".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addMusic;" + state, macCode));
				return;
			} else if ("Up".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addPush;" + "reverse" + ";" + action_speed + ";" + state, macCode));
				return;
			} else if ("Down".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addPush;" + "forward" + ";" + action_speed + ";" + state, macCode));
				return;
			} else if ("AUTO".equalsIgnoreCase(action_name)) {
				if ("AUTO1".equalsIgnoreCase((String) request.getSession().getAttribute("auto_mode"))) {
					SessionConnection.addCmdList(macCode,
							this.getCMDByStr("addAuto1" + state + ";" + auto_time, macCode));
					if ("stop".equals(state)) {
						request.getSession().setAttribute("stop_flag", "true");
					}

				} else if ("AUTO2".equalsIgnoreCase((String) request.getSession().getAttribute("auto_mode"))) {
					SessionConnection.addCmdList(macCode,
							this.getCMDByStr("addAuto2" + state + ";" + auto_time, macCode));
					if ("stop".equals(state)) {
						request.getSession().setAttribute("stop_flag", "true");
					}
				} else if ("AUTO3".equalsIgnoreCase((String) request.getSession().getAttribute("auto_mode"))) {
					SessionConnection.addCmdList(macCode,
							this.getCMDByStr("addAuto3" + state + ";" + auto_time, macCode));
					if ("stop".equals(state)) {
						request.getSession().setAttribute("stop_flag", "true");
					}
				} else if ("AUTO4".equalsIgnoreCase((String) request.getSession().getAttribute("auto_mode"))) {
					SessionConnection.addCmdList(macCode,
							this.getCMDByStr("addAuto4" + state + ";" + auto_time, macCode));
					if ("stop".equals(state)) {
						request.getSession().setAttribute("stop_flag", "true");
					}
				}

				return;
			} else if ("Zero".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addZero" + state, macCode));
				return;
			} else if ("Mute_H_On".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addMute_H_On" + state, macCode));
				return;
			} else if ("Mute_H_Off".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addMute_H_Off" + state, macCode));
				return;
			} else if ("Mute_A_On".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addMute_A_On" + state, macCode));
				return;
			} else if ("Mute_A_Off".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addMute_A_Off" + state, macCode));
				return;
			} else if ("Mute_V_On".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addMute_V_On" + state, macCode));
				return;
			} else if ("Mute_V_Off".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addMute_V_Off" + state, macCode));
				return;
			} else if ("Mute_F_On".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addMute_F_On" + state, macCode));
				return;
			} else if ("Mute_F_Off".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addMute_F_Off" + state, macCode));
				return;
			} else if ("Mute_M_On".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addMute_M_On" + state, macCode));
				return;
			} else if ("Mute_M_Off".equalsIgnoreCase(action_name)) {
				SessionConnection.addCmdList(macCode,
						this.getCMDByStr("addMute_M_Off" + state, macCode));
				return;
			}
		} else {
			// 执行自定义（自定义的命令入库）
			int customId = CustomDao.getCustomIdByNameAndUser((String) request.getSession().getAttribute("username"),
					(String) request.getSession().getAttribute("custom_name"));
			log.info("<addAction>" + "action_name:" + action_name + "action_speed:" + action_speed + "action_time"
					+ action_time);
			com.realrelax.alexa.bean.OperationBean ob = new com.realrelax.alexa.bean.OperationBean();
			ob.setOperationDesc(action_name + ";" + reverse + ";" + action_speed + ";" + state);
			ob.setCustomId(customId);
			ob.setOperationTime(action_time);
			OperationDao.addOperation(ob);
			request.getSession().setAttribute("OperationBeanList",
					CustomUtil.handlecustom((String) request.getSession().getAttribute("custom_name"),
							(String) request.getSession().getAttribute("username")));

		}
	}

	@RequestMapping(value = "/linkRenameCustom")
	public void linkRenameCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String customId = request.getParameter("customId");
		int custom_id = Integer.parseInt(customId);
		request.getSession().setAttribute("custom_id", custom_id);
		String customName = request.getParameter("customName");
		request.getSession().setAttribute("customName", customName);
		response.sendRedirect(
				request.getContextPath() + "/renameCustom.jsp?customName=" + customName + "&customId=" + customId);
	}

	@RequestMapping(value = "/renameCustom")
	public void renameCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String customName = request.getParameter("customNewName");
		CustomDao.updateCustomName(customName,
				Integer.parseInt(request.getSession().getAttribute("custom_id").toString()));
		response.sendRedirect(request.getContextPath() + "/singleServlet_2");
	}

	@RequestMapping(value = "/deleteCustom")
	public void deleteCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String customId = request.getParameter("customId");
		CustomDao.delCustom(Integer.parseInt(customId));
		response.sendRedirect(request.getContextPath() + "/singleServlet_2");
	}

	@RequestMapping(value = "/delOperarion")
	public void delOperarion(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int operationId = Integer.parseInt(request.getParameter("operationId"));
		OperationDao.delOperation(operationId);
		response.sendRedirect(request.getContextPath() + "/custom.jsp");
	}

	@RequestMapping(value = "/delOperationById")
	public void delOperationById(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int operationId = Integer.parseInt(request.getParameter("operation_id"));
		OperationDao.delOperation(operationId);
		response.sendRedirect(request.getContextPath() + "/custom.jsp");
	}

	@RequestMapping(value = "/delOperarionByNam")
	public void delOperarionByNam(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int operationId = Integer.parseInt(request.getParameter("operationId"));
		OperationDao.delOperation(operationId);
		response.sendRedirect(request.getContextPath() + "/custom.jsp");
	}

	@RequestMapping(value = "/delDevice")
	public void delDevice(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String macCode = request.getParameter("macCode");
		DeviceDao.delDeviceByUserName_Mac((String) request.getSession().getAttribute("username"), macCode);
		response.sendRedirect(request.getContextPath() + "/index.jsp");
	}

	@RequestMapping(value = "/changeCustom")
	public void changeCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf-8");
		try {
			String custom_name = URLDecoder.decode(request.getParameter("custom_name"), "UTF-8");
			request.getSession().setAttribute("custom_name", custom_name);
			request.getSession().removeAttribute("OperationBeanList");
			request.getSession().setAttribute("OperationBeanList",
					CustomUtil.handlecustom(custom_name, (String) request.getSession().getAttribute("username")));
		} catch (Exception e) {
			String custom_name = (String) request.getSession().getAttribute("custom_name");
			request.getSession().removeAttribute("OperationBeanList");
			request.getSession().setAttribute("OperationBeanList",
					CustomUtil.handlecustom(custom_name, (String) request.getSession().getAttribute("username")));
		}

		SocketService.taskList.clear();
		SocketService.count = 0;
	}

	@RequestMapping(value = "/startCustom")
	public void startCustom(HttpServletRequest request, HttpServletResponse response)
			throws IOException, InterruptedException {

		request.getSession().setAttribute("stop_flag", "false");
		int customId = CustomDao.getCustomIdByNameAndUser((String) request.getSession().getAttribute("username"),
				(String) request.getSession().getAttribute("custom_name"));
		List<com.realrelax.alexa.bean.OperationBean> operationBeanList = OperationDao.getOperationListByCustomId(customId);
		List<com.realrelax.alexa.bean.OperationBean> operationBeanListNew = new ArrayList<>();
		for (com.realrelax.alexa.bean.OperationBean oBean : operationBeanList) {
			if (oBean.getOperationDesc().contains("start")) {
				operationBeanListNew.add(oBean);
			}
		}
		operationBeanList = operationBeanListNew;
		List<com.realrelax.alexa.bean.OperationBean> operationBeanList_stop = OperationDao.getOperationListByCustomId(customId);
		List<com.realrelax.alexa.bean.OperationBean> operationBeanList_stop_New = new ArrayList<>();
		for (com.realrelax.alexa.bean.OperationBean oBean : operationBeanList_stop) {
			if (oBean.getOperationDesc().contains("start")) {
				operationBeanList_stop_New.add(oBean);
			}
		}
		operationBeanList_stop = operationBeanList_stop_New;
		for (com.realrelax.alexa.bean.OperationBean ob_stop : operationBeanList_stop) {
			ob_stop.setOperationDesc(ob_stop.getOperationDesc().replace("start", "stop"));
		}
		operationBeanList.addAll(operationBeanList_stop);
		List<com.realrelax.alexa.bean.CustomTask> mytaskList = new ArrayList<com.realrelax.alexa.bean.CustomTask>();
		String mac_address = (String) request.getSession().getAttribute("macCode");
		// ---------------------------装载命令-----------------------------------------------
		for (com.realrelax.alexa.bean.OperationBean ob : operationBeanList) {
			int start_second = Integer.parseInt(ob.getOperationTime().split("-")[0].replaceAll(" ", ""));
			int end_second = Integer.parseInt(ob.getOperationTime().split("-")[1].replaceAll(" ", ""));

			com.realrelax.alexa.bean.CustomTask nTask = new com.realrelax.alexa.bean.CustomTask();
			nTask.setCmd(this.getCMDByStr(ob.getOperationDesc(), mac_address));

			if (ob.getOperationDesc().contains("start")) {
				nTask.setSecond((SocketService.customTaskcount + start_second));
			} else {

				nTask.setSecond((SocketService.customTaskcount + end_second));
			}
			nTask.setMac_address(mac_address);

			mytaskList.add(nTask);
		}
		Collections.sort(mytaskList);

		// 如果同一秒数同一命令有多种操作，则不需要关闭命令
		/*
		 * for (int m = 0; m < mytaskList.size() - 1; m++) { CmdBean cmd = new
		 * CmdBean(mytaskList.get(m).getCmd()); CmdBean cmd_next = new
		 * CmdBean(mytaskList.get(m + 1).getCmd()); if
		 * ((cmd.getCMD().equals(Constonts.BACK_MASSAGE) ||
		 * cmd.getCMD().equals(Constonts.WAIST_MSSAGE)) && mytaskList.get(m).getSecond()
		 * + 1 == mytaskList.get(m + 1).getSecond() &&
		 * cmd.getCMD().equals(cmd_next.getCMD()) && cmd.getDATA().startsWith("00")) {
		 * mytaskList.remove(mytaskList.get(m)); } }
		 */

		// 如果板子支持多命令，则需重新加工任务清单
		if ("YES".equalsIgnoreCase(Constonts.SUPPORT_MULTIPLE_COMMANDS) && mytaskList.size() > 1) {
			// 第一步加工mytaskList，降低秒区分
			/*
			 * for (int i = 1; i < mytaskList.size(); i++) { if
			 * (mytaskList.get(i).getSecond() - mytaskList.get(i - 1).getSecond() == 1) {
			 * mytaskList.get(i).setSecond(mytaskList.get(i - 1).getSecond()); } }
			 */

			// 存放新命令
			List<com.realrelax.alexa.bean.CustomTask> mytaskList_new = new ArrayList<com.realrelax.alexa.bean.CustomTask>();

			// 首先将单命令全部加进去（现在改为全部使用多命令）
			/*
			 * for (int i = 0; i < mytaskList.size(); i++) { CustomTask ct =
			 * mytaskList.get(i); if (i == 0) { if (ct.getSecond() != mytaskList.get(i +
			 * 1).getSecond()) { mytaskList_new.add(ct); mytaskList.remove(ct); } } else if
			 * (i < (mytaskList.size() - 1)) { if ((ct.getSecond() != mytaskList.get(i +
			 * 1).getSecond()) && (ct.getSecond() != mytaskList.get(i - 1).getSecond())) {
			 * mytaskList_new.add(ct); mytaskList.remove(ct); } } else { if (ct.getSecond()
			 * != mytaskList.get(i - 1).getSecond()) { mytaskList_new.add(ct);
			 * mytaskList.remove(ct); } } }
			 */

			// 然后添加多命令(mytaskList只剩下未组装的多命令)
			MultiplyCMD mCmd = new MultiplyCMD();
			for (int i = 0; i < mytaskList.size(); i++) {
				com.realrelax.alexa.bean.CustomTask oldct = mytaskList.get(i);
				CmdBean cmd = new CmdBean(oldct.getCmd());
				String cmdstr = cmd.getCMD();
				if (cmdstr.equalsIgnoreCase(Constonts.MUSIC)) {
					mCmd.setFlagBT(cmd.getDATA().substring(0, 2));
				} else if (cmdstr.equalsIgnoreCase(Constonts.HEATING)) {
					mCmd.setFlagHeater(cmd.getDATA().substring(0, 2));
				} else if (cmdstr.equalsIgnoreCase(Constonts.VIBRATING)) {
					mCmd.setFlagShakeMotor(cmd.getDATA().substring(0, 2));
				} else if (cmdstr.equalsIgnoreCase(Constonts.AIRBAG_MODE)) {
					String[] dataArr = new String[8];
					for (int j = 0; j < dataArr.length; j++) {
						dataArr[j] = cmd.getDATA().substring(2 * j, 2 * j + 2);
						if (!(dataArr[j].equalsIgnoreCase("FF"))) {
							StringBuffer buffer = new StringBuffer(mCmd.getFlagAirPumpMode());
							mCmd.setFlagAirPumpMode(buffer.replace(2 * j, 2 * j + 2, dataArr[j]).toString());
						}
					}
				} else if (cmdstr.equalsIgnoreCase(Constonts.BACK_MASSAGE)) {
					StringBuffer buffer = new StringBuffer(mCmd.getFlagMotor());
					mCmd.setFlagMotor(buffer.replace(0, 4, cmd.getDATA()).toString());
				} else if (cmdstr.equalsIgnoreCase(Constonts.WAIST_MSSAGE)) {
					StringBuffer buffer = new StringBuffer(mCmd.getFlagMotor());
					mCmd.setFlagMotor(buffer.replace(4, 8, cmd.getDATA()).toString());
				} else if (cmdstr.equalsIgnoreCase(Constonts.FOOT_MASSAGE)) {
					StringBuffer buffer = new StringBuffer(mCmd.getFlagMotor());
					mCmd.setFlagMotor(buffer.replace(8, 12, cmd.getDATA()).toString());
				} else if (cmdstr.equalsIgnoreCase(Constonts.PUSH_ROD_MOTOR)) {
					StringBuffer buffer = new StringBuffer(mCmd.getFlagMotor());
					mCmd.setFlagMotor(buffer.replace(12, 16, cmd.getDATA()).toString());
				}
				// 判断是否需要添加新的命令
				if (i < mytaskList.size() - 1) {
					if (mytaskList.get(i).getSecond() != mytaskList.get(i + 1).getSecond()) {
						cmd.setCMD(Constonts.WHOLE_STATE);
						cmd.setDataLen("0014");
						cmd.setDATA(mCmd.getFinalCMD());
						com.realrelax.alexa.bean.CustomTask newct = oldct;
						// 调整checksum
						String cmdString = cmd.getFinalCMD();
						// 取cmdString前n-8的，每个字节的值相加
						int sum = 0;
						String cmdSubString = cmdString.substring(0, cmdString.length() - 8);
						for (int j = 0; j < cmdSubString.length() / 2; j++) {
							sum += HexUtil.convertHextoInt(cmdString.substring(2 * j, 2 * j + 2));
						}
						cmd.setCheckSum(String.format("%04x", sum));
						newct.setCmd(cmd.getFinalCMD());
						mytaskList_new.add(newct);
						mCmd = new MultiplyCMD();

					}

				} else {
					cmd.setCMD(Constonts.WHOLE_STATE);
					cmd.setDataLen("0014");
					cmd.setDATA(mCmd.getFinalCMD());
					com.realrelax.alexa.bean.CustomTask newct = oldct;
					// 调整checksum
					String cmdString = cmd.getFinalCMD();
					// 取cmdString前n-8的，每个字节的值相加
					int sum = 0;
					String cmdSubString = cmdString.substring(0, cmdString.length() - 8);
					for (int j = 0; j < cmdSubString.length() / 2; j++) {
						sum += HexUtil.convertHextoInt(cmdString.substring(2 * j, 2 * j + 2));
					}
					cmd.setCheckSum(String.format("%04x", sum));
					newct.setCmd(cmd.getFinalCMD());
					mytaskList_new.add(newct);
					mCmd = new MultiplyCMD();
				}

			}
			mytaskList = mytaskList_new;
			Collections.sort(mytaskList);
		}

		// 对命令进行优化
		String motor_back = "0000";
		String motor_waist = "0000";
		String motor_foot = "0000";
		String motor_push = "0000";

		String BT = "00";
		String Heater = "00";
		String ShakeMotor = "00";
		String Air_one = "00";
		String Air_two = "00";
		String Air_three = "00";
		String Air_four = "00";

		String Air_five = "00";
		String Air_six = "00";
		String Air_seven = "00";
		String Air_eight = "00";

		for (int i = 0; i < mytaskList.size(); i++) {
			com.realrelax.alexa.bean.CustomTask cTask = mytaskList.get(i);
			CmdBean cb = new CmdBean(cTask.getCmd());
			String BT_string = cb.getDATA().substring(2, 4);
			String Heater_string = cb.getDATA().substring(4, 6);
			String ShakeMotor_string = cb.getDATA().substring(6, 8);
			String air_string = cb.getDATA().substring(8, 24);

			String one_tmp = air_string.substring(0, 2);
			String two_tmp = air_string.substring(2, 4);
			String three_tmp = air_string.substring(4, 6);
			String four_tmp = air_string.substring(6, 8);
			String five_tmp = air_string.substring(8, 10);
			String six_tmp = air_string.substring(10, 12);
			String seven_tmp = air_string.substring(12, 14);
			String eight_tmp = air_string.substring(14, 16);

			String motor_string = cb.getDATA().substring(24, 40);
			String back_tmp = motor_string.substring(0, 4);
			String waist_tmp = motor_string.substring(4, 8);
			String foot_tmp = motor_string.substring(8, 12);
			String push_tmp = motor_string.substring(12, 16);
			StringBuilder new_motor_string = new StringBuilder();
			StringBuilder new_other_string = new StringBuilder();

			if (BT_string.startsWith("FF")) {
				new_other_string.append(BT);
			} else if (BT_string.startsWith("00")) {
				new_other_string.append("00");
				BT = "00";
			} else {
				new_other_string.append(BT_string);
				BT = BT_string;
			}

			if (Heater_string.startsWith("FF")) {
				new_other_string.append(Heater);
			} else if (Heater_string.startsWith("00")) {
				new_other_string.append("00");
				Heater = "00";
			} else {
				new_other_string.append(Heater_string);
				Heater = Heater_string;
			}

			if (ShakeMotor_string.startsWith("FF")) {
				new_other_string.append(ShakeMotor);
			} else if (ShakeMotor_string.startsWith("00")) {
				new_other_string.append("00");
				ShakeMotor = "00";
			} else {
				new_other_string.append(ShakeMotor_string);
				ShakeMotor = ShakeMotor_string;
			}

			if (one_tmp.startsWith("FF")) {
				new_other_string.append(Air_one);
			} else if (one_tmp.startsWith("00")) {
				new_other_string.append("00");
				Air_one = "00";
			} else {
				new_other_string.append(one_tmp);
				Air_one = one_tmp;
			}

			if (two_tmp.startsWith("FF")) {
				new_other_string.append(Air_two);
			} else if (two_tmp.startsWith("00")) {
				new_other_string.append("00");
				Air_two = "00";
			} else {
				new_other_string.append(two_tmp);
				Air_two = two_tmp;
			}

			if (three_tmp.startsWith("FF")) {
				new_other_string.append(Air_three);
			} else if (three_tmp.startsWith("00")) {
				new_other_string.append("00");
				Air_three = "00";
			} else {
				new_other_string.append(three_tmp);
				Air_three = three_tmp;
			}

			if (four_tmp.startsWith("FF")) {
				new_other_string.append(Air_four);
			} else if (four_tmp.startsWith("00")) {
				new_other_string.append("00");
				Air_four = "00";
			} else {
				new_other_string.append(four_tmp);
				Air_four = four_tmp;
			}

			if (five_tmp.startsWith("FF")) {
				new_other_string.append(Air_five);
			} else if (five_tmp.startsWith("00")) {
				new_other_string.append("00");
				Air_five = "00";
			} else {
				new_other_string.append(five_tmp);
				Air_five = five_tmp;
			}

			if (six_tmp.startsWith("FF")) {
				new_other_string.append(Air_six);
			} else if (six_tmp.startsWith("00")) {
				new_other_string.append("00");
				Air_six = "00";
			} else {
				new_other_string.append(six_tmp);
				Air_six = six_tmp;
			}

			if (seven_tmp.startsWith("FF")) {
				new_other_string.append(Air_seven);
			} else if (seven_tmp.startsWith("00")) {
				new_other_string.append("00");
				Air_seven = "00";
			} else {
				new_other_string.append(seven_tmp);
				Air_seven = seven_tmp;
			}

			if (eight_tmp.startsWith("FF")) {
				new_other_string.append(Air_eight);
			} else if (eight_tmp.startsWith("00")) {
				new_other_string.append("00");
				Air_eight = "00";
			} else {
				new_other_string.append(eight_tmp);
				Air_eight = eight_tmp;
			}

			if (back_tmp.startsWith("FF")) {
				new_motor_string.append(motor_back);
			} else if (back_tmp.startsWith("00")) {
				new_motor_string.append("0000");
				motor_back = "0000";
			} else {
				new_motor_string.append(back_tmp);
				motor_back = back_tmp;
			}

			if (waist_tmp.startsWith("FF")) {
				new_motor_string.append(motor_waist);
			} else if (back_tmp.startsWith("00")) {
				new_motor_string.append("0000");
				motor_waist = "0000";
			} else {
				new_motor_string.append(waist_tmp);
				motor_waist = waist_tmp;
			}

			if (foot_tmp.startsWith("FF")) {
				new_motor_string.append(motor_foot);
			} else if (back_tmp.startsWith("00")) {
				new_motor_string.append("0000");
				motor_foot = "0000";
			} else {
				new_motor_string.append(foot_tmp);
				motor_foot = foot_tmp;
			}

			if (push_tmp.startsWith("FF")) {
				new_motor_string.append(motor_push);
			} else if (back_tmp.startsWith("00")) {
				new_motor_string.append("0000");
				motor_push = "0000";
			} else {
				new_motor_string.append(push_tmp);
				motor_push = push_tmp;
			}

			cb.setDATA(cb.getDATA().substring(0, 2) + new_other_string.toString() + new_motor_string.toString());

			// 调整checksum
			String cmdString = cb.getFinalCMD();
			// 取cmdString前n-8的，每个字节的值相加
			int sum = 0;
			String cmdSubString = cmdString.substring(0, cmdString.length() - 8);
			for (int j = 0; j < cmdSubString.length() / 2; j++) {
				sum += HexUtil.convertHextoInt(cmdString.substring(2 * j, 2 * j + 2));
			}
			cb.setCheckSum(String.format("%04x", sum));

			cTask.setCmd(cb.getFinalCMD());
			mytaskList.set(i, cTask);
		}

		// 打印出命令释义
		for (com.realrelax.alexa.bean.CustomTask ct : mytaskList) {
			CmdBean cmd = new CmdBean(ct.getCmd());
			String data = cmd.getDATA();
			StringBuilder des = new StringBuilder();
			des.append("时间" + ct.getSecond() + "秒：执行事件   ");
			String device_type_data = data.substring(0, 2);
			String bluetooth_data = data.substring(2, 4);
			String heat_data = data.substring(4, 6);
			String vibrate_data = data.substring(6, 8);
			String air_data = data.substring(8, 24);
			String motor_data = data.substring(24, 40);

			if (device_type_data != "01") {
				des.append("设备类型" + device_type_data + "; ");
			}

			if (bluetooth_data != "FF") {
				des.append("蓝牙" + bluetooth_data + "; ");
			}

			if (heat_data != "FF") {
				des.append("加热" + heat_data + "; ");
			}

			if (vibrate_data != "FF") {
				des.append("震动" + vibrate_data + "; ");
			}

			if (air_data != "FFFFFFFFFFFFFFFF") {
				des.append("气阀数据" + "前四路（" + air_data.substring(0, 8) + "） 后四路 " + air_data.substring(8, 16));
			}

			if (motor_data != "FFFFFFFFFFFFFFFF") {
				des.append("电机数据" + "背部（" + motor_data.substring(0, 4) + "） 腰部 " + motor_data.substring(4, 8) + "） 脚底 "
						+ motor_data.substring(8, 12) + "）推杆 " + motor_data.substring(12, 16));
			}
			log.info(des.toString());

		}

		// 打印出手法
		for (com.realrelax.alexa.bean.CustomTask ct : mytaskList) {
			CmdBean cmd = new CmdBean(ct.getCmd());
			String data = cmd.getDATA();
			StringBuilder des = new StringBuilder();
			des.append(ct.getSecond() + data);
			log.info(des.toString());
		}

		SessionConnection.addCmdList(mac_address,
				this.getCMDByStr("addMute_M_Off" + ";stop", mac_address));

		// 开始执行命令,period为本次到下次任务之间的间隔
		for (int i = 0; i < mytaskList.size(); i++) {
			// 检查是否被停止
			if ("true".equals(request.getSession().getAttribute("stop_flag"))) {
				return;
			}
			// 如果是第一条，则先休眠
			if (i == 0) {
				Thread.sleep(mytaskList.get(0).getSecond() * 1000);
			}
			// 执行命令
			SessionConnection.addCmdList(mac_address, mytaskList.get(i).getCmd());
			// 等待对方响应
			SocketService.last_index.put(mac_address, "wait");
			int wait_time = 0;
			while (SocketService.last_index.get(mac_address).equals("wait") && wait_time < 60) {
				Thread.sleep(100);
				wait_time++;
			}
			int waist_time = 0;
			// 等待,加入补偿算法
			if ((i + 1) != mytaskList.size()) {
				int period = (int) (mytaskList.get(i + 1).getSecond() - mytaskList.get(i).getSecond());
				if (wait_time * 100 < period * 1000) {
					// 如果有足够的补偿时间，则在该处进行补偿
					if (period * 1000 - wait_time * 100 - waist_time > 0) {
						Thread.sleep(period * 1000 - wait_time * 100 - waist_time);
						wait_time = 0;
					} else if (period * 1000 - wait_time * 100 - waist_time / 2 > 0) {
						Thread.sleep(period * 1000 - wait_time * 100 - waist_time / 2);
						wait_time = wait_time / 2;
					} else if (period * 1000 - wait_time * 100 - waist_time / 4 > 0) {
						Thread.sleep(period * 1000 - wait_time * 100 - waist_time / 4);
						wait_time = (int) (wait_time * 0.75);
					} else if (period * 1000 - wait_time * 100 - waist_time / 8 > 0) {
						Thread.sleep(period * 1000 - wait_time * 100 - waist_time / 8);
						wait_time = (int) (wait_time * 0.875);
					} else {
						Thread.sleep(period * 1000 - wait_time * 100 - waist_time);
					}

				} else {
					// 计算需补偿时间
					waist_time += wait_time * 100 - period * 1000;
					System.out.println("网络较慢……补偿" + waist_time + "毫秒");
				}
			}
		}

		SessionConnection.addCmdList(mac_address,
				this.getCMDByStr("addStop;" + "forward" + ";" + "" + ";" + "", mac_address));
	}

	@RequestMapping(value = "/stopCustom")
	public void stopCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 下关机命令
		String mac = (String) request.getSession().getAttribute("macCode");
		SessionConnection.addCmdList(mac,
				this.getCMDByStr("addMute_M_Off" + ";stop", mac));
		SessionConnection.addCmdList(mac,
				this.getCMDByStr("addStop;" + "forward" + ";" + "" + ";" + "", mac));
		request.getSession().setAttribute("stop_flag", "true");
	}

	@RequestMapping(value = "/changeOperationState")
	public void changeOperationState(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int operationId = Integer.parseInt(request.getParameter("id"));
		String flag = request.getParameter("flag");
		OperationDao.changeOperationState(operationId, flag);
	}

	@RequestMapping(value = "/changeTimeById")
	public void changeTimeById(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int operationId = Integer.parseInt(request.getParameter("operation_id"));
		String newSpeed = request.getParameter("newTime");
		OperationDao.changeTimeById(operationId, newSpeed);
	}

	@RequestMapping(value = "/changeSpeedById")
	public void changeSpeedById(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int operationId = Integer.parseInt(request.getParameter("operation_id"));
		String newSpeed = request.getParameter("newSpeed");
		OperationDao.changeSpeedById(operationId, newSpeed);
	}

	@RequestMapping(value = "/pre_device")
	public String pre_device(HttpServletRequest request, HttpServletResponse response) throws IOException {

		int page_num = Integer.parseInt((String) request.getSession().getAttribute("page_num"));
		if (page_num >= 1) {
			request.getSession().setAttribute("page_num", page_num - 1 + "");
		} else {
			page_num = 0;
			return "login.jsp";
		}
		return "device_list.jsp";
	}

	@RequestMapping(value = "/next_device")
	public String next_device(HttpServletRequest request, HttpServletResponse response) throws IOException {

		int page_num = Integer.parseInt((String) request.getSession().getAttribute("page_num"));

		request.getSession().setAttribute("page_num", page_num + 1 + "");

		List<DeviceBean> db = DeviceDao
				.getAllDeviceByUserName(((UserBean) request.getSession().getAttribute("user")).getUname());
		if (page_num >= db.size() - 1) {
			request.getSession().setAttribute("page_num", page_num + "");
			return "add_device_l1.jsp";
		} else {
			return "device_list.jsp";
		}

	}

	@RequestMapping(value = "/setAUTO1")
	public void setAUTO1(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().setAttribute("auto_mode", "AUTO1");
	}

	@RequestMapping(value = "/setAUTO2")
	public void setAUTO2(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().setAttribute("auto_mode", "AUTO2");
	}

	@RequestMapping(value = "/setAUTO3")
	public void setAUTO3(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().setAttribute("auto_mode", "AUTO3");
	}

	@RequestMapping(value = "/setAUTO4")
	public void setAUTO4(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().setAttribute("auto_mode", "AUTO4");
	}

	@RequestMapping(value = "/setTIME1")
	public void setTIME1(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().setAttribute("auto_time", "TIME1");
	}

	@RequestMapping(value = "/setTIME2")
	public void setTIME2(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().setAttribute("auto_time", "TIME2");

	}

	@RequestMapping(value = "/setTIME3")
	public void setTIME3(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().setAttribute("auto_time", "TIME3");
	}

	@RequestMapping(value = "/setTIME4")
	public void setTIME4(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().setAttribute("auto_time", "TIME4");
	}

	@RequestMapping(value = "/setTIME5")
	public void setTIME5(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().setAttribute("auto_time", "TIME5");
	}

	@RequestMapping(value = "/setTIME6")
	public void setTIME6(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().setAttribute("auto_time", "TIME6");
	}

	@RequestMapping(value = "/manual")
	public String manual(HttpServletRequest request, HttpServletResponse response) throws IOException {

		request.getSession().setAttribute("stop_flag", "true");

		return "manual.jsp";
	}

	@RequestMapping(value = "/airStrength")
	public void airStrength(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String mac = (String) request.getSession().getAttribute("macCode");
		String arm = request.getParameter("arm");
		String hip = request.getParameter("hip");
		String leg = request.getParameter("leg");
		String foot = request.getParameter("foot");
		String state = request.getParameter("state");

		// 下综合控制命令
		CmdBean motorCmd = new CmdBean();
		motorCmd.setHead(Constonts.HEAD);
		motorCmd.setTail(Constonts.TAIL);
		motorCmd.setMacAddr(mac);
		motorCmd.setIndex(CmdUtil.generateIndex());
		motorCmd.setCMD(Constonts.WHOLE_STATE);
		motorCmd.setDataLen("0014");

		StringBuilder str = new StringBuilder();
		str.append("01FFFFFF");

		// foot waist leg arm

		if (state.equals("low")) {
			if (foot.equalsIgnoreCase("ON")) {
				str.append("01");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}

			if (hip.equalsIgnoreCase("ON")) {
				str.append("01");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}

			if (leg.equalsIgnoreCase("ON")) {
				str.append("01");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}

			if (arm.equalsIgnoreCase("ON")) {
				str.append("01");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}
		} else if (state.equals("mid")) {
			if (foot.equalsIgnoreCase("ON")) {
				str.append("02");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}

			if (hip.equalsIgnoreCase("ON")) {
				str.append("02");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}

			if (leg.equalsIgnoreCase("ON")) {
				str.append("02");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}

			if (arm.equalsIgnoreCase("ON")) {
				str.append("02");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}
		} else if (state.equals("strong")) {
			if (foot.equalsIgnoreCase("ON")) {
				str.append("03");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}

			if (hip.equalsIgnoreCase("ON")) {
				str.append("03");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}

			if (leg.equalsIgnoreCase("ON")) {
				str.append("03");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}

			if (arm.equalsIgnoreCase("ON")) {
				str.append("03");
			} else if (foot.equalsIgnoreCase("OFF")) {
				str.append("FF");
			}
		}
		str.append("FFFFFFFF");
		str.append("FFFFFFFFFFFFFFFF");

		motorCmd.setDATA(str.toString());
		motorCmd.setCheckSum("0004");
		// 调整checksum
		String cmdString = motorCmd.getFinalCMD();
		// 取cmdString前n-8的，每个字节的值相加
		int sum = 0;
		String cmdSubString = cmdString.substring(0, cmdString.length() - 8);
		for (int i = 0; i < cmdSubString.length() / 2; i++) {
			sum += HexUtil.convertHextoInt(cmdString.substring(2 * i, 2 * i + 2));
		}
		motorCmd.setCheckSum(String.format("%04x", sum));
		SessionConnection.addCmdList(mac, motorCmd.getFinalCMD());
		SessionConnection.addCmdList(mac,
				this.getCMDByStr("addMute_M_Off" + state, mac));

	}

	@RequestMapping(value = "/deviceList")
	public String deviceList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 下关机命令
		String mac = (String) request.getSession().getAttribute("macCode");
		request.getSession().setAttribute("stop_flag", "true");
		// 登录的时候打开待机开关
		SessionConnection.addCmdList(mac,
				this.getCMDByStr("addPower;" + "forward" + ";" + "" + ";" + "stop", mac));

		return "login.jsp";
	}

	@RequestMapping(value = "/manual_1")
	public String manual_1(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 下关机命令
		String mac = (String) request.getSession().getAttribute("macCode");
		request.getSession().setAttribute("stop_flag", "true");
		SessionConnection.addCmdList(mac,
				this.getCMDByStr("addStop;" + "forward" + ";" + "" + ";" + "", mac));

		return "manual.jsp";
	}

	@RequestMapping(value = "/run")
	public String run(HttpServletRequest request, HttpServletResponse response, String mac, String motorType,
			String speed, String direct, String status) throws IOException {
		if (null == status || "off".equals(status)) {
			status = "stop";
		} else {
			status = "start";
		}
		SessionConnection.addCmdList(mac,
				this.getCMDByStr(motorType + ";" + direct + ";" + speed + ";" + status, mac));
		return "manual.jsp";
	}

}
