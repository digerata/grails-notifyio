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
		new NotifyIoService().notify(recipient: "digerata@gmail.com",
									text:"This is my test message.",
									title: "My Application",
									icon:"http://a1.twimg.com/profile_images/59714330/me_bigger.png",
									link:"http://flowz.com",
									sticky:false);
		
	}
}
