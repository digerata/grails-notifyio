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

		def n = new NotifyIoService()
		assert 200 == n.notify(email, "This is my test message.")
    }

	void testInit() {
		def n = new NotifyIoService()
		try {
			n.notify(email, "This is my test message.")

		} catch(Exception e) {
			// don't care, real test is next call.
		}

		assert 200 == n.notify(email, "This is a second test to make sure initialization isn't broken.")
	}

	void testIcon() {
		def n = new NotifyIoService()
		
		assert 200 == n.notify(recipient: email,
									text:"This is my test message.",
									title: "My Application",
									icon:"http://github.com/images/modules/header/logov3.png",
									link:"http://github.com",
									sticky:false);
		
	}
}
