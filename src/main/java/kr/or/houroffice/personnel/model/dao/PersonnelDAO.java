package kr.or.houroffice.personnel.model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import kr.or.houroffice.member.model.vo.Member;
import kr.or.houroffice.personnel.model.vo.Contact;
import kr.or.houroffice.personnel.model.vo.MemDept;

@Repository("PersonnelDAO")
public class PersonnelDAO {

	public int addbookCount(SqlSessionTemplate sqlSession, String selectBox, String searchText) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("selectBox", selectBox);
		map.put("searchText", searchText);
		int result = sqlSession.selectOne("personnel.addbookCount", map);
		return result;
	}
	
	public int myAddbookCount(SqlSessionTemplate sqlSession, String selectBox, String searchText, int memNo) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("selectBox", selectBox);
		map.put("searchText", searchText);
		map.put("memNo", memNo);
		int result = sqlSession.selectOne("personnel.myAddbookCount", map);
		return result;
	}
	
	//사내 주소록, 검색(search)
	public ArrayList<Member> addbookSearch(SqlSessionTemplate sqlSession, String selectBox, String searchText, int currentPage, int recordCountPerpage, int naviCountPerPage) {
		
		// currentPage : 선택한 현재 페이지
		// naviCountPerPage : PageNavi 값이 몇개씩 보여줄 것인지
		// recordCountPerpage : 한 페이지당 몇개씩 게시물이 보이게 할 것인지를 정함.
		
		int startPage = (currentPage - 1) * recordCountPerpage + 1;
		int endPage = (startPage + recordCountPerpage) -1;
				
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("selectBox", selectBox);
		map.put("searchText", searchText);
		map.put("start", startPage);
		map.put("end", endPage);

		List<Member> list = sqlSession.selectList("personnel.addbookSearch", map);

		return (ArrayList<Member>) list;
	}
	
	//개인주소록, 검색(search)
	public ArrayList<Contact> myaddbookSearch(SqlSessionTemplate sqlSession, String selectBox, String searchText,int memNo, int currentPage, int recordCountPerpage, int naviCountPerPage) {

		int startPage = (currentPage - 1) * recordCountPerpage + 1;
		int endPage = (startPage + recordCountPerpage) -1;
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("memNo", memNo);
		map.put("selectBox", selectBox);
		map.put("searchText", searchText);
		map.put("start", startPage);
		map.put("end", endPage);
		
		List<Contact> list = sqlSession.selectList("personnel.myaddbookSearch", map);
		
		return (ArrayList<Contact>) list;
	}
	
	public int insertMyaddbook(SqlSessionTemplate sqlSession, Map<String, Object> params) {
		int result = sqlSession.insert("personnel.insertMyaddbook", params);
		return result;
	}
	
	public int updateMyaddbook(SqlSessionTemplate sqlSession, Map<String, Object> params) {
		int result = sqlSession.update("personnel.updateMyaddbook",params);
		return result;
	}

	//개인주소록 삭제
	public int deleteMyaddbook(SqlSessionTemplate sqlSession, String ck) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("cntNo", ck);
		int result = sqlSession.update("personnel.deleteMyaddbook",map);
		return result;
	}


	// 서치 페이지 네비 메소드
	public String getPageNavi(SqlSessionTemplate sqlSession, String url, String selectBox, String searchText, int currentPage,
			int recordCountPerpage, int naviCountPerPage, int postTotalCount) {

		// 키워드르 바탕으로 검색된 pageNavi를 만드는메소드

		// 121부터 216 까지 복붙

		// 현재 변수를 재확인
		// currentPage : 현재 페이지를 가지고 있는 변수
		// recordCountPrepage : 한 페이지당 보여질 게시물의 개수
		// naviCountPerPage : pageNavi가 몇개씩 보여질 것인지에 대한 갯수

		// 페이지에 따른 게시물 갯수
		// 생성될 페이지 개수를 구하기
		// ex) postTotalCount에 108이라는 값이 있다면? 22개의 page가 생성이 되면됨
		// 108 / 5 -> 몫이 페이지가 되는데 , 이때 나머지가 있으면 +1 없으면 +0
		// 만약 105/5 페이지라면 , ? 몫:21인데 나머지가 0 // 총 페이지는 21page
		// 만약 만약 108/5 페이지라면 , ? 몫:21인데 나머지 3 // 총 페이지는 22page

		int pageTotalCount; // 전체 페이지를 저장하는 변수
		if (postTotalCount % recordCountPerpage > 0) { // 나머지가 0 보다 크다면
			pageTotalCount = postTotalCount / recordCountPerpage + 1;
			// ex ) pageTotalCount = 108 / 5 + 1 -> 22page

		} else { // 나머지가 없다면
			pageTotalCount = postTotalCount / recordCountPerpage + 0;
			// ex) pageTotalCount = 105 / 5 + 0 -> 21page

		}

		// 자 ! 이제는 현재 페이지를 중심으로 pageNavi를 계산 해야 함 (ex. 총 record는 108개 / 총 페이지는
		// 22page)
		// 만약 내가 현재 1page에 있다면 pageNavi는 어떻게 될까? (1~5 페이지를 가진 pageNavi) 1이 스타트
		// 5가 엔드임
		// 만약 내가 현재 3page에 있다면 pageNavi는 어떻게 될까 ? (1~5 페이지를 가진 pageNavi)
		// 만약 내가 현재 6page에 있다면 pageNavi는 어떻게 될까 ? (6~10 페이지를 가진 pageNavi)
		// 만약 내가 현재 12page에 있다면 pageNavi는 어떻게 될까 ? (11~15 페이지를 가진 pageNavi)
		// 만약 내가 현재 21page에 있다면 pageNavi는 어떻게 될까 ? (21~22 페이지를 가진 pageNavi)

		// 위 개념을 바타응로 현재 페이지를 중점으로 startNavi 페이지 번호와 endNavi 페이지 번호를 구할것임
		// startNavi = ((현재페이지 -1 / 보여질 navi개수) * 보여질 navi개수 +1;
		int startNavi = ((currentPage - 1) / naviCountPerPage) * naviCountPerPage + 1;

		// 위에 공식으로 계산을 해보겠습니다.
		// ex) 1페이지 일경우 (결과 : startNavi는 1이 나와야 함)
		// ((1-1)/5)*5+1; -> 1이 계산이 됨
		// ex) 3페이지 일경우 (결과 : startNavi는 1이 나와야 함)
		// ((3-1)/5)*5+1; -> 1이 계산이 됨
		// ex) 6페이지 일경우 (결과 : startNavi는 6이 나와야 함)
		// ((6-1)/5)*5+1; -> 6이 계산이 됨
		// ex) 12페이지 일경우 (결과 : startNavi는 11이 나와야 함)
		// ((12-1)/5)*5+1; -> 6이 계산이 됨

		// endNavi = 시작 navi번호 + 보여질 navi개수 -1;
		int endNavi = startNavi + naviCountPerPage - 1;

		// 위의 공식으로 계산을 해보겠습니다.
		// ex) 1페이지 일 경우 (결과 : endNavi는 5가 나와야 함)
		// 1 + 5 -1 -> 5가 나온다.
		// ex) 3페이지 일 경우 (결과 : endNavi는 5가 나와야 함)
		// 1 + 5 -1 -> 5가 나온다.
		// ex) 12페이지 일 경우 (결과 : endNavi는 15가 나와야 함)
		// 11 + 5 -1 -> 15가 나온다.

		// 문제점이 하나있다. ex)21페이지일 경우 (결과 : endNavi는 22가 나와야 함)
		// 21 + 5 - 1 -> 25가 나온다
		// 즉, 총 페이지 수가 22이므로 22를 넘어가지 않도록 해야 함
		if (endNavi > pageTotalCount) {
			endNavi = pageTotalCount;
		}

		// 여기 까지 계산 되었으면 공식 계산은 끝났음 !
		// 이제 pageNavi의 모양을 구성해야 한다
		StringBuilder sb = new StringBuilder(); // StringBuffer와 같음

		// 만약 첫번째 pageVavi가 아니라면 '<'모양을 추가해라 (첫번째 pageNavi이면 추가하지 말아라)
		// 스타트네비가 1과 같지 않다면 1 2 3 4 5 추가하면안됨
		if (startNavi != 1) {
			sb.append("<a href='/"+url+".ho?searchText=" + searchText + "&currentPage=" + (startNavi - 1) + "'><</a> "); // 전페이지로 감
		}

		for (int i = startNavi; i <= endNavi; i++) {
			if (i == currentPage) // 현재 페이지가 i와 같다면
			{
				sb.append("<a href='/"+url+".ho?searchText=" + searchText + "&currentPage=" + i + "'><b>" + i
						+ "</b></a> "); // 볼드 추가

			} else {
				sb.append("<a href='/"+url+".ho?searchText=" + searchText + "&currentPage=" + i + "'>" + i + "</a> "); // 볼드빼기

			}
		}
		// 만약 마지막 pageVavi가 아니라면 '>'모양을 추가해라 (마지막 pageNavi이면 추가하지 말아라)

		if (endNavi != pageTotalCount) {
			sb.append("<a href='/"+url+".ho?searchText=" + searchText + "&currentPage=" + (endNavi + 1) + "'>></a> "); // 전페이지로
											 																			// 감
		}
		
		// System.out.println(sb); 확인하는 코드
		return sb.toString(); }
	
	//내 개인정보 (마이페이지)
	public MemDept mypage(SqlSessionTemplate sqlSession, int memNo) {

		MemDept md = sqlSession.selectOne("personnel.mypage",memNo);
		return md;
	}

	// 인사정보
	public ArrayList<MemDept> information(SqlSessionTemplate sqlSession, int memNo) {
		List list = sqlSession.selectList("personnel.information", memNo);
		return (ArrayList<MemDept>)list; 
	}

	public int mypageChange(SqlSessionTemplate sqlSession, Map<String, Object> map) {
		int result = sqlSession.update("personnel.mypageChange",map);
		return result;
	}
	
	public int checkPwd(SqlSessionTemplate sqlSession, Map<String, Object> map) {
		int result = sqlSession.selectOne("personnel.checkPwd",map);
		return result;
	}

	public int inforPwChange(SqlSessionTemplate sqlSession, Map<String, Object> map) {
		int result = sqlSession.update("personnel.inforPwChange",map);
		return result;
	}

	public int photoUpdate(SqlSessionTemplate sqlSession, int memNo) {
		int result = sqlSession.update("personnel.photoUpdate",memNo);
		return result;
		
	}



}
