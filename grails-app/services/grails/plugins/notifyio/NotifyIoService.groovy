/*
 *  Grails NotifyIo Plugin, a library for Grails apps to use http://notify.io
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package grails.plugins.notifyio

import java.security.MessageDigest
import sun.misc.BASE64Encoder

import groovyx.net.http.HTTPBuilder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

/**
 * Provides the core functionality.  Two public methods:
 *
 * <pre>
 * notify(recipient, text)
 * notify(params)
 * </pre>
 *
 * Expects configuration to be setup:
 *
 * <pre>
 * notifyio {
 * 		userHash = "[userHash from your notify.io account]"
 * 		apiKey = "[apiKey from your notify.io account]"
 * 	}
 * </pre>
 * <p>
 * Optionally, you can include socketTimeout and connectionTimeout values in milliseconds.  By default, both are set
 * to 5 seconds.
 *
 */
class NotifyIoService {

    boolean transactional = false

	def defaultUrl = "http://api.notify.io/v1/notify/"
	def url = ConfigurationHolder.config.notifyio.url
	def userHash = ConfigurationHolder.config.notifyio.userHash
	def apiKey = ConfigurationHolder.config.notifyio.apiKey

	def socketTimeout = ConfigurationHolder.config.notifyio.socketTimeout
	def connectionTimeout = ConfigurationHolder.config.notifyio.connectionTimeout

	/**
	 * <p>Simple version for sending a notification with just text and a recipient.</p>
	 * 
	 * <p>Returns the numeric HTTP status code received from the notify.io service.  Status of 200 is good,
	 * 404 means the recipient as specified does not exist.  500 is an error.</p>
	 *
	 */
	def notify(recipient, text) {
		return notify(recipient: recipient, text: text)
	}
	
	/**
	 * <p>Sends a notification. Accepts the following parameters:</p>
	 * <ul>
	 * <li> recipient Email of the notify.io account holder</li>
	 * <li> text	The text of the notification</li>
	 * <li> title 	The title or subject of the notification. (optional)</li>
	 * <li> icon 	The URL for an icon to use for the notification. (optional)</li>
	 * <li> link 	The URL for a link you want to associate with a notification. (optional)</li>
	 * <li> tags 	Space separated machine-readable classification tags for the notification (optional)</li>
	 * </ul>
	 * <p>
	 * Returns the numeric HTTP status code received from the notify.io service.  Status of 200 is good,
	 * 404 means the recipient as specified does not exist.  500 is an error.</p>
	 *
	 */
    def notify(params) {
		
		if(userHash.isEmpty() || apiKey.isEmpty()) {
			log.error "Missing configuration parameter notifyio.userHash and/or notifyio.apiKey"
			return false;
		}
		
		if(params.recipient == null) {
			log.error "Unable to send notification, recipient is a required parameter."
			return false;
		}

		if(params.text == null) {
			log.error "Unable to send notification, text is a required parameter."
			return false;
		}
			
		if(url.isEmpty()) {
			url = defaultUrl
		} else {
			url = url.toString() // have to convert from configobject (a map) to string...
		}

		if(socketTimeout instanceof groovy.util.ConfigObject && socketTimeout.isEmpty()) {
			socketTimeout = new Integer(5000)
		}
		if(connectionTimeout instanceof groovy.util.ConfigObject && connectionTimeout.isEmpty()) {
			connectionTimeout = new Integer(5000)
		}

		def postUrl = url +
			/*recipient.encodeAsMD5()*/
			calcMD5(params.recipient)

		log.trace "Post URL: ${postUrl}"

		def http = new HTTPBuilder(postUrl)
		def postBody = [
			'text':params.text,
			'api_key': apiKey
		]
		
		if(params.title)
			postBody.put('title', params.title)
			
		if(params.icon)
			postBody.put('icon', params.icon)
			
		if(params.link)
			postBody.put('link', params.link)
		
		if(params.tags)
			postBody.put('tags', params.tags)
					
		def status = 0
		
		try {

			http.request(POST, URLENC) { req ->
				body = postBody
				requestContentType = URLENC
				req.getParams().setParameter("http.socket.timeout", socketTimeout);
				req.getParams().setParameter("http.connection.timeout", connectionTimeout);

				response.success = { resp ->
					log.trace "Notify response: ${resp.statusLine}"
					println "Notify response: ${resp.statusLine}"
					status = resp.statusLine.statusCode
				}
				response.failure = { resp ->
					log.trace "Notify response: ${resp.statusLine}"
					println "Notify response: ${resp.statusLine}"
					status = resp.statusLine.statusCode
				}
			}

		} catch(Exception e) {
			log.error("Failed to send notification: ${e}")
			println "Failed to send notification: ${e}"
			e.printStackTrace()
		}
		
		return status
    }

	def encode(str) {
		// where the heck is Groovy's "string".encodeAsMD5()?
		MessageDigest md = MessageDigest.getInstance('MD5')
        md.update(str.getBytes('UTF-8'))
		// this nee
        return (new BASE64Encoder()).encode(md.digest())
	}
	
	def calcMD5(String signature) {
		// get Instance from Java Security Classes
		String strBuildup = ""
		MessageDigest md5 = MessageDigest.getInstance("MD5")
		byte[] md5summe = md5.digest(signature.getBytes())
		for (int k = 0; k < md5summe.length; k++) {
			byte b = md5summe[k]
			String temp = Integer.toHexString(b & 0xFF)
			/*
			* toHexString has the side effect of making stuff like 0x0F only
			* one char F(when it should be '0F') so I check the length of
			* string
			*/
			if (temp.length() < 2) {
				temp = "0" + temp
			}
			//temp = temp.toUpperCase()
			strBuildup += temp
		}
		return strBuildup
	}
}

 

