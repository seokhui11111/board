package action;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.BoardVO;
import lombok.AllArgsConstructor;
import persistence.BoardDAO;
import upload.UploadUtil;

@AllArgsConstructor
public class WriteAction implements Action {
	
	private String path;
	
	
	@Override
	public ActionForward execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
//		//qna_board_write.jsp에서 넘긴 값 가져오기
//		String name=req.getParameter("name");
//		String title=req.getParameter("title");
//		String content=req.getParameter("content");
//		String password=req.getParameter("password");
//		String file=req.getParameter("file");
//		//DB작업
////		BoardVO vo=new BoardVO(name,title,content,password,file);
//		BoardVO vo=new BoardVO();
//		vo.setName(name);
//		vo.setTitle(title);
//		vo.setContent(content);
//		vo.setPassword(password);
//		vo.setAttach(file);		
//		BoardDAO dao=new BoardDAO();
//		int result=dao.insertArticle(vo);

		
		//request객체를 UploadUtil에 넘겨주기
		UploadUtil uploadUtil = new UploadUtil();
		HashMap<String, String> dataMap=uploadUtil.getItem(req);
		
		//map에 들어 있는 정보를 vo에 담기
		BoardVO vo = new BoardVO();
		vo.setName(dataMap.get("name")); 
		vo.setTitle(dataMap.get("title")); 
		vo.setContent(dataMap.get("content")); 
		vo.setPassword(dataMap.get("password")); 
		if(dataMap.containsKey("file"))
			vo.setAttach(dataMap.get("file"));
		
		//DB작업
		BoardDAO dao = new BoardDAO();
		int result=dao.insertArticle(vo);
		//result == 1 => 원래 경로 유지
		//result == 0 => path 는 실패한 페이지로 변경
		if(result==0) {
			path="view/qna_board_write.jsp";
		}
		
		return new ActionForward(path, true);		
		
		
		
	}

}
