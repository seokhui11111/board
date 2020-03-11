package action;

import java.net.URLEncoder;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.BoardVO;
import domain.SearchVO;
import lombok.AllArgsConstructor;
import persistence.BoardDAO;
import upload.UploadUtil;

@AllArgsConstructor
public class UpdateAction implements Action {
	
	private String path;
	
	@Override
	public ActionForward execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//qna_board_modify.jsp 넘긴 값 가져오기
		//파일첨부가 될 예정이기 때문에 WriteAction 참고해서 작성
		UploadUtil uploadutil=new UploadUtil();
		HashMap<String, String> dataMap=uploadutil.getItem(req);
		
		int page = Integer.parseInt(dataMap.get("page"));
		
		String bno=dataMap.get("bno");
		String password = dataMap.get("password");
		String title = dataMap.get("title");
		String content = dataMap.get("content");
		String attach=null;
		if(dataMap.containsKey("file")) {
			attach = dataMap.get("file");
		}
		
		String criteria="";
		String keyword="";
		if(dataMap.containsKey("criteria")) {			
			criteria = dataMap.get("criteria");
			keyword = URLEncoder.encode(dataMap.get("keyword"),"utf-8");
		}
		
		//해당글의 비밀번호가 맞는지 확인하기
		BoardDAO dao=new BoardDAO();
		int result=dao.passwordCheck(Integer.parseInt(bno), password);
		
		if(result==0) {//틀림
			path="qModify.do?bno="+bno+"&page="+page+"&criteria="+criteria+"&keyword="+keyword;
			return new ActionForward(path,true);
		}
		
		//맞음
		BoardVO vo=new BoardVO();
		vo.setBno(Integer.parseInt(bno));
		vo.setTitle(title);
		vo.setContent(content);
		vo.setPassword(password);
		vo.setAttach(attach);
		result = dao.updateArticle(vo);
		
		if(result>0) {
			path+="?bno="+bno+"&page="+page+"&criteria="+criteria+"&keyword="+keyword;
		}else {
			path="qModify.do?bno="+bno+"&page="+page+"&criteria="+criteria+"&keyword="+keyword;
		}
		
		//수정이 완료되면 수정이 잘 되었는지 현재 게시물 보여주기
		return new ActionForward(path,true);
	}

}
