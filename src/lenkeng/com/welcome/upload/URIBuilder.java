/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lenkeng.com.welcome.upload;

import android.text.TextUtils;
import org.apache.http.NameValuePair;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lenkeng.com.welcome.util.Logger;

public class URIBuilder {

    private static final String TAG = "URIBuilder";
	private String scheme;
    private String encodedAuthority;
    private String userInfo;
    private String encodedUserInfo;
    private String host;
    private int port;
    private String path;
    private List<NameValuePair> queryParams;
    private String fragment;

    public URIBuilder() {
        this.port = -1;
    }

    public URIBuilder(final String uri) {
        try {
            _digestURI(new URI(uri.replace(" ", "%20")));
        } catch (URISyntaxException e) {
            Logger.e(TAG,e.getMessage());
        }
    }

    public URIBuilder(final URI uri) {
        digestURI(uri);
    }

    private void _digestURI(final URI uri) {
        this.scheme = uri.getScheme();
        this.encodedAuthority = uri.getRawAuthority();
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.encodedUserInfo = uri.getRawUserInfo();
        this.userInfo = uri.getUserInfo();
        this.path = uri.getPath();
        if (path != null) path = path.replace("%20", " ");
        String query = uri.getRawQuery();
        if (query != null) query = query.replace("%20", " ");
        this.queryParams = parseQuery(query);
        this.fragment = uri.getFragment();
    }

    private void digestURI(final URI uri) {
        this.scheme = uri.getScheme();
        this.encodedAuthority = uri.getRawAuthority();
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.encodedUserInfo = uri.getRawUserInfo();
        this.userInfo = uri.getUserInfo();
        this.path = uri.getPath();
        this.queryParams = parseQuery(uri.getRawQuery());
        this.fragment = uri.getFragment();
    }

    private List<NameValuePair> parseQuery(final String query) {
        if (!TextUtils.isEmpty(query)) {
            return URLEncodedUtils.parse(query);
        }
        return null;
    }

    /**
     * Builds a {@link java.net.URI} instance.
     *
     * @param charset
     */
    public URI build(Charset charset) throws URISyntaxException {
        return new URI(buildString(charset));
    }

    private String buildString(Charset charset) {
        StringBuilder sb = new StringBuilder();
        if (this.scheme != null) {
            sb.append(this.scheme).append(':');
        }

        if (this.encodedAuthority != null) {
            sb.append("//").append(this.encodedAuthority);
        } else if (this.host != null) {
            sb.append("//");
            if (this.encodedUserInfo != null) {
                sb.append(this.encodedUserInfo).append("@");
            } else if (this.userInfo != null) {
                sb.append(encodeUserInfo(this.userInfo, charset)).append("@");
            }
            if (InetAddressUtils.isIPv6Address(this.host)) {
                sb.append("[").append(this.host).append("]");
            } else {
                sb.append(this.host);
            }
            if (this.port >= 0) {
                sb.append(":").append(this.port);
            }
        }

        if (this.path != null) {
            sb.append(encodePath(normalizePath(this.path), charset));
        }

        if (this.queryParams != null) {
            sb.append("?").append(encodeQuery(this.queryParams, charset));
        }

        if (this.fragment != null) {
            sb.append("#").append(encodeFragment(this.fragment, charset));
        }
        return sb.toString();
    }

    private String encodeUserInfo(final String userInfo, Charset charset) {
        return URLEncodedUtils.encUserInfo(userInfo, charset);
    }

    private String encodePath(final String path, Charset charset) {
        return URLEncodedUtils.encPath(path, charset).replace("+", "20%");
    }

    private String encodeQuery(final List<NameValuePair> params, Charset charset) {
        return URLEncodedUtils.format(params, charset);
    }

    private String encodeFragment(final String fragment, Charset charset) {
        return URLEncodedUtils.encFragment(fragment, charset);
    }

    /**
     * Sets URI scheme.
     */
    public URIBuilder setScheme(final String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * Sets URI user info. The value is expected to be unescaped and may contain non ASCII
     * characters.
     */
    public URIBuilder setUserInfo(final String userInfo) {
        this.userInfo = userInfo;
        this.encodedAuthority = null;
        this.encodedUserInfo = null;
        return this;
    }

    /**
     * Sets URI user info as a combination of username and password. These values are expected to
     * be unescaped and may contain non ASCII characters.
     */
    public URIBuilder setUserInfo(final String username, final String password) {
        return setUserInfo(username + ':' + password);
    }

    /**
     * Sets URI host.
     */
    public URIBuilder setHost(final String host) {
        this.host = host;
        this.encodedAuthority = null;
        return this;
    }

    /**
     * Sets URI port.
     */
    public URIBuilder setPort(final int port) {
        this.port = port < 0 ? -1 : port;
        this.encodedAuthority = null;
        return this;
    }

    /**
     * Sets URI path. The value is expected to be unescaped and may contain non ASCII characters.
     */
    public URIBuilder setPath(final String path) {
        this.path = path;
        return this;
    }

    /**
     * Removes URI query.
     */
    public URIBuilder removeQuery() {
        this.queryParams = null;
        return this;
    }

    /**
     * Sets URI query.
     * <p/>
     * The value is expected to be encoded form data.
     */
    public URIBuilder setQuery(final String query) {
        this.queryParams = parseQuery(query);
        return this;
    }

    /**
     * Adds parameter to URI query. The parameter name and value are expected to be unescaped
     * and may contain non ASCII characters.
     */
    public URIBuilder addParameter(final String param, final String value) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        this.queryParams.add(new BasicNameValuePair(param, value));
        return this;
    }

    /**
     * Sets parameter of URI query overriding existing value if set. The parameter name and value
     * are expected to be unescaped and may contain non ASCII characters.
     */
    public URIBuilder setParameter(final String param, final String value) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        if (!this.queryParams.isEmpty()) {
            for (Iterator<NameValuePair> it = this.queryParams.iterator(); it.hasNext(); ) {
                NameValuePair nvp = it.next();
                if (nvp.getName().equals(param)) {
                    it.remove();
                }
            }
        }
        this.queryParams.add(new BasicNameValuePair(param, value));
        return this;
    }

    /**
     * Sets URI fragment. The value is expected to be unescaped and may contain non ASCII
     * characters.
     */
    public URIBuilder setFragment(final String fragment) {
        this.fragment = fragment;
        return this;
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getUserInfo() {
        return this.userInfo;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getPath() {
        return this.path;
    }

    public List<NameValuePair> getQueryParams() {
        if (this.queryParams != null) {
            return new ArrayList<NameValuePair>(this.queryParams);
        } else {
            return new ArrayList<NameValuePair>();
        }
    }

    public String getFragment() {
        return this.fragment;
    }

    private static String normalizePath(String path) {
        if (path == null) {
            return null;
        }
        int n = 0;
        for (; n < path.length(); n++) {
            if (path.charAt(n) != '/') {
                break;
            }
        }
        if (n > 1) {
            path = path.substring(n - 1);
        }
        return path;
    }

}
