
grails.config.locations = [ "file:${userHome}/.grails/${appName}-config.groovy" ]

// add the following, with values filled in to ~/.grails/NotifyIo-config.groovy 
notifyio {
	userHash = ""
	apiKey = ""
}

test.email = ""
// The following properties have been added by the Upgrade process...
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
