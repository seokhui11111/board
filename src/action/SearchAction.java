package action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.BoardVO;
import domain.PageVO;
import domain.SearchVO;
import lombok.AllArgsConstructor;
import persistence.BoardDAO;

@AllArgsConstructor
public class SearchAction implements Action {

	private String path;
	
	@Override
	public ActionForward execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//검색 폼에서 넘어오는 값 가져오기
		String criteria = req.getParameter("criteria");
		String keyword = req.getParameter("keyword");
		
		int page = 1;
		if(req.getParameter("page")!=null) {
			page=Integer.parseInt(req.getParameter("page"));
		}
		int amount = 10;
		
		
		//DB작업 
		BoardDAO dao=new BoardDAO();
		
		//검색조건에 맞는 전체게시물 수
		int totalRows = dao.getSearchRows(criteria, keyword);
		
		//총 페이지 수
		int totalPage = totalRows%10==0 ? totalRows/10 : totalRows/10+1;
		
		//endPage
		int endPage = (int)(Math.ceil(page/10.0)*amount);		
		//startPage
		int startPage = endPage-9;
		
		//실제 마지막 페이지 구하기
		if(endPage > totalPage) {
			endPage = totalPage;
		}
		
		PageVO pageVO = new PageVO();
		pageVO.setStartPage(startPage);
		pageVO.setEndPage(endPage);
		pageVO.setPage(page);
		pageVO.setTotalPage(totalPage);	
		
		
		List<BoardVO> list=dao.getSearchList(page,amount,criteria, keyword);
		
		if(list.isEmpty()) {
			path="qList.do";
		}else {
			req.setAttribute("search", new SearchVO(criteria,keyword));
			req.setAttribute("list", list);
			req.setAttribute("pageVO", pageVO);
		}		
		
		return new ActionForward(path,false);
	}

}
