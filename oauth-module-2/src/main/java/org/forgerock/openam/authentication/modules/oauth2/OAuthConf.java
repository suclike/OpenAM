/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright © 2011 ForgeRock AS. All rights reserved.
 * Copyright © 2011 Cybernetica AS.
 * 
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 */

package org.forgerock.openam.authentication.modules.oauth2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.io.IOException;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.shared.datastruct.CollectionHelper;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.sm.DNMapper;
import java.util.Set;


/* 
 * The purpose of OAuthConf is to encapsulate module's configuration
 * and based on this configuration provide a common interface for getting
 * essential URLs, like: 
 * - authentication service URL;
 * - token service URL;
 * - profile service URL.
 *
 * This is first of all necessary for customized Login.jsp, which needs
 * to have the correct link that will lead a user to authentication service.
 * Otherwise, the links should be hardcoded (and hardcoded correctly), what
 * would lead to extra code and complexity of Login.jsp.
 */
public class OAuthConf implements OAuthParam {
    static final String MODULE_NAME = "OAuth";
    static final String BUNDLE_NAME = "amAuthOAuth";

    static final String KEY_CLIENT_ID = "iplanet-am-auth-oauth-client-id";
    static final String KEY_CLIENT_SECRET = "iplanet-am-auth-oauth-client-secret";
    static final String KEY_AUTH_SERVICE = "iplanet-am-auth-oauth-auth-service";
    static final String KEY_TOKEN_SERVICE = "iplanet-am-auth-oauth-token-service";
    static final String KEY_PROFILE_SERVICE = "iplanet-am-auth-oauth-user-profile-service";
    static final String KEY_SCOPE = "iplanet-am-auth-oauth-scope";
    static final String KEY_SSO_LOGIN_URL = "iplanet-am-auth-oauth-sso-login-url";
    static final String KEY_SSO_PROXY_URL = "iplanet-am-auth-oauth-sso-proxy-url";
    static final String KEY_AUTH_LEVEL = "iplanet-am-auth-oauth-auth-level";
    static final String KEY_ACCOUNT_MAPPER = "org-forgerock-auth-oauth-account-mapper";
    static final String KEY_ACCOUNT_MAPPER_CONFIG =
            "org-forgerock-auth-oauth-account-mapper-configuration";
    static final String KEY_ATTRIBUTE_MAPPER = "org-forgerock-auth-oauth-attribute-mapper";
    static final String KEY_ATTRIBUTE_MAPPER_CONFIG =
            "org-forgerock-auth-oauth-attribute-mapper-configuration";
    static final String KEY_CREATE_ACCOUNT = "org-forgerock-auth-oauth-createaccount-flag";


    static final String CLIENT = "genericHTML";

    private static Debug debug = Debug.getInstance(MODULE_NAME);

    private static OAuthConf NULL = new OAuthConf() {
        public String getAuthServiceUrl(HttpServletRequest req)
                throws AuthLoginException {
            return null;
        }
    };
 
    private String clientId;
    private String clientSecret;

    private String scope;

    private String authServiceUrl;
    private String tokenServiceUrl;
    private String profileServiceUrl;
    private String ssoLoginUrl;
    private String ssoProxyUrl;
    private String accountMapper;
    private String attributeMapper;
    private String createAccountFlag;
    private Set accountMapperConfig;
    private Set attributeMapperConfig;

    // this is intended for Login.jsp use only
    public static OAuthConf newInstance(HttpServletRequest req, String module) {
        String realm = req.getParameter(PARAM_REALM);
        if (debug.messageEnabled()) {
            debug.message("newInstance(): realm = " + realm);
        }

        String orgDN = DNMapper.orgNameToDN(realm);
        AppConfigurationEntry[] entries =
                Configuration.getConfiguration().getAppConfigurationEntry(
                        "MODULE=" + module + ";ORGANIZATION=" + orgDN + ";CLIENT=" + CLIENT);

        if (entries == null) {
            debug.error("No configured login modules found for org " + orgDN);
            return NULL;
        }
        if (entries.length != 1) {
            debug.error("entries.length = " + entries.length + " for org " + orgDN);
            return NULL;
        }
        return new OAuthConf(entries[0].getOptions());
    }

    OAuthConf() {
    }

    OAuthConf(Map config) {
        clientId = CollectionHelper.getMapAttr(config, KEY_CLIENT_ID);
        clientSecret = CollectionHelper.getMapAttr(config, KEY_CLIENT_SECRET);
        scope = CollectionHelper.getMapAttr(config, KEY_SCOPE);
        authServiceUrl = CollectionHelper.getMapAttr(config, KEY_AUTH_SERVICE);
        tokenServiceUrl = CollectionHelper.getMapAttr(config, KEY_TOKEN_SERVICE);
        profileServiceUrl = CollectionHelper.getMapAttr(config, KEY_PROFILE_SERVICE);
        ssoLoginUrl = CollectionHelper.getMapAttr(config, KEY_SSO_LOGIN_URL);
        ssoProxyUrl = CollectionHelper.getMapAttr(config, KEY_SSO_PROXY_URL);
        accountMapper = CollectionHelper.getMapAttr(config, KEY_ACCOUNT_MAPPER);
        accountMapperConfig = (Set) config.get(KEY_ACCOUNT_MAPPER_CONFIG);
        attributeMapper = CollectionHelper.getMapAttr(config, KEY_ATTRIBUTE_MAPPER);
        attributeMapperConfig = (Set) config.get(KEY_ATTRIBUTE_MAPPER_CONFIG);
        createAccountFlag = CollectionHelper.getMapAttr(config, KEY_CREATE_ACCOUNT);
    }

