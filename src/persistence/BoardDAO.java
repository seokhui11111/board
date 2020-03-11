package persistence;

import static persistence.JDBCUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import domain.BoardVO;

public class BoardDAO {
	
	//글쓰기
	//insert into board(bno,name,password,title,content,attach,re_ref,re_lev,re_seq) values
	//(board_seq.nextval,?,?,?,?,?,board_seq.currVal,0,0)
	public int insertArticle(BoardVO vo) {
		int result=0;
		String sql="insert into board(bno,name,password,title,content,attach,re_ref,re_lev,re_seq) values"
				+ "(board_seq.nextval,?,?,?,?,?,board_seq.currVal,0,0)";				
		Connection con=null;
		PreparedStatement pstmt=null;
		try {
			con=getConnection();
			pstmt=con.prepareStatement(sql);
			
			pstmt.setString(1, vo.getName());
			pstmt.setString(2, vo.getPassword());
			pstmt.setString(3, vo.getTitle());
			pstmt.setString(4, vo.getContent());
			pstmt.setString(5, vo.getAttach());
			
			result=pstmt.executeUpdate();
			if(result>0) {
				commit(con);
			}
			
		} catch (SQLException e) {			
			e.printStackTrace();
			rollback(con);
		} finally {
			close(pstmt);
			close(con);
		}
		
		return result;
	}
	// select bno,title, name, regdate, readcount
	// bno 역순으로 추출
	public List<BoardVO> getList(int page, int amount){
		//page:사용자가 현재 요청한 페이지
		//amount : 한 페이지에 보여줄 게시물 수
		int start = page * amount;
		int end = (page-1) * amount;
		
		
		List<BoardVO> list = new ArrayList<BoardVO>();
		String sql="select bno,title,name,regdate,readcount,re_lev";
		sql+=" from (select B.*,rownum rnum ";
		sql+=" from (select bno,title,name,regdate,readcount,re_lev";
		sql+=" from board order by re_ref desc, re_seq asc) B";
		sql+=" where bno>0 and rownum <= ?) where rnum > ?";
		
		try(Connection con = getConnection();
				PreparedStatement pstmt=con.prepareStatement(sql)) {
			
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				BoardVO vo = new BoardVO();
				vo.setBno(rs.getInt(1));
				vo.setTitle(rs.getString(2));
				vo.setName(rs.getString(3));
				vo.setRegdate(rs.getDate(4));
				vo.setReadcount(rs.getInt(5));
				vo.setRe_lev(rs.getInt(6));
				list.add(vo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
		
		//조회수 업데이트
		// update board set readcount=readcount+1 where bno=?
	public int readCountUpdate(int bno) {
		int result=0;
		
		String sql="update board set readcount=readcount+1 where bno=?";
		Connection con=null;
		PreparedStatement pstmt=null;
		try{
			con=getConnection();
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, bno);
			result = pstmt.executeUpdate();
			if(result>0) {
				commit(con);
			}
		}catch (Exception e) {
			rollback(con);
			e.printStackTrace();
		}finally {
			close(pstmt);
			close(con);
		}
		return result;
	}
		
		
		//내용 가져오기
		//select bno,name,title,content,attach 
		//from board where bno=?
	public BoardVO getRow(int bno){
		String sql="select bno,name,title,content,attach,re_ref,re_lev,re_seq from board where bno=?";
		
		try(Connection con=getConnection();
			PreparedStatement pstmt=con.prepareStatement(sql)) {
			
			pstmt.setInt(1, bno);			
			ResultSet rs = pstmt.executeQuery();			
			if(rs.next()) {
				BoardVO vo = new BoardVO();
				vo.setBno(rs.getInt(1));
				vo.setName(rs.getString(2));
				vo.setTitle(rs.getString(3));
				vo.setContent(rs.getString(4));
				vo.setAttach(rs.getString(5));
				vo.setRe_ref(rs.getInt(6));
				vo.setRe_lev(rs.getInt(7));
				vo.setRe_seq(rs.getInt(8));
				return vo;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//비밀번호 확인
	//수정,삭제 시 확인
	//
	public int passwordCheck(int bno, String password) {
		int result=0;
		String sql="select * from board where bno=? and password=?";
		try(Connection con=getConnection();
			PreparedStatement pstmt=con.prepareStatement(sql)) {
			
			pstmt.setInt(1, bno);
			pstmt.setString(2, password);
			
			ResultSet rs=pstmt.executeQuery();
			
			if(rs.next()) {
				result=1;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return result;
	}
	
	//게시판 수정
	//제목,내용,파일첨부(옵션)
	public int updateArticle(BoardVO vo) {
		int result=0;
		String sql=null;
		Connection con=null;
		PreparedStatement pstmt=null;
		
		try {
			con=getConnection();
			
			if(vo.getAttach()==null) {
				sql="update board set title=?,content=? where bno=?";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, vo.getTitle());
				pstmt.setString(2, vo.getContent());
				pstmt.setInt(3, vo.getBno());
			}else {
				sql="update board set title=?,content=?,attach=? where bno=?";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, vo.getTitle());
				pstmt.setString(2, vo.getContent());
				pstmt.setString(3, vo.getAttach());
				pstmt.setInt(4, vo.getBno());
			}
			result=pstmt.executeUpdate();
			
			if(result>0) {
				commit(con);
			}
			
		} catch (Exception e) {
			rollback(con);
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(con);
		}		
		return result;
	}
	
	//게시글 삭제
	public int deleteArticle(int bno) {
		int result=0;
		String sql="delete from board where bno=?";
		Connection con=null;
		PreparedStatement pstmt=null;
		
		try {
			con=getConnection();
			pstmt=con.prepareStatement(sql);
			
			pstmt.setInt(1, bno);
			result=pstmt.executeUpdate();
			
			if(result>0) {
				commit(con);
			}
		} catch (Exception e) {
			rollback(con);
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(con);			
		}
		
		return result;
	}
	
	//댓글
	public int replyArticle(BoardVO vo) {
		int result=0;
		Connection con=null;
		PreparedStatement pstmt=null;
		//기존 댓글 re_seq 값 변경하기
		String sql="update board set re_seq=re_seq+1 where re_ref=? and re_seq>?";
		
		try {
			
			con=getConnection();
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, vo.getRe_ref());
			pstmt.setInt(2, vo.getRe_seq());
			result = pstmt.executeUpdate();
			if(result>0) {
				commit(con);
			}
			close(pstmt);
			//댓글 삽입하기
			int re_seq=vo.getRe_seq()+1;
			int re_lev=vo.getRe_lev()+1;
			
			sql="insert into board(bno,name,password,title,content,attach,re_ref,re_lev,re_seq)" + 
					"values (board_seq.nextval,?,?,?,?,?,?,?,?)";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, vo.getName());
			pstmt.setString(2, vo.getPassword());
			pstmt.setString(3, vo.getTitle());
			pstmt.setString(4, vo.getContent());
			pstmt.setString(5, vo.getAttach());
			pstmt.setInt(6, vo.getRe_ref());
			pstmt.setInt(7, re_lev);
			pstmt.setInt(8, re_seq);
			
			result=pstmt.executeUpdate();
			if(result>0) {
				commit(con);
			}
			
		} catch (Exception e) {
			rollback(con);
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(con);
		}
		
		return result;
	}
	
	//search에 맞는 전체 행수 구하기
		public int getSearchRows(String criteria,String keyword) {
			int totalRows=0;
			String sql = "select count(*) from board where "+criteria+" like ?";
			try(Connection con=getConnection();
				PreparedStatement pstmt=con.prepareStatement(sql)) {
				pstmt.setString(1, "%"+keyword+"%");
				ResultSet rs = pstmt.executeQuery();
				
				if(rs.next()) {
					totalRows = rs.getInt(1);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return totalRows;
		}
		
	
	
	//검색
	public List<BoardVO> getSearchList(int page,int amount,String criteria, String keyword){
		String sql="select bno,title,name,regdate,readcount,re_lev";
		sql+=" from (select B.*,rownum rnum ";
		sql+=" from (select bno,title,name,regdate,readcount,re_lev";
		sql+=" from board where " +criteria+" like ?";
		sql+=" order by re_ref desc, re_seq asc) B";
		sql+=" where bno>0 and rownum <= ?) where rnum > ?";
		
		int start = page*amount;
		int end = (page-1)*amount;
		
		List<BoardVO> list = new ArrayList<BoardVO>();
		try(Connection con=getConnection();
			PreparedStatement pstmt=con.prepareStatement(sql)) {
			
			pstmt.setString(1, "%"+keyword+"%");
			pstmt.setInt(2, start);
			pstmt.setInt(3, end);
			ResultSet rs=pstmt.executeQuery();
			
			while(rs.next()) {
				BoardVO vo = new BoardVO();
				vo.setBno(rs.getInt(1));
				vo.setTitle(rs.getString(2));
				vo.setName(rs.getString(3));
				vo.setRegdate(rs.getDate(4));
				vo.setReadcount(rs.getInt(5));
				vo.setRe_lev(rs.getInt(6));
				list.add(vo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
		
		return list;
	}
	
	
	
	
	//전체 게시물 수
	public int getRows() {
		int totalRows=0;
		try(Connection con=getConnection();
			PreparedStatement pstmt=con.prepareStatement("select count(*) from board")) {
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				totalRows = rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalRows;
	}
}