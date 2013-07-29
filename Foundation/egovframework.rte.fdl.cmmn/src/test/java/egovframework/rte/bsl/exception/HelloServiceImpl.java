package egovframework.rte.bsl.exception;

import egovframework.rte.fdl.cmmn.AbstractServiceImpl;

public class HelloServiceImpl extends AbstractServiceImpl implements HelloService {

	public String sayHello(String name) throws Exception {
		String helloStr = "Hello " + name;
		try {
			int i = 1 / 0;
		} catch (ArithmeticException ae) {
			leaveaTrace("message.exception.test");
		}

		return helloStr;
	}

	public void deleteMethod() throws Exception {
		// TODO Auto-generated method stub

	}

	public String insertMethod() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateMethod() throws Exception {
		// TODO Auto-generated method stub
		try {
			int i = 1 / 0;
		} catch (ArithmeticException ae) {
			throw processException("info.nodata.msg", ae);
		}
		
		
			
	}

}
