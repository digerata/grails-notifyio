/*
 *  Grails NotifyIo Plugin, an extensive application base for Grails
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
import static groovyx.net.http.ContentType.URLENC
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import groovy.util.ConfigObject
	
class NotifyIoService {

    boolean transactional = false

	def defaultUrl = "http://api.notify.io/v1/notify/"
	def url = ConfigurationHolder.config.notifyio.url
	def userHash = ConfigurationHolder.config.notifyio.userHash
	def apiKey = ConfigurationHolder.config.notifyio.apiKey
	
	def notify(recipient, text) {
		notify(recipient: recipient, text: text)
	}
	
	/**
	 * Sends a notification. Accepts the following parameters:
	 *
	 * - recipient Email of the notify.io account holder
	 * - text	The text of the notification
	 * - title 	The title or subject of the notification. (optional) 
	 * - icon 	The URL for an icon to use for the notification. (optional)
	 * - link 	The URL for a link you want to associate with a notification. (optional)
	 * - sticky Stay on the screen (optional)
	 * - tags 	Space separated machine-readable classification tags for the notification (optional)
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
					
		if(params.sticky)
			postBody.put('sticky', params.sticky)
		
		def status = false
		
		http.post(body: postBody, requestContentType: URLENC ) { resp ->
			log.trace "Notify response: ${resp.statusLine}"
			status = resp.statusLine.statusCode == 200
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

 

