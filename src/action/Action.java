package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//Action interface
public interface Action {
	public ActionForward execute(HttpServletRequest req,HttpServletResponse res) throws Exception;
}
