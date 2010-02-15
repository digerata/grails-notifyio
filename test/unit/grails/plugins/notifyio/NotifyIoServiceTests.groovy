package grails.plugins.notifyio

import grails.test.*

class NotifyIoServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testBasic() {
		new NotifyIoService().notify("digerata@gmail.com", "This is my test message.");
    }

	void testIcon() {
		new NotifyIoService().notify("digerata@gmail.com", "This is my test message.",
			"My Application", "http://a1.twimg.com/profile_images/59714330/me_bigger.png",
			"http://flowz.com", true);
		
	}
}