    public void redirectToLoginUrl(HttpServletResponse res) throws IOException {
       res.sendRedirect(ssoLoginUrl); 
    }

    public String getAuthServiceUrl(HttpServletRequest req)
            throws AuthLoginException {
        return getAuthServiceUrl(req, isAdvice(req));
    }

    public String  getAccountMapper()
            throws AuthLoginException {

        return accountMapper;
    }

    public String  getAttributeMapper()
            throws AuthLoginException {

        return attributeMapper;
    }

    public Set  getAccountMapperConfig()
            throws AuthLoginException {

        return accountMapperConfig;
    }

    public Set  getAttributeMapperConfig()
            throws AuthLoginException {

        return attributeMapperConfig;
    }

    public boolean  getCreateAccountFlag()
            throws AuthLoginException {

        return createAccountFlag.equalsIgnoreCase("true");
    }

    String getAuthServiceUrl(HttpServletRequest req,
            boolean isSessionUpgrade) throws AuthLoginException {
        
        if (authServiceUrl.indexOf("?") == -1) {
                authServiceUrl = authServiceUrl + "?" + 
                        PARAM_CLIENT_ID + "=" + clientId;
            } else {
                authServiceUrl = authServiceUrl + "&" + 
                        PARAM_CLIENT_ID + "=" + clientId;
            }
            
        String url = authServiceUrl
            + param(PARAM_SCOPE, scope) 
            + param(PARAM_REDIRECT_URI,
                    getRedirectUri(req, isSessionUpgrade));
        return url;
    }


    String getTokenServiceUrl(HttpServletRequest req,
            boolean isSessionUpgrade, String authServiceURL) throws AuthLoginException {
        
        String code = req.getParameter(PARAM_CODE);
        if (code == null) {
            debug.error("process: code == null");
            throw new AuthLoginException(BUNDLE_NAME,
                            "authCode == null", null);
        }
        
        if (debug.messageEnabled()) {
            debug.message("authentication code: " + code);
        }
        
        if (tokenServiceUrl.indexOf("?") == -1) {
                tokenServiceUrl = tokenServiceUrl + "?" + 
                        PARAM_CLIENT_ID + "=" + clientId;
        } else {
                tokenServiceUrl = tokenServiceUrl + "&" + 
                        PARAM_CLIENT_ID + "=" + clientId;
        }
        
        String url = tokenServiceUrl
            + param(PARAM_REDIRECT_URI,
                    authServiceURL)
            + param(PARAM_SCOPE, scope)
            + param(PARAM_CLIENT_SECRET, clientSecret)
            + param(PARAM_CODE, code);
        return url;
    }
  
    String getProfileServiceUrl(String token) {
        
        if (profileServiceUrl.indexOf("?") == -1) {
                return profileServiceUrl + "?" + token;
            } else {
                return profileServiceUrl + "&" + token;
        }
        
    }

    // NB! Important regarding Facebook authentication:
    // Facebook generates authentication code based on redirect_uri. Technically
    // this means that to get a token, we must pass the same redirect_uri to the
    // token service *including* all encoded query parameters. Otherwise,
    // Facebook will generate 'error validating verification code' response.
    // Facebook also has a special attitude to slash-symbols and we have to
    // double-encode these parameters, that may contain them...
    private String getRedirectUri(HttpServletRequest req,
            boolean isSessionUpgrade) throws AuthLoginException {
        try {
            // Request parameters 'realm' and 'goto' must be double-encoded,
            // because we can get the access token only with exactly the same
            // redirect_uri, which was used for generating verification code (link at Login.jsp).
            StringBuffer uri = new StringBuffer()
                 .append(useProxy() && isSessionUpgrade ? ssoProxyUrl : ssoLoginUrl);
     
            StringBuffer originalURL = req.getRequestURL();
            
            debug.message("OAuthConf.getRedirectUri: URL is" + originalURL +
                    "?" + req.getQueryString());
                                    
            String queryString = req.getQueryString();
            if (queryString != null) {
                uri.append("?" + queryString);
            }
            
            if (debug.messageEnabled()) {
                debug.message("OAuthConf.getRedirectUri:Final URL is" + 
                        PARAM_REDIRECT_URI + ": " + uri);
            }

            return encodeUri(uri.toString());
        } catch (UnsupportedEncodingException uee) {
            debug.error("getRedirectUriEncoded: UnsupportedEncodingException: " + uee.getMessage());
            throw new AuthLoginException(BUNDLE_NAME, uee.getMessage(), null);
        }

    }

    private boolean isAdvice(HttpServletRequest req) {
        String advice = req.getParameter("sunamcompositeadvice");
        if (debug.messageEnabled()) {
            debug.message("isAdvice: " + advice);
        }
        return advice != null;
    }

    private boolean useProxy() {
        return ssoProxyUrl != null;
    }

    private String encodeUri(String url)
            throws UnsupportedEncodingException {
    	return URLEncoder.encode(url, "UTF-8");
    }

    private String param(String key, String value) {
        return "&" + key + "=" + value;
    }

    private String first(String key, String value) {
        return "?" + key + "=" + value;
    }
}
