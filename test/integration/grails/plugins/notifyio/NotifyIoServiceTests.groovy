package grails.plugins.notifyio

import grails.test.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class NotifyIoServiceTests extends GrailsUnitTestCase {
	// Maybe not traditional, but a developer is only hitting one config file this way
	def email = ConfigurationHolder.config.test.email

    protected void setUp() {
        super.setUp()
		assert !email.isEmpty(), "We need a notify.io account to test.  Set test.email in your configuration."
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testBasic() {

		new NotifyIoService().notify(email, "This is my test message.");
    }

	void testIcon() {
		new NotifyIoService().notify(recipient: email,
									text:"This is my test message.",
									title: "My Application",
									icon:"http://github.com/images/modules/header/logov3.png",
									link:"http://github.com",
									sticky:false);
		
	}
}
