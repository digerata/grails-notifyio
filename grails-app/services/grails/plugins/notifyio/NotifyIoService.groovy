package grails.plugins.notifyio

import java.security.MessageDigest
import sun.misc.BASE64Encoder

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC
	
class NotifyIoService {

    boolean transactional = false
	def userHash = "b3af12775119d1af5614b41913d6d921"
	def apiKey = "pgrsl9sqni55rq4und"
	def url = "http://api.notify.io/v1/notify/"
	
	def notify(recipient, text) {
		notify(recipient, text, null, null, null, null)
	}
	
    def notify(recipient, text, title, icon, link, sticky) {

		println "has for user ${recipient} is ${encode(recipient)}"
		def postUrl = url +		
/*			recipient.encodeAsMD5()*/
			calcMD5(recipient)
			
		apiKey
		
		def http = new HTTPBuilder(postUrl)
		def postBody = [
			'text':text,
			'api_key': apiKey
		]
		
		if(title)
			postBody.put('title', title)
			
		if(icon)
			postBody.put('icon', icon)
			
		if(link)
			postBody.put('link', link)
			
		if(sticky)
			postBody.put('sticky', sticky)
		

		http.post(/*path: 'update.xml', */body: postBody, requestContentType: URLENC ) { resp ->
			println "Got back response: ${resp.statusLine}"
			assert resp.statusLine.statusCode == 200
		}
		
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

 

