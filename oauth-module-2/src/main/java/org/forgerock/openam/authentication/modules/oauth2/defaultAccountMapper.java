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

import com.iplanet.sso.SSOException;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchOpModifier;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.encode.Base64;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 *
 * @author victor
 */
public class defaultAccountMapper implements AccountMapper {

    public defaultAccountMapper() {
    }
    private static Debug debug = Debug.getInstance("OAuth");
    private static final SecureRandom random = new SecureRandom();

    @Override
    public AMIdentity getAccount(Set accountMapConfiguration, JSONObject json,
            AMIdentityRepository idrepo, boolean allowCreate, Map attributes)
            throws AuthLoginException {

        Map attr = new HashMap();
        AMIdentity identity = null;
        Iterator it = accountMapConfiguration.iterator();
        while (it.hasNext()) {
            try {
                String entry = (String) it.next();
                if (entry.indexOf("=") == -1) {
                    if (debug.messageEnabled()) {
                        debug.message("defaultAccountMapper.getAttributes: " + 
                                "Invalid entry." + entry);
                    }
                    continue;
                }
                StringTokenizer st = new StringTokenizer(entry, "=");
                String responseName = st.nextToken();
                String localName = st.nextToken();

                String data = "";
                if (responseName != null && responseName.indexOf(".") != -1) {
                    StringTokenizer parts = new StringTokenizer(responseName, ".");
                    data = json.getJSONObject(parts.nextToken()).getString(parts.nextToken());
                } else {
                    data = json.getString(responseName);
                }
                attr.put(localName, addToSet(new HashSet(), data));
            } catch (JSONException ex) {
                debug.error("defaultAccountMapper.getAttributes: Error when "
                        + "trying to get attributes from the response ", ex);
                throw new AuthLoginException("Configuration problem, attribute ",ex);
            }

        }

        IdSearchControl ctrl = getSearchControl(IdSearchOpModifier.OR, attr);

        // AMIdentityRepository repo = getAMIdentityRepository(realm);
        IdSearchResults results = null;
        try {
            results = idrepo.searchIdentities(IdType.USER, "*", ctrl);
        } catch (IdRepoException ex) {
            debug.error("defaultAccountMapper.getAttributes: Problem while  "
                    + "searching  for the user. IdRepo", ex);
        } catch (SSOException ex) {
            debug.error("defaultAccountMapper.getAttributes: Problem while  "
                    + "searching  for the user. SSOExc", ex);
        }

        Iterator iter = results.getSearchResults().iterator();
        if (iter.hasNext()) {
            identity = (AMIdentity) iter.next();
            if (debug.messageEnabled()) {
                debug.message("getUser: user found : " + identity.getName());
            }
        }

        if ((identity == null) && allowCreate) {
            try {
                String userId = UUID.randomUUID().toString();
                attributes.put("userPassword",addToSet(new HashSet(), getRandomPassword()));
                attributes.put("inetuserstatus",addToSet(new HashSet(), "Active"));
                identity = idrepo.createIdentity(IdType.USER, userId, attributes);

            } catch (IdRepoException ire) {
                debug.error("defaultAccountMapper.getAccount: IRE ", ire);
                debug.message("LDAPERROR Code = " + ire.getLDAPErrorCode());
                if (ire.getLDAPErrorCode() != null && !ire.getLDAPErrorCode().equalsIgnoreCase("68")) {
                    //throw new IdRepoException();
                    throw new AuthLoginException("Failed to create user");
                }
            } catch (SSOException ex) {
                debug.error("defaultAccountMapper.getAttributes: Problem while  "
                    + "creating the user. SSOExc", ex);
                throw new AuthLoginException("Failed to create user");
            }
        }

        return identity;
    }

    private Set addToSet(Set set, String attribute) {
	set.add(attribute);
	return set;
    }

    private IdSearchControl getSearchControl(
            IdSearchOpModifier modifier, Map avMap) {
    	IdSearchControl control = new IdSearchControl();

    	control.setMaxResults(1);
        control.setSearchModifiers(modifier, avMap);

    	return control;
    }

     private String getRandomPassword() {
	        byte[] pass = new byte[20];
	        random.nextBytes(pass);
	       return Base64.encode(pass);
     }
}
